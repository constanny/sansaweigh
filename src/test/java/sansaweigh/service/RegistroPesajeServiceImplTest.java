package sansaweigh.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sansaweigh.client.ExternalScaleClient;
import sansaweigh.dto.ActualizarEstadoRequest;
import sansaweigh.dto.RegistroPesajeRequest;
import sansaweigh.dto.RegistroPesajeResponse;
import sansaweigh.exception.BusinessRuleException;
import sansaweigh.exception.IllegalWeighingStateException;
import sansaweigh.model.RegistroPesaje;
import sansaweigh.model.enums.CategoriaPeso;
import sansaweigh.model.enums.EstadoPesaje;
import sansaweigh.repository.RegistroPesajeRepository;
import sansaweigh.service.impl.RegistroPesajeServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroPesajeServiceImplTest {

    @Mock
    private RegistroPesajeRepository repository;

    @Mock
    private ExternalScaleClient externalScaleClient;

    @InjectMocks
    private RegistroPesajeServiceImpl service;

    private RegistroPesaje registroBase;

    @BeforeEach
    void setUp() {
        registroBase = RegistroPesaje.builder()
                .id("1")
                .idBalanza("4")
                .idPaquete("PKG-001")
                .pesoSansas(5.0)
                .categoriaPeso(CategoriaPeso.LIVIANO)
                .estado(EstadoPesaje.INGRESADO)
                .build();
    }

    // =====================
    // Tests de clasificación
    // =====================

    @Test
    void crear_paqueteLiviano_retornaCategorialLiviano() {
        RegistroPesajeRequest request = new RegistroPesajeRequest("4", "PKG-001", 5.0);
        when(repository.save(any())).thenReturn(registroBase);

        RegistroPesajeResponse response = service.crear(request);

        assertThat(response.getCategoriaPeso()).isEqualTo(CategoriaPeso.LIVIANO);
    }

    @Test
    void crear_paqueteMediano_retornaCategoriaMediano() {
        RegistroPesaje registroMediano = RegistroPesaje.builder()
                .id("2").idBalanza("4").idPaquete("PKG-002")
                .pesoSansas(20.0).categoriaPeso(CategoriaPeso.MEDIANO)
                .estado(EstadoPesaje.INGRESADO).build();

        RegistroPesajeRequest request = new RegistroPesajeRequest("4", "PKG-002", 20.0 * 1.337);
        when(repository.save(any())).thenReturn(registroMediano);

        RegistroPesajeResponse response = service.crear(request);

        assertThat(response.getCategoriaPeso()).isEqualTo(CategoriaPeso.MEDIANO);
    }

    // =====================
    // Tests de transición de estados
    // =====================

    @Test
    void actualizarEstado_deIngresadoAPesado_exitoso() {
        ActualizarEstadoRequest request = new ActualizarEstadoRequest(EstadoPesaje.PESADO);
        RegistroPesaje actualizado = RegistroPesaje.builder()
                .id("1").idBalanza("4").idPaquete("PKG-001")
                .pesoSansas(5.0).categoriaPeso(CategoriaPeso.LIVIANO)
                .estado(EstadoPesaje.PESADO).build();

        when(repository.findById("1")).thenReturn(Optional.of(registroBase));
        when(repository.save(any())).thenReturn(actualizado);

        RegistroPesajeResponse response = service.actualizarEstado("1", request);

        assertThat(response.getEstado()).isEqualTo(EstadoPesaje.PESADO);
    }

    @Test
    void actualizarEstado_transicionInvalida_lanzaExcepcion() {
        ActualizarEstadoRequest request = new ActualizarEstadoRequest(EstadoPesaje.APROBADO);
        when(repository.findById("1")).thenReturn(Optional.of(registroBase));

        assertThatThrownBy(() -> service.actualizarEstado("1", request))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    @Test
    void actualizarEstado_dePesadoAAprobado_exitoso() {
        registroBase.setEstado(EstadoPesaje.PESADO);
        ActualizarEstadoRequest request = new ActualizarEstadoRequest(EstadoPesaje.APROBADO);
        RegistroPesaje actualizado = RegistroPesaje.builder()
                .id("1").idBalanza("4").idPaquete("PKG-001")
                .pesoSansas(5.0).categoriaPeso(CategoriaPeso.LIVIANO)
                .estado(EstadoPesaje.APROBADO).build();

        when(repository.findById("1")).thenReturn(Optional.of(registroBase));
        when(repository.save(any())).thenReturn(actualizado);

        RegistroPesajeResponse response = service.actualizarEstado("1", request);

        assertThat(response.getEstado()).isEqualTo(EstadoPesaje.APROBADO);
    }

    @Test
    void actualizarEstado_dePesadoARechazado_exitoso() {
        registroBase.setEstado(EstadoPesaje.PESADO);
        ActualizarEstadoRequest request = new ActualizarEstadoRequest(EstadoPesaje.RECHAZADO);
        RegistroPesaje actualizado = RegistroPesaje.builder()
                .id("1").idBalanza("4").idPaquete("PKG-001")
                .pesoSansas(5.0).categoriaPeso(CategoriaPeso.LIVIANO)
                .estado(EstadoPesaje.RECHAZADO).build();

        when(repository.findById("1")).thenReturn(Optional.of(registroBase));
        when(repository.save(any())).thenReturn(actualizado);

        RegistroPesajeResponse response = service.actualizarEstado("1", request);

        assertThat(response.getEstado()).isEqualTo(EstadoPesaje.RECHAZADO);
    }

    @Test
    void actualizarEstado_registroNoExiste_lanzaExcepcion() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizarEstado("999", new ActualizarEstadoRequest(EstadoPesaje.PESADO)))
                .isInstanceOf(RuntimeException.class);
    }

    // =====================
    // Tests de obtenerPorFecha
    // =====================

    @Test
    void obtenerPorFecha_retornaLista() {
        LocalDateTime desde = LocalDateTime.now().minusDays(1);
        LocalDateTime hasta = LocalDateTime.now();

        when(repository.findByCreatedAtBetween(desde, hasta)).thenReturn(List.of(registroBase));

        List<RegistroPesajeResponse> resultado = service.obtenerPorFecha(desde, hasta);

        assertThat(resultado).hasSize(1);
    }
    @Test
    void crear_paquetePesado_balanzaNoPrima_exitoso() {
        // Balanza ID 4 no es primo, no aplica restricción
        RegistroPesaje registroPesado = RegistroPesaje.builder()
                .id("3").idBalanza("4").idPaquete("PKG-003")
                .pesoSansas(60.0).categoriaPeso(CategoriaPeso.PESADO)
                .estado(EstadoPesaje.INGRESADO).build();

        RegistroPesajeRequest request = new RegistroPesajeRequest("4", "PKG-003", 60.0 * 1.337);
        when(repository.save(any())).thenReturn(registroPesado);

        RegistroPesajeResponse response = service.crear(request);

        assertThat(response.getCategoriaPeso()).isEqualTo(CategoriaPeso.PESADO);
    }
    @Test
    void actualizarEstado_deAprobadoADespachado_exitoso() {
        registroBase.setEstado(EstadoPesaje.APROBADO);
        ActualizarEstadoRequest request = new ActualizarEstadoRequest(EstadoPesaje.DESPACHADO);
        RegistroPesaje actualizado = RegistroPesaje.builder()
                .id("1").idBalanza("4").idPaquete("PKG-001")
                .pesoSansas(5.0).categoriaPeso(CategoriaPeso.LIVIANO)
                .estado(EstadoPesaje.DESPACHADO).build();

        when(repository.findById("1")).thenReturn(Optional.of(registroBase));
        when(repository.save(any())).thenReturn(actualizado);

        RegistroPesajeResponse response = service.actualizarEstado("1", request);
        assertThat(response.getEstado()).isEqualTo(EstadoPesaje.DESPACHADO);
    }

    @Test
    void actualizarEstado_deDespachadoACualquier_lanzaExcepcion() {
        registroBase.setEstado(EstadoPesaje.DESPACHADO);
        ActualizarEstadoRequest request = new ActualizarEstadoRequest(EstadoPesaje.APROBADO);
        when(repository.findById("1")).thenReturn(Optional.of(registroBase));

        assertThatThrownBy(() -> service.actualizarEstado("1", request))
                .isInstanceOf(IllegalWeighingStateException.class);
    }

    @Test
    void actualizarEstado_deRechazadoADespachado_exitoso() {
        registroBase.setEstado(EstadoPesaje.RECHAZADO);
        ActualizarEstadoRequest request = new ActualizarEstadoRequest(EstadoPesaje.DESPACHADO);
        RegistroPesaje actualizado = RegistroPesaje.builder()
                .id("1").idBalanza("4").idPaquete("PKG-001")
                .pesoSansas(5.0).categoriaPeso(CategoriaPeso.LIVIANO)
                .estado(EstadoPesaje.DESPACHADO).build();

        when(repository.findById("1")).thenReturn(Optional.of(registroBase));
        when(repository.save(any())).thenReturn(actualizado);

        RegistroPesajeResponse response = service.actualizarEstado("1", request);
        assertThat(response.getEstado()).isEqualTo(EstadoPesaje.DESPACHADO);
    }
}