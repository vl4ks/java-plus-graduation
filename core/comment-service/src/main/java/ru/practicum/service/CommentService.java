package ru.practicum.service;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.SortType;
import ru.practicum.dto.UserDtoForAdmin;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long eventId, Long userId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto);

    void deleteComment(Long userId, Long eventId, Long commentId);

    void deleteComment(Long commentId, Long eventId);

    List<CommentDto> getAllComments(Long eventId, SortType sortType, Integer from, Integer size);

    CommentDto addLike(Long userId, Long commentId);

    UserDtoForAdmin addBanCommited(Long userId, Long eventId);

    void deleteBanCommited(Long userId, Long eventId);

    void deleteLike(Long userId, Long commentId);

    CommentDto getComment(Long id);
}
