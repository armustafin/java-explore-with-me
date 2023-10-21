package ru.practicum.explore.repository;

import com.fasterxml.jackson.annotation.JsonFormat;
import dto.StatDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "stats")
@Getter
@Setter
@NoArgsConstructor
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "income_date")
    private LocalDateTime dateTimeIncome;
    private String uri;
    private String ip;
    @Column(name = "app")
    private String app;

    public static Stat toStat(StatDto dto) {
        Stat stat = new Stat();
        stat.setApp(dto.getApp());
        stat.setIp(dto.getIp());
        stat.setUri(dto.getUri());
        stat.setDateTimeIncome(dto.getTimeStamp());
        return stat;
    }
}
