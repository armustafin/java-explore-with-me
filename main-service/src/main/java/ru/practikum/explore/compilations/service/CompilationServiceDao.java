package ru.practikum.explore.compilations.service;

import com.querydsl.core.BooleanBuilder;
import dto.ViewStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.stat.client.StatisticClient;
import ru.practikum.explore.compilations.dto.*;
import ru.practikum.explore.compilations.repisotory.CompilationRepisotory;
import ru.practikum.explore.events.dto.Event;
import ru.practikum.explore.events.dto.EventShortDto;
import ru.practikum.explore.events.dto.EventsMapper;
import ru.practikum.explore.events.repesitory.EventsRepisotory;
import ru.practikum.explore.exception.InvalidExistException;
import ru.practikum.explore.requests.dto.StatusRequest;
import ru.practikum.explore.requests.dto.ViewRequst;
import ru.practikum.explore.requests.repisotory.RequestRepisotory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceDao implements CompilationService {
    private final String stringStart = "0001-01-01 00:00:00";
    private final String stringEnd = "3001-01-01 00:00:00";

    private final EventsRepisotory eventsRepisotory;
    private final CompilationRepisotory compilationRepisotory;
    private final EventsMapper eventsMapper;
    private final RequestRepisotory requestRepisotory;
    private final StatisticClient statisticClient;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(boolean pinned, PageRequest of) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QCompilations qCompilations = QCompilations.compilations;
        Set<Event> events = new HashSet<>();

        booleanBuilder.and(qCompilations.pinned.eq(pinned));
        List<Compilations> compilations = compilationRepisotory.findAll(booleanBuilder, of).toList();
        for (Compilations com : compilations) {
            events.addAll(com.getEvents());
        }

        List<String> uris = events.stream().map(event1 -> "/events/" + event1.getId()).collect(Collectors.toList());

        List<ViewRequst> viewReqest = requestRepisotory.findViewReqest(events.stream().collect(Collectors.toList()),
                StatusRequest.CONFIRMED);

        List<ViewStat> viewStatList = statisticClient.getAllStatistic(stringStart, stringEnd, uris, true);
        long views;
        if (viewStatList.size() == 0) {
            views = 0;
        } else {
            views = viewStatList.stream().mapToLong(value -> value.getHits()).sum();
        }

        Map<Integer, EventShortDto> eventShortDtos = eventsMapper.toPublicEventMap(events.stream()
                .collect(Collectors.toList()), viewStatList, viewReqest);

        return eventsMapper.toPublicCollections(compilations, eventShortDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getbyId(Integer compId) {
        Compilations compilations = compilationRepisotory.findById(compId)
                .orElseThrow(() -> new InvalidExistException("Compilation with id=" + compId + " was not found"));

        return getCompilationDto(compilations);
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto dto) {
        Compilations compilations = new Compilations();
        compilations.setTitle(dto.getTitle());
        compilations.setPinned(dto.isPinned());
        if (dto.getEvents() != null) {
            compilations.setEvents(eventsRepisotory.findAllById(dto.getEvents()));
        } else {
            compilations.setEvents(new ArrayList<>());
        }
        compilationRepisotory.save(compilations);
        return getCompilationDto(compilations);
    }

    @Override
    @Transactional
    public void deleteCompilation(Integer compId) {
        Compilations compilations = compilationRepisotory.findById(compId)
                .orElseThrow(() -> new InvalidExistException("Compilation with id=" + compId + " was not found"));
        compilationRepisotory.delete(compilations);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilation(Integer compId, UpdateCompilationRequest dto) {
        Compilations compilations = compilationRepisotory.findById(compId)
                .orElseThrow(() -> new InvalidExistException("Compilation with id=" + compId + " was not found"));
        if (dto.getPinned() != null) {
            compilations.setPinned(dto.getPinned());
        }
        if (dto.getTitle() != null) {
            compilations.setTitle(dto.getTitle());
        }
        if (dto.getEvents() != null) {
            List<Event> events = eventsRepisotory.findAllById(dto.getEvents());
            compilations.setEvents(events);
        }

        compilationRepisotory.save(compilations);
        return getCompilationDto(compilations);
    }

    private CompilationDto getCompilationDto(Compilations compilations) {

        List<String> uris = compilations.getEvents().stream()
                .map(event1 -> "/events/" + event1.getId()).collect(Collectors.toList());

        List<ViewRequst> viewReqest = requestRepisotory.findViewReqest(compilations.getEvents(),
                StatusRequest.CONFIRMED);
        List<ViewStat> viewStatList = statisticClient.getAllStatistic(stringStart, stringEnd, uris, true);
        Map<Integer, EventShortDto> eventShortDtos = eventsMapper.toPublicEventMap(compilations.getEvents(),
                viewStatList, viewReqest);

        return eventsMapper.mapperCompilationDto(compilations, eventShortDtos);
    }
}
