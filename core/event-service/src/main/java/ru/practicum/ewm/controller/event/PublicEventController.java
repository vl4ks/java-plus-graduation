package ru.practicum.ewm.controller.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.ewm.DateTimeFormat.DATE_PATTERN;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;
    private final String AuthHeaderKey = "X-EWM-USER-ID";

    @GetMapping
    public Collection<EventShortDto> get(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<@Positive Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request
    ) {
        log.info("Пришел GET запрос /events с параметрами: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        final Collection<EventShortDto> events = eventService.findAllByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
        log.info("Отправлен ответ GET /events с телом: {}", events);
        return events;
    }

    @GetMapping("/{eventId}")
    public EventFullDto findById(@RequestHeader(AuthHeaderKey) Long userId, @PathVariable Long eventId) {
        log.info("Пришел GET запрос /events/{}", eventId);
        final EventFullDto event = eventService.findById(userId, eventId);
        log.info("Отправлен ответ GET /events/{} с телом: {}", userId, eventId);
        return event;
    }

    @GetMapping("/recommendations")
    public List<EventShortDto> getEventsRecommendations(@RequestHeader(AuthHeaderKey) Long userId,
                                                        @RequestParam(defaultValue = "10") int maxResults) {
        log.info("Пришел GET запрос /events/recommendations от пользователя {} с параметром maxResults={}",
                userId, maxResults);
        List<EventShortDto> recommendations = eventService.getEventsRecommendations(userId, maxResults);
        log.info("Отправлен ответ GET /events/recommendations пользователю {} с телом: {}",
                userId, recommendations);
        return recommendations;
    }

    @PutMapping("/{eventId}/like")
    public void addLikeToEvent(@PathVariable Long eventId, @RequestHeader(AuthHeaderKey) Long userId) {
        log.info("Пришел PUT запрос /events/{}/like от пользователя {}", eventId, userId);
        eventService.addLikeToEvent(eventId, userId);
        log.info("Обработан PUT запрос /events/{}/like от пользователя {}", eventId, userId);
    }
}
