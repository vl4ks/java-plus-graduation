package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ResponseHitDto;
import ru.practicum.ResponseStatsDto;
import ru.practicum.StatClientService;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryDtoMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventDtoMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.*;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.mapper.LocationDtoMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.service.LocationService;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserDtoMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

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

    private final StatClientService statClientService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final UserDtoMapper userDtoMapper;
    private final CategoryDtoMapper categoryDtoMapper;
    private final LocationDtoMapper locationDtoMapper;
    private final CategoryRepository categoryRepository;

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        final User user = findUserById(userId);
        final Category category = findCategoryById(eventDto.getCategory());
        final Location location = saveLocation(eventDto.getLocation());

        validateEventDate(eventDto.getEventDate());
        final Event event = eventDtoMapper.mapFromDto(eventDto, category, location, user);
        final Event createdEvent = eventRepository.save(event);

        return eventDtoMapper.mapToFullDto(
                createdEvent,
                categoryDtoMapper.mapToDto(event.getCategory()),
                locationDtoMapper.mapToDto(event.getLocation()),
                userDtoMapper.mapToShortDto(event.getInitiator()),
                0L
        );
    }

    @Override
    public Collection<EventShortDto> findAllByPublic(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && LocalDateTime.parse(rangeStart, formatter).isAfter(LocalDateTime.parse(rangeEnd, formatter))) {
            throw new IncorrectRequestException("RangeStart is after Range End");
        }
        if (sort != null && !sort.equals("EVENT_DATE") && !sort.equals("VIEWS")) {
            throw new IncorrectRequestException("Unknown sort type");
        }
        saveView(request);
        final Collection<Event> events = eventRepository.findAllByPublic(text, categories, paid, rangeStart == null ? null : LocalDateTime.parse(rangeStart, formatter), rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, formatter), onlyAvailable, (Pageable) PageRequest.of(from, size));
        return events.stream()
                .map(event -> eventDtoMapper.mapToShortDto(
                        event,
                        categoryDtoMapper.mapToDto(event.getCategory()),
                        userDtoMapper.mapToShortDto(event.getInitiator()),
                        countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now())

                ))
                .sorted((e1, e2) -> sort == null || sort.equals("EVENT_DATE") ? e1.getEventDate().compareTo(e2.getEventDate()) : e1.getViews().compareTo(e2.getViews()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<EventShortDto> findAllByPrivate(Long userId, Integer from, Integer size) {
        final User user = findUserById(userId);
        final Collection<Event> events = eventRepository.findAllByInitiatorId(user.getId(), PageRequest.of(from, size));
        return events.stream()
                .map(event -> eventDtoMapper.mapToShortDto(
                        event,
                        categoryDtoMapper.mapToDto(event.getCategory()),
                        userDtoMapper.mapToShortDto(event.getInitiator()),
                        countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now())

                ))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<EventFullDto> findAllByAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        final Collection<Event> events = eventRepository.findAllByAdmin(users, states, categories, rangeStart == null ? null : LocalDateTime.parse(rangeStart, formatter), rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, formatter), (Pageable) PageRequest.of(from, size));
        return events.stream()
                .map(event -> eventDtoMapper.mapToFullDto(
                        event,
                        categoryDtoMapper.mapToDto(event.getCategory()),
                        locationDtoMapper.mapToDto(event.getLocation()),
                        userDtoMapper.mapToShortDto(event.getInitiator()),
                        countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now())

                ))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public EventFullDto findById(Long userId, Long eventId, Boolean isPublic, HttpServletRequest request) {
        final Event event = findEventById(eventId);

        if (isPublic && !event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        } else if (isPublic) {
            saveView(request);
        } else {
            findUserById(userId);
        }

        return eventDtoMapper.mapToFullDto(
                event,
                categoryDtoMapper.mapToDto(event.getCategory()),
                locationDtoMapper.mapToDto(event.getLocation()),
                userDtoMapper.mapToShortDto(event.getInitiator()),
                countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now())
        );
    }

    @Override
    public EventFullDto updateByPrivate(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        final User user = findUserById(userId);
        final Event event = findEventById(eventId);

        validateUser(event.getInitiator(), user);
        validateEventDate(eventDto.getEventDate());
        validateStatusForPrivate(event.getState(), eventDto.getStateAction());

        final Category category = findCategoryById(eventDto.getCategory());
        final Location location = saveLocation(eventDto.getLocation());
        eventDtoMapper.updateFromDto(event, eventDto, category, location);

        final Event updatedEvent = eventRepository.save(event);

        return eventDtoMapper.mapToFullDto(
                updatedEvent,
                categoryDtoMapper.mapToDto(updatedEvent.getCategory()),
                locationDtoMapper.mapToDto(updatedEvent.getLocation()),
                userDtoMapper.mapToShortDto(updatedEvent.getInitiator()),
                countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now())
        );
    }

    @Override
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        validateEventDateForAdmin(
                eventDto.getEventDate() == null ? event.getEventDate() : LocalDateTime.parse(eventDto.getEventDate(), formatter),
                eventDto.getStateAction()
        );
        validateStatusForAdmin(event.getState(), eventDto.getStateAction());

        Category category = null;
        if (eventDto.getCategory() != null) {
            category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id=" + eventDto.getCategory() + " не найдена"));
        }

        Location location = null;
        if (eventDto.getLocation() != null) {
            location = saveLocation(eventDto.getLocation());
        }

        eventDtoMapper.updateFromDto(event, eventDto, category, location);

        if (eventDto.getStateAction() == StateAction.PUBLISH_EVENT) {
            event.setPublishedOn(LocalDateTime.now());
        }

        Event updatedEvent = eventRepository.save(event);

        return eventDtoMapper.mapToFullDto(
                updatedEvent,
                categoryDtoMapper.mapToDto(updatedEvent.getCategory()),
                locationDtoMapper.mapToDto(updatedEvent.getLocation()),
                userDtoMapper.mapToShortDto(updatedEvent.getInitiator()),
                countViews(event.getId(), event.getCreatedOn(), LocalDateTime.now())
        );
    }

    @Override
    public void updateEventConfirmedRequests(Long eventId, Long confirmedRequests) {
        final Event event = findEventById(eventId);
        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);
    }

    private void validateUser(User user, User initiator) {
        if (!initiator.getId().equals(user.getId())) {
            throw new NotFoundException("Trying to change information not from initiator of event");
        }
    }

    private void validateEventDate(String eventDate) {
        if (eventDate != null && LocalDateTime.parse(eventDate, formatter).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectRequestException("Event date should be early than 2 hours than current moment " + eventDate + " " + LocalDateTime.parse(eventDate, formatter));
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

    private User findUserById(Long userId) {
        final UserDto userDto = userService.findById(userId);
        final User user = userDtoMapper.mapFromDto(userDto);
        return user;
    }

    private Category findCategoryById(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        final CategoryDto categoryDto = categoryService.findById(categoryId);
        final Category category = categoryDtoMapper.mapFromDto(categoryDto);
        return category;
    }

    private Location saveLocation(LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }
        final LocationDto createdLocationDto = locationService.create(locationDto);
        final Location location = locationDtoMapper.mapFromDto(createdLocationDto);
        return location;
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
                .timestamp(LocalDateTime.now().format(formatter))
                .build();

        ResponseHitDto hitDto = ResponseHitDto.builder()
                .uri(viewDto.getUri())
                .ip(viewDto.getIp())
                .timestamp(viewDto.getTimestamp())
                .build();

        statClientService.saveHit(hitDto);
        log.info("Просмотр успешно записан.");
    }

    private Long countViews(Long eventId, LocalDateTime start, LocalDateTime end) {
        final List<String> uris = List.of(
                "/events/" + eventId
        );
        List<ResponseStatsDto> stats = statClientService.getStats(start, end, uris, true);
        return stats.stream()
                .mapToLong(ResponseStatsDto::getHits)
                .sum();
    }
}
