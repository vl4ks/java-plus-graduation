package ru.practicum.category.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

@Component
public class CategoryDtoMapper {
    public CategoryDto mapToDto(Category category) {
        final CategoryDto categoryDto = new CategoryDto(
                category.getId(),
                category.getName()
        );
        return categoryDto;
    }

    public Category mapFromDto(NewCategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public Category mapFromDto(CategoryDto categoryDto) {
        final Category category = new Category(
                categoryDto.getId(),
                categoryDto.getName()
        );
        return category;
    }
}
