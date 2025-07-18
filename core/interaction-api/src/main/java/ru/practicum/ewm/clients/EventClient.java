package ru.practicum.ewm.clients;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.ewm.dto.EventFullDto;


@FeignClient(name = "event-service")
public interface EventClient {

    @PutMapping("/admin/events/request/{eventId}")
    EventFullDto setConfirmedRequests(@PathVariable("eventId") Long eventId, @RequestBody Long count) throws FeignException;

    @GetMapping("/admin/events/{eventId}")
    EventFullDto findById(@PathVariable Long eventId) throws FeignException;
}
