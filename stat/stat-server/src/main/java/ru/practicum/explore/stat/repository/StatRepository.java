package ru.practicum.explore.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Integer>, CrudRepository<Stat, Integer> {

    @Query(value = "select app, uri, COUNT(distinct ip) as hits  " +
            "from Stat " +
            "where (dateTimeIncome BETWEEN :start AND :end AND (uri IN :uris OR :uris IS NULL))  " +
            "GROUP BY app, uri " +
            "ORDER BY hits")
    List<Object[]> getAllStatistic(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select ST.app AS app, ST.uri As uri, COUNT(ST.id) AS hits " +
            " from STATS  AS ST " +
            " where (ST.INCOME_DATE BETWEEN :start AND :end) AND (ST.uri IN :uris OR :uris IS NULL) " +
            " GROUP BY app, uri " +
            "ORDER BY hits", nativeQuery = true)
    List<Object[]> getAllStatisticNonUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                            @Param("uris") List<String> uris);
}

