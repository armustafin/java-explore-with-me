package ru.practikum.explore.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practikum.explore.categories.dto.Category;
import ru.practikum.explore.categories.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<Category> getAll(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.getAll(PageRequest.of(from / size, size));
    }

    @GetMapping("/categories/{catId}")
    public Category getAll(@PathVariable Integer catId) {
        return categoryService.getbyId(catId);
    }
}
