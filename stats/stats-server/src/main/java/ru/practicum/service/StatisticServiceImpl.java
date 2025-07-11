package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ResponseHitDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.reposirory.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service("statisticServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Override
    public ResponseHitDto create(ResponseHitDto createHitDto) {
        Hit hit = statisticRepository.save(HitMapper.INSTANCE.mapToHit(createHitDto));

        return HitMapper.INSTANCE.mapToResponseDto(hit);
    }

    @Override
    public List<ResponseStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start == null || end == null) {
            throw new ValidationException("You need to chose start and end dates.");
        }

        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания.");
        }
        if (!uris.isEmpty() && unique) {
            return statisticRepository.getStatsWithUrisUnique(start, end, uris);
        } else if (uris.isEmpty() && unique) {
            return statisticRepository.getStatsWithoutUrisUnique(start, end);
        } else if (!uris.isEmpty()) {
            return statisticRepository.getStatsWithUrisNotUnique(start, end, uris);
        } else {
            return statisticRepository.getStatsWithoutUrisNotUnique(start, end);
        }
    }
}
