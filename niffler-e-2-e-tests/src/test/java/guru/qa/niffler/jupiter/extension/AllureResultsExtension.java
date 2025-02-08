package guru.qa.niffler.jupiter.extension;
import okhttp3.*;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AllureResultsExtension implements SuiteExtension {

    private static final Logger LOG = LoggerFactory.getLogger(AllureResultsExtension.class);

    private static final String ALLURE_DOCKER_API = System.getenv("ALLURE_DOCKER_API");
    private static final String GITHUB_TOKEN = System.getenv("GITHUB_TOKEN");
    private static final String BUILD_URL = System.getenv("BUILD_URL");
    private static final String HEAD_COMMIT_MESSAGE = System.getenv("HEAD_COMMIT_MESSAGE");
    private static final String EXECUTION_TYPE = System.getenv("EXECUTION_TYPE");
    private static final String CHECK_RESULTS_EVERY_SECONDS = System.getenv("CHECK_RESULTS_EVERY_SECONDS");
    private static final String KEEP_HISTORY = System.getenv("KEEP_HISTORY");
    private static final String ALLURE_RESULTS_PATH = "allure-results";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public void afterSuite() {
        LOG.info("Test execution completed. Preparing to upload results to Allure Docker Service...");

        // Validate ALLURE_DOCKER_API environment variable
        if (ALLURE_DOCKER_API == null || ALLURE_DOCKER_API.isEmpty()) {
            LOG.error("ALLURE_DOCKER_API environment variable is not configured. Skipping results upload.");
            return;
        }

        // Check if the allure-results directory exists
        File resultsDir = new File(ALLURE_RESULTS_PATH);
        if (!resultsDir.exists() || !resultsDir.isDirectory()) {
            LOG.error("Allure results directory not found at path: {}", ALLURE_RESULTS_PATH);
            return;
        }

        // Send the results to the Allure Docker Service
        if (!sendResults()) {
            LOG.error("Failed to send Allure results to the server.");
            return;
        }

        // Request report generation from Allure Docker Service
        generateReport();
    }

    private boolean sendResults() {
        try {
            File resultsDir = new File(ALLURE_RESULTS_PATH);
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);

            // Add files from allure-results directory
            for (File file : resultsDir.listFiles()) {
                if (file.isFile()) {
                    builder.addFormDataPart("results", file.getName(),
                            RequestBody.create(file, MediaType.parse("application/octet-stream")));
                }
            }

            builder.addFormDataPart("executionType", EXECUTION_TYPE);
            builder.addFormDataPart("githubToken", GITHUB_TOKEN);
            builder.addFormDataPart("buildUrl", BUILD_URL);
            builder.addFormDataPart("commitMessage", HEAD_COMMIT_MESSAGE);
            builder.addFormDataPart("checkResultsEverySeconds", CHECK_RESULTS_EVERY_SECONDS);
            builder.addFormDataPart("keepHistory", KEEP_HISTORY);

            RequestBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .url(ALLURE_DOCKER_API + "/send-results")
                    .header("Authorization", "Bearer " + GITHUB_TOKEN)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    LOG.info("Successfully uploaded Allure results. Response code: {}", response.code());
                    return true;
                } else {
                    LOG.error("Failed to upload Allure results. Server responded with code: {}", response.code());
                    return false;
                }
            }
        } catch (IOException e) {
            LOG.error("An error occurred while sending results to Allure: {}", e.getMessage(), e);
            return false;
        }
    }

    private void generateReport() {
        try {
            Request request = new Request.Builder()
                    .url(ALLURE_DOCKER_API + "/generate-report")
                    .header("Authorization", "Bearer " + GITHUB_TOKEN)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    LOG.info("Successfully initiated Allure report generation. Response code: {}", response.code());
                } else {
                    LOG.error("Failed to initiate Allure report generation. Server responded with code: {}", response.code());
                }
            }
        } catch (IOException e) {
            LOG.error("An error occurred while generating the Allure report: {}", e.getMessage(), e);
        }
    }
}
