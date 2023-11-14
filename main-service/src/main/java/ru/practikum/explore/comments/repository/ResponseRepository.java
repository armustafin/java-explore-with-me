package ru.practikum.explore.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practikum.explore.comments.dto.Comments;
import ru.practikum.explore.comments.dto.Response;

import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Integer>, QuerydslPredicateExecutor<Response> {
    List<Response> findAllByCommentIn(List<Comments> comments);
}
