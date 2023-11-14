package ru.practikum.explore.comments.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class CommentsNewDto {
        private int eventId;
        @Size(min = 3, max = 2000)
        private String comment;
}
