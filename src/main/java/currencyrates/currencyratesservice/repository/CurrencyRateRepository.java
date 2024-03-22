package currencyrates.currencyratesservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import currencyrates.currencyratesservice.model.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    List<CurrencyRate> findAllByExchangeDate(LocalDate exchangeDate);

    Optional<CurrencyRate> findCurrencyRateByCcAndExchangeDate(String currencyCode, LocalDate date);

    int deleteAllByExchangeDate(LocalDate date);
}
