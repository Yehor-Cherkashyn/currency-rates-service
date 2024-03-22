package currencyrates.currencyratesservice.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import currencyrates.currencyratesservice.exception.CurrencyRateFetchException;
import currencyrates.currencyratesservice.exception.CurrencyRateNotFoundException;
import currencyrates.currencyratesservice.exception.CurrencyRateSaveException;
import currencyrates.currencyratesservice.model.CurrencyRate;
import currencyrates.currencyratesservice.repository.CurrencyRateRepository;
import currencyrates.currencyratesservice.service.CurrencyRateService;
import currencyrates.currencyratesservice.service.NbuService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CurrencyRateServiceImpl implements CurrencyRateService {
    //region Fields
    private static final Logger logger = LogManager.getLogger(CurrencyRateServiceImpl.class);
    private CurrencyRateRepository currencyRateRepository;
    private NbuService nbuService;
    //endregion

    @Override
    public CurrencyRate saveRateToDB(CurrencyRate currencyRate) {
        try {
            return currencyRateRepository.save(currencyRate);
        } catch (Exception e) {
            logger.error("Error saving currency rates to the database", e);

            throw new CurrencyRateSaveException("Failed to save currency rates.", e);
        }
    }

    @Override
    public List<CurrencyRate> saveAllToDB(List<CurrencyRate> currencyRates) {
        try {
            return currencyRateRepository.saveAll(currencyRates);
        } catch (Exception e) {
            logger.error("Error saving currency rates to the database", e);

            throw new CurrencyRateSaveException("Failed to save currency rates.", e);
        }
    }

    @Override
    public List<CurrencyRate> findCurrencyByCurrentDate() {
        return findAndSaveRatesByDate(LocalDate.now());
    }

    @Override
    public List<CurrencyRate> findAllRatesByDate(LocalDate date) {
        return findAndSaveRatesByDate(date);
    }

    @Override
    public CurrencyRate findRateByDateAndCurrency(String currencyCode, LocalDate date) {
        logger.info("Searching for currency rate in the database "
                + "for currency code: {} on date: {}", currencyCode, date);

        return currencyRateRepository.findCurrencyRateByCcAndExchangeDate(currencyCode, date)
                .orElseGet(() -> {
                    logger.info("No currency rate found in the database "
                                    + "for currency code: {} on date {}, fetching from NBU",
                            currencyCode, date);
                    CurrencyRate fetchedRate =
                            nbuService.fetchRateForCurrencyOnDate(currencyCode, date);
                    if (fetchedRate != null) {
                        fetchedRate.setReceivingDate(LocalDateTime.now());
                        return saveRateToDB(fetchedRate);
                    } else {
                        throw new CurrencyRateFetchException("No currency rate received from NBU "
                                + "for currency code: " + currencyCode + " on date " + date);
                    }
                });
    }

    @Override
    @Transactional
    public void deleteByDate(LocalDate date) {
        logger.info("Deleting all currency rates on date {}", date);

        int isDeleted = currencyRateRepository.deleteAllByExchangeDate(date);

        if (isDeleted > 0) {
            logger.info("Deleted currency rates on date: {}", date);
        } else {
            throw new CurrencyRateNotFoundException("No currency rates found to delete on date: "
                    + date);
        }
    }

    private List<CurrencyRate> findAndSaveRatesByDate(LocalDate date) {
        List<CurrencyRate> currencyRates = currencyRateRepository.findAllByExchangeDate(date);

        if (currencyRates != null && !currencyRates.isEmpty()) {
            logger.info("Found {} currency rates in the database for date: {}",
                    currencyRates.size(), date);

            return currencyRates;
        } else {
            logger.info("No currency rates found in the database "
                    + "for date: {}, fetching from NBU", date);

            List<CurrencyRate> fetchedRates;

            if (date.isEqual(LocalDate.now())) {
                fetchedRates = nbuService.fetchCurrentRates();
            } else {
                fetchedRates = nbuService.fetchRatesForDate(date);
            }

            if (fetchedRates == null || fetchedRates.isEmpty()) {
                logger.warn("No currency rates received from NBU for date: {}", date);

                throw new CurrencyRateNotFoundException("No currency rates available "
                        + "for date: " + date);
            }
            fetchedRates.forEach(cr -> cr.setReceivingDate(LocalDateTime.now()));

            return saveAllToDB(fetchedRates);
        }
    }
}
