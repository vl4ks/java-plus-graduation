package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.*;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    Event getEvent(NewEventDto newEventDto);

    EventFullDto getEventDto(Event event);

    EventShortDto getEventShortDto(Event event);

}
