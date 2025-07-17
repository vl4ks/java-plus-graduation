package ru.practicum.ewm.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.EventSimilarity;

import java.util.List;
import java.util.Set;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    List<EventSimilarity> findAllByEventAIn(Set<Long> eventIds, PageRequest pageRequest);

    List<EventSimilarity> findAllByEventBIn(Set<Long> eventIds, PageRequest pageRequest);

    List<EventSimilarity> findAllByEventA(Long eventId, PageRequest pageRequest);

    List<EventSimilarity> findAllByEventB(Long eventId, PageRequest pageRequest);

    boolean existsByEventAAndEventB(Long eventA, Long eventB);

    EventSimilarity findByEventAAndEventB(Long eventA, Long eventB);

    @Query("SELECT es FROM EventSimilarity es WHERE es.eventA IN :eventIds OR es.eventB IN :eventIds ORDER BY es.score DESC")
    List<EventSimilarity> findAllByEventAInOrEventBIn(
            @Param("eventIds") Set<Long> eventIdsA,
            @Param("eventIds") Set<Long> eventIdsB,
            Pageable pageable);

    @Query("SELECT es FROM EventSimilarity es WHERE es.eventA = :eventId OR es.eventB = :eventId ORDER BY es.score DESC")
    List<EventSimilarity> findAllByEventAOrEventB(
            @Param("eventId") Long eventIdA,
            @Param("eventId") Long eventIdB,
            Pageable pageable);
}
