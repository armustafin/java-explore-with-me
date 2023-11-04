package ru.practikum.explore;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import ru.practicum.explore.stat.client.StatisticClient;


@SpringBootApplication
public class MainService {
    public static void main(String[] args) {
        SpringApplication.run(MainService.class, args);
        StatisticClient statisticClient = loadStatisticClient();
    }

    @Bean
    static StatisticClient loadStatisticClient() {
        return new StatisticClient("http://localhost:9090", new RestTemplateBuilder());
    }
}
