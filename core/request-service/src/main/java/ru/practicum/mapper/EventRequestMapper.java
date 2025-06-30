package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.EventRequest;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventRequestMapper {

    @Mapping(target = "created", expression = "java(eventRequest.getCreated())")
    ParticipationRequestDto toParticipationRequestDto(EventRequest eventRequest);
}
