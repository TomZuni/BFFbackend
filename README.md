# RutaExpress — Backend for Frontend (BFF)

**Curso:** Desarrollo Backend III (PBY2203) — Duoc UC
**Actividad:** Semana 4 — Analizando el patrón arquitectónico con Backend for Frontend (BFF)

## 1. Objetivo del proyecto

RutaExpress administra cuentas con saldo prepago para el envío de paquetes.
Antes de esta actividad, un único backend ("legacy") exponía siempre el
dato completo de cada cuenta y sus movimientos, sin importar qué cliente
consumía la información. Esto generaba los problemas típicos de un backend
compartido por múltiples frontends: datos innecesarios para clientes
livianos, acoplamiento entre canales y una lógica de adaptación que crecía
sin control dentro del backend central.

Este proyecto aplica el patrón **Backend for Frontend (BFF)** para resolver
ese problema: cada canal (Web, Móvil, Cajero) tiene su propio backend
intermedio, que consume el backend central y adapta la respuesta según sus
propias necesidades.

## 2. Análisis de la estrategia de implementación elegida

Estrategia elegida: **BFF independiente por cliente** (un microservicio
Spring Boot por canal, cada uno con su propio código, sus propios DTOs y su
propio ciclo de vida), en lugar de:

- Un **BFF único parametrizado** que decida con `if/switch` qué campos
  devolver según el canal — esto habría vuelto a acoplar los tres canales
  en un solo componente y reintroducido el problema que el patrón busca
  evitar.
- Una **librería compartida** de DTOs/mapeo entre BFFs — habría creado un
  acoplamiento de despliegue entre canales que en la práctica evolucionan
  a ritmos distintos (ej. Cajero cambia mucho menos que Web).

Con BFF independiente por cliente:

- Cada canal evoluciona sin afectar a los demás.
- Cada BFF expone solo lo que su cliente necesita.
- El backend central (`ms-clientes`) no conoce la existencia de los BFFs
  ni de sus reglas de adaptación: solo expone el dato completo, tal como
  lo haría un sistema legacy.

## 3. Arquitectura

```
Web        -> bff-web    (8091) -\
Móvil      -> bff-movil  (8092) --> ms-clientes (8090, backend central)
Cajero     -> bff-cajero (8093) -/
```

| Módulo | Puerto | Rol |
| --- | --- | --- |
| `ms-clientes` | 8090 | Backend legacy central: expone el dato completo de cuentas y movimientos, sin personalización. Persiste en H2 en memoria. |
| `bff-web` | 8091 | Vista rica: todos los campos + historial completo de movimientos + resumen calculado (totales por tipo de movimiento). Pensado para una interfaz de escritorio compleja. |
| `bff-movil` | 8092 | Vista liviana: solo campos esenciales + los 3 últimos movimientos, para minimizar el consumo de datos de la app móvil. |
| `bff-cajero` | 8093 | Vista mínima y segura: consulta de saldo, últimos 3 movimientos básicos (solo fecha y monto) y retiro de saldo. Es el recorte más agresivo de los tres, acorde a una pantalla de autoservicio. |

Cada BFF define su **propia copia** de los DTOs "de entrada" (paquete
`dtos.core`), reflejando el contrato de `ms-clientes`. Esto es intencional
en la estrategia de backends independientes: ningún BFF comparte módulos
de código con el backend central ni con los otros BFFs.

## 4. Personalización por cliente (resumen)

| Dato | BFF Web | BFF Móvil | BFF Cajero |
| --- | --- | --- | --- |
| Datos de cuenta | Completos (nombre, saldo, tipo, antigüedad) | Esenciales (id, nombre, saldo) | Mínimos (id, saldo) |
| Movimientos | Historial completo + resumen calculado | Últimos 3 (fecha, monto, tipo) | Últimos 3 (solo fecha y monto) |
| Operaciones | Consulta | Consulta | Consulta de saldo, movimientos y **retiro de saldo** |

## 5. Autenticación y autorización por canal

Cada BFF valida una **API key propia del canal** en el header
`X-Canal-Key`, simulando que cada canal tiene su propio mecanismo de
acceso hacia su BFF (sin afectar el acceso de los demás canales):

| Canal | Header | Valor esperado (ver `application.properties` de cada módulo) |
| --- | --- | --- |
| Web | `X-Canal-Key` | `web-2026-key` |
| Móvil | `X-Canal-Key` | `movil-2026-key` |
| Cajero | `X-Canal-Key` | `cajero-2026-key` |

Una solicitud sin el header correcto recibe `401 Unauthorized`. Este es un
mecanismo simplificado para la actividad; en un entorno productivo se
reemplazaría por OAuth2/JWT u otro esquema de identidad por canal.

`ms-clientes` no valida esta key: solo los BFFs la exigen a sus propios
clientes, tal como corresponde al patrón (el backend central no conoce a
los canales finales).

## 6. Cómo ejecutar el proyecto

Requisitos: JDK 17+ y Maven.

Levantar los módulos **en este orden** (cada uno en una terminal distinta,
desde su propia carpeta):

```bash
cd ms-clientes  && mvn spring-boot:run   # puerto 8090 — levantar primero
cd bff-web      && mvn spring-boot:run   # puerto 8091
cd bff-movil    && mvn spring-boot:run   # puerto 8092
cd bff-cajero   && mvn spring-boot:run   # puerto 8093
```

`ms-clientes` carga automáticamente datos de ejemplo (`data/*.csv`) al
iniciar, con cuentas de id `201` a `208`.

## 7. Endpoints y ejemplos de prueba

Todos los ejemplos usan la cuenta `201`. Reemplaza el valor del header
`X-Canal-Key` según el canal que estés probando (ver sección 5).

### BFF Web (`http://localhost:8091`)
```bash
curl -H "X-Canal-Key: web-2026-key" http://localhost:8091/bff/web/cuentas
curl -H "X-Canal-Key: web-2026-key" http://localhost:8091/bff/web/cuentas/201
```

### BFF Móvil (`http://localhost:8092`)
```bash
curl -H "X-Canal-Key: movil-2026-key" http://localhost:8092/bff/movil/cuentas
curl -H "X-Canal-Key: movil-2026-key" http://localhost:8092/bff/movil/cuentas/201
```

### BFF Cajero (`http://localhost:8093`)
```bash
curl -H "X-Canal-Key: cajero-2026-key" http://localhost:8093/bff/cajero/cuentas/201/saldo
curl -H "X-Canal-Key: cajero-2026-key" http://localhost:8093/bff/cajero/cuentas/201/movimientos

curl -X POST -H "X-Canal-Key: cajero-2026-key" -H "Content-Type: application/json" \
     -d '{"monto": 5000}' \
     http://localhost:8093/bff/cajero/cuentas/201/retiros
```

### Casos de error a probar (para la evidencia)
```bash
# Sin header -> 401
curl -i http://localhost:8093/bff/cajero/cuentas/201/saldo

# Cuenta inexistente -> 404
curl -H "X-Canal-Key: cajero-2026-key" http://localhost:8093/bff/cajero/cuentas/999/saldo

# Retiro mayor al saldo disponible -> 422
curl -X POST -H "X-Canal-Key: cajero-2026-key" -H "Content-Type: application/json" \
     -d '{"monto": 999999999}' \
     http://localhost:8093/bff/cajero/cuentas/201/retiros
```


## 8. Estructura del proyecto

```
Semana4-BFF-RutaExpress/
├── README.md
├── ms-clientes/   # backend central (legacy)
├── bff-web/       # BFF para el canal Web
├── bff-movil/     # BFF para el canal Móvil
└── bff-cajero/    # BFF para el canal Cajero (autoservicio)
```
