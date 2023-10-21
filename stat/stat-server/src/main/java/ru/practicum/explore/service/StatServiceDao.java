package ru.practicum.explore.service;

import dto.RequestStat;
import dto.StatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.repository.Stat;
import ru.practicum.explore.repository.StatRepository;

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
    public Stat create(StatDto statDto) {
        return statRepository.save(Stat.toStat(statDto));
    }

    @Override
    public List<RequestStat> getAllStatistic(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        if (unique) {
            return statRepository.getAllStatistic(start, end, uris);
        } else {
            return statRepository.getAllStatisticNonUnique(start, end, uris);
        }
    }
}
