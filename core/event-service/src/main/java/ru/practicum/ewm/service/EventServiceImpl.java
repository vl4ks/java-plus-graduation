package ru.practicum.ewm.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.clients.RequestClient;
import ru.practicum.ewm.clients.StatClient;
import ru.practicum.ewm.clients.UserClient;
import ru.practicum.ewm.RecommendationsClient;
import ru.practicum.ewm.UserActionClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.grpc.stats.event.ActionTypeProto;
import ru.practicum.ewm.grpc.stats.event.RecommendedEventProto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.mapper.EventDtoMapper;
import ru.practicum.ewm.mapper.LocationDtoMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.storage.CategoryRepository;
import ru.practicum.ewm.storage.EventRepository;
import ru.practicum.ewm.storage.LocationRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service("eventServiceImpl")
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    @Value("${spring.application.name}")
    private String appName;

    private final StatClient statClient;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventDtoMapper eventMapper;
    private final LocationDtoMapper locationDtoMapper;
    private final RequestClient requestClient;
    private final UserClient userClient;
    private final UserActionClient userActionClient;
    private final RecommendationsClient recommendationsClient;

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        UserDto userDto = userClient.findById(userId);

        validateEventDate(eventDto.getEventDate());

        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + eventDto.getCategory()));

        Location location = locationRepository.save(locationDtoMapper.mapFromDto(eventDto.getLocation()));

        if (eventDto.getCommenting() == null) {
            eventDto.setCommenting(true);
        }
        Event event = eventMapper.toEvent(eventDto, category);

        event.setInitiatorId(userId);
        event.setState(State.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setLocation(location);
        if (event.getConfirmedRequests() == null) {
            event.setConfirmedRequests(0L);
        }
        event.setCommenting(eventDto.getCommenting() != null ? eventDto.getCommenting() : true);

        eventRepository.save(event);
        return eventMapper.mapToFullDto(event, 0d, userDto);
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

        final Collection<Event> events = eventRepository.findAllByPublic(
                text, categories, paid,
                rangeStart == null ? null : rangeStart,
                rangeEnd == null ? null : rangeEnd,
                onlyAvailable, (Pageable) PageRequest.of(from, size));

        return events.stream()
                .map(event -> {
                    UserDto userDto = userClient.findById(event.getInitiatorId());
                    EventShortDto dto = eventMapper.mapToShortDto(event, 0d, userDto);
                    dto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
//                    dto.setInitiator(event.getInitiatorId());
                    return dto;
                })
                .sorted((e1, e2) -> {
                    if (sort == null || sort.equals("EVENT_DATE")) {
                        return e1.getEventDate().compareTo(e2.getEventDate());
                    } else {
                        Double rating1 = e1.getRating() != null ? e1.getRating() : 0.0;
                        Double rating2 = e2.getRating() != null ? e2.getRating() : 0.0;
                        return rating2.compareTo(rating1);
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<EventShortDto> findAllByPrivate(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(event -> {
                    UserDto userDto = userClient.findById(userId);
                    return eventMapper.mapToShortDto(
                            event,
                            0d,
                            userDto
                    );
                })
                .toList();
    }

    @Override
    public Collection<EventFullDto> findAllByAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        final Collection<Event> events = eventRepository.findAllByAdmin(users, states, categories,
                rangeStart,
                rangeEnd,
                (Pageable) PageRequest.of(from, size));
        List<Long> initiatorIds = events.stream()
                .map(Event::getInitiatorId)
                .toList();

        Map<Long, UserDto> usersMap = userClient.getAllUsers(initiatorIds, 0, initiatorIds.size())
                .stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));

        return events.stream()
                .map(event -> {
                    UserDto userDto = usersMap.get(event.getInitiatorId());
                    return eventMapper.mapToFullDto(
                            event,
                            0d,
                            userDto);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }


    @Override
    @Transactional
    public EventFullDto findEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
        if (!Objects.equals(event.getInitiatorId(), userId)) {
            throw new ValidationException("Можно просмотреть только своё событие");
        }
        UserDto userDto = userClient.findById(userId);
        return eventMapper.mapToFullDto(event, 0d, userDto);
    }

    @Override
    public EventFullDto findById(Long userId, Long eventId) {
        final Event event = findEventById(eventId);


        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Посмотреть можно только опубликованное событие.");
        }
        UserDto userDto = userClient.findById(event.getInitiatorId());
        EventFullDto result = eventMapper.mapToFullDto(event, 0d, userDto);

        List<ParticipationRequestDto> confirmedRequests = requestClient
                .findAllConfirmedByEventId(List.of(event.getId())).get(event.getId());
        result.setConfirmedRequests(confirmedRequests != null ? (long) confirmedRequests.size() : 0L);

        userActionClient.collectUserAction(eventId, userId, ActionTypeProto.ACTION_VIEW, Instant.now());
        return result;
    }

    @Override
    public List<EventShortDto> getEventsRecommendations(Long userId, int maxResults) {
        Map<Long, Double> recommendations = recommendationsClient
                .getRecommendationsForUser(userId, maxResults)
                .collect(Collectors.toMap(RecommendedEventProto::getEventId, RecommendedEventProto::getScore));

        List<Event> events = eventRepository.findAllById(recommendations.keySet());
        List<Long> initiatorIds = events.stream()
                .map(Event::getInitiatorId)
                .toList();

        Map<Long, UserDto> initiators = userClient.getAllUsers(initiatorIds, 0, initiatorIds.size()).stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));
        return events.stream()
                .map(event -> eventMapper.mapToShortDto(event, recommendations.get(event.getId()),
                        initiators.get(event.getInitiatorId())))
                .toList();
    }

    @Override
    public void addLikeToEvent(Long eventId, Long userId) {
        if (!requestClient.checkExistStatusRequest(eventId, userId, EventRequestStatus.CONFIRMED)) {
            throw new ValidationException("Пользователь не участвует в этом событии.");
        }
        userActionClient.collectUserAction(eventId, userId, ActionTypeProto.ACTION_LIKE, Instant.now());
    }



    @Override
    @Transactional
    public EventFullDto updateByPrivate(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        final Event event = findEventById(eventId);

        validateEventDate(eventDto.getEventDate());
        validateStatusForPrivate(event.getState(), eventDto.getStateAction());

        Category category = null;
        if (eventDto.getCategory() != null) {
            category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + eventDto.getCategory()));
        }

        eventMapper.updateEventFromUserRequest(event, eventDto, category);

        UserDto userDto = userClient.findById(userId);

        return eventMapper.mapToFullDto(event, 0d, userDto);
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

        Category category = null;
        if (eventDto.getCategory() != null) {
            category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + eventDto.getCategory()));
        }

        eventMapper.updateEventFromAdminRequest(event, eventDto, category);

        if (event.getState() == State.PUBLISHED) {
            event.setPublishedOn(LocalDateTime.now());
            event.setConfirmedRequests(0L);
        }

        UserDto userDto = userClient.findById(event.getInitiatorId());

        return eventMapper.mapToFullDto(event, 0d, userDto);
    }

    @Transactional
    @Override
    public void setConfirmedRequests(Long eventId, Long count) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
        event.setConfirmedRequests(count);
        eventRepository.save(event);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfUserEvent(Long userId, Long eventId) {
        if (userClient.findById(userId) == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено."));

        if (!Objects.equals(event.getInitiatorId(), userId)) {
            log.error("userId не соответствует id инициатору события");
            throw new ValidationException("Событие должно быть создано текущим пользователем");
        }
        return requestClient.findAllByEventId(eventId);
    }

    @Override
    public EventFullDto getAdminEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        UserDto userDto = userClient.findById(event.getInitiatorId());

        return eventMapper.mapToFullDto(event, 0d, userDto);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatus(EventRequestStatusUpdateRequest updateRequest,
                                                               Long userId, Long eventId) {
        if (userClient.findById(userId) == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено."));
        if (!Objects.equals(event.getInitiatorId(), userId)) {
            throw new ValidationException("Событие должно быть создано текущим пользователем");
        }
        if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
            throw new ConflictException("Лимит участников уже исчерпан");
        }
        Collection<Long> requestIds = updateRequest.getRequestIds();
        log.info("Получили список id запросов на участие: {}", requestIds);
        Collection<ParticipationRequestDto> requestList = requestClient.findAllByIds(requestIds);
        if (requestList.stream().anyMatch(request -> !Objects.equals(request.getEvent(), eventId))) {
            throw new ValidationException("Все запросы должны принадлежать одному событию");
        }
        List<ParticipationRequestDto> confirmedRequestsList = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            switch (updateRequest.getStatus()) {
                case CONFIRMED -> requestList.forEach(request -> {
                    if (request.getStatus() != EventRequestStatus.PENDING) {
                        throw new ConflictException("Можно изменить только статус PENDING");
                    }
                    if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
                        request = requestClient.setStatusRequest(request.getId(), EventRequestStatus.REJECTED);
                        rejectedRequests.add(request);
                    } else {
                        request = requestClient.setStatusRequest(request.getId(), EventRequestStatus.CONFIRMED);
                        Long confirmedRequests = event.getConfirmedRequests();
                        event.setConfirmedRequests(++confirmedRequests);
                        confirmedRequestsList.add(request);
                    }
                });
                case REJECTED -> requestList.forEach(request -> {
                    if (request.getStatus() != EventRequestStatus.PENDING) {
                        throw new ConflictException("Можно изменить только статус PENDING");
                    }
                    request = requestClient.setStatusRequest(request.getId(), EventRequestStatus.REJECTED);
                    rejectedRequests.add(request);
                });
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequestsList, rejectedRequests);
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
}


