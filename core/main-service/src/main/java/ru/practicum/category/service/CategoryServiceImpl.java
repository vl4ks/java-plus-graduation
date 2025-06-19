package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryDtoMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DuplicateException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service("categoryServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryDtoMapper categoryDtoMapper;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        try {
            Category category = categoryRepository.save(categoryDtoMapper.mapFromDto(newCategoryDto));
            return categoryDtoMapper.mapToDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Категория с таким именем уже существует");
        }
    }

    @Override
    public Collection<CategoryDto> findAll(Integer from, Integer size) {
        final Collection<Category> categories = categoryRepository.findAll(PageRequest.of(from, size)).getContent();
        return categories.stream()
                .map(categoryDtoMapper::mapToDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public CategoryDto findById(Long categoryId) {
        final Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category with id=" + categoryId + " was not found")
        );
        return categoryDtoMapper.mapToDto(category);
    }


    @Override
    public CategoryDto update(Long categoryId, CategoryDto categoryDto) {
        final Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category with id=" + categoryId + " was not found")
        );
        if (!category.getName().equals(categoryDto.getName())) {
            checkForCategoryDuplicates(categoryDto.getName());
        }

        final Category updatedCategory = categoryRepository.save(category);
        return categoryDtoMapper.mapToDto(updatedCategory);

    }

    @Transactional
    @Override
    public void delete(Long categoryId) {
        final Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category with id=" + categoryId + " was not found")
        );

        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ConflictException("Нельзя удалить категорию с привязанными событиями");
        }
        categoryRepository.delete(category);
    }

    private void checkForCategoryDuplicates(String categoryName) {
        Boolean isDuplicate = categoryRepository.existsByNameIgnoreCase(categoryName);
        if (isDuplicate) {
            throw new DuplicateException("This category already exists");
        }
    }
}
