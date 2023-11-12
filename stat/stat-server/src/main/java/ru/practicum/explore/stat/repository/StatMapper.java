package ru.practicum.explore.stat.repository;

import dto.StatDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StatMapper {
    public Stat toStat(StatDto dto) {
        Stat stat = new Stat();
        stat.setApp(dto.getApp());
        stat.setIp(dto.getIp());
        stat.setUri(dto.getUri());
        stat.setDateTimeIncome(LocalDateTime.now());
        return stat;
    }
}

