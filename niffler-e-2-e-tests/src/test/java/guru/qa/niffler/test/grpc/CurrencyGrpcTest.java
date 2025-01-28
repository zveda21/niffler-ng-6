package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyGrpcTest extends BaseGrpcTest {

  @Test
  void allCurrenciesShouldReturned() {
    final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
    final List<Currency> allCurrenciesList = response.getAllCurrenciesList();
    assertEquals(4, allCurrenciesList.size());
  }

  @Test
  void calculateRateShouldReturnCorrectAmountForUSDToEUR() {
    double amount = 34.00;
    CurrencyValues spendCurrency = CurrencyValues.USD;
    CurrencyValues changeToCurrency = CurrencyValues.EUR;

    CalculateRequest request = CalculateRequest.newBuilder()
            .setAmount(amount)
            .setSpendCurrency(spendCurrency)
            .setDesiredCurrency(changeToCurrency)
            .build();

    CalculateResponse response = blockingStub.calculateRate(request);
    assertEquals(31.48, response.getCalculatedAmount(), 0.01);
  }

  @Test
  void calculateRateShouldReturnCorrectAmountForUSDToRUB() {
    double amount = 100;
    CurrencyValues spendCurrency = CurrencyValues.USD;
    CurrencyValues changeToCurrency = CurrencyValues.RUB;

    CalculateRequest request = CalculateRequest.newBuilder()
            .setAmount(amount)
            .setSpendCurrency(spendCurrency)
            .setDesiredCurrency(changeToCurrency)
            .build();

    CalculateResponse response = blockingStub.calculateRate(request);
    assertEquals(6666.67, response.getCalculatedAmount(), 0.01);
  }

  @Test
  void calculateRateShouldReturnSameAmountForRUBToRUB() {
    double amount = 250;
    CurrencyValues spendCurrency = CurrencyValues.RUB;
    CurrencyValues changeToCurrency = CurrencyValues.RUB;

    CalculateRequest request = CalculateRequest.newBuilder()
            .setAmount(amount)
            .setSpendCurrency(spendCurrency)
            .setDesiredCurrency(changeToCurrency)
            .build();

    CalculateResponse response = blockingStub.calculateRate(request);
    assertEquals(250.0, response.getCalculatedAmount(), 0.01);
  }
}
