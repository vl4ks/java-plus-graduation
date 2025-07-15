package ru.practicum.ewm.mapper;

import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.storage.EventRepository;

import java.util.Collections;
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
