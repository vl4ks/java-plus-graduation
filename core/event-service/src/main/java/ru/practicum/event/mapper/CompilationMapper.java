package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.event.model.Compilation;
import ru.practicum.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> listEventDto);

    Compilation fromCompilationDto(NewCompilationDto compilationDto, List<Event> events);

    void update(@MappingTarget Compilation compilation, UpdateCompilationRequest updateCompilationRequest);

    CompilationDto getCompilationDto(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    Compilation getCompilation(NewCompilationDto newCompilationDto);

}
