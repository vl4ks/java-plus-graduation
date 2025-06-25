package ru.practicum.service;

import ru.practicum.CreateHitDto;

import java.util.List;

public interface StatisticService {
    ResponseHitDto create(CreateHitDto createHitDto);

    List<ResponseStatsDto> get(String start, String end, List<String> uris, Boolean unique);
}