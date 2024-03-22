package currencyrates.currencyratesservice.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import currencyrates.currencyratesservice.dto.CurrencyRateResponseDto;
import currencyrates.currencyratesservice.exception.CurrencyRateFetchException;
import currencyrates.currencyratesservice.exception.CurrencyRateNotFoundException;
import currencyrates.currencyratesservice.model.CurrencyRate;
import currencyrates.currencyratesservice.service.CurrencyRateService;
import currencyrates.currencyratesservice.service.mapper.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CurrencyRateController.class)
class CurrencyRateControllerTest {
    //region Fields
    private static final String DATE_PATTERN = "yyyyMMdd";
    private static final String CURRENT_CURRENCY_RATES_URL = "/currency-rates/current";
    private static final String CURRENCY_RATES_BY_DATE_URL = "/currency-rates/by-date";
    private static final String CURRENCY_RATES_BY_VALCODE_AND_DATE_URL = "/currency-rates/by-currency-and-date";
    private static final String VALCODE = "valcode";
    private static final String DATE = "date";
    private static final String CC = "USD";
    private static final String INVALID_CURRENCY = "SSS";
    private static final String INVALID_DATE = "20201301";
    private static final BigDecimal RATE = BigDecimal.valueOf(27.5);
    private static final int ONE_COUNT = 1;
    private static final Long R030 = 840L;
    private CurrencyRate rateTest;
    private CurrencyRateResponseDto dtoTest;
    private List<CurrencyRate> ratesTest;
    private List<CurrencyRateResponseDto> dtosTest;
    //endregion

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyRateService currencyRateService;

    @MockBean
    private DtoMapper<CurrencyRate, CurrencyRateResponseDto> dtoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        dtoTest = new CurrencyRateResponseDto();
        dtoTest.setExchangeDate(LocalDate.now());
        dtoTest.setCurrencyName(CC);
        dtoTest.setRate(RATE);
        dtoTest.setCurrencyCode(CC);
        dtoTest.setReceivingDate(LocalDateTime.now());

        dtosTest = Collections.singletonList(dtoTest);

        rateTest = new CurrencyRate();
        rateTest.setR030(R030);
        rateTest.setCc(CC);
        rateTest.setTxt(CC);
        rateTest.setRate(RATE);
        rateTest.setExchangeDate(LocalDate.now());
        rateTest.setReceivingDate(LocalDateTime.now());

        ratesTest = Collections.singletonList(rateTest);
    }

    @Test
    void getCurrentRates_success() throws Exception {
        when(currencyRateService.findCurrencyByCurrentDate()).thenReturn(ratesTest);
        when(dtoMapper.toDto(any(CurrencyRate.class))).thenReturn(dtoTest);

        mockMvc.perform(get(CURRENT_CURRENCY_RATES_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtosTest)));
    }

    @Test
    void getCurrentRates_emptyList() throws Exception {
        when(currencyRateService.findCurrencyByCurrentDate()).thenReturn(Collections.emptyList());
        when(dtoMapper.toDto(any(CurrencyRate.class))).thenReturn(null);

        mockMvc.perform(get(CURRENT_CURRENCY_RATES_URL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getRatesByDate_success() throws Exception {
        LocalDate date = LocalDate.now();

        when(currencyRateService.findAllRatesByDate(date)).thenReturn(ratesTest);
        when(dtoMapper.toDto(any(CurrencyRate.class))).thenReturn(dtoTest);

        mockMvc.perform(get(CURRENCY_RATES_BY_DATE_URL)
                        .param(DATE, date.format(DateTimeFormatter.ofPattern(DATE_PATTERN))))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtosTest)));
    }

    @Test
    void getRatesByDate_futureDate() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(ONE_COUNT);

        when(currencyRateService.findAllRatesByDate(futureDate))
                .thenThrow(new CurrencyRateFetchException(
                        "The date cannot be specified later than the current one"));

        mockMvc.perform(get(CURRENCY_RATES_BY_DATE_URL)
                        .param(DATE, futureDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN))))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof CurrencyRateFetchException))
                .andExpect(result -> assertEquals(
                        "The date cannot be specified later than the current one",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void getRatesByDate_invalidDate() throws Exception {
        mockMvc.perform(get(CURRENCY_RATES_BY_DATE_URL).param(DATE, INVALID_DATE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRatesByCurrencyAndDate_success() throws Exception {
        LocalDate date = LocalDate.now();
        String currencyCode = CC;

        when(currencyRateService.findRateByDateAndCurrency(currencyCode, date))
                .thenReturn(rateTest);
        when(dtoMapper.toDto(any(CurrencyRate.class))).thenReturn(dtoTest);

        mockMvc.perform(get(CURRENCY_RATES_BY_VALCODE_AND_DATE_URL)
                        .param(VALCODE, currencyCode)
                        .param(DATE,
                                date.format(DateTimeFormatter.ofPattern(DATE_PATTERN))))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoTest)));
    }

    @Test
    void getRatesByCurrencyAndDate_invalidCurrency() throws Exception {
        LocalDate date = LocalDate.now();
        String invalidCurrencyCode = INVALID_CURRENCY;

        when(currencyRateService.findRateByDateAndCurrency(invalidCurrencyCode, date))
                .thenThrow(new CurrencyRateNotFoundException("Currency code not found"));

        mockMvc.perform(get(CURRENCY_RATES_BY_VALCODE_AND_DATE_URL)
                        .param(VALCODE, invalidCurrencyCode)
                        .param(DATE, date.format(DateTimeFormatter.ofPattern(DATE_PATTERN))))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof CurrencyRateNotFoundException))
                .andExpect(result -> assertEquals(
                        "Currency code not found", result.getResolvedException().getMessage()));
    }

    @Test
    void deleteRatesByDate_success() throws Exception {
        LocalDate date = LocalDate.now();
        doNothing().when(currencyRateService).deleteByDate(date);

        mockMvc.perform(delete(CURRENCY_RATES_BY_DATE_URL)
                        .param(DATE, date.format(DateTimeFormatter.ofPattern(DATE_PATTERN))))
                .andExpect(status().isOk());

        verify(currencyRateService, times(ONE_COUNT)).deleteByDate(date);
    }
}
