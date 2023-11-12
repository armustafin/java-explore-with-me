package ru.practikum.explore.categories.dto;

import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public Category toCategory(NewCategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return category;
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
