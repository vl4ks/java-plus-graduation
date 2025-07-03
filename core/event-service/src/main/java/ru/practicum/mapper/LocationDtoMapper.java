package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.LocationDto;
import ru.practicum.model.Location;

@Component
public class LocationDtoMapper {
    public Location mapFromDto(LocationDto locationDto) {
        final Location location = new Location(
                null,
                locationDto.getLat(),
                locationDto.getLon()
        );
        return location;
    }

    public LocationDto mapToDto(Location location) {
        final LocationDto locationDto = new LocationDto(
                location.getLat(),
                location.getLon()
        );
        return locationDto;
    }
}
