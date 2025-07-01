package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EventRequest;

import java.util.Collection;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    Collection<EventRequest> findByRequesterId(Long requesterId);

    Collection<EventRequest> findByEventId(Long eventId);

    EventRequest findByEventIdAndRequesterId(Long eventId, Long requesterId);

    Collection<EventRequest> findByIdIn(Collection<Long> requestsIds);
}
