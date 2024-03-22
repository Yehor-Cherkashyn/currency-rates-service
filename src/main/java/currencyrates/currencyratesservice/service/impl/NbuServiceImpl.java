package currencyrates.currencyratesservice.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import currencyrates.currencyratesservice.model.CurrencyRate;
import currencyrates.currencyratesservice.service.NbuService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Profile("dev")
public class NbuServiceImpl implements NbuService {
    //region Fields
    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String URL_DATE_ENDPOINT = "date";
    private static final String URL_VALCODE_ENDPOINT = "valcode";
    private static final int DURATION = 10;
    private static final Logger logger = LogManager.getLogger(NbuServiceImpl.class);
    private final RestTemplate restTemplate;
    @Value("${nbu.api.url}")
    private String nbuApiUrl;
    //endregion

    public NbuServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(DURATION))
                .setReadTimeout(Duration.ofSeconds(DURATION))
                .build();
    }

    @Override
    public List<CurrencyRate> fetchCurrentRates() {
        logger.info("Fetching current currency rates from NBU");

        return fetchCurrencyRates(nbuApiUrl, CurrencyRate[].class);
    }

    @Override
    public List<CurrencyRate> fetchRatesForDate(LocalDate date) {
        logger.info("Fetching currency rates from NBU on date: {}", date);

        String formattedDate = date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        String urlWithDate = UriComponentsBuilder.fromHttpUrl(nbuApiUrl)
                .queryParam(URL_DATE_ENDPOINT, formattedDate)
                .toUriString();

        return fetchCurrencyRates(urlWithDate, CurrencyRate[].class);
    }

    @Override
    public CurrencyRate fetchRateForCurrencyOnDate(String currencyCode, LocalDate date) {
        logger.info("Fetching currency rates from NBU for currency code: {} on date: {}",
                date, currencyCode);

        String formattedDate = date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        String urlWithCurrencyAndDate = UriComponentsBuilder.fromHttpUrl(nbuApiUrl)
                .queryParam(URL_VALCODE_ENDPOINT, currencyCode)
                .queryParam(DATE_FORMAT, formattedDate)
                .toUriString();
        List<CurrencyRate> rates = fetchCurrencyRates(
                urlWithCurrencyAndDate,
                CurrencyRate[].class);

        return rates.isEmpty() ? null : rates.get(0);
    }

    private <T> List<T> fetchCurrencyRates(String url, Class<T[]> responseType) {
        logger.info("Making HTTP request to URL: {}", url);

        try {
            ResponseEntity<T[]> response = restTemplate.getForEntity(url, responseType);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.debug("Received currency rates: {}", Arrays.toString(response.getBody()));

                return Arrays.asList(response.getBody());
            } else {
                logger.warn("No currency rates received. Status code: {}",
                        response.getStatusCode());

                return List.of();
            }
        } catch (RestClientException e) {
            logger.error("Error fetching currency rates", e);

            return List.of();
        }
    }
}
