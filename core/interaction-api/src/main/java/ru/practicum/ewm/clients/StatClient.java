package ru.practicum.ewm.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ResponseHitDto;
import ru.practicum.ewm.dto.ResponseStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.DateTimeFormat.DATE_PATTERN;

@FeignClient(name = "stats-server")
public interface StatClient {

    @PostMapping("/hit")
    String saveHit(@RequestBody ResponseHitDto requestBody);

    @GetMapping("/stats")
    List<ResponseStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime end,
                                    @RequestParam(defaultValue = "") List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique);
}
