package ru.practikum.explore.categories.repisitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practikum.explore.categories.dto.Category;


public interface CategoryRepository extends JpaRepository<Category, Integer>, QuerydslPredicateExecutor<Category> {

    boolean existsByName(String name);

    Category findByName(String name);
}
