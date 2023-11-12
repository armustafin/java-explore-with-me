package ru.practicum.explore.stat.controller;

import dto.ViewStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;

import dto.StatDto;
import ru.practicum.explore.stat.client.StatisticClient;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatController {

    private final StatisticClient statisticClient;

    private static final String FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";

    @GetMapping("/stats")
    public List<ViewStat> getAllStatistic(@RequestParam
                                          @DateTimeFormat(pattern = FORMAT_DATE) @Valid LocalDateTime start,
                                          @DateTimeFormat(pattern = FORMAT_DATE) @Valid @RequestParam LocalDateTime end,
                                          @RequestParam List<String> uris,
                                          @RequestParam Boolean unique) {

        log.info("Get statics with next parametrs start {}, end={}, uris={}, uniqui={}", start, end, uris, unique);
        return statisticClient.getAllStatistic(start.toString(), end.toString(), uris, unique);
    }

    @PostMapping("/hit")
    public void create(@RequestBody @Valid StatDto statDto) {
        statisticClient.create(statDto);
    }
}
