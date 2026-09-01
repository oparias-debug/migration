# Trazabilidad — Módulo Operación y Mantenimiento (SIIP)

Generado a partir de: `project_siip_v3.xlsx` (WBS) + `use_cases-unificado.md`.
Módulo procesado: **Operación y Mantenimiento (WBS M-13, 1 caso de uso: CU-OYM-01)**.
Depende de `Proyecto` y `Usuario` (módulo Preinversión) y de `CierreProyecto` (módulo Ejecución).

## Tabla de trazabilidad

| Entidad JPA | Tabla Oracle | Caso de uso de origen |
|---|---|---|
| `ProyectoFinalizadoEvaluacion` | `PROYECTO_FINALIZADO_EVALUACION` | CU-OYM-01 |
| `DocumentoEvaluacionExPost` | `DOCUMENTO_EVALUACION_EXPOST` | CU-OYM-01 |

## Supuestos explícitos [SUPUESTO — pendientes de validación]

1. **"Fecha de Finalización" y "Tiempo transcurrido"** (campos mostrados en la pantalla de CU-OYM-01) **no se duplican** — se calculan en la capa de servicio/consulta a partir de `CierreProyecto.fechaCierre` (módulo Ejecución y Seguimiento, CU-EJE-07), ya que el propio documento marca ambos campos como "no editables" y de cálculo automático.
2. **RN04** (precarga de proyectos con ≥3 años desde el cierre) y **RN07** (mensaje si no hay proyectos en el año seleccionado) son reglas de consulta/filtrado, no requieren columnas adicionales.
3. **RN08** (bloqueo del checkbox de selección salvo autorización DGICP) se modeló con los campos `usuarioExcepcion`, `fechaExcepcion` y `motivoExcepcion` en `ProyectoFinalizadoEvaluacion`. El documento aclara que la autorización corresponde al nivel **DGICP** (jerárquicamente superior a "Jefe DGI"/"Subjefe DGI"), pero el catálogo de roles (`RolUsuario`, módulo Preinversión) no distingue ese nivel — **pendiente agregar un rol o mecanismo de autorización específico para DGICP** si se requiere validarlo a nivel de base de datos en vez de solo a nivel de aplicación.

## Pendiente para siguiente iteración

- **M-14 — Convenios de Financiamiento**: 3 CU (`MPD`) — resolverá los supuestos de `Convenio` pendientes desde el módulo Programación
- **M-00 / M-15 — Administración e Interfaces**: sin CU documentado
