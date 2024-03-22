package currencyrates.currencyratesservice.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import currencyrates.currencyratesservice.exception.CurrencyRateFetchException;
import currencyrates.currencyratesservice.model.CurrencyRate;
import currencyrates.currencyratesservice.service.NbuService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mock")
public class MockNbuServiceImpl implements NbuService {
    //region Fields
    private static final Logger logger = LogManager.getLogger(MockNbuServiceImpl.class);
    private final ObjectMapper objectMapper;
    @Value("${nbu.api.mock-data}")
    private String nbuDataPath;
    //endregion

    public MockNbuServiceImpl() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public List<CurrencyRate> fetchCurrentRates() {
        logger.info("Fetching mock data for current currency rates");

        return fetchMockData();
    }

    @Override
    public List<CurrencyRate> fetchRatesForDate(LocalDate date) {
        logger.info("Fetching mock data for currency rates on date: {}", date);

        isAfterThanCurrentDate(date);

        final List<CurrencyRate> currencyRates = fetchMockData();
        currencyRates.forEach(rate -> rate.setExchangeDate(date));

        return currencyRates;
    }

    @Override
    public CurrencyRate fetchRateForCurrencyOnDate(String currencyCode, LocalDate date) {
        isAfterThanCurrentDate(date);

        logger.info("Fetching mock data for currency code: {} on date: {}", currencyCode, date);

        return fetchMockData().stream()
                .filter(rate -> currencyCode.equalsIgnoreCase(rate.getCc()))
                .peek(rate -> rate.setExchangeDate(date))
                .findFirst()
                .orElse(null);
    }

    private List<CurrencyRate> fetchMockData() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(nbuDataPath)) {
            if (is == null) {
                logger.error("Mock data file '{}' not found", nbuDataPath);

                return List.of();
            }
            CurrencyRate[] currencyRates = objectMapper.readValue(is, CurrencyRate[].class);

            logger.info("Successfully loaded mock data");

            return Arrays.asList(currencyRates);
        } catch (IOException e) {
            logger.error("Failed to load mock data from '{}'", nbuDataPath, e);

            return List.of();
        }
    }

    public void isAfterThanCurrentDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new CurrencyRateFetchException(
                    "The date cannot be specified later than the current one");
        }
    }
}
