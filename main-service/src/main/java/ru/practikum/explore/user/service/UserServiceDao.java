package ru.practikum.explore.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practikum.explore.exception.InvalidExistException;
import ru.practikum.explore.user.dto.*;
import ru.practikum.explore.user.repisitory.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceDao implements UserService {
    @Autowired
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll(Integer[] ids, PageRequest pageRequest) {
        if (ids == null) {
            return userRepository.findAll(pageRequest).stream()
                    .map(user -> userMapper.toUserDto(user)).collect(Collectors.toList());
        }
        return userRepository.findAll(QUser.user.id.in(ids),pageRequest).stream()
                .map(user -> userMapper.toUserDto(user)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public User add(UserDto userDto) {
        return userRepository.save(userMapper.toUser(userDto));
    }

    @Override
    @Transactional(readOnly = false)
    public User deleteById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        userRepository.deleteById(userId);
        return user;
    }
}
