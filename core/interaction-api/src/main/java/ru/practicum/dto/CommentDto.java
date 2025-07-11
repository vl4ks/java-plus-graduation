package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static ru.practicum.DateTimeFormat.DATE_PATTERN;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CommentDto {
    Long id;
    String text;
    Long event;
    String eventName;
    String authorName;
    Integer likes;
    @JsonFormat(pattern = DATE_PATTERN)
    LocalDateTime created;
}
