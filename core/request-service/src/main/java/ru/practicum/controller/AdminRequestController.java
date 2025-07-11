package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventRequestStatus;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.EventRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@Slf4j
@RequiredArgsConstructor
public class AdminRequestController {
    @GetMapping("/event/{eventId}")
    public List<ParticipationRequestDto> findAllByEventId(@PathVariable Long eventId) {
        var result = requestService.findAllByEventId(eventId);
        return result;
    }

    private final EventRequestService requestService;
    @GetMapping("/{ids}")
    public List<ParticipationRequestDto> findAllByIds(@PathVariable List<Long> ids) {
        var result = requestService.findAllByIds(ids);
        return result;
    }

    @PutMapping("/status/{id}/{status}")
    public ParticipationRequestDto setStatusRequest(@PathVariable Long id, @PathVariable EventRequestStatus status) {
        return requestService.setStatusRequest(id, status);
    }
}
