# Banco XYZ — Backend for Frontend (BFF)

**Curso:** Desarrollo Backend III (PBY2203) — Duoc UC
**Actividad:** Semana 5 — Implementando el patrón arquitectónico Backend for Frontend (BFF)

## 1. Objetivo del proyecto

Banco XYZ necesita optimizar la comunicación entre distintos frontends (Web,
Móvil, Cajeros Automáticos) y sus sistemas legacy. Antes de esta actividad,
un backend central expone siempre el dato completo de clientes, cuentas,
tarjetas y movimientos, sin importar qué canal lo consume. Esto obliga a
cada cliente a filtrar/transformar información que no necesita, acopla a
los tres canales a un mismo contrato de datos y hace más lento y pesado
cada intercambio (especialmente crítico para el canal móvil y para
operaciones críticas de cajero).

Este proyecto aplica el patrón **Backend for Frontend (BFF)**: cada canal
tiene su propio backend intermedio, que integra los servicios legacy y
adapta la respuesta según sus propias necesidades.

Los datos utilizados siguen la estructura típica de un core bancario legacy
(clientes, cuentas, tarjetas, movimientos), inspirada en el repositorio de
referencia [`KariVillagran/bank_legacy_data`](https://github.com/KariVillagran/bank_legacy_data).
Los CSV de ejemplo (`src/main/resources/data/*.csv` de `ms-clientes` y
`ms-tarjetas`) pueden reemplazarse por los datos oficiales del repositorio
sin tocar código, siempre que se respete el orden de columnas indicado más
abajo.

## 2. Análisis de la estrategia de implementación elegida

Estrategia elegida: **BFF independiente por canal**, con **dos backends
centrales independientes** (`ms-clientes` y `ms-tarjetas`) a los que cada
BFF debe consultar y agregar información, en lugar de:

- **Un BFF único parametrizado** (`if`/`switch` por canal): habría vuelto
  a acoplar los tres canales en un solo componente, el problema exacto que
  el patrón busca evitar.
- **Un solo backend central con todos los datos**: en un core bancario
  real, clientes/cuentas y medios de pago (tarjetas) suelen vivir en
  sistemas distintos con ciclos de vida propios. Separarlos en dos
  servicios (`ms-clientes`, `ms-tarjetas`) obliga a que cada BFF resuelva
  una **integración real**, en vez de simplemente reenviar la respuesta de
  un único origen.
- **Una librería compartida de DTOs/mapeo entre BFFs**: crearía
  acoplamiento de despliegue entre canales que evolucionan a ritmos
  distintos (el canal Cajero cambia mucho menos que el canal Web).

Con esta estrategia:

- Cada canal evoluciona sin afectar a los demás (código, DTOs y ciclo de
  despliegue propios).
- Cada BFF expone solo lo que su cliente necesita, con el nivel de
  detalle/seguridad adecuado a ese canal.
- Los backends centrales no conocen la existencia de los BFF ni de sus
  reglas de adaptación: solo exponen el dato completo, tal como lo haría
  un sistema legacy.

## 3. Arquitectura

```
                     ┌──────────────────────┐
                     │   ms-clientes (8090)  │  clientes, cuentas, movimientos, retiros
                     └──────────┬────────────┘
                                │
   Web    → bff-web    (8091) ─┤
   Móvil  → bff-movil  (8092) ─┼──── agregan info de AMBOS backends
   Cajero → bff-cajero (8093) ─┤
                                │
                     ┌──────────┴────────────┐
                     │   ms-tarjetas (8095)   │  tarjetas por cuenta
                     └───────────────────────┘
```

| Módulo | Puerto | Rol |
| --- | --- | --- |
| `ms-clientes` | 8090 | Backend legacy central #1: clientes, cuentas y movimientos. Expone el dato completo, sin personalización. H2 en memoria. |
| `ms-tarjetas` | 8095 | Backend legacy central #2: tarjetas asociadas a cada cuenta. Independiente de `ms-clientes` (distinto ciclo de vida). H2 en memoria. |
| `bff-web` | 8091 | Vista rica: cliente + todas sus cuentas + todas sus tarjetas + historial completo de movimientos + resumen calculado. Agrega `ms-clientes` + `ms-tarjetas`. |
| `bff-movil` | 8092 | Vista liviana: nombre corto, saldo total, cuentas y tarjetas con datos enmascarados, últimos 5 movimientos sin descripción. Agrega `ms-clientes` + `ms-tarjetas`. |
| `bff-cajero` | 8093 | Interfaz mínima y segura: opera **siempre por número de tarjeta** (como un cajero real). Valida vigencia en `ms-tarjetas` antes de tocar la cuenta en `ms-clientes`. Sin datos personales. |

Cada BFF define su **propia copia** de los DTOs "de entrada" (paquete
`dtos.core`), reflejando el contrato de los backends centrales. Ningún BFF
comparte módulos de código con los backends ni con los otros BFFs.

## 4. Personalización y agregación por canal

| Dato | BFF Web | BFF Móvil | BFF Cajero |
| --- | --- | --- | --- |
| Identificación | Por `clienteId` | Por `clienteId` | Por **número de tarjeta** |
| Datos de cliente | Completos (rut, nombre, correo, teléfono) | Solo nombre corto | No se exponen |
| Cuentas | Todas, con todos los campos | Todas, con número enmascarado | No aplica (solo la cuenta de la tarjeta usada) |
| Tarjetas | Todas, número completo | Todas, número enmascarado, sin fecha de vencimiento | Solo la propia (enmascarada) |
| Movimientos | Historial completo + resumen calculado (depósitos/retiros/pagos) | Últimos 5, sin descripción | Últimos 3, solo fecha y monto |
| Operaciones | Consulta | Consulta | Consulta de saldo, movimientos y **retiro** |
| Servicios integrados | `ms-clientes` + `ms-tarjetas` | `ms-clientes` + `ms-tarjetas` | `ms-tarjetas` (validación) + `ms-clientes` (saldo/retiro) |

### Optimización de respuestas

Cada BFF recorta campos y cantidad de datos según su canal:

- **Web** entrega el historial completo de movimientos más un resumen
  calculado (`totalDepositos`, `totalRetiros`, `totalPagos`, `saldoNeto`),
  evitando que el cliente tenga que recalcular esos totales.
- **Móvil** limita los movimientos a los últimos 5, quita la descripción y
  enmascara números de cuenta/tarjeta, reduciendo notoriamente el tamaño
  del payload frente a Web.
- **Cajero** es el más agresivo: 3 movimientos, sin descripción, sin datos
  personales, sin exponer nunca el número de tarjeta completo.

## 5. Autenticación por canal

Cada BFF valida una **API key propia del canal** en el header
`X-Canal-Key`, simulando que cada canal tiene su propio mecanismo de
acceso hacia su BFF, sin afectar el acceso de los demás:

| Canal | Header | Valor esperado |
| --- | --- | --- |
| Web | `X-Canal-Key` | `web-2026-key` |
| Móvil | `X-Canal-Key` | `movil-2026-key` |
| Cajero | `X-Canal-Key` | `cajero-2026-key` |

Una solicitud sin el header correcto recibe `401 Unauthorized`. Es un
mecanismo simplificado para la actividad; en producción se reemplazaría
por OAuth2/JWT u otro esquema de identidad por canal. Los backends
centrales (`ms-clientes`, `ms-tarjetas`) no validan esta key: solo los BFF
la exigen a sus propios clientes.

## 6. Cómo ejecutar el proyecto

Requisitos: JDK 17+ y Maven.

Levantar los módulos **en este orden** (cada uno en una terminal distinta,
desde su propia carpeta). Los dos backends centrales deben estar arriba
antes de levantar cualquier BFF:

```bash
cd ms-clientes  && mvn spring-boot:run   # puerto 8090
cd ms-tarjetas  && mvn spring-boot:run   # puerto 8095
cd bff-web      && mvn spring-boot:run   # puerto 8091
cd bff-movil    && mvn spring-boot:run   # puerto 8092
cd bff-cajero   && mvn spring-boot:run   # puerto 8093
```

Ambos backends cargan automáticamente datos de ejemplo
(`data/*.csv`) al iniciar. Clientes de prueba: `501` a `505`; cuentas:
`701` a `706`; tarjetas de ejemplo (usar como `numeroTarjeta`):
`4551011122223701` ... `4551011122223706` (ver tabla completa en
`ms-tarjetas/src/main/resources/data/tarjetas.csv`; la tarjeta `...3704`
está `BLOQUEADA` a propósito, para poder evidenciar el error 403).

## 7. Endpoints y ejemplos de prueba

### BFF Web (`http://localhost:8091`)
```bash
curl -H "X-Canal-Key: web-2026-key" http://localhost:8091/bff/web/clientes/501/dashboard
curl -H "X-Canal-Key: web-2026-key" http://localhost:8091/bff/web/cuentas/701/movimientos
```

### BFF Móvil (`http://localhost:8092`)
```bash
curl -H "X-Canal-Key: movil-2026-key" http://localhost:8092/bff/movil/clientes/501/resumen
curl -H "X-Canal-Key: movil-2026-key" http://localhost:8092/bff/movil/cuentas/701/movimientos
```

### BFF Cajero (`http://localhost:8093`)
```bash
curl -H "X-Canal-Key: cajero-2026-key" http://localhost:8093/bff/cajero/tarjetas/4551011122223701/saldo
curl -H "X-Canal-Key: cajero-2026-key" http://localhost:8093/bff/cajero/tarjetas/4551011122223701/movimientos

curl -X POST -H "X-Canal-Key: cajero-2026-key" -H "Content-Type: application/json" \
     -d '{"monto": 50000}' \
     http://localhost:8093/bff/cajero/tarjetas/4551011122223701/retiros
```

### Casos de error a probar (para la evidencia)
```bash
# Sin header -> 401
curl -i http://localhost:8093/bff/cajero/tarjetas/4551011122223701/saldo

# Tarjeta bloqueada -> 403
curl -H "X-Canal-Key: cajero-2026-key" http://localhost:8093/bff/cajero/tarjetas/4551011122223704/saldo

# Tarjeta inexistente -> 404
curl -H "X-Canal-Key: cajero-2026-key" http://localhost:8093/bff/cajero/tarjetas/0000000000000000/saldo

# Cliente inexistente -> 404
curl -H "X-Canal-Key: web-2026-key" http://localhost:8091/bff/web/clientes/999/dashboard

# Retiro mayor al saldo disponible -> 422
curl -X POST -H "X-Canal-Key: cajero-2026-key" -H "Content-Type: application/json" \
     -d '{"monto": 999999999}' \
     http://localhost:8093/bff/cajero/tarjetas/4551011122223701/retiros

# Monto invalido (<= 0) -> 400
curl -X POST -H "X-Canal-Key: cajero-2026-key" -H "Content-Type: application/json" \
     -d '{"monto": 0}' \
     http://localhost:8093/bff/cajero/tarjetas/4551011122223701/retiros
```

Guarda las capturas de pantalla o salidas de consola de estas ejecuciones
como evidencia de ejecución (ver sección 10).

## 8. Estructura del proyecto

```
Banco-XYZ-BFF/
├── README.md
├── ms-clientes/   # backend central: clientes, cuentas, movimientos, retiros
├── ms-tarjetas/   # backend central: tarjetas
├── bff-web/       # BFF para el canal Web
├── bff-movil/     # BFF para el canal Móvil
└── bff-cajero/    # BFF para el canal Cajero (autoservicio)
```

Cada módulo Maven es independiente (propio `pom.xml`, sin proyecto padre
reactor), de forma que cada canal pueda construirse, versionarse y
desplegarse por separado.

## 9. Organización del código y extensibilidad

Dentro de cada BFF, la responsabilidad está separada en capas:

- `controllers`: solo exponen endpoints HTTP y delegan al `service`.
- `services`: la lógica de personalización (qué integrar, cómo
  transformar, qué recortar) vive aquí.
- `clients`: encapsulan las llamadas HTTP a cada backend central.
- `dtos` / `dtos.core`: contrato propio del canal vs. contrato "de
  entrada" que refleja lo que expone cada backend.
- `security`: filtro de autenticación propio del canal.
- `exceptions`: mapeadas a códigos HTTP con `@ResponseStatus`.

**Para agregar un nuevo canal** (por ejemplo, un BFF para *smart
watches*) no es necesario tocar ni `ms-clientes`, ni `ms-tarjetas`, ni los
BFF existentes: basta con crear un nuevo módulo `bff-<canal>` que copie
esta misma estructura de carpetas, defina sus propios DTOs (con los
campos que ese canal realmente necesita) y agregue las llamadas que
correspondan a los `clients` de los backends centrales.

## 10. Entrega

- **Código fuente:** este repositorio/carpeta comprimida.
- **Documentación:** este `README.md`.
- **Evidencia de ejecución:** capturas de pantalla o salidas de consola de
  cada endpoint listado en la sección 7, incluyendo los casos de error.

Comprime la carpeta completa (código + README + evidencias) con la
nomenclatura solicitada: `ExpX_Sx_nombre completo` (ej.
`Exp2_S5_José_Gonzalez_Alvarado`).
