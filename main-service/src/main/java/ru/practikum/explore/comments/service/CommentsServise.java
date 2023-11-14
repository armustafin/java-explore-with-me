package ru.practikum.explore.comments.service;

import org.springframework.data.domain.PageRequest;
import ru.practikum.explore.comments.dto.CommentsNewDto;
import ru.practikum.explore.comments.dto.CommentsShortDto;
import ru.practikum.explore.comments.dto.ResponseDto;
import ru.practikum.explore.comments.dto.ResponseNewDto;
import ru.practikum.explore.events.dto.EventDto;


import java.util.List;

public interface CommentsServise {
    EventDto getAllByEventId(Integer eventId);

    CommentsShortDto getbyId(Integer id);

    List<CommentsShortDto> getAllByUserId(Integer userId, PageRequest of);

    CommentsShortDto addComments(Integer userId, CommentsNewDto commentsDto);

    ResponseDto addRequest(Integer userId, Integer commentsId, ResponseNewDto responseDto);

    CommentsShortDto patchRequests(Integer userId, Integer commentsId, Integer requestId, ResponseNewDto responseDto);

    CommentsShortDto patchComments(Integer userId, Integer commentsId, CommentsNewDto commentsDto);
}
