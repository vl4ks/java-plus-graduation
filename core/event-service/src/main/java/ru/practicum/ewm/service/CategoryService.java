package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {

    CategoryDto create(NewCategoryDto newCategoryDto);

    Collection<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto findById(Long categoryId);

    CategoryDto update(Long categoryId, CategoryDto categoryDto);

    void delete(Long categoryId);
}
