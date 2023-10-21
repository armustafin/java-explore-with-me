package ru.practicum.explore.service;

import dto.RequestStat;
import dto.StatDto;
import ru.practicum.explore.repository.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    Stat create(StatDto statDto);

    List<RequestStat> getAllStatistic(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);
}
