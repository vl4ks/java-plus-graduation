package ru.practicum.ewm.producer;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final Producer<Long, SpecificRecordBase> producer;

    public void send(SpecificRecordBase message, Instant timestamp, Long eventId, String topic) {

        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(topic, null,
                timestamp.toEpochMilli(), eventId, message);

        producer.send(record);
        producer.flush();
    }
}
