package com.n26.mapper;

import com.n26.dto.TransactionUnitDto;
import com.n26.model.TransactionUnit;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static com.n26.validator.TransactionValidator.TIMESTAMP_FORMATTER;

@Mapper(componentModel = "spring")
public interface TransactionUnitMapper extends GenericMapper<TransactionUnit, TransactionUnitDto> {

    @Override
    @Mapping(target = "timestamp", expression = "java(toLocalDateTime(dto.getTimestamp()))")
    TransactionUnit toEntity(TransactionUnitDto dto);

    default LocalDateTime toLocalDateTime(String timestamp) {
        return LocalDateTime.parse(timestamp, TIMESTAMP_FORMATTER);
    }
}
