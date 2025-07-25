package ru.practicum.ewm.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.EventService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventShortDto> get(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size
    ) {
        log.info("Пришел GET запрос /users/{}/events?from={}&size={}", userId, from, size);
        final Collection<EventShortDto> events = eventService.findAllByPrivate(userId, from, size);
        log.info("Отправлен ответ GET /users/{}/events?from={}&size={} с телом: {}", userId, from, size, events);
        return events;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId, @RequestBody @Valid NewEventDto eventDto) {
        log.info("Пришел POST запрос /users/{}/events с телом {}", userId, eventDto);
        final EventFullDto event = eventService.create(userId, eventDto);
        log.info("Отправлен ответ POST /users/{}/events с телом: {}", userId, event);
        return event;
    }

    @GetMapping("/{eventId}")
    public EventFullDto findById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Пришел GET запрос /users/{}/events/{}", userId, eventId);
        final EventFullDto event = eventService.findEvent(userId, eventId);
        log.info("Отправлен ответ GET /users/{}/events/{}: ", userId, eventId);
        return event;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid UpdateEventUserRequest eventDto) {
        log.info("Пришел PATCH запрос /users/{}/events/{} с телом {}", userId, eventId, eventDto);
        final EventFullDto event = eventService.updateByPrivate(userId, eventId, eventDto);
        log.info("Отправлен ответ PATCH /users/{}/events/{} с телом: {}", userId, eventId, event);
        return event;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOfUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Пришел GET запрос /users/{}/events/{}/requests", userId, eventId);
        List<ParticipationRequestDto> requests = eventService.getRequestsOfUserEvent(userId, eventId);
        log.info("Отправлен ответ GET /users/{}/events/{}/requests с телом: {}",
                userId, eventId, requests);
        return requests;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatus(@PathVariable Long userId, @PathVariable Long eventId,
                                                               @Valid @RequestBody
                                                               EventRequestStatusUpdateRequest updateRequest) {
        log.info("Пришел PATCH запрос /users/{}/events/{}/requests с телом {}", userId, eventId, updateRequest);
        EventRequestStatusUpdateResult result = eventService.updateRequestsStatus(updateRequest, userId, eventId);
        log.info("Отправлен ответ PATCH /users/{}/events/{}/requests с телом: {}", userId, eventId, result);
        return result;
    }
}
