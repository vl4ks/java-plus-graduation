package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.EventRequest;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventRequestMapper {

    @Mapping(target = "created", expression = "java(eventRequest.getCreated())")
    @Mapping(target = "event", source = "eventId")
    @Mapping(target = "requester", source = "requesterId")
    ParticipationRequestDto toParticipationRequestDto(EventRequest eventRequest);

    List<ParticipationRequestDto> toParticipationRequestDtoList(List<EventRequest> requests);

}
