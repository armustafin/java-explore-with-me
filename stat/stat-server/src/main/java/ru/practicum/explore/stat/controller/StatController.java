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
    private static final String FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";

    private final StatService statService;

    @GetMapping("/stats")
    public List<ViewStat> getAllStatistic(@RequestParam String start,
                                          @RequestParam String end,
                                          @RequestParam(required = false) List<String> uris,
                                          @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        List<String> uries = null;
        if (uris != null) {
            uries = uris.stream().map(str -> str.replaceAll("^\\[|\\]$", ""))
                    .collect(Collectors.toList());
        }
        return statService.getAllStatistics(LocalDateTime.parse(start, DateTimeFormatter.ofPattern(FORMAT_DATE)),
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern(FORMAT_DATE)), uries, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatDto create(@RequestBody StatDto statDto) {
        return statService.create(statDto);
    }
}
