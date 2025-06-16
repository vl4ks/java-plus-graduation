package ru.practicum.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

@Component
public class UserDtoMapper {
    public UserDto mapToDto(User user) {
        final UserDto userDto = new UserDto(
                user.getEmail(),
                user.getId(),
                user.getName()
        );
        return userDto;
    }

    public UserShortDto mapToShortDto(User user) {
        final UserShortDto userDto = new UserShortDto(
                user.getId(),
                user.getName()
        );
        return userDto;
    }

    public User mapFromDto(NewUserRequest newUserRequest) {
        final User user = new User(
                null,
                newUserRequest.getEmail(),
                newUserRequest.getName()
        );
        return user;
    }

    public User mapFromDto(UserDto userDto) {
        final User user = new User(
                userDto.getId(),
                userDto.getEmail(),
                userDto.getName()
        );
        return user;
    }
}
