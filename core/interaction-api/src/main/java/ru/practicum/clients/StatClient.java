package ru.practicum.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.dto.ResponseStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.DateTimeFormat.DATE_PATTERN;

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
