package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatClientService {
    private final StatClient statClient;

    @Value("${spring.application.name}")
    private String appName;

    public String saveHit(ResponseHitDto requestBody) {
        requestBody.setApp(appName);
        return statClient.saveHit(requestBody);
    }

    public List<ResponseStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return statClient.getStats(start, end, uris, unique);
    }
}
