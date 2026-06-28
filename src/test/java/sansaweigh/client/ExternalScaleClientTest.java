package sansaweigh.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import sansaweigh.model.ScaleSpecification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalScaleClientTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private ObjectMapper objectMapper;
    private ExternalScaleClient client;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        client = new ExternalScaleClient(redisTemplate, objectMapper);
    }

    @Test
    void recover_conCacheDisponible_retornaEspecificacion() {
        ScaleSpecification spec = new ScaleSpecification("1", "Balanza", "Brand", 100.0, 0.01, -0.05);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("scale:1")).thenReturn(spec);

        ScaleSpecification result = client.recover(new Exception("error"), "1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
    }

    @Test
    void recover_sinCacheUsaDefault_retornaEspecificacionDefault() {
        ScaleSpecification defaultSpec = new ScaleSpecification("-1", "Default", "Default", 50.0, 0.1, 0.0);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("scale:99")).thenReturn(null);
        when(valueOperations.get("scale:-1")).thenReturn(defaultSpec);

        ScaleSpecification result = client.recover(new Exception("error"), "99");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("-1");
    }

    @Test
    void recover_sinCacheNiDefault_lanzaExcepcion() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        assertThatThrownBy(() -> client.recover(new Exception("error"), "99"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }
}