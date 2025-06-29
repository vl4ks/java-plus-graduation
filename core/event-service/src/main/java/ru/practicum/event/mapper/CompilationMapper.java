package ru.practicum.event.mapper;

import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.event.model.Compilation;
import ru.practicum.event.model.Event;
import ru.practicum.event.storage.EventRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    @Mapping(target = "events", expression = "java(map(updateCompilationRequest.getEvents(), eventRepository))")
    void update(@MappingTarget Compilation compilation, UpdateCompilationRequest updateCompilationRequest, @Context EventRepository eventRepository);

    CompilationDto getCompilationDto(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation getCompilation(NewCompilationDto newCompilationDto, @Context EventRepository eventRepository);

    default Set<Event> map(Set<Long> eventIds, @Context EventRepository eventRepository) {
        if (eventIds == null) return Collections.emptySet();
        return eventIds.stream()
                .map(id -> eventRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Event not found: " + id)))
                .collect(Collectors.toSet());
    }

}
