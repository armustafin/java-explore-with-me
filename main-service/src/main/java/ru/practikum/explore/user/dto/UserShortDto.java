package ru.practikum.explore.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class UserShortDto {

    private int id;
    @NotBlank
    private String name;
}
