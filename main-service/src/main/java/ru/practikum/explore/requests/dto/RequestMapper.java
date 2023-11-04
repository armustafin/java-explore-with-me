package ru.practikum.explore.requests.dto;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {
    public List<ParticipationRequestDto> toListParticipationRequestDto(List<Request> requsters) {
        return requsters.stream().map(request -> mapToDto(request)).collect(Collectors.toList());
    }

    public ParticipationRequestDto mapToDto(Request request) {

        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setRequester(request.requester.getId());
        participationRequestDto.setId(request.getId());
        participationRequestDto.setCreated(request.getCreated());
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setStatus(request.getStatus());
        return participationRequestDto;
    }

    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setStatus(request.getStatus());
        dto.setRequester(request.getRequester().getId());
        dto.setEvent(request.getEvent().getId());
        dto.setCreated(request.getCreated());
        dto.setId(request.getId());
        return dto;
    }
}
