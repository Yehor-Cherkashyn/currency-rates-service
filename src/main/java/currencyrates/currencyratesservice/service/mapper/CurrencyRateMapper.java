package currencyrates.currencyratesservice.service.mapper;

import currencyrates.currencyratesservice.dto.CurrencyRateResponseDto;
import currencyrates.currencyratesservice.model.CurrencyRate;
import org.springframework.stereotype.Component;

@Component
public class CurrencyRateMapper implements DtoMapper<CurrencyRate, CurrencyRateResponseDto> {
    @Override
    public CurrencyRateResponseDto toDto(CurrencyRate model) {
        CurrencyRateResponseDto responseDto = new CurrencyRateResponseDto();
        responseDto.setCurrencyName(model.getTxt());
        responseDto.setRate(model.getRate());
        responseDto.setCurrencyCode(model.getCc());
        responseDto.setExchangeDate(model.getExchangeDate());
        responseDto.setReceivingDate(model.getReceivingDate());

        return responseDto;
    }
}
