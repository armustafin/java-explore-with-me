package ru.practikum.explore.user.dto;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(UserDto userDto) {
        User user = new User();
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto(user.getName(), user.getEmail());
        userDto.setId(user.getId());
        return userDto;
    }
}
