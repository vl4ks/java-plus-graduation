package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.event.mapper.CompilationMapper;
import ru.practicum.event.model.Compilation;
import ru.practicum.event.storage.CompilationRepository;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.*;

@Slf4j
@Service("compilationServiceImpl")
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

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
    public List<CompilationDto> getAllCompilations(Integer from, Integer size, Boolean pinned) {
        log.info("Получение всех подборок с from={}, size={}, pinned={}", from, size, pinned);
        PageRequest pageRequest = PageRequest.of(from, size);
        return compilationRepository.findAllByPinned(pinned, pageRequest).stream()
                .map(CompilationMapper.INSTANCE::getCompilationDto)
                .toList();
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
