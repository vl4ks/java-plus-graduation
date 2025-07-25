package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;


@Component
public class EventDtoMapper {
    public EventDtoMapper(LocationDtoMapper locationDtoMapper) {
        this.locationDtoMapper = locationDtoMapper;
    }

    private final LocationDtoMapper locationDtoMapper;

    public EventFullDto mapToFullDto(Event event, Double rating, UserDto userDto) {
        if (event == null) {
            return null;
        }

        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setInitiator(UserShortDto.builder()
                .name(userDto.getName())
                .id(userDto.getId())
                .build().getId());
        eventFullDto.setLocation(locationDtoMapper.mapToDto(event.getLocation()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setRating(rating);
        eventFullDto.setCommenting(event.getCommenting());

        return eventFullDto;
    }

    public EventShortDto mapToShortDto(Event event, Double rating, UserDto userDto) {
        if (event == null) {
            return null;
        }

        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setInitiator(UserShortDto.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .build().getId());
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setRating(rating);
        eventShortDto.setCommenting(event.getCommenting());

        return eventShortDto;
    }

    public Event toEvent(NewEventDto newEventDto, Category category) {
        if (newEventDto == null) {
            return null;
        }

        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setPaid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false);
        event.setParticipantLimit(newEventDto.getParticipantLimit() != null ? newEventDto.getParticipantLimit() : 0L);
        event.setRequestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true);
        event.setTitle(newEventDto.getTitle());
        event.setConfirmedRequests(0L);
        event.setCommenting(newEventDto.getCommenting());
        return event;
    }


    public void updateEventFromUserRequest(Event event, UpdateEventUserRequest updateRequest, Category category) {
        if (updateRequest == null) {
            return;
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (category != null) {
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(locationDtoMapper.mapFromDto(updateRequest.getLocation()));
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getStateAction() != null) {
            event.setState(toEventState(updateRequest.getStateAction()));
        }
        if (updateRequest.getCommenting() != null) {
            event.setCommenting(updateRequest.getCommenting());
        }

    }

    public void updateEventFromAdminRequest(Event event, UpdateEventAdminRequest updateRequest, Category category) {
        if (updateRequest == null) {
            return;
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (category != null) {
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(locationDtoMapper.mapFromDto(updateRequest.getLocation()));
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getStateAction() != null) {
            event.setState(toEventState(updateRequest.getStateAction()));
        }
        if (updateRequest.getCommenting() != null) {
            event.setCommenting(updateRequest.getCommenting());
        }

    }

    private State toEventState(StateAction stateAction) {
        if (stateAction == null) {
            return null;
        }

        switch (stateAction) {
            case SEND_TO_REVIEW:
                return State.PENDING;
            case CANCEL_REVIEW:
                return State.CANCELED;
            case PUBLISH_EVENT:
                return State.PUBLISHED;
            case REJECT_EVENT:
                return State.CANCELED;
            default:
                throw new IllegalArgumentException("Unknown state action: " + stateAction);
        }
    }
}
