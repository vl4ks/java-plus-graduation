package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.SortType;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Slf4j
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    private List<CommentDto> getAllCommentsByEventId(@PathVariable Long eventId,
                                                     @RequestParam(defaultValue = "LIKES") SortType sort,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получили публичный запрос на получение комментариев для мероприятия с eventId={}. Параметры: сортировка={}, from={}, size={}",
                eventId, sort, from, size);
        return commentService.getAllComments(eventId, sort, from, size);
    }
}
