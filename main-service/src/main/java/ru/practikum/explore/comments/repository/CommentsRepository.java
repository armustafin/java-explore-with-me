package ru.practikum.explore.comments.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import ru.practikum.explore.comments.dto.Comments;
import ru.practikum.explore.events.dto.Event;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Integer>, QuerydslPredicateExecutor<Comments> {


    List<Comments> findAllByEventId(Integer id);


    List<Comments> findAllByCommentatorId(Integer id, PageRequest pageRequest);

    List<Comments> findAllByEventIn(List<Event> events);
}
