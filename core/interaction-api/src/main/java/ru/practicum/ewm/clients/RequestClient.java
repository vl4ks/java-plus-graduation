package ru.practicum.ewm.clients;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventRequestStatus;
import ru.practicum.ewm.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(name = "request-service")
public interface RequestClient {

    @GetMapping("/requests/event/{eventId}")
    List<ParticipationRequestDto> findAllByEventId(@PathVariable Long eventId) throws FeignException;

    @GetMapping("/requests/{ids}")
    Collection<ParticipationRequestDto> findAllByIds(@PathVariable Collection<Long> ids) throws FeignException;

    @PutMapping("/requests/status/{id}/{status}")
    ParticipationRequestDto setStatusRequest(@PathVariable Long id, @PathVariable EventRequestStatus status)  throws FeignException;

    @GetMapping("/requests/event/confirmed/{eventId}")
    Map<Long, List<ParticipationRequestDto>> findAllConfirmedByEventId(@PathVariable List<Long> eventId) throws FeignException;


    @GetMapping("/requests/{eventId}/check-user-confirmed/{userId}")
    boolean checkExistStatusRequest(@PathVariable Long eventId,@PathVariable Long userId,
                                    @RequestParam EventRequestStatus status);

    @PostMapping("/users/{userId}/requests")
    ParticipationRequestDto create(@PathVariable Long userId,
                                   @RequestParam Long eventId);

    @GetMapping("/users/{userId}/requests")
    List<ParticipationRequestDto> getAll(@PathVariable Long userId);

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto cancel(@PathVariable Long userId,
                                   @PathVariable Long requestId);
}
