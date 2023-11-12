package ru.practikum.explore.categories.service;

import org.springframework.data.domain.PageRequest;
import ru.practikum.explore.categories.dto.Category;
import ru.practikum.explore.categories.dto.CategoryDto;
import ru.practikum.explore.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<Category> getAll(PageRequest of);

    Category getbyId(Integer catId);

    Category add(NewCategoryDto categoryDto);

    Category deleteById(Integer catId);

    Category patch(Integer catId, CategoryDto categoryDto);
}
