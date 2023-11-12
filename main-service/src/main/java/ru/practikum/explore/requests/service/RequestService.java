package ru.practikum.explore.requests.service;

import ru.practikum.explore.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getAllParticipationRequest(Integer userId);

    ParticipationRequestDto addRequest(Integer userId, Integer eventId);

    ParticipationRequestDto canceledRequst(Integer userId, Integer requestId);
}
