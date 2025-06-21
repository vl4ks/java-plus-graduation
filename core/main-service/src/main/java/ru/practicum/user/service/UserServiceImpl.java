package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserDtoMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service("userServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    @Transactional
    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ConflictException("This email is already registered") {
            };
        }
        User user = userDtoMapper.mapFromDto(newUserRequest);
        User createdUser = userRepository.save(user);
        return userDtoMapper.mapToDto(createdUser);
    }

    @Override
    public Collection<UserDto> findAll(List<Long> ids, Integer from, Integer size) {
        final Collection<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(PageRequest.of(from, size)).getContent();
        } else {
            users = userRepository.findAllById(ids);
        }
        return users.stream()
                .map(userDtoMapper::mapToDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public UserDto findById(Long userId) {
        final User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " was not found")
        );
        return userDtoMapper.mapToDto(user);
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        final User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " was not found")
        );
        userRepository.delete(user);
    }
}
