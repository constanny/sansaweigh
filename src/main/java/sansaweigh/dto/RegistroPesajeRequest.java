package sansaweigh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroPesajeRequest {

    private String idBalanza;
    private String idPaquete;

    /** Peso en kilogramos — el sistema convierte internamente a Sansas */
    private double pesoKg;
}