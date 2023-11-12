package ru.practikum.explore.events.service;

import org.springframework.data.domain.PageRequest;
import ru.practikum.explore.events.dto.*;
import ru.practikum.explore.requests.dto.EventRequestStatusUpdateRequest;
import ru.practikum.explore.requests.dto.EventRequestStatusUpdateResult;
import ru.practikum.explore.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventsService {

    List<EventShortDto> getAll(EventsParam parametrs, PageRequest of);

    List<EventShortDto> getAllByUserId(Integer userId, PageRequest of);

    EventFullDto addEvent(Integer userId, NewEventDto event);

    EventFullDto getEventById(Integer userId, Integer eventId);

    EventFullDto editEventById(Integer userId, Integer eventId, UpdateEventUserRequest event);

    List<ParticipationRequestDto> getRequestsByEvent(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult patchRequestsByEvent(Integer userId, Integer eventId, EventRequestStatusUpdateRequest ev);

    EventFullDto getbyId(Integer catId, HttpServletRequest request);

    List<EventFullDto> getAllByAdmin(EventsAdminParam eventsParam, PageRequest of);

    EventFullDto patchEventAdmin(Integer eventId, UpdateEventAdminRequest up);
}
