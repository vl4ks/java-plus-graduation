package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.DateTimeFormat.DATE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000, message = "Name should be from 20 to 2000 symbols")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Name should be from 20 to 7000 symbols")
    private String description;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(message = "Limit of participants can't be negative")
    private Long participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    @Size(min = 3, max = 120, message = "Name should be from 3 to 120 symbols")
    private String title;

    private Boolean commenting;
}
