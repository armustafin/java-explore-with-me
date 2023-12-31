package ru.practicum.explore.stat.controller;


import dto.StatDto;
import dto.ViewStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import ru.practicum.explore.stat.exception.InvalidRequestException;
import ru.practicum.explore.stat.service.StatService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatController {
    private static final String FORMAT_DATE = "yyyy-MM-dd%20HH:mm:ss";

    private final StatService statService;

    @GetMapping("/stats")
    public List<ViewStat> getAllStatistic(@RequestParam String start,
                                          @RequestParam String end,
                                          @RequestParam(required = false) List<String> uris,
                                          @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        LocalDateTime startDate;
        LocalDateTime endDate;
        List<String> uries = null;

        if (uris != null) {
            uries = uris.stream().map(str -> str.replaceAll("^\\[|\\]$", ""))
                    .collect(Collectors.toList());
        }
        try {
            startDate = LocalDateTime.parse(UriUtils.encodePath(start, "UTF-8"),
                    DateTimeFormatter.ofPattern(FORMAT_DATE));
            endDate = LocalDateTime.parse(UriUtils.encodePath(end, "UTF-8"),
                    DateTimeFormatter.ofPattern(FORMAT_DATE));
        } catch (Exception e) {
            throw new InvalidRequestException("Error parametr date");
        }
        return statService.getAllStatistics(startDate, endDate, uries, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatDto create(@RequestBody StatDto statDto) {
        return statService.create(statDto);
    }
}
