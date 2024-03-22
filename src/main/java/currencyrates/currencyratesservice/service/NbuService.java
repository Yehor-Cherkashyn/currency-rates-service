package currencyrates.currencyratesservice.service;

import java.time.LocalDate;
import java.util.List;
import currencyrates.currencyratesservice.model.CurrencyRate;

public interface NbuService {
    List<CurrencyRate> fetchCurrentRates();

    List<CurrencyRate> fetchRatesForDate(LocalDate date);

    CurrencyRate fetchRateForCurrencyOnDate(String currencyCode, LocalDate date);
}
