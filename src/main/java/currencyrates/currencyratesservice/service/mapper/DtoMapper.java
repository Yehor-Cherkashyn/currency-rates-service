package currencyrates.currencyratesservice.service.mapper;

public interface DtoMapper<M, S> {
    S toDto(M model);
}
