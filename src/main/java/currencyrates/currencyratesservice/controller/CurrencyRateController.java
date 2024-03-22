package currencyrates.currencyratesservice.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import currencyrates.currencyratesservice.dto.CurrencyRateResponseDto;
import currencyrates.currencyratesservice.model.CurrencyRate;
import currencyrates.currencyratesservice.service.CurrencyRateService;
import currencyrates.currencyratesservice.service.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/currency-rates")
public class CurrencyRateController {
    //region Fields
    private static final String DATE = "date";
    private static final String DATE_PATTERN = "yyyyMMdd";
    private static final String VALCODE = "valcode";
    private static final Logger logger = LogManager.getLogger(CurrencyRateController.class);
    private final CurrencyRateService currencyRateService;
    private final DtoMapper<CurrencyRate,
            CurrencyRateResponseDto> dtoMapper;
    //endregion

    @GetMapping("/current")
    public List<CurrencyRateResponseDto> getCurrentRates() {
        logger.info("Received request to get current currency rates");

        final List<CurrencyRateResponseDto> currencyRates =
                currencyRateService.findCurrencyByCurrentDate()
                .stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());

        logger.info("Responding with {} current currency rates", currencyRates.size());

        return currencyRates;
    }

    @GetMapping("/by-date")
    public List<CurrencyRateResponseDto> getRatesByDate(@RequestParam(DATE)
                                                            @DateTimeFormat(pattern = DATE_PATTERN)
                                                            LocalDate date) {
        logger.info("Received request to get currency rates on date: {}", date);

        final List<CurrencyRateResponseDto> currencyRates =
                currencyRateService.findAllRatesByDate(date)
                .stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());

        logger.info("Responding with {} currency rates on date: {}", currencyRates.size(), date);

        return currencyRates;
    }

    @GetMapping("/by-currency-and-date")
    public CurrencyRateResponseDto getRatesByCurrencyAndDate(@RequestParam(value = VALCODE)
                                                                 String currencyCode,
                                                             @RequestParam(DATE)
                                                             @DateTimeFormat(pattern = DATE_PATTERN)
                                                             LocalDate date) {
        logger.info("Received request to get currency rates "
                + "for currency code: {} on date: {}", currencyCode, date);

        final CurrencyRateResponseDto dto =
                dtoMapper.toDto(currencyRateService.findRateByDateAndCurrency(currencyCode, date));

        logger.info("Responding for currency rates "
                + "for currency code: {} on date: {}", currencyCode, date);

        return dto;
    }

    @DeleteMapping("/by-date")
    public void deleteRatesByDate(@RequestParam(DATE)
                                      @DateTimeFormat(pattern = DATE_PATTERN)
                                      LocalDate date) {
        logger.info("Received request to delete currency rates on date: {}", date);

        currencyRateService.deleteByDate(date);
    }
}
