package ru.practicum.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.EventFullDto;

import java.util.Optional;

@FeignClient(name = "event-service")
public interface EventClient {
    @GetMapping("/events/{eventId}")
    Optional<EventFullDto> findEvent(@PathVariable Long eventId);

    @PutMapping("/events/{eventId}/confirmed")
    void setConfirmed(@PathVariable Long eventId,
                      @RequestBody Long requests);

    @GetMapping("/events/{eventId}")
    Optional<EventFullDto> findById(@PathVariable Long eventId);
}
