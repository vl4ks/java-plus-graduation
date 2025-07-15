package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.model.Comment;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment){
        return toCommentDto(comment , "", "");
    }

    public CommentDto toCommentDto(Comment comment, String eventName, String authorName) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(comment.getEventId())
                .eventName(eventName)
                .authorName(authorName)
                .likes(comment.getLikes().size())
                .created(comment.getCreated())
                .build();
    }

    public Comment toComment(NewCommentDto newCommentDto, Long eventId, Long userId) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .eventId(eventId)
                .authorId(userId)
                .created(LocalDateTime.now())
                .build();
    }
}
