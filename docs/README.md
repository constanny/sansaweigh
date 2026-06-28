# SansaWeigh - Sistema de Gestión de Pesaje

## Descripción
Microservicio para gestionar estaciones de pesaje de paquetes de la empresa logística SansaWeigh.

## Características
- Clasificación de paquetes por peso en unidades Sansa (1 Sansa = 1.337 kg)
- Categorías: LIVIANO (hasta 10S), MEDIANO (10-50S), PESADO (+50S)
- Máquina de estados: INGRESADO → PESADO → APROBADO/RECHAZADO → DESPACHADO
- Restricción horaria para paquetes pesados (no procesa entre 20:00-06:00)
- Regla de balanza prima: balanzas con ID primo no procesan pesados en días impares
- Caché de especificaciones de balanzas en Redis (TTL 120s)
- Fallback automático a caché cuando API externa no está disponible

## Tecnologías
- Java 21
- Spring Boot 4.x
- MongoDB
- Redis
- JUnit 5 + Mockito
- Swagger/OpenAPI 3.0

## Endpoints
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | /api/pesajes | Crear registro de pesaje |
| PATCH | /api/pesajes/{id}/estado | Actualizar estado |
| GET | /api/pesajes?desde=&hasta= | Obtener por fecha |

## Swagger UI
Disponible en: `http://localhost:8080/swagger-ui.html`