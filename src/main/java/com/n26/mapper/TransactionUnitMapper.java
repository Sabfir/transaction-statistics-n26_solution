package com.n26.mapper;

import com.n26.dto.TransactionUnitDto;
import com.n26.model.TransactionUnit;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static com.n26.validator.TransactionValidator.TIMESTAMP_FORMATTER;

@Mapper(componentModel = "spring")
public interface TransactionUnitMapper extends GenericMapper<TransactionUnit, TransactionUnitDto> {

    @Override
    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "toLocalDateTime")
    TransactionUnit toEntity(TransactionUnitDto dto);

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(String timestamp) {
        return LocalDateTime.parse(timestamp, TIMESTAMP_FORMATTER);
    }
}
