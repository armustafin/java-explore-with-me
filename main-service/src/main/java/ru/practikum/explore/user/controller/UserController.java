package ru.practikum.explore.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practikum.explore.user.dto.User;
import ru.practikum.explore.user.dto.UserDto;
import ru.practikum.explore.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAll(@RequestParam(required = false) Integer[] ids,
                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        return userService.getAll(ids, PageRequest.of(from / size, size));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody UserDto user) {
        return userService.add(user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User deleteById(@PathVariable Integer userId) {
        return userService.deleteById(userId);
    }
}
