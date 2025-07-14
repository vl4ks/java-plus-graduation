package ru.practicum.service;

import ru.practicum.dto.ResponseHitDto;
import ru.practicum.dto.ResponseStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {
    ResponseHitDto create(ResponseHitDto responseHitDto);

    List<ResponseStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}