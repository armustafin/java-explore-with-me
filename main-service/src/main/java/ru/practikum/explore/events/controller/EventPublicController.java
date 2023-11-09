package ru.practikum.explore.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practikum.explore.events.dto.EventFullDto;
import ru.practikum.explore.events.dto.EventShortDto;
import ru.practikum.explore.events.dto.EventsParam;
import ru.practikum.explore.events.dto.SortEvent;
import ru.practikum.explore.events.service.EventsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {
    private final EventsService eventsService;
    private static final String FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";

    @GetMapping("")
    public List<EventShortDto> getAll(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Integer> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @DateTimeFormat(pattern = FORMAT_DATE)
                                      @RequestParam(required = false) LocalDateTime rangeStart,
                                      @DateTimeFormat(pattern = FORMAT_DATE)
                                      @RequestParam(required = false) LocalDateTime rangeEnd,
                                      @RequestParam(defaultValue = "EVENT_DATE") SortEvent sort,
                                      @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                      @Positive @RequestParam(defaultValue = "10") Integer size,
                                      HttpServletRequest request) {

        // создать класс евенты парам
        EventsParam eventsParam = new EventsParam();
        eventsParam.setIp(request.getRemoteAddr());

        eventsParam.setPaid(paid);
        eventsParam.setUri(request.getRequestURI());
        eventsParam.setSort(sort);
        eventsParam.setCategories(categories);
        eventsParam.setRangeStart(rangeStart);
        eventsParam.setRangeEnd(rangeEnd);
        eventsParam.setOnlyAvailable(onlyAvailable);

        return eventsService.getAll(eventsParam, PageRequest.of(from / size, size));
    }

    @GetMapping("/{id}")
    public EventFullDto getbyId(@PathVariable Integer id,  HttpServletRequest request) {
        return eventsService.getbyId(id, request);
    }
}
