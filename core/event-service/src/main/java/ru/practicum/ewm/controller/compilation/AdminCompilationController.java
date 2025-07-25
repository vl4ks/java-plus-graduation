package ru.practicum.ewm.controller.compilation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.service.CompilationService;

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

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Запрос на удаление подборки событий - ADMIN");
        compilationService.deleteCompilation(compId);
    }
}
