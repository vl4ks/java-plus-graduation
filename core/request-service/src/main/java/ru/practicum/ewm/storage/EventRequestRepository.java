package ru.practicum.ewm.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.dto.EventRequestStatus;
import ru.practicum.ewm.model.EventRequest;

import java.util.Collection;
import java.util.List;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    Collection<EventRequest> findByRequesterId(Long requesterId);

    Collection<EventRequest> findByEventId(Long eventId);

    @Query("select r from EventRequest r where r.status = 'CONFIRMED' and r.eventId in ?1")
    List<EventRequest> findConfirmedRequests(List<Long> ids);

    Collection<EventRequest> findByIdIn(Collection<Long> requestsIds);

    List<EventRequest> findAllByEventId(Long eventId);

    boolean existsByEventIdAndRequesterIdAndStatus(Long eventId, Long userId, EventRequestStatus status);
}
