package ru.practikum.explore.comments.dto;

import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class CommentsShortDto {
    private Integer id;
    private Integer eventId;
    private String comment;
    private List<ResponseDto> responses;
}
