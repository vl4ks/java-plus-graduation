package ru.practicum.event.controller.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@Slf4j
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventShortDto> get(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<@Positive Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
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
    public EventFullDto findById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Пришел GET запрос /events/{}", eventId);
        final EventFullDto event = eventService.findById(null, eventId, true, request);
        log.info("Отправлен ответ GET /events/{} с телом: {}", eventId, event);
        return event;
    }
}
