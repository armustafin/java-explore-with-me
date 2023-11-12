package ru.practicum.explore.stat.service;

import dto.StatDto;
import dto.ViewStat;
import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    StatDto create(StatDto statDto);

    List<ViewStat> getAllStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
