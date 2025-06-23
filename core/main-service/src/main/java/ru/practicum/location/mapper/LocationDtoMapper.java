package ru.practicum.location.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;

@Component
public class LocationDtoMapper {
    public LocationDto mapToDto(Location location) {
        final LocationDto locationDto = new LocationDto(
                location.getLat(),
                location.getLon()
        );
        return locationDto;
    }

    public Location mapFromDto(LocationDto locationDto) {
        final Location location = new Location(
                null,
                locationDto.getLat(),
                locationDto.getLon()
        );
        return location;
    }
}
