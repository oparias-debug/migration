# Contrato CU-PRE-08 — Área de Influencia

> **⚠️ Renombrado al procesar CU-PRE-12:** `redocly join` detectó una colisión real
> de `operationId` entre este fragmento y `CU-PRE-12.openapi.yaml` — ambos usaban
> `autocompletarDesdePoblacionObjetivo` para acciones "Traer ubicación de Población
> Objetivo" sobre recursos distintos (`area-influencia` vs. `localizacion`). Se
> renombró a `autocompletarAreaInfluenciaDesdePoblacionObjetivo` en este archivo
> (y a `autocompletarLocalizacionDesdePoblacionObjetivo` en CU-PRE-12) para que
> ambos sean únicos y, de paso, más específicos sobre qué recurso completan. Si ya
> se había generado código cliente/servidor contra el nombre anterior, debe
> regenerarse.

## Endpoints generados

| Método | Path | operationId | Actor(es) | Origen |
|---|---|---|---|---|
| GET | `/proyectos/{idProyecto}/area-influencia` | `obtenerAreaInfluencia` | Técnico URP, Técnico PRE | Anexo A.1, RN01, RN02 |
| PUT | `/proyectos/{idProyecto}/area-influencia` | `guardarAreaInfluencia` | Técnico URP | FA-01, RN03-RN05, RN08 |
| POST | `/proyectos/{idProyecto}/area-influencia/autocompletado` | `autocompletarAreaInfluenciaDesdePoblacionObjetivo` | Técnico URP | RN07, FA-03 |

## Contradicción RN07 vs. RN08 — cómo se resolvió en el contrato

RN07 sugiere que los 4 campos autocompletados (Región, Departamento, Distrito, Ubicación Específica) podrían editarse después de usar "Traer ubicación de Población Objetivo"; RN08 dice explícitamente que Región/Departamento/Distrito "permanecerán bloqueados para edición". El propio CU y la BDD ya habían optado por la interpretación de RN08 (más restrictiva, consistente con el Anexo B.1). Este contrato lleva esa decisión un paso más allá, **codificándola directamente en la forma del schema**, no solo en la documentación: `AreaInfluenciaFilaRequest` (el cuerpo de `guardarAreaInfluencia`) **no tiene las propiedades `region` ni `departamento` en absoluto** — solo `distrito` y `ubicacionEspecifica`. `region`/`departamento` únicamente existen en la forma de lectura (`AreaInfluenciaFila`), siempre derivados por el servidor. Esto hace estructuralmente imposible que un cliente intente enviar `region`/`departamento`, en vez de depender de que el backend simplemente los ignore si llegaran en el body.

### Matiz que no estaba en el resumen de HU: cada fila tiene su propio Distrito, no un único trío fijo
Al revisar el mockup del Anexo A.1 con cuidado, las filas agregadas (RN03) muestran su propia celda de "Distrito (agregar — selector desplegable)", no solo una celda de "Ubicación Específica" añadida a un único Región/Departamento/Distrito fijo. Esto significa que "adicionar una fila" (RN03) agrega una fila completa con su propio distrito (constreñido, según el texto de RN03, a los distritos "registrados en CU-PRE-07"), no solo otra línea de texto libre. Se modeló `AreaInfluenciaFilaRequest` con `distrito` como parte de cada fila, no como un campo único a nivel de todo el recurso.

### El "bloqueo" de RN08 se interpretó como restricción de UI, no de servidor, para filas ya existentes
RN08 dice que Región/Departamento/Distrito "permanecerán bloqueados para edición" una vez autocompletados — pero RN03 permite agregar filas nuevas, lo cual requiere necesariamente fijar un `distrito` nuevo. Se interpretó que el bloqueo aplica a modificar una celda ya poblada en la interfaz (evitar que el usuario cambie el distrito de una fila que ya vino de "Traer ubicación"), no a impedir que el servidor reciba `distrito` al agregar una fila genuinamente nueva. Como todo el array se reemplaza en cada `PUT` (mismo criterio que el resto del contrato), esta distinción no se puede aplicar de forma diferenciada por fila a nivel de servidor de todas formas — se documentó la interpretación, sin inventar una regla de servidor que distinga "fila nueva" de "fila ya autocompletada".

## Decisiones de modelado adicionales

### `x-roles` sin "Usuarios Internos/Externos" (mismo criterio que CU-PRE-06/07)
RN01 vuelve a mencionar genéricamente "Todos los demás actores"/"Otros actores" sin nombrarlos. Mismo pendiente para el Gestor de Requisitos que en los dos fragmentos anteriores.

### Sin gating de "primer guardado" para Técnico PRE
Igual que CU-PRE-06/07: RN02 no menciona esa condición, así que no se modeló.

### Sin validación de "distrito dentro de las ubicaciones de CU-PRE-07" (RN03)
Documentada como expectativa en la descripción del campo `distrito`, no forzada como error 400 — no hay escenario BDD que verifique el rechazo.

### `autocompletarAreaInfluenciaDesdePoblacionObjetivo` sin error si Población Objetivo está vacía
Si CU-PRE-07 todavía no tiene ubicaciones registradas, el resultado es simplemente `filas: []`, no un error — no hay respaldo BDD para modelar ese caso como 404/409/400.

## Errores mapeados desde la historia BDD

| Escenario Gherkin | Endpoint | Código |
|---|---|---|
| Autocompletar la ubicación desde Población Objetivo (camino feliz) | `autocompletarAreaInfluenciaDesdePoblacionObjetivo` | 200 |
| Región, Departamento y Distrito permanecen bloqueados (RN08) | `guardarAreaInfluencia` | 200 (estructural — `region`/`departamento` no existen en el request) |
| Agregar una fila de ubicación específica (RN03) | `guardarAreaInfluencia` | 200 (parte del array `filas` del siguiente guardado) |
| Eliminar una fila de ubicación específica (RN04) | `guardarAreaInfluencia` | 200 (misma lógica) |
| Guardar la información (camino feliz) | `guardarAreaInfluencia` | 200 |
| Intentar guardar con campos pendientes de completar (RN05) | `guardarAreaInfluencia` | 200 (no es error — solo bordes rojos de cliente) |
| Avanzar a Análisis de mercado (FA-02) | *(sin endpoint — navegación de cliente, sin validación documentada)* | — |
| Consultar el área de influencia (Técnico PRE) | `obtenerAreaInfluencia` | 200 |
| — | todos | 401 — práctica REST estándar, sin escenario específico |
| — | todos | 403 — inferido de `x-roles`/RN01/RN02, sin escenario BDD negativo explícito |

## Explícitamente fuera de alcance (marcado, no inventado)

- **Contradicción RN07 vs. RN08**: resuelta a favor de RN08 en la forma del schema (ver arriba), no solo documentada — pero sigue siendo la interpretación de este análisis, no una confirmación del negocio.
- **Identificación concreta de "Todos los demás actores"/"Otros actores" (RN01)**: mismo pendiente que CU-PRE-06/07.
- **Texto del ícono de ayuda contextual "?" (RN06)**: no especificado.
- **Campos obligatorios exactos y texto del mensaje de RN05**: la tabla de Validaciones no los precisa; no se inventaron.
- **Constreñimiento de `distrito` a los ya registrados en CU-PRE-07 (RN03)**: documentado, no forzado como error.
- **Relación exacta de las postcondiciones CU-PRO-19/CU-EJE-03 con este CU**: el propio documento las lista sin verbo que aclare la relación; no se modeló ningún efecto secundario hacia esos dos CU.

## Conflictos con `<contratos_existentes>`

Ninguno. Verificado con `redocly join` de los 10 fragmentos juntos: cero colisiones de `path`/`operationId`, y `redocly lint` del conjunto fusionado solo reporta las advertencias cosméticas esperables. Este fragmento depende únicamente de `Error` en `CU-PRE-01.openapi.yaml`.
