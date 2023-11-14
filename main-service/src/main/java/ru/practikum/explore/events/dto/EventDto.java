package ru.practikum.explore.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practikum.explore.categories.dto.CategoryDto;
import ru.practikum.explore.comments.dto.CommentsDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventDto {
    private int id;
    @Size(min = 20, max = 2000)
    private String annotation;

    private CategoryDto category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    List<CommentsDto> comments;
}
