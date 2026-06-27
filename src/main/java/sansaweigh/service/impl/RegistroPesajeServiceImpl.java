package sansaweigh.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import sansaweigh.service.RegistroPesajeService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistroPesajeServiceImpl implements RegistroPesajeService {

    private static final double SANSA_A_KG = 1.337;

    private final RegistroPesajeRepository repository;
    private final ExternalScaleClient externalScaleClient;

    @Override
    public RegistroPesajeResponse crear(RegistroPesajeRequest request) {
        // 1. Convertir kg a Sansas
        double pesoSansas = request.getPesoKg() / SANSA_A_KG;

        // 2. Clasificar paquete
        CategoriaPeso categoria = clasificar(pesoSansas);

        // 3. Validar restricción horaria para paquetes pesados
        if (categoria == CategoriaPeso.PESADO) {
            validarHorario();
        }

        // 4. Validar regla de balanza prima
        validarBalanzaPrima(request.getIdBalanza(), categoria);

        // 5. Crear registro
        RegistroPesaje registro = RegistroPesaje.builder()
                .idBalanza(request.getIdBalanza())
                .idPaquete(request.getIdPaquete())
                .pesoSansas(pesoSansas)
                .categoriaPeso(categoria)
                .estado(EstadoPesaje.INGRESADO)
                .build();

        registro.getHistorialEstados().add(
                new RegistroPesaje.TransicionEstado(EstadoPesaje.INGRESADO, LocalDateTime.now())
        );

        RegistroPesaje guardado = repository.save(registro);
        return toResponse(guardado);
    }

    @Override
    public RegistroPesajeResponse actualizarEstado(String id, ActualizarEstadoRequest request) {
        RegistroPesaje registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado: " + id));

        validarTransicion(registro.getEstado(), request.getNuevoEstado());

        registro.setEstado(request.getNuevoEstado());
        registro.getHistorialEstados().add(
                new RegistroPesaje.TransicionEstado(request.getNuevoEstado(), LocalDateTime.now())
        );

        RegistroPesaje actualizado = repository.save(registro);
        return toResponse(actualizado);
    }

    @Override
    public List<RegistroPesajeResponse> obtenerPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return repository.findByCreatedAtBetween(desde, hasta)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =====================
    // Métodos privados
    // =====================

    private CategoriaPeso clasificar(double pesoSansas) {
        if (pesoSansas <= 10) return CategoriaPeso.LIVIANO;
        if (pesoSansas <= 50) return CategoriaPeso.MEDIANO;
        return CategoriaPeso.PESADO;
    }

    private void validarHorario() {
        LocalTime ahora = LocalTime.now();
        LocalTime inicio = LocalTime.of(20, 0);
        LocalTime fin = LocalTime.of(6, 0);

        boolean esNocturno = ahora.isAfter(inicio) || ahora.isBefore(fin);
        if (esNocturno) {
            throw new BusinessRuleException(
                    "No se pueden procesar paquetes PESADOS en horario nocturno (20:00 - 06:00)"
            );
        }
    }

    private void validarBalanzaPrima(String idBalanza, CategoriaPeso categoria) {
        if (categoria != CategoriaPeso.PESADO) return;

        int id;
        try {
            id = Integer.parseInt(idBalanza);
        } catch (NumberFormatException e) {
            return;
        }

        if (esPrimo(id)) {
            int diaMes = LocalDateTime.now().getDayOfMonth();
            if (diaMes % 2 != 0) {
                throw new BusinessRuleException(
                        "Balanza con ID primo no puede registrar paquetes PESADOS en días impares"
                );
            }
        }
    }

    private boolean esPrimo(int numero) {
        if (numero < 2) return false;
        for (int i = 2; i <= Math.sqrt(numero); i++) {
            if (numero % i == 0) return false;
        }
        return true;
    }

    private void validarTransicion(EstadoPesaje actual, EstadoPesaje nuevo) {
        boolean valida = switch (actual) {
            case INGRESADO -> nuevo == EstadoPesaje.PESADO;
            case PESADO -> nuevo == EstadoPesaje.APROBADO || nuevo == EstadoPesaje.RECHAZADO;
            case APROBADO, RECHAZADO -> nuevo == EstadoPesaje.DESPACHADO;
            case DESPACHADO -> false;
        };

        if (!valida) {
            throw new IllegalWeighingStateException(
                    "Transición no permitida: " + actual + " → " + nuevo
            );
        }
    }

    private RegistroPesajeResponse toResponse(RegistroPesaje r) {
        return RegistroPesajeResponse.builder()
                .id(r.getId())
                .idBalanza(r.getIdBalanza())
                .idPaquete(r.getIdPaquete())
                .pesoSansas(r.getPesoSansas())
                .pesoKg(r.getPesoSansas() * SANSA_A_KG)
                .categoriaPeso(r.getCategoriaPeso())
                .estado(r.getEstado())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}