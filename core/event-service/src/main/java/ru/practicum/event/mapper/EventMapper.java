package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.*;
import ru.practicum.event.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    Event getEvent(NewEventDto newEventDto);

    EventFullDto getEventDto(Event event);

    EventFullDto toEventFullDto(Event event, CategoryDto categoryDto, LocationDto locationDto, UserShortDto initiatorDto, Long views);

    EventShortDto getEventShortDto(Event event);

    EventShortDto toEventShortDto(Event event, CategoryDto categoryDto, UserShortDto initiatorDto, Long views);

    Event fromNewEventDto(NewEventDto newEventDto, Category category, Location location, Long initiatorId);

    void updateFromDto(Event event, UpdateEventUserRequest eventDto, Category category, Location location);

    void updateFromDto(Event event, UpdateEventAdminRequest eventDto, Category category, Location location);
}
