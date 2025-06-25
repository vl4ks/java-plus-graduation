package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EventRequest;

import java.util.Collection;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {

    Collection<EventRequest> findByRequesterId(Long requesterId);

    Collection<EventRequest> findByEventId(Long eventId);

    EventRequest findByEventIdAndRequesterId(Long eventId, Long requesterId);

    Collection<EventRequest> findByIdIn(Collection<Long> requestsIds);
}
