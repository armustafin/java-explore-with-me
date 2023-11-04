package ru.practikum.explore.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practikum.explore.categories.dto.CategoryDto;
import ru.practikum.explore.user.dto.UserShortDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventFullDto {
    private int id;
    @Size(min = 20, max = 2000)
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    @Size(min = 20, max = 7000)
    private String description;
    private Location location;
    private Boolean paid;
    private Boolean requestModeration;
    private int participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    @Size(min = 3, max = 120)
    private String title;
    private StatusEvent state;
    private int views;
}
