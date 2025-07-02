package ru.practicum.clients;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.UserDto;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserClient {
    @GetMapping("/{userId}")
    UserDto findById(@PathVariable Long userId) throws FeignException;

}
