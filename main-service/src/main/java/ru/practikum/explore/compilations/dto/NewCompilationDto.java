package ru.practikum.explore.compilations.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class NewCompilationDto {
    @Size(min = 1, max = 50)
    @NotBlank
    private String title;
    private List<Integer> events;
    private boolean pinned;
}
