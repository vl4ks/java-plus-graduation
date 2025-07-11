package ru.practicum.reposirory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Hit, Long> {

    @Query("""
            SELECT new ru.practicum.dto.ResponseStatsDto(h.app, h.uri, COUNT(h))
            FROM Hit AS h
            WHERE h.timestamp >= :start AND h.timestamp <= :end AND h.uri IN :uris
            GROUP BY h.uri, h.app
            ORDER BY COUNT(h) DESC
            """)
    List<ResponseStatsDto> getStatsWithUrisNotUnique(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end,
                                                     @Param("uris") List<String> uris);

    @Query("""
            SELECT new ru.practicum.dto.ResponseStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip))
            FROM Hit AS h
            WHERE h.timestamp >= :start AND h.timestamp <= :end AND h.uri IN :uris
            GROUP BY h.uri, h.app
            """)
    List<ResponseStatsDto> getStatsWithUrisUnique(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end,
                                                  @Param("uris") List<String> uris);

    @Query("""
            SELECT new ru.practicum.dto.ResponseStatsDto(h.app, h.uri, COUNT(h))
            FROM Hit AS h
            WHERE h.timestamp >= :start AND h.timestamp <= :end
            GROUP BY h.uri, h.app
            """)
    List<ResponseStatsDto> getStatsWithoutUrisNotUnique(@Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.dto.ResponseStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip))
            FROM Hit AS h
            WHERE h.timestamp >= :start AND h.timestamp <= :end
            GROUP BY h.uri, h.app
            """)
    List<ResponseStatsDto> getStatsWithoutUrisUnique(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);
}
