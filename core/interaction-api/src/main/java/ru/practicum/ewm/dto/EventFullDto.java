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
public class EventFullDto {
    private Long id;

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime createdOn;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime publishedOn;

    private String description;

    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime eventDate;

    private Long initiator;

    private LocationDto location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    private State state;

    private String title;

    private Double rating;

    private Boolean commenting;
}
