# Swagger UI

## Acceso

La interfaz Swagger UI está disponible cuando la aplicación está corriendo:
http://localhost:8080/swagger-ui.html

## Requisitos para levantar la aplicación

1. Tener Docker instalado
2. Levantar MongoDB y Redis: docker compose up -d
3. Ejecutar la aplicación: .\mvnw spring-boot:run

## Endpoints disponibles

| Método | URL | Descripción |
|--------|-----|-------------|
| POST | /api/pesajes | Crear registro de pesaje |
| PATCH | /api/pesajes/{id}/estado | Actualizar estado |
| GET | /api/pesajes | Obtener registros por fecha |

## OpenAPI Spec

La especificación completa en formato JSON está disponible en: http://localhost:8080/api-docs

