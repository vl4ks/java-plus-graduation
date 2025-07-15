package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.ewm.DateTimeFormat.DATE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank(message = "Annotation can't be empty")
    @Size(min = 20, max = 2000, message = "Name should be from 20 to 2000 symbols")
    private String annotation;

    @NotNull(message = "Category can't be empty")
    private Long category;

    @NotBlank(message = "Description can't be empty")
    @Size(min = 20, max = 7000, message = "Name should be from 20 to 7000 symbols")
    private String description;


    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime eventDate;

    @NotNull(message = "Location can't be empty")
    @Valid
    private LocationDto location;

    private Boolean paid = false;

    @PositiveOrZero(message = "Limit of participants can't be negative")
    private Long participantLimit = 0L;

    private Boolean requestModeration = true;

    @NotBlank(message = "Title can't be empty")
    @Size(min = 3, max = 120, message = "Name should be from 3 to 120 symbols")
    private String title;

    private Boolean commenting;
}
