package ru.practicum.ewm.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.dto.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {

    EventFullDto create(Long userId, NewEventDto eventDto);

    Collection<EventShortDto> findAllByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request);

    Collection<EventShortDto> findAllByPrivate(Long userId, Integer from, Integer size);

    Collection<EventFullDto> findAllByAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto findById(Long userId, Long eventId);

    List<EventShortDto> getEventsRecommendations(Long userId, int maxResults);

    void addLikeToEvent(Long eventId, Long userId);

    EventFullDto findEvent(Long eventId, Long userId);

    EventFullDto updateByPrivate(Long userId, Long eventId, UpdateEventUserRequest eventDto);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest eventDto);

    void setConfirmedRequests(Long eventId, Long count);

    List<ParticipationRequestDto> getRequestsOfUserEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(EventRequestStatusUpdateRequest updateRequest,
                                                        Long userId, Long eventId);

    EventFullDto getAdminEventById(Long eventId);
}
