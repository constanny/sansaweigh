package sansaweigh.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sansaweigh.dto.ActualizarEstadoRequest;
import sansaweigh.dto.RegistroPesajeRequest;
import sansaweigh.dto.RegistroPesajeResponse;
import sansaweigh.model.enums.CategoriaPeso;
import sansaweigh.model.enums.EstadoPesaje;
import sansaweigh.service.RegistroPesajeService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroPesajeControllerTest {

    @Mock
    private RegistroPesajeService service;

    @InjectMocks
    private RegistroPesajeController controller;

    private RegistroPesajeResponse responseBase;

    @BeforeEach
    void setUp() {
        responseBase = RegistroPesajeResponse.builder()
                .id("1")
                .idBalanza("4")
                .idPaquete("PKG-001")
                .pesoSansas(5.0)
                .pesoKg(6.685)
                .categoriaPeso(CategoriaPeso.LIVIANO)
                .estado(EstadoPesaje.INGRESADO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void crear_retorna201ConResponse() {
        RegistroPesajeRequest request = new RegistroPesajeRequest("4", "PKG-001", 5.0);
        when(service.crear(any())).thenReturn(responseBase);

        ResponseEntity<RegistroPesajeResponse> response = controller.crear(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getIdPaquete()).isEqualTo("PKG-001");
    }

    @Test
    void actualizarEstado_retorna200ConResponse() {
        ActualizarEstadoRequest request = new ActualizarEstadoRequest(EstadoPesaje.PESADO);
        RegistroPesajeResponse pesado = RegistroPesajeResponse.builder()
                .id("1").idBalanza("4").idPaquete("PKG-001")
                .pesoSansas(5.0).pesoKg(6.685)
                .categoriaPeso(CategoriaPeso.LIVIANO)
                .estado(EstadoPesaje.PESADO)
                .build();

        when(service.actualizarEstado(eq("1"), any())).thenReturn(pesado);

        ResponseEntity<RegistroPesajeResponse> response = controller.actualizarEstado("1", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getEstado()).isEqualTo(EstadoPesaje.PESADO);
    }

    @Test
    void obtenerPorFecha_retornaLista() {
        LocalDateTime desde = LocalDateTime.now().minusDays(1);
        LocalDateTime hasta = LocalDateTime.now();

        when(service.obtenerPorFecha(any(), any())).thenReturn(List.of(responseBase));

        ResponseEntity<List<RegistroPesajeResponse>> response = controller.obtenerPorFecha(desde, hasta);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }
}