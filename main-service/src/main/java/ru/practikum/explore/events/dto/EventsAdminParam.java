package ru.practikum.explore.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventsAdminParam {

    private List<Integer> users;
    private List<StatusEvent> states;
    private List<Integer> categories;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;


    public boolean isExistUsers() {
        if (getUsers() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isExistStates() {
        if (getStates() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isExistCategories() {
        if (getCategories() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isExistRangeStart() {
        if (getRangeStart() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isExistRangeEnd() {
        if (getRangeEnd() == null) {
            return false;
        } else {
            return true;
        }
    }
}
