package ru.practikum.explore.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practikum.explore.categories.dto.Category;
import ru.practikum.explore.categories.dto.CategoryDto;
import ru.practikum.explore.categories.dto.CategoryMapper;
import ru.practikum.explore.categories.dto.NewCategoryDto;
import ru.practikum.explore.categories.repisitory.CategoryRepository;
import ru.practikum.explore.exception.InvalidExistException;

import java.util.List;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceDao implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> getAll(PageRequest of) {
        return categoryRepository.findAll(of).toList();
    }

    @Override
    public Category getbyId(Integer catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new InvalidExistException("Category with id=" + catId + " was not found="));
        return category;
    }

    @Transactional(readOnly = false)
    @Override
    public Category add(NewCategoryDto categoryDto) {
        if (categoryRepository.findByName(categoryDto.getName()) != null) {
            throw new DataIntegrityViolationException("exist name!");
        }
        return categoryRepository.save(categoryMapper.toCategory(categoryDto));
    }

    @Override
    @Transactional(readOnly = false)
    public Category deleteById(Integer catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new InvalidExistException("Category with id=" + catId + " was not found="));
        categoryRepository.deleteById(catId);
        return category;
    }

    @Override
    @Transactional(readOnly = false)
    public Category patch(Integer catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new InvalidExistException("Category with id=" + catId + " was not found="));
        if (categoryRepository.findByName(categoryDto.getName()) != null) {
            throw new DataIntegrityViolationException("exist name!");
        }
        category.setName(categoryDto.getName());
        return categoryRepository.save(category);
    }
}
