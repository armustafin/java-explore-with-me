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
public class EventsParam {
    private String ip;
    private String uri;
    private String text;
    private List<Integer> categories;
    private Boolean paid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;
    private SortEvent sort;
    private Boolean onlyAvailable;

    public boolean isExistIp() {
        if (getIp() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isExistUri() {
        if (getUri() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isExistText() {
        if (getText() == null) {
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

    public boolean isExistSort() {
        if (getSort() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isExistPaid() {
        if (getPaid() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isExistOnlyAviable() {
        if (getOnlyAvailable() == null) {
            return false;
        } else {
            return true;
        }
    }

}
