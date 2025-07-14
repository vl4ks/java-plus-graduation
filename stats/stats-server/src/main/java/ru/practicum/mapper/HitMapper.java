package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.model.Hit;


@Mapper
public interface HitMapper {

    HitMapper INSTANCE = Mappers.getMapper(HitMapper.class);

    @Mapping(target = "timestamp", source = "timestamp")
    Hit mapToHit(ResponseHitDto responseHitDto);

    @Mapping(target = "timestamp", source = "timestamp")
    ResponseHitDto mapToResponseDto(Hit hit);
}
