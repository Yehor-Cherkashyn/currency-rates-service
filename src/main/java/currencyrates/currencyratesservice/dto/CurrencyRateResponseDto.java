package currencyrates.currencyratesservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CurrencyRateResponseDto {
    @JsonProperty("currency_name")
    private String currencyName;
    @JsonProperty("rate")
    private BigDecimal rate;
    @JsonProperty("currency_code")
    private String currencyCode;
    @JsonProperty("exchange_date")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate exchangeDate;
    @JsonProperty("receiving_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime receivingDate;
}
