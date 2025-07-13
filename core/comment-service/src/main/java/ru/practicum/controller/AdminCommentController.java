package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.UserDtoForAdmin;
import ru.practicum.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@Slf4j
public class AdminCommentController {
    private final CommentService commentService;

    @PutMapping("/ban/{userId}")
    public UserDtoForAdmin addBanCommited(@PathVariable Long userId, @RequestParam(defaultValue = "0") Long eventId) {
        log.info("Получили запрос от администратора с userId={} на добавление запрета комментирования мероприятия c eventId={}",
                userId, eventId);
        return commentService.addBanCommited(userId, eventId);
    }

    @DeleteMapping("/ban/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBanCommited(@PathVariable Long userId, @RequestParam(defaultValue = "0") Long eventId) {
        log.info("Получили запрос от администратора с userId={} на отмену запрета комментирования мероприятия c eventId={}",
                userId, eventId);
        commentService.deleteBanCommited(userId, eventId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId, @RequestParam(defaultValue = "0") Long eventId) {
        log.info("Получили запрос от администратора на удаление комментария с commentId={} для мероприятия c eventId={}",
                commentId, eventId);
        commentService.deleteComment(commentId, eventId);
    }

    @GetMapping
    public CommentDto getComment(@RequestParam(defaultValue = "0") Long id) {
        log.info("Получили запрос от администратора на получение комментария с id={}", id);
        return commentService.getComment(id);
    }
}
