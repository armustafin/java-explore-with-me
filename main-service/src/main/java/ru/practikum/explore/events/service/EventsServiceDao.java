package ru.practikum.explore.events.service;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;

import dto.StatDto;
import dto.ViewStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.stat.client.StatisticClient;
import ru.practikum.explore.categories.dto.Category;

import ru.practikum.explore.categories.repisitory.CategoryRepository;
import ru.practikum.explore.events.dto.*;
import ru.practikum.explore.events.repesitory.EventsRepisotory;
import ru.practikum.explore.events.repesitory.LocationRepisotory;
import ru.practikum.explore.exception.ConflictException;
import ru.practikum.explore.exception.InvalidExistException;
import ru.practikum.explore.exception.InvalidRequestException;
import ru.practikum.explore.requests.dto.*;

import ru.practikum.explore.requests.repisotory.RequestRepisotory;
import ru.practikum.explore.user.dto.User;
import ru.practikum.explore.user.repisitory.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Primary
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventsServiceDao implements EventsService {
    private final String stringStart = "0001-01-01 00:00:00";
    private final String stringEnd = "3001-01-01 00:00:00";
    @Value("${app.name}")
    private String appName;


    private final EventsRepisotory eventsRepisotory;
    private final EventsMapper eventsMapper;
    private final RequestRepisotory requestRepisotory;
    private final StatisticClient statisticClient;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestMapper requsterMapper;
    private final LocationRepisotory locationRepisotory;

    @Override
    public List<EventShortDto> getAll(EventsParam parametrs, PageRequest of) {
        // Запрос к базе данных
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QEvent event = QEvent.event;
        QRequest request = QRequest.request;
        LocalDateTime rangeStart;
        LocalDateTime rangeEnd;

        if (parametrs.isExistRangeStart() && parametrs.isExistRangeEnd()) {
            if (parametrs.getRangeStart().isAfter(parametrs.getRangeEnd())) {
                throw new InvalidRequestException("Error request start after end");
            }
        }
        if (parametrs.isExistText()) {
            booleanBuilder.and(event.annotation.containsIgnoreCase(parametrs.getText()))
                    .or(event.description.containsIgnoreCase(parametrs.getText()));
        }

        if (parametrs.isExistCategories()) {
            List<Integer> categories = parametrs.getCategories();
            booleanBuilder.and(event.category.id.in(categories));
        }

        if (parametrs.isExistPaid()) {
            booleanBuilder.and(event.paid.eq(parametrs.getPaid()));
        }
        if (parametrs.isExistRangeStart()) {
            rangeStart = parametrs.getRangeStart();
        } else {
            rangeStart = LocalDateTime.now();
        }
        if (parametrs.isExistRangeEnd()) {
            rangeEnd = parametrs.getRangeEnd();
        } else {
            rangeEnd = LocalDateTime.of(3000, 1, 1, 0, 0);
        }
        booleanBuilder.and(event.eventDate.after(rangeStart)).and(event.eventDate.before(rangeEnd));

        if (parametrs.isExistOnlyAviable()) {
            booleanBuilder.and(event.participantLimit.goe(JPAExpressions.select(request.id.countDistinct())
                    .from(request).where(request.status.eq(StatusRequest.CONFIRMED)
                            .and(request.event.eq(event))))).or(event.participantLimit.eq(0));

        }

        List<Event> events = eventsRepisotory.findAll(booleanBuilder, of).toList();

        List<String> uris = events.stream().map(event1 -> "/events/" + event1.getId()).collect(Collectors.toList());

        List<ViewRequst> viewReqest = requestRepisotory.findViewReqest(events, StatusRequest.CONFIRMED);

        // Запрос к базе данных статитики
        List<ViewStat> viewStatList = statisticClient.getAllStatistic(stringStart,
                stringEnd, uris, false);
        // Преоброзовать три списка в один через  маппер
        List<EventShortDto> eventShortDtos = eventsMapper.toPublicEventList(events, viewStatList, viewReqest);
        if (parametrs.isExistSort()) {
            SortEvent sort = parametrs.getSort();
            if (sort.equals(SortEvent.EVENT_DATE)) {
                Collections.sort(eventShortDtos, EventShortDto.dateComparator);
            } else {
                Collections.sort(eventShortDtos, EventShortDto.viewsComparator);
            }
        }

        // Запись в статистику
        StatDto dto = new StatDto();
        dto.setTimeStamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setApp(appName);
        dto.setIp(parametrs.getIp());
        dto.setUri(parametrs.getUri());
        statisticClient.create(dto);

        return eventShortDtos;
    }

    @Override
    public List<EventShortDto> getAllByUserId(Integer userId, PageRequest of) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QEvent event = QEvent.event;
        LocalDateTime rangeStart = LocalDateTime.of(1, 1, 1, 0, 0);
        LocalDateTime rangeEnd = LocalDateTime.of(3000, 1, 1, 0, 0);
        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        booleanBuilder.and(event.initiator.id.eq(userId));
        List<Event> events = eventsRepisotory.findAll(booleanBuilder, of).toList();


        List<String> uris = events.stream().map(event21 -> "/events/" + event21.getId()).collect(Collectors.toList());
        List<ViewRequst> viewReqest = requestRepisotory.findViewReqest(events, StatusRequest.CONFIRMED);

        // Запрос к базе данных статитики
        List<ViewStat> viewStatList = statisticClient.getAllStatistic(stringStart, stringEnd,
                uris, false);
        List<EventShortDto> eventShortDtos = eventsMapper.toPublicEventList(events, viewStatList, viewReqest);
        Collections.sort(eventShortDtos, EventShortDto.dateComparator);

        return eventShortDtos;
    }

    @Override
    @Transactional()
    public EventFullDto addEvent(Integer userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        if (LocalDateTime.now().plus(Duration.ofHours(2)).isAfter(newEventDto.getEventDate())) {
            throw new InvalidRequestException("Field: eventDate. Error: должно содержать дату," +
                    " которая еще не наступила. Value: " + newEventDto.getEventDate().toString());
        }
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new InvalidExistException("Category with id=" + newEventDto.getCategory() + " was not found"));
        Event event = eventsRepisotory.save(eventsMapper.toEvent(newEventDto, category, user));

        return eventsMapper.toFullEventDto(event, 0, 0);
    }

    @Override
    public EventFullDto getEventById(Integer userId, Integer eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        Event event = eventsRepisotory.findById(eventId)
                .orElseThrow(() -> new InvalidExistException("Event with id=" + eventId + " was not found"));
        String uris = "/events/" + event.getId();
        // Запрос к базе данных статитики
        List<ViewStat> viewStatList = statisticClient.getAllStatistic(stringStart,
                stringEnd, List.of(uris), true);
        long views;
        if (viewStatList.size() == 0) {
            views = 0;
        } else {
            views = viewStatList.stream().mapToLong(value -> value.getHits()).sum();
        }
        int confirmedRequests = requestRepisotory.findAllByStatusAndEvent(StatusRequest.CONFIRMED, event).size();
        return eventsMapper.toFullEventDto(event, views, confirmedRequests);
    }

    @Override
    @Transactional()
    public EventFullDto editEventById(Integer userId, Integer eventId, UpdateEventUserRequest updateEvent) {
        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        Event event = eventsRepisotory.findById(eventId)
                .orElseThrow(() -> new InvalidExistException("Event with id=" + eventId + " was not found"));

        //изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
        if (event.getState() == StatusEvent.PUBLISHED) {
            throw new ConflictException("Event must not be published");
        }

        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }

        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }

        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }

        if (updateEvent.getEventDate() != null) {
            if (LocalDateTime.now().plus(Duration.ofHours(2)).isAfter(updateEvent.getEventDate())) {
                throw new InvalidRequestException("Field: eventDate. Error: должно содержать дату," +
                        " которая еще не наступила. Value: " + updateEvent.getEventDate().toString());
            }
            event.setEventDate(updateEvent.getEventDate());
        }

        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }

        Location location;
        if (updateEvent.getLocation() != null) {
            location = locationRepisotory.getLocationsByLatAndLon(updateEvent.getLocation().getLat(),
                    updateEvent.getLocation().getLon());
            if (location != null) {
                event.setLocation(location);
            } else {
                if (event.getLocation().getLat() != updateEvent.getLocation().getLat() ||
                        event.getLocation().getLon() != updateEvent.getLocation().getLon()) {
                    location = new Location();
                    location.setLat(updateEvent.getLocation().getLat());
                    location.setLon(updateEvent.getLocation().getLon());
                    locationRepisotory.save(location);
                    event.setLocation(location);
                }
            }
        }
        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new InvalidExistException("Category with id=" + updateEvent.getCategory() + " was not found"));
            event.setCategory(category);
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(StatusEvent.PENDING);
            } else {
                event.setState(StatusEvent.CANCELED);
            }
        }
        return getEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(Integer userId, Integer eventId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        Event event = eventsRepisotory.findById(eventId)
                .orElseThrow(() -> new InvalidExistException("Event with id=" + eventId + " was not found"));

        List<Request> requsters = requestRepisotory.findAllByStatusAndEvent(StatusRequest.PENDING, event);

        return requsterMapper.toListParticipationRequestDto(requsters);
    }

    @Override
    @Transactional()
    public EventRequestStatusUpdateResult patchRequestsByEvent(Integer userId, Integer eventId,
                                                               EventRequestStatusUpdateRequest ev) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        Request request;
        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        Event event = eventsRepisotory.findById(eventId)
                .orElseThrow(() -> new InvalidExistException("Event with id=" + eventId + " was not found"));

        List<Integer> requestIds = ev.getRequestIds();
        boolean isNeedAllowed = true;
        int limit = event.getParticipantLimit();
        if (limit == 0) {
            isNeedAllowed = false;
        }
        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        int countRequest = requestRepisotory.findAllByStatusAndEvent(StatusRequest.CONFIRMED, event).size();
        if (limit == 0 || !event.getRequestModeration()) {
            for (int id : requestIds) {
                request = requestRepisotory.findById(id)
                        .orElseThrow(() -> new InvalidExistException("Requst with id=" + id + " was not found"));
                if (request.getStatus() != StatusRequest.PENDING) {
                    throw new DataIntegrityViolationException("Request must have status PENDING");
                }
                if (limit == 0 || countRequest < limit) {
                    request.setStatus(ev.getStatus());
                    countRequest++;
                    confirmedRequests.add(requsterMapper.toParticipationRequestDto(request));
                } else {
                    request.setStatus(StatusRequest.REJECTED);
                    rejectedRequests.add(requsterMapper.toParticipationRequestDto(request));
                }
            }
            result.setConfirmedRequests(confirmedRequests);
            result.setRejectedRequests(rejectedRequests);
            return result;
        } else {
            for (int id : requestIds) {
                request = requestRepisotory.findById(id)
                        .orElseThrow(() -> new InvalidExistException("Requst with id=" + id + " was not found"));
                if (request.getStatus() != StatusRequest.PENDING) {
                    throw new DataIntegrityViolationException("Request must have status PENDING");
                }
                if (countRequest < limit) {
                    request.setStatus(ev.getStatus());
                    countRequest++;
                    confirmedRequests.add(requsterMapper.toParticipationRequestDto(request));
                } else {
                    request.setStatus(StatusRequest.REJECTED);
                    rejectedRequests.add(requsterMapper.toParticipationRequestDto(request));
                }
            }
            result.setConfirmedRequests(confirmedRequests);
            result.setRejectedRequests(rejectedRequests);
            return result;
        }
    }

    @Override
    public EventFullDto getbyId(Integer id, HttpServletRequest request) {
        Event event = eventsRepisotory.findById(id)
                .orElseThrow(() -> new InvalidExistException("Event with id=" + id + " was not found"));
        if (event.getState() != StatusEvent.PUBLISHED) {
            throw new InvalidExistException("Status not Published");
        }
        EventFullDto eventFullDto = getEventFullDto(event);
        StatDto statDto = new StatDto();
        statDto.setUri(getStringUri(event));
        statDto.setIp(request.getRemoteAddr());
        statDto.setTimeStamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        statDto.setApp(appName);
        statisticClient.create(statDto);
        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getAllByAdmin(EventsAdminParam eventsParam, PageRequest of) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QEvent event = QEvent.event;
        QRequest request = QRequest.request;
        LocalDateTime rangeStart;
        LocalDateTime rangeEnd;

        if (eventsParam.isExistRangeStart() && eventsParam.isExistRangeEnd()) {
            if (eventsParam.getRangeStart().isAfter(eventsParam.getRangeEnd())) {
                throw new InvalidRequestException("Error request start after end");
            }
        }
        if (eventsParam.isExistUsers()) {
            booleanBuilder.and(event.initiator.id.in(eventsParam.getUsers()));
        }

        if (eventsParam.isExistStates()) {
            booleanBuilder.and(event.state.in(eventsParam.getStates()));
        }

        if (eventsParam.isExistCategories()) {
            booleanBuilder.and(event.category.id.in(eventsParam.getCategories()));
        }
        if (eventsParam.isExistRangeStart()) {
            rangeStart = eventsParam.getRangeStart();
        } else {
            rangeStart = LocalDateTime.now();
        }
        if (eventsParam.isExistRangeEnd()) {
            rangeEnd = eventsParam.getRangeEnd();
        } else {
            rangeEnd = LocalDateTime.of(3000, 1, 1, 0, 0);
        }
        booleanBuilder.and(event.eventDate.after(rangeStart)).and(event.eventDate.before(rangeEnd));

        List<Event> events = eventsRepisotory.findAll(booleanBuilder, of).toList();

        List<String> uris = events.stream().map(event1 -> "/events/" + event1.getId()).collect(Collectors.toList());

        List<ViewRequst> viewReqest = requestRepisotory.findViewReqest(events, StatusRequest.CONFIRMED);

        String stringStart = rangeStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String stringEnd = rangeEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // Запрос к базе данных статитики
        List<ViewStat> viewStatList = statisticClient.getAllStatistic(stringStart,
                stringEnd, uris, true);

        // Преоброзовать три списка в один через  маппер
        List<EventFullDto> eventFullDtos = eventsMapper.toAdminEventList(events, viewStatList, viewReqest);

        return eventFullDtos;
    }

    @Override
    @Transactional
    public EventFullDto patchEventAdmin(Integer eventId, UpdateEventAdminRequest up) {
        Event event = eventsRepisotory.findById(eventId)
                .orElseThrow(() -> new InvalidExistException("Event with id=" + eventId + " was not found"));
        //дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)

        if (up.getEventDate() != null) {
            event.setEventDate(up.getEventDate());
        }
        if (LocalDateTime.now().plus(Duration.ofHours(1)).isAfter(event.getEventDate())) {
            throw new InvalidRequestException("Field: eventDate. Error: должно содержать дату," +
                    " которая еще не наступила. Value: " + event.getEventDate().toString());
        }
        if (up.getPaid() != null) {
            event.setPaid(up.getPaid());
        }
        if (up.getDescription() != null) {
            event.setDescription(up.getDescription());
        }
        if (up.getAnnotation() != null) {
            event.setAnnotation(up.getAnnotation());
        }
        if (up.getCategory() != null) {
            Category category = categoryRepository.findById(up.getCategory())
                    .orElseThrow(() -> new InvalidExistException("Category with id=" + up.getCategory() + " was not found"));
            event.setCategory(category);
        }
        Location location;
        if (up.getLocation() != null) {
            location = locationRepisotory.getLocationsByLatAndLon(up.getLocation().getLat(),
                    up.getLocation().getLon());
            if (location != null) {
                event.setLocation(location);
            } else {
                if (event.getLocation().getLat() != up.getLocation().getLat() ||
                        event.getLocation().getLon() != up.getLocation().getLon()) {
                    location = new Location();
                    location.setLat(up.getLocation().getLat());
                    location.setLon(up.getLocation().getLon());
                    locationRepisotory.save(location);
                    event.setLocation(location);
                }
            }
        }
        if (up.getRequestModeration() != null) {
            event.setRequestModeration(up.getRequestModeration());
        }
        if (up.getParticipantLimit() != null) {
            event.setParticipantLimit(up.getParticipantLimit());
        }
        if (up.getTitle() != null) {
            event.setTitle(up.getTitle());
        }
        if (up.getStateAction() == StateAction.PUBLISH_EVENT && event.getState() != StatusEvent.PENDING) {
            throw new ConflictException("Cannot publish the event because it's not in the right state: " +
                    event.getState());
        } else if (up.getStateAction() == StateAction.PUBLISH_EVENT) {
            event.setState(StatusEvent.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }

        // событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
        if (up.getStateAction() == StateAction.REJECT_EVENT && event.getState() == StatusEvent.PUBLISHED) {
            throw new ConflictException("Cannot canseled the event because it's not in the right state: " +
                    event.getState());
        } else if (up.getStateAction() == StateAction.REJECT_EVENT) {
            event.setState(StatusEvent.CANCELED);
        }
        // событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)

        return getEventFullDto(event);
    }

    private EventFullDto getEventFullDto(Event event) {
        String uris = getStringUri(event);
        // Запрос к базе данных статитики
        List<ViewStat> viewStatList = statisticClient.getAllStatistic(stringStart,
                stringEnd, List.of(uris), true);
        long views;

        if (viewStatList.size() == 0) {
            views = 0;
        } else {
            views = viewStatList.stream().mapToLong(value -> value.getHits()).sum();
        }
        eventsRepisotory.save(event);
        int confirmedRequests = requestRepisotory.findAllByStatusAndEvent(StatusRequest.CONFIRMED, event).size();
        return eventsMapper.toFullEventDto(event, views, confirmedRequests);
    }

    private String getStringUri(Event event) {
        return "/events/" + event.getId();
    }
}

