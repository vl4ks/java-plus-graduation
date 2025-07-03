package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User mapToUser(NewUserRequest request);

    UserDto mapToUserDto(User user);

    List<UserDto> mapToUsersDto(List<User> users);

    List<UserShortDto> mapToUserShortDto(List<User> users);
}
