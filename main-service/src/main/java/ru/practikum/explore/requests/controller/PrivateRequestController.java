package ru.practikum.explore.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practikum.explore.requests.dto.ParticipationRequestDto;
import ru.practikum.explore.requests.service.RequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping("")
    public List<ParticipationRequestDto> getAllParticipationRequest(@PathVariable Integer userId) {
        return requestService.getAllParticipationRequest(userId);
    }

    @PostMapping
    public ParticipationRequestDto addRequest(@PathVariable Integer userId,
                                              @RequestParam Integer eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto canceledRequst(@PathVariable Integer userId,
                                                  @PathVariable Integer requestId) {
        return requestService.canceledRequst(userId, requestId);
    }
}
