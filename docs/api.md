# API Reference

## Crear Registro de Pesaje
**POST** `/api/pesajes`

Body:
```json
{
  "idBalanza": "4",
  "idPaquete": "PKG-001",
  "pesoKg": 10.5
}
```

Respuesta exitosa (201):
```json
{
  "id": "abc123",
  "idBalanza": "4",
  "idPaquete": "PKG-001",
  "pesoSansas": 7.85,
  "pesoKg": 10.5,
  "categoriaPeso": "LIVIANO",
  "estado": "INGRESADO"
}
```

---

## Actualizar Estado
**PATCH** `/api/pesajes/{id}/estado`

Body:
```json
{
  "nuevoEstado": "PESADO"
}
```

Estados válidos: `INGRESADO → PESADO → APROBADO/RECHAZADO → DESPACHADO`

Errores:
- `400` si la transición no es válida
- `422` si viola una regla de negocio

---

## Obtener por Fecha
**GET** `/api/pesajes?desde=2026-01-01T00:00:00&hasta=2026-12-31T23:59:59`

Respuesta (200): lista de registros de pesaje.