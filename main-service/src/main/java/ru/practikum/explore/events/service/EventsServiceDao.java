package ru.practikum.explore.events.service;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;

import dto.StatDto;
import dto.ViewStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.stat.client.StatisticClient;
import ru.practikum.explore.categories.dto.Category;

import ru.practikum.explore.categories.repisitory.CategoryRepository;
import ru.practikum.explore.events.dto.*;
import ru.practikum.explore.events.repesitory.EventsRepisotory;
import ru.practikum.explore.exception.ConflictException;
import ru.practikum.explore.exception.InvalidExistException;
import ru.practikum.explore.exception.InvalidRequestException;
import ru.practikum.explore.requests.dto.*;

import ru.practikum.explore.requests.repisotory.RequestRepisotory;
import ru.practikum.explore.user.dto.User;
import ru.practikum.explore.user.repisitory.UserRepository;

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
    @Value("${app.name}")
    private String appName;

    private final EventsRepisotory eventsRepisotory;
    private final EventsMapper eventsMapper;
    private final RequestRepisotory requestRepisotory;
    private final StatisticClient statisticClient;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestMapper requsterMapper;

    @Override
    public List<EventShortDto> getAll(EventsParam parametrs, PageRequest of) {
        // Запрос к базе данных
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QEvent event = QEvent.event;
        QRequest request = QRequest.request;
        LocalDateTime rangeStart;
        LocalDateTime rangeEnd;

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
            rangeEnd = LocalDateTime.MAX;
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

        String stringStart = LocalDateTime.MIN.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String stringEnd = LocalDateTime.MAX.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // Запрос к базе данных статитики
        List<ViewStat> viewStatList = (List<ViewStat>) statisticClient.getAllStatistic(stringStart,
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
        statisticClient.create(new StatDto(LocalDateTime.now(), parametrs.getUri(), parametrs.getIp(),
                appName));

        return eventShortDtos;
    }

    @Override
    public List<EventShortDto> getAllByUserId(Integer userId, PageRequest of) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QEvent event = QEvent.event;
        LocalDateTime rangeStart = LocalDateTime.MIN;
        LocalDateTime rangeEnd = LocalDateTime.MAX;

        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        booleanBuilder.and(event.initiator.id.eq(userId));
        List<Event> events = eventsRepisotory.findAll(booleanBuilder, of).toList();

        List<String> uris = events.stream().map(event1 -> "/events/" + event1.getId()).collect(Collectors.toList());

        List<ViewRequst> viewReqest = requestRepisotory.findViewReqest(events, StatusRequest.CONFIRMED);

        String stringStart = rangeStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String stringEnd = rangeEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<ViewStat> viewStatList = (List<ViewStat>) statisticClient.getAllStatistic(stringStart,
                stringEnd, uris, false);

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
            throw new ConflictException("Field: eventDate. Error: должно содержать дату," +
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
        String stringStart = LocalDateTime.MIN.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String stringEnd = LocalDateTime.MAX.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // Запрос к базе данных статитики
        List<ViewStat> viewStatList = (List<ViewStat>) statisticClient.getAllStatistic(stringStart,
                stringEnd, List.of(uris), false);
        int views;
        if (viewStatList.size() == 0) {
            views = 0;
        } else {
            views = viewStatList.get(0).getHits();
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
            event.setTitle(updateEvent.getAnnotation());
        }

        if (updateEvent.getEventDate() != null) {
            if (LocalDateTime.now().plus(Duration.ofHours(2)).isAfter(updateEvent.getEventDate())) {
                throw new ConflictException("Field: eventDate. Error: должно содержать дату," +
                        " которая еще не наступила. Value: " + updateEvent.getEventDate().toString());
            }
            event.setEventDate(updateEvent.getEventDate());
        }

        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }

        if (updateEvent.getLocation() != null) {
            event.setLocation(updateEvent.getLocation());
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
                    throw new InvalidRequestException("Request must have status PENDING");
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
                    throw new InvalidRequestException("Request must have status PENDING");
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
    public Category getbyId(Integer catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new InvalidExistException("Category with id=" + catId + " was not found"));

        return category;
    }

    @Override
    public List<EventFullDto> getAllByAdmin(EventsAdminParam eventsParam, PageRequest of) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QEvent event = QEvent.event;
        QRequest request = QRequest.request;
        LocalDateTime rangeStart;
        LocalDateTime rangeEnd;

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
            rangeEnd = LocalDateTime.MAX;
        }
        booleanBuilder.and(event.eventDate.after(rangeStart)).and(event.eventDate.before(rangeEnd));

        List<Event> events = eventsRepisotory.findAll(booleanBuilder, of).toList();

        List<String> uris = events.stream().map(event1 -> "/events/" + event1.getId()).collect(Collectors.toList());

        List<ViewRequst> viewReqest = requestRepisotory.findViewReqest(events, StatusRequest.CONFIRMED);

        String stringStart = rangeStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String stringEnd = rangeEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // Запрос к базе данных статитики
        List<ViewStat> viewStatList = (List<ViewStat>) statisticClient.getAllStatistic(stringStart,
                stringEnd, uris, false);

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
            throw new ConflictException("Field: eventDate. Error: должно содержать дату," +
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
        if (up.getLocation() != null) {
            event.setLocation(up.getLocation());
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
        String uris = "/events/" + event.getId();
        String stringStart = LocalDateTime.MIN.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String stringEnd = LocalDateTime.MAX.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // Запрос к базе данных статитики
        List<ViewStat> viewStatList = (List<ViewStat>) statisticClient.getAllStatistic(stringStart,
                stringEnd, List.of(uris), false);
        int views;
        if (viewStatList.size() == 0) {
            views = 0;
        } else {
            views = viewStatList.get(0).getHits();
        }
        eventsRepisotory.save(event);
        int confirmedRequests = requestRepisotory.findAllByStatusAndEvent(StatusRequest.CONFIRMED, event).size();
        return eventsMapper.toFullEventDto(event, views, confirmedRequests);
    }
}

