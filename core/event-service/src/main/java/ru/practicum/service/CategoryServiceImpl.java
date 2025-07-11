package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.storage.EventRepository;
import ru.practicum.storage.CategoryRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("categoryServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.fromCategoryDto(newCategoryDto);
        checkForCategoryDuplicates(newCategoryDto.getName(), category.getId());
        Category createdCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(createdCategory);
    }

    @Override
    public Collection<CategoryDto> findAll(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from, size)).getContent().stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category with id=" + categoryId + " was not found")
        );
        return categoryMapper.toCategoryDto(category);
    }


    @Override
    public CategoryDto update(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category with id=" + categoryId + " was not found")
        );

        if (!category.getName().equalsIgnoreCase(categoryDto.getName())) {
            checkForCategoryDuplicates(categoryDto.getName(), categoryId);
            category.setName(categoryDto.getName());
        }

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(updatedCategory);

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
