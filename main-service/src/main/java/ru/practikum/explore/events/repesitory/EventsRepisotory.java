package ru.practikum.explore.events.repesitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practikum.explore.events.dto.Event;


public interface EventsRepisotory extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {
}
