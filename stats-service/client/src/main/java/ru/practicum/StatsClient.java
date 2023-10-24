package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

@Component
public class StatsClient extends BaseClient {
    private final String applicationName;

    public StatsClient(@Value("${stats-server.url}") String serverUrl,
                       RestTemplateBuilder builder, @Value("${spring.application.name}") String applicationName) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
        this.applicationName = applicationName;
    }

    public ResponseEntity<Object> getStats(String start, String end,
                                           List<String> uris, boolean onlyUniqueHits) {

        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "onlyUniqueHits", onlyUniqueHits
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={onlyUniqueHits}", parameters);
    }

    public ResponseEntity<Object> addHit(EndpointHitDto hit) {
        hit.setApp(applicationName);
        return post("/hit", hit);
    }
}