package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.ewm.DateTimeFormat.DATE_PATTERN;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseHitDto {

    private Long id;

    private String app;

    private String uri;

    private String ip;

    @NotNull
    @JsonProperty("timestamp")
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime timestamp;
}
