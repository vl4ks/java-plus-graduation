package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.service.StatisticService;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {
    final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final StatisticService statisticService;

    @GetMapping("/stats")
    public List<ResponseStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime end,
                                           @RequestParam(required = false, defaultValue = "") List<String> uris,
                                           @RequestParam(required = false) boolean unique) {
        log.info("Получен запрос на получение статистики по посещениям");
        return statisticService.get(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseHitDto createHit(@RequestBody @Valid ResponseHitDto responseHitDto) {
        log.info("Сохранение информации об обращении к эндпоинту");
        return statisticService.create(responseHitDto);
    }
}