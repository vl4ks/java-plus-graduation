package ru.practicum.ewm.clients;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.dto.SortType;
import ru.practicum.ewm.dto.UserDtoForAdmin;

import java.util.List;

@FeignClient(name = "comment-service")
public interface CommentClient {
    @PutMapping("/admin/comments/ban/{userId}")
    UserDtoForAdmin addBanCommited(@PathVariable Long userId, @RequestParam(defaultValue = "0") Long eventId) throws FeignException;

    @DeleteMapping("/admin/comments/ban/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteBanCommited(@PathVariable Long userId, @RequestParam(defaultValue = "0") Long eventId) throws FeignException;

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable Long commentId, @RequestParam(defaultValue = "0") Long eventId) throws FeignException;

    @GetMapping("/admin/comments")
    CommentDto getComment(@RequestParam(defaultValue = "0") Long id) throws FeignException;

    @PostMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto createComment(@PathVariable Long userId, @RequestParam(defaultValue = "0") Long eventId,
                             @RequestBody NewCommentDto newCommentDto) throws FeignException;

    @PatchMapping("/users/{userId}/comments/{commentId}")
    CommentDto updateComment(@PathVariable Long userId, @PathVariable Long commentId,
                             @RequestParam(defaultValue = "0") Long eventId,
                             @RequestBody NewCommentDto newCommentDto) throws FeignException;

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable Long userId, @PathVariable Long commentId,
                       @RequestParam(defaultValue = "0") Long eventId) throws FeignException;

    @PutMapping("/users/{userId}/comments/{commentId}/like")
    CommentDto addLike(@PathVariable Long userId, @PathVariable Long commentId) throws FeignException;

    @DeleteMapping("/users/{userId}/comments/{commentId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteLike(@PathVariable Long userId, @PathVariable Long commentId) throws FeignException;

    @GetMapping("/comments/{eventId}")
    List<CommentDto> getAllCommentsByEventId(@PathVariable Long eventId,
                                             @RequestParam(defaultValue = "LIKES") SortType sort,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "20") Integer size) throws FeignException;
}
