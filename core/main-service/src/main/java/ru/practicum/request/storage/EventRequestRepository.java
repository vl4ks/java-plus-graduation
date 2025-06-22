package ru.practicum.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.EventRequest;

import java.util.Collection;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {

    Collection<EventRequest> findByRequesterId(Long requesterId);

    Collection<EventRequest> findByEventId(Long eventId);

    EventRequest findByEventIdAndRequesterId(Long eventId, Long requesterId);

    Collection<EventRequest> findByIdIn(Collection<Long> requestsIds);
}
