package ru.practikum.explore.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practikum.explore.user.dto.User;
import ru.practikum.explore.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll(Integer[] ids, PageRequest of);

    User add(UserDto user);

    User deleteById(Integer userId);
}
