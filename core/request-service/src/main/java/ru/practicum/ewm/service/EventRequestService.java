package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventRequestStatus;
import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EventRequestService {

    ParticipationRequestDto create(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest requestsToUpdate);

    ParticipationRequestDto cancel(Long userId, Long requestId);

    Collection<ParticipationRequestDto> getByRequesterId(Long requesterId);

    Collection<ParticipationRequestDto> getByEventId(Long eventInitiatorId, Long eventId);

    List<ParticipationRequestDto> findAllByIds(List<Long> ids);

    List<ParticipationRequestDto> findAllByEventId(Long eventId);

    ParticipationRequestDto setStatusRequest(Long id, EventRequestStatus status);

    Map<Long, List<ParticipationRequestDto>> findAllConfirmedByEventId(List<Long> eventIds);

    boolean checkExistsByEventIdAndRequesterIdAndStatus(Long eventId, Long userId, EventRequestStatus status);
}

