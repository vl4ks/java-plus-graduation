package ru.practicum.service;

import ru.practicum.dto.EventRequestStatus;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

public interface EventRequestService {

    ParticipationRequestDto create(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest requestsToUpdate);

    ParticipationRequestDto cancel(Long userId, Long requestId);

    Collection<ParticipationRequestDto> getByRequesterId(Long requesterId);

    Collection<ParticipationRequestDto> getByEventId(Long eventInitiatorId, Long eventId);

    List<ParticipationRequestDto> findAllByIds(List<Long> ids);

    ParticipationRequestDto setStatusRequest(Long id, EventRequestStatus status);
}

