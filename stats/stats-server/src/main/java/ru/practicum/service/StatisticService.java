package ru.practicum.service;

import ru.practicum.dto.ResponseHitDto;
import ru.practicum.dto.ResponseStatsDto;

import java.util.List;

public interface StatisticService {
    ResponseHitDto create(ResponseHitDto responseHitDto);

    List<ResponseStatsDto> get(String start, String end, List<String> uris, Boolean unique);
}