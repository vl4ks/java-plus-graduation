package ru.practicum.clients;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.EventFullDto;


@FeignClient(name = "event-service", path = "/events")
public interface EventClient {

    @PutMapping("/{eventId}/confirmed")
    void setConfirmed(@PathVariable Long eventId,
                      @RequestBody Long requests);

    @GetMapping("/{eventId}")
    EventFullDto findById(@PathVariable Long eventId) throws FeignException;
}
