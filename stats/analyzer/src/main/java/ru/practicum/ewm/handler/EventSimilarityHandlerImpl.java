package ru.practicum.ewm.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.EventSimilarityMapper;
import ru.practicum.ewm.model.EventSimilarity;
import ru.practicum.ewm.repository.EventSimilarityRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventSimilarityHandlerImpl implements EventSimilarityHandler {
    private final EventSimilarityRepository eventSimilarityRepository;
    private final EventSimilarityMapper eventSimilarityMapper;

    @Transactional
    @Override
    public void handle(EventSimilarityAvro eventSimilarity) {
        Long eventA = eventSimilarity.getEventA();
        Long eventB = eventSimilarity.getEventB();

        if (!eventSimilarityRepository.existsByEventAAndEventB(eventA, eventB)) {
            eventSimilarityRepository.save(eventSimilarityMapper.mapToEventSimilarity(eventSimilarity));
        } else {
            EventSimilarity oldEventSimilarity = eventSimilarityRepository.findByEventAAndEventB(eventA, eventB);
            oldEventSimilarity.setScore(eventSimilarity.getScore());
            oldEventSimilarity.setTimestamp(eventSimilarity.getTimestamp());
        }
    }
}
