package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class StatsClient extends BaseClient {
    private final String appId;

    public StatsClient(@Value("${stats-server.url}") String serverUrl,
                       RestTemplateBuilder builder, String appId) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );

        this.appId = appId;
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end,
                                           List<String> uris, boolean onlyUniqueHits) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", start,
                "uris", uris,
                "onlyUniqueHits", onlyUniqueHits
        );
        return get("/stats?start={start}&end={end}&uris={uris}$unique={onlyUniqueHits}", parameters);
    }

    public ResponseEntity<Object> addHit(EndpointHitDto hit) {
        hit.setApp(appId);
        return post("/hit", hit);
    }
}