package ru.practikum.explore.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practikum.explore.comments.dto.CommentMapper;

import ru.practikum.explore.comments.dto.Comments;
import ru.practikum.explore.comments.dto.CommentsNewDto;
import ru.practikum.explore.comments.dto.CommentsShortDto;
import ru.practikum.explore.comments.dto.Response;
import ru.practikum.explore.comments.dto.ResponseDto;
import ru.practikum.explore.comments.dto.ResponseNewDto;
import ru.practikum.explore.comments.repository.CommentsRepository;
import ru.practikum.explore.comments.repository.ResponseRepository;
import ru.practikum.explore.events.dto.Event;
import ru.practikum.explore.events.dto.EventDto;
import ru.practikum.explore.events.repesitory.EventsRepisotory;
import ru.practikum.explore.exception.ConflictException;
import ru.practikum.explore.exception.InvalidExistException;
import ru.practikum.explore.user.dto.User;
import ru.practikum.explore.user.repisitory.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentsServiseDao implements CommentsServise {
    private final CommentsRepository commentsRepository;
    private final ResponseRepository responseRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final EventsRepisotory eventsRepisotory;

    @Override
    public EventDto getAllByEventId(Integer eventId) {

        Event event = eventsRepisotory.findById(eventId)
                .orElseThrow(() -> new InvalidExistException("Event with id=" + eventId
                        + " was not found"));
        List<Comments> comments = commentsRepository.findAllByEventId(eventId);
        return commentMapper.toEventDto(event, comments);
    }

    @Override
    public CommentsShortDto getbyId(Integer id) {
        Comments com = commentsRepository.findById(id)
                .orElseThrow(() -> new InvalidExistException("Comment with id=" + id + " was not found"));
        List<Response> responses = responseRepository.findAllByCommentIn(List.of(com));
        return commentMapper.toShort(com, responses);
    }

    @Override
    public List<CommentsShortDto> getAllByUserId(Integer userId, PageRequest pageRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        List<Comments> comments = commentsRepository.findAllByCommentatorId(userId, pageRequest);
        List<Response> responses = responseRepository.findAllByCommentIn(comments);
        return comments.stream().map(com -> commentMapper.toShort(com, responses)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public CommentsShortDto addComments(Integer userId, CommentsNewDto commentsDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        Event event = eventsRepisotory.findById(commentsDto.getEventId())
                .orElseThrow(() -> new InvalidExistException("Event with id=" + commentsDto.getEventId()
                        + " was not found"));

        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("User must not be initiator events");
        }
        Comments comments = new Comments();
        comments.setCommentator(user);
        comments.setEvent(event);
        comments.setComment(commentsDto.getComment());
        comments.setPublished(LocalDateTime.now());
        commentsRepository.save(comments);

        return commentMapper.toShort(comments, new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = false)
    public ResponseDto addRequest(Integer userId, Integer commentsId, ResponseNewDto responseDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));

        Comments comments = commentsRepository.findById(commentsId)
                .orElseThrow(() -> new InvalidExistException("Comment with id=" + commentsId + " was not found"));
        if (comments.getCommentator().getId() == userId) {
            throw new ConflictException("User must not be commetator");
        }
        Response response = new Response();
        response.setResponse(responseDto.getResponse());
        response.setComment(comments);
        response.setRespounser(user);
        response.setPublished(LocalDateTime.now());
        responseRepository.save(response);

        return commentMapper.toResponseDto(response);
    }

    @Override
    @Transactional(readOnly = false)
    public CommentsShortDto patchRequests(Integer userId, Integer commentsId, Integer responseId,
                                          ResponseNewDto responseDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));

        Comments comments = commentsRepository.findById(commentsId)
                .orElseThrow(() -> new InvalidExistException("Comment with id=" + commentsId + " was not found"));
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new InvalidExistException("Comment with id=" + responseId + " was not found"));
        if (response.getRespounser().getId() != userId) {
            throw new ConflictException("User must be responser");
        }
        if (comments.getCommentator().getId() == response.getRespounser().getId()) {
            throw new ConflictException("User must not be commetator");
        }
        response.setResponse(responseDto.getResponse());
        List<Response> responses = responseRepository.findAllByCommentIn(List.of(comments));
        return commentMapper.toShort(comments, responses);
    }

    @Override
    @Transactional(readOnly = false)
    public CommentsShortDto patchComments(Integer userId, Integer commentsId, CommentsNewDto commentsDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));

        Comments comments = commentsRepository.findById(commentsId)
                .orElseThrow(() -> new InvalidExistException("Comment with id=" + commentsId + " was not found"));

        if (comments.getCommentator().getId() != userId) {
            throw new ConflictException("User must not be commetator");
        }

        List<Response> responses = responseRepository.findAllByCommentIn(List.of(comments));
        comments.setComment(commentsDto.getComment());
        return commentMapper.toShort(comments, responses);

    }
}
