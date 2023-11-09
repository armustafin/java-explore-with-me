package ru.practikum.explore.events.dto;


import dto.ViewStat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practikum.explore.categories.dto.Category;
import ru.practikum.explore.categories.dto.CategoryDto;
import ru.practikum.explore.compilations.dto.CompilationDto;
import ru.practikum.explore.compilations.dto.Compilations;
import ru.practikum.explore.requests.dto.ViewRequst;
import ru.practikum.explore.user.dto.User;
import ru.practikum.explore.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EventsMapper {
    @Value("${app.name}")
    private String appName;

    public List<EventShortDto> toPublicEventList(List<Event> events, List<ViewStat> viewStats,
                                                 List<ViewRequst> viewRequsts) {
        return events.stream().map(event -> toMapperShort(event, viewStats, viewRequsts)).collect(Collectors.toList());
    }

    private EventShortDto toMapperShort(Event event, List<ViewStat> viewStats, List<ViewRequst> viewRequsts) {
        long views = 0;
        int confirmedRequests = 0;
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setPaid(event.isPaid());
        eventShortDto.setId(event.getId());
        String uri = "/events/" + event.getId();
        for (ViewStat vs : viewStats) {
            if (appName.equals(vs.getApp()) && vs.getUri().equals(uri)) {
                views = vs.getHits();
                break;
            }
        }
        eventShortDto.setViews(views);

        for (ViewRequst vr : viewRequsts) {
            if (vr.getEvent().equals(event.getId())) {
                confirmedRequests = vr.getHit();
                break;
            }
        }
        eventShortDto.setConfirmedRequests(confirmedRequests);
        return eventShortDto;
    }

    public Map<Integer, EventShortDto> toPublicEventMap(List<Event> events, List<ViewStat> viewStatList,
                                                        List<ViewRequst> viewRequsts) {
        Map<Integer, EventShortDto> eventShortDto = new HashMap<>();
        List<EventShortDto> eventShortDtos = events.stream()
                .map(event -> toMapperShort(event, viewStatList, viewRequsts)).collect(Collectors.toList());
        for (EventShortDto ev : eventShortDtos) {
            eventShortDto.put(ev.getId(), ev);
        }
        return eventShortDto;
    }

    public List<CompilationDto> toPublicCollections(List<Compilations> compilations,
                                                    Map<Integer, EventShortDto> eventShortDtos) {

        List<CompilationDto> compilationDtos = new ArrayList<>();
        for (Compilations com : compilations) {
            compilationDtos.add(mapperCompilationDto(com, eventShortDtos));
        }
        return compilations.stream().map(com -> mapperCompilationDto(com, eventShortDtos)).collect(Collectors.toList());
    }

    public CompilationDto mapperCompilationDto(Compilations com, Map<Integer, EventShortDto> eventShortDtos) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(com.getId());
        compilationDto.setPinned(com.isPinned());
        compilationDto.setTitle(com.getTitle());
        compilationDto.setEvents(com.getEvents().stream()
                .map(event -> eventShortDtos.get(event.getId())).collect(Collectors.toList()));
        return compilationDto;
    }

    public Event toEvent(NewEventDto newEventDto, Category category, User user) {
        Event event = new Event();
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setCreatedOn(LocalDateTime.now());
        event.setLocation(newEventDto.getLocation());
        event.setState(StatusEvent.PENDING);
        if (newEventDto.getRequestModeration() != null) {
            event.setRequestModeration(newEventDto.getRequestModeration());
        } else {
            event.setRequestModeration(true);
        }
        event.setParticipantLimit(newEventDto.getParticipantLimit());

        event.setAnnotation(newEventDto.getAnnotation());
        if (newEventDto.getPaid() != null) {
            event.setPaid(newEventDto.getPaid());
        } else {
            event.setPaid(false);
        }

        event.setCategory(category);
        event.setInitiator(user);
        event.setTitle(newEventDto.getTitle());
        return event;
    }

    public EventFullDto toFullEventDto(Event event, long views, Integer confirmedRequests) {
        EventFullDto fullDto = new EventFullDto();
        fullDto.setId(event.getId());
        fullDto.setDescription(event.getDescription());
        fullDto.setEventDate(event.getEventDate());
        fullDto.setCreatedOn(event.getCreatedOn());
        fullDto.setLocation(toLocationDto(event.getLocation()));
        fullDto.setConfirmedRequests(confirmedRequests);
        fullDto.setState(event.getState());
        fullDto.setParticipantLimit(event.getParticipantLimit());
        fullDto.setRequestModeration(event.getRequestModeration());
        fullDto.setAnnotation(event.getAnnotation());
        fullDto.setPaid(event.isPaid());
        fullDto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        fullDto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        fullDto.setTitle(event.getTitle());
        fullDto.setViews(views);
        return fullDto;
    }

    public List<EventFullDto> toAdminEventList(List<Event> events, List<ViewStat> viewStats, List<ViewRequst> vr) {
        return events.stream().map(event -> toMapperFull(event, viewStats, vr)).collect(Collectors.toList());
    }

    public EventFullDto toMapperFull(Event event, List<ViewStat> viewStats, List<ViewRequst> viewRequsts) {
        long views = 0;
        int confirmedRequests = 0;
        EventFullDto fullDto = new EventFullDto();
        fullDto.setId(event.getId());
        fullDto.setDescription(event.getDescription());
        fullDto.setEventDate(event.getEventDate());
        fullDto.setCreatedOn(event.getCreatedOn());
        fullDto.setLocation(toLocationDto(event.getLocation()));
        String uri = "/events/" + event.getId();
        for (ViewStat vs : viewStats) {
            if (appName.equals(vs.getApp()) && vs.getUri().equals(uri)) {
                views = vs.getHits();
                break;
            }
        }
        fullDto.setViews(views);

        for (ViewRequst vr : viewRequsts) {
            if (vr.getEvent().equals(event.getId())) {
                confirmedRequests = vr.getHit();
                break;
            }
        }
        fullDto.setConfirmedRequests(confirmedRequests);
        fullDto.setState(event.getState());
        fullDto.setParticipantLimit(event.getParticipantLimit());
        fullDto.setRequestModeration(event.getRequestModeration());
        fullDto.setAnnotation(event.getAnnotation());
        fullDto.setPaid(event.isPaid());
        fullDto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        fullDto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        fullDto.setTitle(event.getTitle());
        return fullDto;
    }

    private LocationDto toLocationDto(Location location) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());
        return locationDto;
    }
}
