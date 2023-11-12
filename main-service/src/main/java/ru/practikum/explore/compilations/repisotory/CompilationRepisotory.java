package ru.practikum.explore.compilations.repisotory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practikum.explore.compilations.dto.Compilations;


public interface CompilationRepisotory extends JpaRepository<Compilations, Integer>,
        QuerydslPredicateExecutor<Compilations> {
}
