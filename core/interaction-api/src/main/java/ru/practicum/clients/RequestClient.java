package ru.practicum.clients;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventRequestStatus;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

@FeignClient(name = "request-service")
public interface RequestClient {

    @GetMapping("/requests/event/{eventId}")
    List<ParticipationRequestDto> findAllByEventId(@PathVariable Long eventId) throws FeignException;

    @GetMapping("/requests/{ids}")
    Collection<ParticipationRequestDto> findAllByIds(@PathVariable Collection<Long> ids) throws FeignException;

    @PutMapping("/requests/status/{id}/{status}")
    ParticipationRequestDto setStatusRequest(@PathVariable Long id, @PathVariable EventRequestStatus status)  throws FeignException;

    @PostMapping("/users/{userId}/requests")
    ParticipationRequestDto create(@PathVariable Long userId,
                                   @RequestParam Long eventId);

    @GetMapping("/users/{userId}/requests")
    List<ParticipationRequestDto> getAll(@PathVariable Long userId);

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto cancel(@PathVariable Long userId,
                                   @PathVariable Long requestId);
}
