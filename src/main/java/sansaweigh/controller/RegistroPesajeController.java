package sansaweigh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sansaweigh.dto.ActualizarEstadoRequest;
import sansaweigh.dto.RegistroPesajeRequest;
import sansaweigh.dto.RegistroPesajeResponse;
import sansaweigh.service.RegistroPesajeService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pesajes")
@RequiredArgsConstructor
public class RegistroPesajeController {

    private final RegistroPesajeService service;

    @PostMapping
    public ResponseEntity<RegistroPesajeResponse> crear(@RequestBody RegistroPesajeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<RegistroPesajeResponse> actualizarEstado(
            @PathVariable String id,
            @RequestBody ActualizarEstadoRequest request) {
        return ResponseEntity.ok(service.actualizarEstado(id, request));
    }

    @GetMapping
    public ResponseEntity<List<RegistroPesajeResponse>> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(service.obtenerPorFecha(desde, hasta));
    }
}
