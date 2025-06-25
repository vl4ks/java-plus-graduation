package ru.practicum.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String categoryName);
}
