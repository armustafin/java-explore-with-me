package ru.practikum.explore.user.repisitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practikum.explore.user.dto.User;


public interface UserRepository extends JpaRepository<User, Integer>, QuerydslPredicateExecutor<User> {


}
