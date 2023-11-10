package ru.practicum.explore.stat.controller;


import dto.StatDto;
import dto.ViewStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.stat.service.StatService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatController {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatService statService;

    @GetMapping("/stats")
    public List<ViewStat> getAllStatistic(@RequestParam String start, @RequestParam String end,
                                          @RequestParam(required = false) String[] uris,
                                          @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        return statService.getAllStatistics(LocalDateTime.parse(start, DATE_TIME_FORMATTER),
                LocalDateTime.parse(end, DATE_TIME_FORMATTER),
                List.of(uris).stream().map(str -> str.replaceAll("^\\[|\\]$", ""))
                        .collect(Collectors.toList()), unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatDto create(@RequestBody StatDto statDto) {
        return statService.create(statDto);
    }
}
