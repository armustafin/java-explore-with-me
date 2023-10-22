package ru.practicum.explore.stat.controller;


import dto.StatDto;
import dto.ViewStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.stat.repository.Stat;
import ru.practicum.explore.stat.service.StatService;

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
    public List<ViewStat> getAllStatistic(@RequestParam String start, @RequestParam String end,
                                          @RequestParam(required = false) List<String> uris,
                                          @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        return statService.getAllStatistic(LocalDateTime.parse(start, DATE_TIME_FORMATTER),
                LocalDateTime.parse(end, DATE_TIME_FORMATTER), uris, unique);
    }

    @PostMapping("/hit")
    public Stat create(@RequestBody @Valid StatDto statDto) {
        return statService.create(statDto);
    }
}
