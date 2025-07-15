package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.clients.EventClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventRequestMapper;
import ru.practicum.ewm.model.EventRequest;
import ru.practicum.ewm.storage.EventRequestRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service("eventRequestServiceImpl")
@RequiredArgsConstructor
public class EventRequestServiceImpl implements EventRequestService {
    private final EventRequestRepository eventRequestRepository;
    private final EventRequestMapper eventRequestMapper;
    private final EventClient eventClient;

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        var event = eventClient.findById(eventId);
        if (eventRequestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Request already exists");
        }

        if (event.getInitiator().equals(userId)) {
            throw new ConflictException("Initiator of event can't be the same with requester");
        }
        validateEventForRequest(event);

        EventRequestStatus status = (event.getParticipantLimit().equals(0L) || !event.getRequestModeration()) ?
                EventRequestStatus.CONFIRMED : EventRequestStatus.PENDING;

        EventRequest request = EventRequest.builder()
                .eventId(eventId)
                .requesterId(userId)
                .status(status)
                .created(LocalDateTime.now())
                .build();

        EventRequest savedRequest = eventRequestRepository.save(request);

        if (status == EventRequestStatus.CONFIRMED) {
            Long newCount = event.getConfirmedRequests() + 1;
            eventClient.setConfirmedRequests(eventId, newCount);
        }

        return eventRequestMapper.toParticipationRequestDto(savedRequest);
    }

    @Override
    public EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest requestsToUpdate) {
        EventFullDto event = eventClient.findById(eventId);


        if (!event.getInitiator().equals(userId)) {
            throw new ConflictException("Not initiator of event can't be change status of requests");
        }
        validateEventForRequest(event);

        Collection<EventRequest> requests = eventRequestRepository.findByIdIn(requestsToUpdate.getRequestIds());
        validateRequests(requests);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        switch (requestsToUpdate.getStatus()) {
            case EventRequestStatus.REJECTED -> rejectRequests(result, requests);
            case EventRequestStatus.CONFIRMED -> {
                confirmRequests(result, event, requests);
            }
            default -> throw new ForbiddenException("Unknown state to update");
        }

        return result;
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        EventRequest request = eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!userId.equals(request.getRequesterId())) {
            throw new NotFoundException("Not owner (userId=" + userId + ") of request trying to cancel it");
        }
        if (request.getStatus() == EventRequestStatus.CONFIRMED) {
            var event = eventClient.findById(request.getEventId());
            Long confirmedRequests = event.getConfirmedRequests();
            eventClient.setConfirmedRequests(request.getEventId(), --confirmedRequests);
        }
        request.setStatus(EventRequestStatus.CANCELED);
        EventRequest updatedRequest = eventRequestRepository.save(request);
        return eventRequestMapper.toParticipationRequestDto(updatedRequest);
    }

    @Override
    public Collection<ParticipationRequestDto> getByRequesterId(Long requesterId) {
        Collection<EventRequest> requests = eventRequestRepository.findByRequesterId(requesterId);
        return requests.stream()
                .map(eventRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<ParticipationRequestDto> getByEventId(Long eventInitiatorId, Long eventId) {
        EventFullDto event = eventClient.findById(eventId);

        if (!event.getInitiator().equals(eventInitiatorId)) {
            throw new ConflictException("User with id=" + eventInitiatorId + " is not the initiator of event with id=" + eventId);
        }

        Collection<EventRequest> requests = eventRequestRepository.findByEventId(eventId);
        return requests.stream()
                .map(eventRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<ParticipationRequestDto> findAllByIds(List<Long> ids) {
        var result =  eventRequestMapper.toParticipationRequestDtoList(eventRequestRepository.findAllById(ids));
        return result;
    }

    @Override
    public List<ParticipationRequestDto> findAllByEventId(Long eventId) {
        var result =  eventRequestMapper.toParticipationRequestDtoList(eventRequestRepository.findAllByEventId(eventId));
        return result;
    }

    @Transactional
    @Override
    public ParticipationRequestDto setStatusRequest(Long id, EventRequestStatus status) {
        var result = eventRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(" не найдено запроса с id: " + id));
        result.setStatus(status);
        return eventRequestMapper.toParticipationRequestDto(result);
    }

    @Override
    public Map<Long, List<ParticipationRequestDto>> findAllConfirmedByEventId(List<Long> eventIds) {
        List<EventRequest> confirmedRequests = eventRequestRepository.findConfirmedRequests(eventIds);

        Map<Long, List<ParticipationRequestDto>> result = new HashMap<>();

        for (EventRequest request : confirmedRequests) {
            var eventId = request.getEventId();
            List<ParticipationRequestDto> list = result.get(eventId);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(eventRequestMapper.toParticipationRequestDto(request));
            result.put(eventId, list);
        }
        return result;
    }

    @Override
    public boolean checkExistsByEventIdAndRequesterIdAndStatus(Long eventId, Long userId, EventRequestStatus status) {
        return eventRequestRepository.existsByEventIdAndRequesterIdAndStatus(eventId, userId, status);
    }

    private void validateEventForRequest(EventFullDto event) {
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Can't send request to unpublished event");
        }
        if (event.getParticipantLimit().equals(0L)) {
            return;
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Limit of event can't be full");
        }
    }

    private void validateRequests(Collection<EventRequest> requests) {
        for (EventRequest request : requests) {
            if (!request.getStatus().equals(EventRequestStatus.PENDING)) {
                throw new ConflictException("Trying to change status of not pending request");
            }
        }
    }

    private void rejectRequests(EventRequestStatusUpdateResult result, Collection<EventRequest> requests) {
        for (EventRequest request : requests) {
            request.setStatus(EventRequestStatus.REJECTED);
            final EventRequest updatedRequest = eventRequestRepository.save(request);
            result.getRejectedRequests().add(eventRequestMapper.toParticipationRequestDto(updatedRequest));
        }
    }

    private void confirmRequests(EventRequestStatusUpdateResult result, EventFullDto event, Collection<EventRequest> requests) {
        final Long limit = event.getParticipantLimit();

        Long currentConfirmed = event.getConfirmedRequests();
        for (EventRequest request : requests) {
            if (currentConfirmed >= limit) {
                request.setStatus(EventRequestStatus.REJECTED);
                final EventRequest updatedRequest = eventRequestRepository.save(request);
                result.getRejectedRequests().add(eventRequestMapper.toParticipationRequestDto(updatedRequest));
            } else {
                request.setStatus(EventRequestStatus.CONFIRMED);
                final EventRequest updatedRequest = eventRequestRepository.save(request);
                result.getConfirmedRequests().add(eventRequestMapper.toParticipationRequestDto(updatedRequest));
                currentConfirmed++;
            }
        }
    }
}
