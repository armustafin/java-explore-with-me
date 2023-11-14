package ru.practikum.explore.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practikum.explore.comments.dto.CommentsShortDto;
import ru.practikum.explore.comments.service.CommentsServise;
import ru.practikum.explore.events.dto.EventDto;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentsControler {
    private final CommentsServise commentsServise;

    @GetMapping("/events/{eventId}")
    public EventDto getAll(@PathVariable Integer eventId) {
           return commentsServise.getAllByEventId(eventId);
    }

    @GetMapping("/{id}")
    public CommentsShortDto getbyId(@PathVariable Integer id) {
        return commentsServise.getbyId(id);
    }
}
