package ru.practicum.ewm.deserializer;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public class UserActionDeserializer extends AvroDeserializer<UserActionAvro> {
    public UserActionDeserializer() {
        super(UserActionAvro.getClassSchema());
    }
}
