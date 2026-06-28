package sansaweigh.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sansaweigh.exception.BusinessRuleException;
import sansaweigh.exception.GlobalExceptionHandler;
import sansaweigh.exception.IllegalWeighingStateException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalState_retorna400() {
        IllegalWeighingStateException ex = new IllegalWeighingStateException("transición inválida");
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalState(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("error");
    }

    @Test
    void handleBusinessRule_retorna422() {
        BusinessRuleException ex = new BusinessRuleException("regla violada");
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessRule(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).containsKey("error");
    }

    @Test
    void handleGeneric_retorna500() {
        Exception ex = new Exception("error genérico");
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsKey("error");
    }
}