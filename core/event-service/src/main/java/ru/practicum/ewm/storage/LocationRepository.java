package ru.practicum.ewm.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {}
