package ru.practicum.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

@FeignClient(name = "request-service")
public interface RequestClient {

    @PostMapping("/users/{userId}/requests")
    ParticipationRequestDto create(@PathVariable Long userId,
                                   @RequestParam Long eventId);

    @GetMapping("/users/{userId}/requests")
    List<ParticipationRequestDto> getAll(@PathVariable Long userId);

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto cancel(@PathVariable Long userId,
                                   @PathVariable Long requestId);
}
