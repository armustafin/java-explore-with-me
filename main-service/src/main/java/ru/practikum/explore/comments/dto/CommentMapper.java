package ru.practikum.explore.comments.dto;

import org.springframework.stereotype.Component;
import ru.practikum.explore.categories.dto.CategoryDto;
import ru.practikum.explore.events.dto.Event;
import ru.practikum.explore.events.dto.EventDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    public CommentsShortDto toShort(Comments comments, List<Response> responses) {
        CommentsShortDto dto = new CommentsShortDto();
        dto.setComment(comments.getComment());
        dto.setEventId(comments.getEvent().getId());
        dto.setId(comments.getId());
        List<ResponseDto> responseDto = responses.stream().filter(com -> com.getComment().getId() == comments.getId())
                .map(com ->toResponseDto(com)).collect(Collectors.toList());
        dto.setResponses(responseDto);
        return dto;
    }

    public ResponseDto toResponseDto(Response response) {
        ResponseDto dto = new ResponseDto();
        dto.setResponse(response.getResponse());
        dto.setId(response.getId());
        dto.setUserId(response.getRespounser().getId());
        dto.setCommentId(response.getComment().getId());
        return dto;
    }

    public CommentsDto commentsDto(Comments comments) {
        CommentsDto dto = new CommentsDto();
        dto.setComment(comments.getComment());
        dto.setUserId(comments.getCommentator().getId());
        dto.setId(comments.getId());
        return dto;
    }

    public EventDto toEventDto(Event event, List<Comments> comments) {
        EventDto eventDto = new EventDto();
        eventDto.setEventDate(event.getEventDate());
        eventDto.setAnnotation(event.getAnnotation());
        eventDto.setCategory(new CategoryDto(event.getCategory().getId(),
                event.getCategory().getName()));
        eventDto.setId(event.getId());
        eventDto.setComments(comments.stream().map(com -> commentsDto(com)).collect(Collectors.toList()));

        return eventDto;
    }
}
