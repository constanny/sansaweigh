package sansaweigh.service;

import sansaweigh.dto.ActualizarEstadoRequest;
import sansaweigh.dto.RegistroPesajeRequest;
import sansaweigh.dto.RegistroPesajeResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroPesajeService {

    RegistroPesajeResponse crear(RegistroPesajeRequest request);

    RegistroPesajeResponse actualizarEstado(String id, ActualizarEstadoRequest request);

    List<RegistroPesajeResponse> obtenerPorFecha(LocalDateTime desde, LocalDateTime hasta);
}