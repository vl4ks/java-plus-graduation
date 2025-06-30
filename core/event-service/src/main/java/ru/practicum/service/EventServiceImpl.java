package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.clients.StatClient;
import ru.practicum.dto.*;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.EventUpdater;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.storage.EventRepository;
import ru.practicum.storage.LocationRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.IncorrectRequestException;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("eventServiceImpl")
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final StatClient statClient;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;
    private final LocationRepository locationRepository;

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        validateEventDate(eventDto.getEventDate());

        Event event = EventMapper.INSTANCE.getEvent(eventDto);

        Location location = locationRepository.save(event.getLocation());

        event.setInitiatorId(userId);
        event.setState(State.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setLocation(location);

        return EventMapper.INSTANCE.getEventDto(eventRepository.save(event));
    }

    @Override
    public Collection<EventShortDto> findAllByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                     LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                                     Integer size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IncorrectRequestException("RangeStart is after Range End");
        }
        if (sort != null && !sort.equals("EVENT_DATE") && !sort.equals("VIEWS")) {
            throw new IncorrectRequestException("Unknown sort type");
        }
        saveView(request);

        final Collection<Event> events = eventRepository.findAllByPublic(
                text, categories, paid,
                rangeStart == null ? null : rangeStart,
                rangeEnd == null ? null : rangeEnd,
                onlyAvailable, (Pageable) PageRequest.of(from, size));

        return events.stream()
                .map(event -> {
                    EventShortDto dto = EventMapper.INSTANCE.getEventShortDto(event);
                    dto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
                    dto.setInitiator(event.getInitiatorId());
                    dto.setViews(countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now()));
                    return dto;
                })
                .sorted((e1, e2) -> sort == null || sort.equals("EVENT_DATE") ? e1.getEventDate().compareTo(e2.getEventDate()) : e1.getViews().compareTo(e2.getViews()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<EventShortDto> findAllByPrivate(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(EventMapper.INSTANCE::getEventShortDto)
                .toList();
    }

    @Override
    public Collection<EventFullDto> findAllByAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        final Collection<Event> events = eventRepository.findAllByAdmin(users, states, categories,
                rangeStart == null ? null : LocalDateTime.parse(rangeStart, formatter),
                rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, formatter),
                (Pageable) PageRequest.of(from, size));
        return events.stream()
                .map(EventMapper.INSTANCE::getEventDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public EventFullDto findEvent(Long eventId, Long userId) {
        return EventMapper.INSTANCE.getEventDto(
                eventRepository.findByIdAndUserId(eventId, userId)
                        .orElseThrow(() -> new NotFoundException("event is not found with id = " + eventId))
        );
    }

    @Override
    public EventFullDto findById(Long eventId, HttpServletRequest request) {
        eventRepository.findAll().forEach(e -> log.info("EVENT: id={}, state={}", e.getId(), e.getState()));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event is not found with id = " + eventId));

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("event is not published with id = " + eventId);
        }

        saveView(request);

        event.setViews(countViews(eventId,event.getCreatedOn(), LocalDateTime.now()));
        eventRepository.save(event);
        return EventMapper.INSTANCE.getEventDto(event);
    }

    @Override
    public EventFullDto updateByPrivate(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        final Event event = findEventById(eventId);

        validateEventDate(eventDto.getEventDate());
        validateStatusForPrivate(event.getState(), eventDto.getStateAction());

        EventUpdater.INSTANCE.update(event, eventDto);

        return EventMapper.INSTANCE.getEventDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        validateEventDateForAdmin(
                eventDto.getEventDate() == null ? event.getEventDate() : eventDto.getEventDate(),
                eventDto.getStateAction()
        );

        validateStatusForAdmin(event.getState(), eventDto.getStateAction());


        if (eventDto.getStateAction() == StateAction.PUBLISH_EVENT) {
            event.setPublishedOn(LocalDateTime.now());
        }

        EventUpdater.INSTANCE.update(event, eventDto);

        if (event.getState() == State.PUBLISHED) {
            event.setPublishedOn(LocalDateTime.now());
            event.setConfirmedRequests(0L);
            event.setViews(0L);
        }
        return EventMapper.INSTANCE.getEventDto(event);
    }

    @Override
    public void updateEventConfirmedRequests(Long eventId, Long confirmedRequests) {
        final Event event = findEventById(eventId);
        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);
    }


    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectRequestException("Event date should be early than 2 hours than current moment " + eventDate + " " + eventDate);
        }
    }

    private void validateEventDateForAdmin(LocalDateTime eventDate, StateAction stateAction) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectRequestException("Event date should be early than 2 hours than current moment");
        }
        if (stateAction != null && stateAction.equals(StateAction.PUBLISH_EVENT) && eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ForbiddenException("Event date should be early than 1 hours than publish moment");
        }
    }

    private void validateStatusForPrivate(State state, StateAction stateAction) {
        if (state.equals(State.PUBLISHED)) {
            throw new ConflictException("Can't change event not cancelled or in moderation");
        }
        switch (stateAction) {
            case null:
            case StateAction.CANCEL_REVIEW:
            case StateAction.SEND_TO_REVIEW:
                return;
            default:
                throw new ForbiddenException("Unknown state action");
        }
    }

    private void validateStatusForAdmin(State state, StateAction stateAction) {
        if (!state.equals(State.PENDING) && stateAction.equals(StateAction.PUBLISH_EVENT)) {
            throw new ConflictException("Can't publish not pending event");
        }
        if (state.equals(State.PUBLISHED) && stateAction.equals(StateAction.REJECT_EVENT)) {
            throw new ConflictException("Can't reject already published event");
        }
        if (stateAction != null && !stateAction.equals(StateAction.REJECT_EVENT) && !stateAction.equals(StateAction.PUBLISH_EVENT)) {
            throw new ForbiddenException("Unknown state action");
        }
    }


    private Event findEventById(Long eventId) {
        final Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found")
        );

        return event;
    }

    private void saveView(HttpServletRequest request) {
        final NewEventViewDto viewDto = NewEventViewDto.builder()
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        ResponseHitDto hitDto = ResponseHitDto.builder()
                .app("event-service")
                .uri(viewDto.getUri())
                .ip(viewDto.getIp())
                .timestamp(viewDto.getTimestamp())
                .build();

        statClient.saveHit(hitDto);
        log.info("Просмотр успешно записан.");
    }


    private Long countViews(Long eventId, LocalDateTime start, LocalDateTime end) {
        final List<String> uris = List.of(
                "/events/" + eventId
        );
        List<ResponseStatsDto> stats = statClient.getStats(start, end, uris, true);
        return stats.stream()
                .mapToLong(ResponseStatsDto::getHits)
                .sum();
    }
}


