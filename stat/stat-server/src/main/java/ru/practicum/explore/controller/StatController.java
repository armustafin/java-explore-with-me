package ru.practicum.explore.controller;

import dto.RequestStat;
import dto.StatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.repository.Stat;
import ru.practicum.explore.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/stats")
    public List<RequestStat> getAllStatistic(@RequestParam String start, @RequestParam String end,
                                             @RequestParam String[] uris, @RequestParam Boolean unique) {

        log.info("Get statics with next parametrs start {}, end={}, uris={}, uniqui={}", start, end, uris, unique);
        return statService.getAllStatistic(LocalDateTime.parse(start, DATE_TIME_FORMATTER),
                LocalDateTime.parse(end, DATE_TIME_FORMATTER), uris, unique);
    }

    @PostMapping("/hit")
    public Stat create(@RequestBody @Valid StatDto statDto) {
        return statService.create(statDto);
    }
}
