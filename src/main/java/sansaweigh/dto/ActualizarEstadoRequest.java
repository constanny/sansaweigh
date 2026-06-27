package sansaweigh.dto;

import sansaweigh.model.enums.EstadoPesaje;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoRequest {
    private EstadoPesaje nuevoEstado;
}