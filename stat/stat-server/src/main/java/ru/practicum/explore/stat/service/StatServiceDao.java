package ru.practicum.explore.stat.service;

import dto.StatDto;
import dto.ViewStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.stat.repository.Stat;
import ru.practicum.explore.stat.repository.StatMapper;
import ru.practicum.explore.stat.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceDao implements StatService {

    private final StatRepository statRepository;
    private final StatMapper statMapper;

    @Override
    @Transactional(readOnly = false)
    public StatDto create(StatDto statDto) {
        StatDto dto = new StatDto();
        Stat stat = statRepository.save(statMapper.toStat(statDto));
        dto.setIp(stat.getIp());
        dto.setApp(stat.getApp());
        dto.setUri(stat.getUri());
        dto.setTimeStamp(stat.getDateTimeIncome().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return dto;
    }

    @Override
    public List<ViewStat> getAllStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<Object[]> listObject;
        List<ViewStat> result = new ArrayList<>();

        if (unique) {
            listObject = statRepository.getAllStatistic(start, end, uris);
            for (Object[] obj : listObject) {
                result.add(new ViewStat((String) (obj[0]), (String) (obj[1]), Integer.parseInt(obj[2].toString())));
            }
        } else {
            listObject = statRepository.getAllStatisticNonUnique(start, end, uris);
            for (Object[] obj : listObject) {
                result.add(new ViewStat((String) (obj[0]), (String) (obj[1]), Integer.parseInt(obj[2].toString())));
            }
        }
        return result;
    }
}
