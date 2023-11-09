package ru.practikum.explore.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

import ru.practikum.explore.categories.dto.CategoryDto;

import ru.practikum.explore.user.dto.UserShortDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Comparator;

@Getter
@Setter
public class EventShortDto {

    private int id;
    @Size(min = 20, max = 2000)
    private String annotation;

    private CategoryDto category;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private String title;

    private boolean paid;
    private long views;

    public static final Comparator<EventShortDto> viewsComparator = new Comparator<EventShortDto>() {
        @Override
        public int compare(EventShortDto ev1, EventShortDto ev2) {
            return (int) (ev1.getViews() - ev2.getViews());
        }
    };

    public static final Comparator<EventShortDto> dateComparator = new Comparator<EventShortDto>() {
        @Override
        public int compare(EventShortDto ev1, EventShortDto ev2) {
            return ev1.getEventDate().compareTo(ev2.getEventDate());
        }
    };
}
