# Configuración del Entorno

## Requisitos Previos
- Java 21+
- Maven 3.9+
- MongoDB 6+
- Redis 7+

## Variables de Configuración

En `src/main/resources/application.properties`:

```properties
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/sansaweigh

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# API Externa
external.scale.api.url=http://localhost:9090/api/scales

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
```

## Cómo Ejecutar

1. Clonar el repositorio: git clone https://github.com/constanny/sansaweigh.git
2. Entrar a la carpeta: cd sansaweigh
3. Ejecutar: .\mvnw spring-boot:run
4. Acceder a Swagger UI: http://localhost:8080/swagger-ui.html
5. Ejecutar Tests: .\mvnw test