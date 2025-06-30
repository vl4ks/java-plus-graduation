package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.LocationDto;
import ru.practicum.model.Location;

@Component
public class LocationMapper {
    public Location toLocation(LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }

        Location location = new Location();
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());
        return location;
    }

    public LocationDto toLocationDto(Location location) {
        if (location == null) {
            return null;
        }

        return new LocationDto(location.getLat(), location.getLon());
    }
}
