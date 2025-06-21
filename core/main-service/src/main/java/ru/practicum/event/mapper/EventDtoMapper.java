package ru.practicum.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EventDtoMapper {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventFullDto mapToFullDto(
            Event event,
            CategoryDto categoryDto,
            LocationDto locationDto,
            UserShortDto initiatorDto,
            Long views
    ) {
        final EventFullDto eventFullDto = new EventFullDto(
                event.getAnnotation(),
                categoryDto,
                event.getConfirmedRequests(),
                event.getCreatedOn().format(formatter),
                event.getPublishedOn() == null ? null : event.getPublishedOn().format(formatter),
                event.getDescription(),
                event.getEventDate().format(formatter),
                event.getId(),
                initiatorDto,
                locationDto,
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                views
        );
        return eventFullDto;
    }

    public EventShortDto mapToShortDto(
            Event event,
            CategoryDto categoryDto,
            UserShortDto initiatorDto,
            Long views
    ) {
        final EventShortDto eventShortDto = new EventShortDto(
                event.getAnnotation(),
                categoryDto,
                event.getConfirmedRequests(),
                event.getEventDate().format(formatter),
                event.getId(),
                initiatorDto,
                event.getPaid(),
                event.getTitle(),
                views
        );
        return eventShortDto;
    }

    public Event mapFromDto(
            NewEventDto newEventDto,
            Category category,
            Location location,
            User initiator
    ) {
        final Event event = new Event(
                null,
                initiator,
                newEventDto.getTitle(),
                newEventDto.getAnnotation(),
                newEventDto.getDescription(),
                category,
                location,
                LocalDateTime.parse(newEventDto.getEventDate(), formatter),
                newEventDto.getPaid(),
                newEventDto.getRequestModeration(),
                newEventDto.getParticipantLimit(),
                0L,
                State.PENDING,
                LocalDateTime.now(),
                null
        );
        return event;
    }

    public void updateFromDto(
            Event event,
            UpdateEventUserRequest eventDto,
            Category category,
            Location location
    ) {
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventDto.getEventDate(), formatter));
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getStateAction() != null && eventDto.getStateAction() == StateAction.SEND_TO_REVIEW) {
            event.setState(State.PENDING);
        }
        if (eventDto.getStateAction() != null && eventDto.getStateAction() == StateAction.CANCEL_REVIEW) {
            event.setState(State.CANCELED);
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (category != null) {
            event.setCategory(category);
        }
        if (location != null) {
            event.setLocation(location);
        }
    }

    public void updateFromDto(
            Event event,
            UpdateEventAdminRequest eventDto,
            Category category,
            Location location
    ) {
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventDto.getEventDate(), formatter));
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getStateAction() != null && eventDto.getStateAction() == StateAction.PUBLISH_EVENT) {
            event.setState(State.PUBLISHED);
        }
        if (eventDto.getStateAction() != null && eventDto.getStateAction() == StateAction.REJECT_EVENT) {
            event.setState(State.CANCELED);
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (category != null) {
            event.setCategory(category);
        }
        if (location != null) {
            event.setLocation(location);
        }
    }
}
