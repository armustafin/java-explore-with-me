package ru.practicum.explore.stat.service;

import dto.StatDto;
import dto.ViewStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.stat.repository.Stat;
import ru.practicum.explore.stat.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceDao implements StatService {

    private final StatRepository statRepository;

    @Override
    @Transactional(readOnly = false)
    public Stat create(StatDto statDto) {
        return statRepository.save(Stat.toStat(statDto));
    }

    @Override
    public List<ViewStat> getAllStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            List<ViewStat> ls = statRepository.getAllStatistic(start, end, uris);
            return ls;
        } else {
            return statRepository.getAllStatisticNonUnique(start, end, uris);
        }
    }
}
