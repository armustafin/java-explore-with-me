package ru.practikum.explore;


import dto.StatDto;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.explore.stat.client.StatisticClient;

import java.time.LocalDateTime;

public class Main {
	public static void main(String[] args) {
		RestTemplateBuilder builder = new RestTemplateBuilder();
		StatisticClient statisticClient = new StatisticClient("http://localhost:9090",
				builder);
		StatDto statDto = new StatDto();
		statDto.setApp("app");
		statDto.setIp("1.1.12.00");
		statDto.setUri("http://localhost:9");
		statDto.setTimeStamp(LocalDateTime.now());
		statisticClient.create(statDto);
	}
}
