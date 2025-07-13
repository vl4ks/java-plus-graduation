package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
@Slf4j
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId, @RequestParam(defaultValue = "0") Long eventId,
                                    @RequestBody NewCommentDto newCommentDto) {
        log.info("Получили запрос от пользователя с userId={} на создание комментария для мероприятия c eventId={}. Текст: {}",
                userId, eventId, newCommentDto.getText());
        return commentService.createComment(eventId, userId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId, @PathVariable Long commentId,
                                    @RequestParam(defaultValue = "0") Long eventId,
                                    @RequestBody NewCommentDto newCommentDto) {
        log.info("Получили запрос от пользователя с userId={} на редактирование комментария с commentId={} для мероприятия c eventId={}. Новый текст: {}",
                userId, commentId, eventId, newCommentDto.getText());
        return commentService.updateComment(userId, eventId, commentId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId,
                              @RequestParam(defaultValue = "0") Long eventId) {
        log.info("Получили запрос от пользователя с userId={} на удаление комментария с commentId={} для мероприятия c eventId={}",
                userId, commentId, eventId);
        commentService.deleteComment(userId, eventId, commentId);
    }

    @PutMapping("/{commentId}/like")
    public CommentDto addLike(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Получили запрос от пользователя с userId={} на добавление лайка к комментарию с commentId={}",
                userId, commentId);
        return commentService.addLike(userId, commentId);
    }

    @DeleteMapping("/{commentId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Получили запрос от пользователя с userId={} на удаление лайка у комментария с commentId={}",
                userId, commentId);
        commentService.deleteLike(userId, commentId);
    }
}
