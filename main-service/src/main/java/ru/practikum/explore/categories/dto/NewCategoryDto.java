package ru.practikum.explore.categories.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class NewCategoryDto {
    @NotBlank(message = "Please provide name user")
    @Size(min = 1, max = 50)
    private String name;
}
