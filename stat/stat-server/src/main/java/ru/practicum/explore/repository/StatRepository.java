package ru.practicum.explore.repository;

import dto.RequestStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Integer>, JpaSpecificationExecutor<Stat> {

    @Query("select app, uri, COUNT(distinct ip) AS hits " +
            "from Stat " +
            "where (dateTimeIncome BETWEEN :start AND :end) AND uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT(DISTINCT ip) DESC")
    List<RequestStat> getAllStatistic(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("select app, uri, COUNT(id) AS hits " +
            "from Stat " +
            "where (dateTimeIncome BETWEEN :start AND :end) AND uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT(id) DESC")
    List<RequestStat> getAllStatisticNonUnique(LocalDateTime start, LocalDateTime end, String[] uris);
}
