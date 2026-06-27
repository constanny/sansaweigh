package sansaweigh.model;

import sansaweigh.model.enums.CategoriaPeso;
import sansaweigh.model.enums.EstadoPesaje;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "registros_pesaje")
public class RegistroPesaje {

    @Id
    private String id;

    private String idBalanza;
    private String idPaquete;

    private double pesoSansas;

    private CategoriaPeso categoriaPeso;

    private EstadoPesaje estado;

    @Builder.Default
    private List<TransicionEstado> historialEstados = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransicionEstado {
        private EstadoPesaje estado;
        private LocalDateTime timestamp;
    }
}
