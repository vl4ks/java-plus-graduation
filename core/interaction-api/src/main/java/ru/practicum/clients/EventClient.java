package ru.practicum.clients;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ru.practicum.dto.EventFullDto;

import java.util.Optional;


@FeignClient(name = "event-service", path = "/events")
public interface EventClient {

    @GetMapping("/{eventId}")
    Optional<EventFullDto> findById(@PathVariable Long eventId) throws FeignException;
}
