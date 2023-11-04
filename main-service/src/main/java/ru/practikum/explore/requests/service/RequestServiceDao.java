package ru.practikum.explore.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practikum.explore.events.dto.Event;
import ru.practikum.explore.events.dto.StatusEvent;
import ru.practikum.explore.events.repesitory.EventsRepisotory;
import ru.practikum.explore.exception.ConflictException;
import ru.practikum.explore.exception.InvalidExistException;
import ru.practikum.explore.requests.dto.ParticipationRequestDto;
import ru.practikum.explore.requests.dto.Request;
import ru.practikum.explore.requests.dto.RequestMapper;
import ru.practikum.explore.requests.dto.StatusRequest;
import ru.practikum.explore.requests.repisotory.RequestRepisotory;
import ru.practikum.explore.user.dto.User;
import ru.practikum.explore.user.repisitory.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceDao implements RequestService {
    private final EventsRepisotory eventsRepisotory;
    private final RequestRepisotory requestRepisotory;
    private final UserRepository userRepository;
    private final RequestMapper requsterMapper;

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequest(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        return requestRepisotory.findAllByRequesterId(userId).stream()
                .map(request -> requsterMapper.toParticipationRequestDto(request)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Integer userId, Integer eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        Event event = eventsRepisotory.findById(eventId)
                .orElseThrow(() -> new InvalidExistException("Event with id=" + eventId + " was not found"));
        //  нельзя добавить повторный запрос (Ожидается код ошибки 409)
        List<Request> currentRequsts = requestRepisotory.findAllByRequesterIdAndEventId(userId, eventId);
        if (currentRequsts.size() > 0) {
            throw new ConflictException("Повторный запрос");
        }
        //инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("инициатор события не может добавить запрос");
        }
        //нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (!event.getState().equals(StatusEvent.PUBLISHED)) {
            throw new ConflictException("нельзя участвовать в неопубликованном событии");
        }
        //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        if (event.getParticipantLimit() > 0) {
            int confirmedRequests = requestRepisotory.findAllByStatusAndEvent(StatusRequest.CONFIRMED, event).size();
            if (confirmedRequests >= event.getParticipantLimit()) {
                throw new ConflictException("достигнут лимит запросов на участие");
            }
        }

        //если для события отключена пре-модерация запросов на участие, то запрос должен
        // автоматически перейти в состояние подтвержденного

        Request requster = new Request();
        if (event.getRequestModeration()) {
            requster.setStatus(StatusRequest.PENDING);
        } else {
            requster.setStatus(StatusRequest.CONFIRMED);
        }
        requster.setRequester(user);
        requster.setCreated(LocalDateTime.now());
        requster.setEvent(event);
        requster = requestRepisotory.save(requster);
        return requsterMapper.mapToDto(requster);
    }

    @Override
    @Transactional
    public ParticipationRequestDto canceledRequst(Integer userId, Integer requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidExistException("User with id=" + userId + " was not found"));
        Request request = requestRepisotory.findById(requestId)
                .orElseThrow(() -> new InvalidExistException("Requst with id=" + requestId + " was not found"));
        request.setStatus(StatusRequest.CANCEL);
        return requsterMapper.mapToDto(request);
    }
}
