package sansaweigh.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import sansaweigh.model.ScaleSpecification;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

@Component
public class ExternalScaleClient {

    private static final String CACHE_PREFIX = "scale:";
    private static final String DEFAULT_SCALE_KEY = "scale:-1";
    private static final long TTL_SECONDS = 120;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${external.scale.api.url}")
    private String apiUrl;

    public ExternalScaleClient(RedisTemplate<String, Object> redisTemplate,
                               ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2)
    )
    public ScaleSpecification getScaleSpecifications(String scaleId) throws Exception {
        String url = apiUrl + "/" + scaleId;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ScaleSpecification spec = objectMapper.readValue(response.body(), ScaleSpecification.class);

        redisTemplate.opsForValue().set(CACHE_PREFIX + scaleId, spec, TTL_SECONDS, TimeUnit.SECONDS);

        return spec;
    }

    @Recover
    public ScaleSpecification recover(Exception e, String scaleId) {
        Object cached = redisTemplate.opsForValue().get(CACHE_PREFIX + scaleId);
        if (cached != null) {
            return objectMapper.convertValue(cached, ScaleSpecification.class);
        }

        Object defaultSpec = redisTemplate.opsForValue().get(DEFAULT_SCALE_KEY);
        if (defaultSpec != null) {
            return objectMapper.convertValue(defaultSpec, ScaleSpecification.class);
        }

        throw new RuntimeException("No se pudo obtener especificación de balanza: " + scaleId);
    }
}