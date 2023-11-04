package ru.practikum.explore.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practikum.explore.events.dto.*;
import ru.practikum.explore.events.service.EventsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEvientController {
    private final EventsService eventsService;

    @GetMapping
    public List<EventFullDto> getAllByAdmin(@RequestParam(required = false) List<Integer> users,
                                            @RequestParam(required = false) List<StatusEvent> states,
                                            @RequestParam(required = false) List<Integer> categories,
                                            @RequestParam(required = false) LocalDateTime rangeStart,
                                            @RequestParam(required = false) LocalDateTime rangeEnd,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                            @Positive @RequestParam(defaultValue = "10") Integer size) {

        // создать класс евенты парам
        EventsAdminParam eventsParam = new EventsAdminParam();
        eventsParam.setUsers(users);
        eventsParam.setStates(states);
        eventsParam.setCategories(categories);
        eventsParam.setRangeStart(rangeStart);
        eventsParam.setRangeEnd(rangeEnd);

        return eventsService.getAllByAdmin(eventsParam, PageRequest.of(from / size, size));
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto patchEventAdmin(@PathVariable Integer eventId,
                                        @Valid @RequestBody UpdateEventAdminRequest up) {

        return eventsService.patchEventAdmin(eventId, up);
    }
}
