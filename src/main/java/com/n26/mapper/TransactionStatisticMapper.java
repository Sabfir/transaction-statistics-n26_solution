package com.n26.mapper;

import com.n26.dto.TransactionStatisticDto;
import com.n26.model.TransactionStatistic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionStatisticMapper extends GenericMapper<TransactionStatistic, TransactionStatisticDto> {

    @Override
    @Mapping(target = "avg", source = "average")
    TransactionStatisticDto toDto(TransactionStatistic entity);
}
