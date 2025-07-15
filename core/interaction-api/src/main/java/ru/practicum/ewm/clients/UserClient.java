package ru.practicum.ewm.clients;

import feign.FeignException;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserDtoForAdmin;

import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserClient {

    @GetMapping
    List<UserDto> getAllUsers(@RequestParam(defaultValue = "") List<Long> ids,
                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                              @Positive @RequestParam(defaultValue = "10") Integer size) throws FeignException;

    @GetMapping("/{userId}")
    UserDto findById(@PathVariable Long userId) throws FeignException;

    @GetMapping("/admin/{userId}")
    UserDtoForAdmin adminFindById(@PathVariable Long userId) throws FeignException;
}
