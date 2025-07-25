package ru.practicum.ewm.service;


import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto create(NewUserRequest newUserRequest);

    Collection<UserDto> findAll(List<Long> ids, Integer from, Integer size);

    UserDto findById(Long userId);

    void delete(Long userId);
}
