package ru.practikum.explore.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practikum.explore.events.dto.*;
import ru.practikum.explore.events.service.EventsService;
import ru.practikum.explore.requests.dto.EventRequestStatusUpdateRequest;
import ru.practikum.explore.requests.dto.EventRequestStatusUpdateResult;
import ru.practikum.explore.requests.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class EventPrivateController {
    private final EventsService eventsService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getAll(@PathVariable Integer userId,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventsService.getAllByUserId(userId, PageRequest.of(from / size, size));
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Integer userId,
                                 @Valid @RequestBody NewEventDto event) {
        // создать класс евенты парам
        return eventsService.addEvent(userId, event);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventById(@PathVariable Integer userId,
                                     @PathVariable Integer eventId) {
        // создать класс евенты парам
        return eventsService.getEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto editEventById(@PathVariable Integer userId,
                                      @PathVariable Integer eventId,
                                      @Valid @RequestBody UpdateEventUserRequest event) {
        // создать класс евенты парам
        return eventsService.editEventById(userId, eventId, event);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByEvent(@PathVariable Integer userId,
                                                            @PathVariable Integer eventId) {
        // создать класс евенты парам
        return eventsService.getRequestsByEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult patchRequestsByEvent(@PathVariable Integer userId,
                                                               @PathVariable Integer eventId,
                                                               @Valid @RequestBody EventRequestStatusUpdateRequest ev) {
        // создать класс евенты парам
        return eventsService.patchRequestsByEvent(userId, eventId, ev);
    }
}
