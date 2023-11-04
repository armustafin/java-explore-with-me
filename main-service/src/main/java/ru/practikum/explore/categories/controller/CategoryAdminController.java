package ru.practikum.explore.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practikum.explore.categories.dto.Category;
import ru.practikum.explore.categories.dto.CategoryDto;
import ru.practikum.explore.categories.dto.NewCategoryDto;
import ru.practikum.explore.categories.service.CategoryService;
import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Category create(@Valid @RequestBody NewCategoryDto categoryDto) {
        return categoryService.add(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Category deleteById(@PathVariable Integer catId) {
        return categoryService.deleteById(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public Category patch(@PathVariable Integer catId, @Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.patch(catId, categoryDto);
    }
}
