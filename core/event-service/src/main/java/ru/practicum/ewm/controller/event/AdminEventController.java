package ru.practicum.ewm.controller.event;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.ewm.DateTimeFormat.DATE_PATTERN;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventFullDto> get(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false)  @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false)  @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size
        ) {
        log.info("Пришел GET запрос /admin/events с параметрами: users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        final Collection<EventFullDto> events = eventService.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Отправлен ответ GET /admin/events с телом: {}", events);
        return events;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@Positive @PathVariable Long eventId, @RequestBody @Validated UpdateEventAdminRequest eventDto) {
        log.info("Пришел PATCH запрос /admin/events/{} с телом {}", eventId, eventDto);
        final EventFullDto event = eventService.updateByAdmin(eventId, eventDto);
        log.info("Отправлен ответ PATCH /admin/events/{} с телом: {}", eventId, event);
        return event;
    }

    @GetMapping("/{eventId}")
    public EventFullDto findById(@PathVariable("eventId") @Positive Long eventId) {
        log.info("Получение подробной информации об опубликованном событии по его идентификатору.");
            return eventService.getAdminEventById(eventId);
    }

    @PutMapping("/request/{eventId}")
    public void setConfirmedRequests(@PathVariable("eventId") Long eventId, @RequestBody Long count) {
        eventService.setConfirmedRequests(eventId, count);
    }
}
