package ru.practicum.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.EventRequest;

import java.time.format.DateTimeFormatter;

@Component
public class EventRequestDtoMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public ParticipationRequestDto mapToResponseDto(EventRequest eventRequest) {
        final ParticipationRequestDto participationRequestDto = new ParticipationRequestDto(
                eventRequest.getCreated().format(FORMATTER),
                eventRequest.getEventId(),
                eventRequest.getId(),
                eventRequest.getRequesterId(),
                eventRequest.getStatus()
        );
        return participationRequestDto;
    }
}
