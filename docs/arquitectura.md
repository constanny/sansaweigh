# Arquitectura

## Estructura del Proyecto

- controller/ → Endpoints REST
- service/ → Lógica de negocio
- repository/ → Acceso a MongoDB
- model/ → Entidades y enums
- dto/ → Objetos de transferencia
- client/ → Integración API externa
- exception/ → Manejo de errores
- config/ → Configuración Redis

## Capas

### Controller
Recibe las peticiones HTTP y delega al Service.

### Service
Contiene toda la lógica de negocio:
- Conversión kg a Sansas
- Clasificación de paquetes
- Validación de restricciones
- Máquina de estados

### Repository
Acceso a MongoDB mediante Spring Data.

### Client
Integración con API externa de balanzas con:
- 3 reintentos exponenciales
- Fallback a caché Redis
- TTL de 120 segundos

## Diagrama de Flujo

Request → Controller → Service → Repository (MongoDB)

Service → ExternalScaleClient → Redis Cache