package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.ewm.DateTimeFormat.DATE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  ParticipationRequestDto {
    private Long id;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private EventRequestStatus status;
}
