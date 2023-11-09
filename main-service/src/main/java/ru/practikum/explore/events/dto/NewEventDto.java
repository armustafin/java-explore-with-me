package ru.practikum.explore.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class NewEventDto {

    @Size(min = 20, max = 2000)
    @NotNull
    private String annotation;
    @Size(min = 20, max = 7000)
    @NotNull
    private String description;
    private Integer category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private int participantLimit;
    private Boolean requestModeration;
    @Size(min = 3, max = 120)
    private String title;
}

