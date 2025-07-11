package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.BanComment;

public interface BanCommentRepository extends JpaRepository<BanComment, Long> {
    BanComment findByUserIdAndEventId(Long userId, Long eventId);
}
