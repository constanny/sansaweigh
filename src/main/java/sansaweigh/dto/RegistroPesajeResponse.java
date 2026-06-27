package sansaweigh.dto;

import sansaweigh.model.enums.CategoriaPeso;
import sansaweigh.model.enums.EstadoPesaje;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroPesajeResponse {

    private String id;
    private String idBalanza;
    private String idPaquete;
    private double pesoSansas;
    private double pesoKg;
    private CategoriaPeso categoriaPeso;
    private EstadoPesaje estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}