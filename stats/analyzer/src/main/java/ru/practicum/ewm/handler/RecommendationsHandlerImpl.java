package ru.practicum.ewm.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.grpc.stats.event.InteractionsCountRequestProto;
import ru.practicum.ewm.grpc.stats.event.RecommendedEventProto;
import ru.practicum.ewm.grpc.stats.event.SimilarEventsRequestProto;
import ru.practicum.ewm.grpc.stats.event.UserPredictionsRequestProto;
import ru.practicum.ewm.model.EventSimilarity;
import ru.practicum.ewm.model.UserAction;
import ru.practicum.ewm.repository.EventSimilarityRepository;
import ru.practicum.ewm.repository.UserActionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationsHandlerImpl implements RecommendationsHandler {
    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        List<UserAction> userActions = userActionRepository.findAllByUserId(userId,
                PageRequest.of(0, maxResults, Sort.by(Sort.Direction.DESC, "timestamp")));
        if (userActions.isEmpty()) {
            return List.of();
        }

        Set<Long> userEventIds = userActions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        List<EventSimilarity> eventSimilarities = eventSimilarityRepository.findAllByEventAInOrEventBIn(
                userEventIds, userEventIds,
                PageRequest.of(0, maxResults * 2, Sort.by(Sort.Direction.DESC, "score")));

        Set<Long> allUserEventIds = userActionRepository.findAllEventIdsByUserId(userId);

        Set<Long> newEventIds = eventSimilarities.stream()
                .map(es -> es.getEventA().equals(userEventIds) ? es.getEventB() : es.getEventA())
                .filter(eventId -> !allUserEventIds.contains(eventId))
                .limit(maxResults * 2L)
                .collect(Collectors.toSet());

        return newEventIds.stream()
                .map(eId -> RecommendedEventProto.newBuilder()
                        .setEventId(eId)
                        .setScore(calculateScore(eId, userId, allUserEventIds, maxResults))
                        .build())
                .sorted(Comparator.comparing(RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long eventId = request.getEventId();
        Long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        List<EventSimilarity> eventSimilarities = eventSimilarityRepository.findAllByEventAOrEventB(
                eventId, eventId,
                PageRequest.of(0, maxResults * 2, Sort.by(Sort.Direction.DESC, "score")));

        if (eventSimilarities.isEmpty()) {
            return List.of();
        }

        Set<Long> userViewedEvents = userActionRepository.findAllEventIdsByUserId(userId);

        List<RecommendedEventProto> recommendations = eventSimilarities.stream()
                .map(es -> {
                    Long similarEventId = es.getEventA().equals(eventId) ? es.getEventB() : es.getEventA();
                    return new AbstractMap.SimpleEntry<>(similarEventId, es.getScore());
                })
                .filter(entry -> !userViewedEvents.contains(entry.getKey()))
                .distinct()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(maxResults)
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return recommendations;
    }

    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        return new ArrayList<>(request.getEventIdList().stream()
                .map(eId -> RecommendedEventProto.newBuilder()
                        .setEventId(eId)
                        .setScore(userActionRepository.getSumWeightByEventId(eId))
                        .build())
                .sorted(Comparator.comparing(RecommendedEventProto::getScore).reversed())
                .toList());
    }

    private float calculateScore(Long eventId, Long userId, Set<Long> userEventIds, int limit) {
        List<EventSimilarity> eventSimilarities = eventSimilarityRepository.findAllByEventAOrEventB(
                eventId, eventId,
                PageRequest.of(0, limit * 2, Sort.by(Sort.Direction.DESC, "score")));

        Map<Long, Double> viewedEventScores = eventSimilarities.stream()
                .filter(es -> {
                    Long otherEvent = es.getEventA().equals(eventId) ? es.getEventB() : es.getEventA();
                    return userEventIds.contains(otherEvent);
                })
                .collect(Collectors.toMap(
                        es -> es.getEventA().equals(eventId) ? es.getEventB() : es.getEventA(),
                        EventSimilarity::getScore
                ));

        if (viewedEventScores.isEmpty()) {
            return 0f;
        }

        Map<Long, Float> actionMarks = userActionRepository.findAllByEventIdInAndUserId(
                        viewedEventScores.keySet(), userId).stream()
                .collect(Collectors.toMap(UserAction::getEventId, UserAction::getMark));

        float sumWeightedMarks = (float) viewedEventScores.entrySet().stream()
                .mapToDouble(entry -> actionMarks.getOrDefault(entry.getKey(), 0f) * entry.getValue())
                .sum();

        float sumScores = (float) viewedEventScores.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        return sumWeightedMarks / sumScores;
    }
}
