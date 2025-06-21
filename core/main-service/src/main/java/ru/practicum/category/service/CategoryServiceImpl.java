package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("categoryServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryDtoMapper categoryDtoMapper;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        final Category category = categoryDtoMapper.mapFromDto(newCategoryDto);
        checkForCategoryDuplicates(newCategoryDto.getName(), category.getId());
        final Category createdCategory = categoryRepository.save(category);
        return categoryDtoMapper.mapToDto(createdCategory);
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

        if (!category.getName().equalsIgnoreCase(categoryDto.getName())) {
            checkForCategoryDuplicates(categoryDto.getName(), categoryId);
            category.setName(categoryDto.getName());
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

    private void checkForCategoryDuplicates(String categoryName, Long currentCategoryId) {
        Optional<Category> existing = categoryRepository.findByNameIgnoreCase(categoryName);
        if (existing.isPresent() && !existing.get().getId().equals(currentCategoryId)) {
            throw new ConflictException("This category already exists");
        }
    }
}
