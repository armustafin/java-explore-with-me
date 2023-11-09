package ru.practikum.explore.events.repesitory;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practikum.explore.events.dto.Location;

public interface LocationRepisotory extends JpaRepository<Location, Integer> {

    Location getLocationsByLatAndLon(float lat, float lon);
}
