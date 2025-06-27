package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.State;
import ru.practicum.dto.StateAction;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.event.model.Event;

@Mapper
public interface EventUpdater {
    EventUpdater INSTANCE = Mappers.getMapper(EventUpdater.class);

    @Mapping(target = "state", source = "stateAction")
    void update(@MappingTarget Event baseEvent, UpdateEventUserRequest updateEventUserRequest);

    @Mapping(target = "state", source = "stateAction")
    void update(@MappingTarget Event baseEvent, UpdateEventAdminRequest updateEventAdminRequest);

    @ValueMapping(target = "PENDING", source = "SEND_TO_REVIEW")
    @ValueMapping(target = "CANCELED", source = "CANCEL_REVIEW")
    @ValueMapping(target = "PUBLISHED", source = "PUBLISH_EVENT")
    @ValueMapping(target = "CANCELED", source = "REJECT_EVENT")
    State toEventState(StateAction eventActionState);
}
