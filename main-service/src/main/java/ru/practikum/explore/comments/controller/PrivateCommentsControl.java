package ru.practikum.explore.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practikum.explore.comments.dto.CommentsNewDto;
import ru.practikum.explore.comments.dto.CommentsShortDto;
import ru.practikum.explore.comments.dto.ResponseDto;
import ru.practikum.explore.comments.dto.ResponseNewDto;
import ru.practikum.explore.comments.service.CommentsServise;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class PrivateCommentsControl {
    private final CommentsServise commentsServise;
    private static final String FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";

    @GetMapping("/{userId}/comments")
    public List<CommentsShortDto> getAll(@PathVariable Integer userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentsServise.getAllByUserId(userId, PageRequest.of(from / size, size));
    }

    @PostMapping("/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentsShortDto addComments(@PathVariable Integer userId,
                                        @Valid @RequestBody CommentsNewDto commentsDto) {
        return commentsServise.addComments(userId, commentsDto);
    }

    @PostMapping("/{userId}/comments/{commentsId}/response")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto addRequest(@PathVariable Integer userId,
                                  @PathVariable Integer commentsId,
                                  @Valid @RequestBody ResponseNewDto responseDto) {
        return commentsServise.addRequest(userId, commentsId, responseDto);
    }

    @PatchMapping("/{userId}/comments/{commentsId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentsShortDto patchComments(@PathVariable Integer userId,
                                          @PathVariable Integer commentsId,
                                          @Valid @RequestBody CommentsNewDto commentsDto) {
        return commentsServise.patchComments(userId, commentsId, commentsDto);
    }


    @PatchMapping("/{userId}/comments/{commentsId}/response/{responseId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentsShortDto patchRequests(@PathVariable Integer userId,
                                          @PathVariable Integer commentsId,
                                          @PathVariable Integer responseId,
                                          @Valid @RequestBody ResponseNewDto responseDto) {
        return commentsServise.patchRequests(userId, commentsId, responseId, responseDto);
    }
}