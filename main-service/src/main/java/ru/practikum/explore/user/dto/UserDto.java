package ru.practikum.explore.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class UserDto {
    private int id;
    @Size(min = 2, max = 250)
    @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
    private String name;
    @Size(min = 6, max = 254)
    @Email(message = "Please provide a email")
    @NotBlank
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
