package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.rest.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatGraphQlTest extends BaseGraphQlTest {

    @User
    @Test
    @ApiLogin
    void statTest(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        Assertions.assertEquals(
                0.0,
                result.total
        );
    }

    @User(
            categories = {
                    @Category(name = "Food"),
                    @Category(name = "Transport", archived = true)
            },
            spendings = {
                    @Spending(
                            category = "Food",
                            description = "Test",
                            amount = 200
                    ),
                    @Spending(
                            category = "Transport",
                            description = "Test",
                            amount = 150
                    )
            }

    )
    @Test
    @ApiLogin
    void statTestsjdjsak(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;

        assertNotNull(data.stat);
        assertEquals(350.0, result.total, "Total should match the combined spending amount");
        assertFalse(result.statByCategories.isEmpty(), "Stat by categories should not be empty");

        // Assert the response contains non archived category
        boolean hasFirstNonArchivedCategory = result.statByCategories.stream()
                .anyMatch(category -> category.categoryName.equals("Food") && category.sum == 200.0);
        Assertions.assertTrue(hasFirstNonArchivedCategory, "Food category should be included in the response");

        // Assert the response contains archived category
        boolean hasSecondArchivedCategory = result.statByCategories.stream()
                .anyMatch(category -> category.categoryName.equals("Archived") && category.sum == 150.0);
        Assertions.assertTrue(hasSecondArchivedCategory, "Archived categories should be included in the response");
    }
}
