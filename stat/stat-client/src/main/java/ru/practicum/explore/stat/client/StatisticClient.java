package ru.practicum.explore.stat.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.StatDto;
import dto.ViewStat;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import ru.practicum.explore.stat.exception.InvalidRequestException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Component
public class StatisticClient {
    private String serverUrl;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final WebClient webClient;
    public static final int TIMEOUT = 1000;

    @Autowired
    public StatisticClient(@Value("${client.url}") String serverUrl) {
        final var tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
                });

        this.webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .defaultCookie("cookie-name", "cookie-value")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.serverUrl = serverUrl;
    }

    public List<ViewStat> getAllStatistic(String start, String end, @RequestParam List<String> uris, Boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new InvalidRequestException("Error request start after end");
            }
        }
        String response =
                webClient
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/stats/")
                                .queryParam("start", start)
                                .queryParam("end", end)
                                .queryParam("uris", uris)
                                .queryParam("unique", unique)
                                .build())
                        .retrieve()
                        .onStatus(HttpStatus::is4xxClientError,
                                error -> Mono.error(new RuntimeException("API not found")))
                        .onStatus(HttpStatus::is5xxServerError,
                                error -> Mono.error(new RuntimeException("Server is not responding")))
                        .bodyToMono(String.class)
                        .block();
        ObjectMapper mapper = new ObjectMapper();

        List<ViewStat> list;
        try {
            list = mapper.readValue(response, new TypeReference<List<ViewStat>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return list;

    }

    public StatDto create(StatDto statDto) {
        return webClient.post()
                .uri("/hit")
                .body(Mono.just(statDto), StatDto.class)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatus::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToMono(StatDto.class).block();
    }
}
