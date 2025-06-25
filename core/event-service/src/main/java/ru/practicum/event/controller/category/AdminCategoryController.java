package ru.practicum.event.controller.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.event.service.CategoryService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Пришел POST запрос /admin/categories с телом {}", newCategoryDto);
        final CategoryDto createdCategory = categoryService.create(newCategoryDto);
        log.info("Отправлен ответ POST /admin/categories с телом {}", createdCategory);
        return createdCategory;
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto update(@Positive @PathVariable Long categoryId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Пришел PATCH запрос /admin/categories/{} с телом {}", categoryId, categoryDto);
        final CategoryDto updatedCategory = categoryService.update(categoryId, categoryDto);
        log.info("Отправлен ответ PATCH /admin/categories/{} с телом {}", categoryId, updatedCategory);
        return updatedCategory;
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long categoryId) {
        log.info("Пришел DELETE запрос /admin/categories/{}", categoryId);
        categoryService.delete(categoryId);
        log.info("Отправлен ответ DELETE /admin/categories/{}", categoryId);
    }
}
