package currencyrates.currencyratesservice.service;

import java.time.LocalDate;
import java.util.List;
import currencyrates.currencyratesservice.model.CurrencyRate;

public interface CurrencyRateService {
    CurrencyRate saveRateToDB(CurrencyRate currencyRate);

    List<CurrencyRate> saveAllToDB(List<CurrencyRate> currencyRates);

    List<CurrencyRate> findCurrencyByCurrentDate();

    List<CurrencyRate> findAllRatesByDate(LocalDate date);

    CurrencyRate findRateByDateAndCurrency(String currencyCode, LocalDate date);

    void deleteByDate(LocalDate date);
}
