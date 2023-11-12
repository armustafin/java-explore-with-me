package ru.practikum.explore.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@Setter
@AllArgsConstructor
public class CategoryDto {

    private int id;
    @NotBlank(message = "Please provide name user")
    @Size(min = 1, max = 50)
    private String name;
}
