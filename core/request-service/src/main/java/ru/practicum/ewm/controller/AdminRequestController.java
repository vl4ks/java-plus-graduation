package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventRequestStatus;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.EventRequestService;

import java.util.List;
import java.util.Map;

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


    @GetMapping("/event/confirmed/{eventId}")
    public Map<Long, List<ParticipationRequestDto>> findAllConfirmedByEventId(@PathVariable List<Long> eventId) {
        var result = requestService.findAllConfirmedByEventId(eventId);
        return result;
    }

    @GetMapping("/{eventId}/check-user-confirmed/{userId}")
    public boolean checkExistsByEventIdAndRequesterIdAndStatus(@PathVariable Long eventId,@PathVariable Long userId,
                                                               @RequestParam EventRequestStatus status) {
        return requestService.checkExistsByEventIdAndRequesterIdAndStatus(eventId, userId, status);
    }
}
