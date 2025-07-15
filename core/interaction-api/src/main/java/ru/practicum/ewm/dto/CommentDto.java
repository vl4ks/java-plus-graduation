package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

import static ru.practicum.ewm.DateTimeFormat.DATE_PATTERN;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CommentDto {
    private Long id;
    private String text;
    private Long event;
    private String eventName;
    private String authorName;
    private Integer likes;
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime created;
}
