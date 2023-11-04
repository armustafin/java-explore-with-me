package ru.practikum.explore.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateEventUserRequest {

    @Size(min = 20, max = 7000)
    private String description;
    @Size(min = 20, max = 2000)
    private String annotation;
    private Integer category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Boolean requestModeration;
    private Integer participantLimit;
    private StateAction stateAction;
    @Size(min = 3, max = 120)
    private String title;
}
