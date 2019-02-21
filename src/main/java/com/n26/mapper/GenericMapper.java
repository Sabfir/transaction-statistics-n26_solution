package com.n26.mapper;

import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.MappingTarget;

/**
 * Generic interface for mappers from entity to DTO and back.
 */
public interface GenericMapper<E, D> {

    D toDto(E entity);

    List<D> toDto(List<E> entities);

    @InheritInverseConfiguration
    E toEntity(D dto);

    @InheritInverseConfiguration
    E toEntity(D dto, @MappingTarget E entity);

    List<E> toEntity(List<D> dtos);
}
