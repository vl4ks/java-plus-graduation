package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.EventRequest;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventRequestMapper {
    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Mapping(target = "created", expression = "java(eventRequest.getCreated().format(FORMATTER))")
    ParticipationRequestDto toParticipationRequestDto(EventRequest eventRequest);
}
