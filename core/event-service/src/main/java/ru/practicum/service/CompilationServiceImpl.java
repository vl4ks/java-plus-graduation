package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventDtoMapper;
import ru.practicum.model.Compilation;
import ru.practicum.storage.CompilationRepository;
import ru.practicum.storage.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("compilationServiceImpl")
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventDtoMapper eventDtoMapper;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        log.info("Добавление подборки {}", newCompilationDto.toString());
        Compilation compilation = compilationMapper.getCompilation(newCompilationDto, eventRepository);
        compilation = compilationRepository.save(compilation);

        return compilationMapper.getCompilationDto(compilation);

    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = validateCompilation(compId);
        log.info("Обновление подборки c {}, на {}", updateCompilationRequest.toString(), compilation.toString());
        CompilationMapper.INSTANCE.update(compilation, updateCompilationRequest, eventRepository);
        return CompilationMapper.INSTANCE.getCompilationDto(compilation);
    }

    @Transactional
    @Override
    public void deleteCompilation(Long id) {
        log.info("Удаление подборки c {}", id);
        validateCompilation(id);
        compilationRepository.deleteById(id);
        log.info("Подборка удалена");
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Получение всех подборок с pinned={}, from={}, size={}", pinned, from, size);
        log.info("Получение всех подборок с from={}, size={}, pinned={}", from, size, pinned);
        PageRequest pageRequest = PageRequest.of(from, size);
        List<Compilation> compilations;
        if (pinned != null) {
            log.info("Получение всех подборок с pinned: {}", pinned);
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
            log.info("Получены подборки с pinned={}: {}", pinned, compilations);
        } else {
            log.info("Получение всех подборок без фильтрации по pinned");
            compilations = compilationRepository.findAll(pageRequest).getContent();
            log.info("Получены все подборки: {}", compilations);

        }
        return compilations.stream()
                .map(compilationMapper::getCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        log.info("Получение подборки с compId={}", compId);
        Compilation compilation = validateCompilation(compId);
        log.info("Подборка найдена: {}", compilation);

        return CompilationMapper.INSTANCE.getCompilationDto(compilation);
    }

    private Compilation validateCompilation(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка с id = " + compId + " не найдена."));
    }
}
