package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.model.Location;

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
