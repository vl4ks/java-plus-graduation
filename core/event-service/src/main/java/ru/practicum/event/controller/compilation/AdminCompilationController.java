package ru.practicum.event.controller.compilation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.event.service.CompilationService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Запрос на добавление подборки событий - ADMIN");
        return compilationService.addCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@Valid @RequestBody UpdateCompilationRequest updateCompilation,
                                            @PathVariable Long compId) {
        log.info("Запрос на обновление подборки событий -ADMIN");
        return compilationService.updateCompilation(compId, updateCompilation);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long id) {
        log.info("Запрос на удаление подборки событий - ADMIN");
        compilationService.deleteCompilation(id);
    }
}
