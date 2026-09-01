# Trazabilidad — Módulo Ejecución y Seguimiento del PAIP (SIIP)

Generado a partir de: `project_siip_v3.xlsx` (WBS) + `use_cases-unificado.md`.
Módulo procesado: **Ejecución y Seguimiento del PAIP (WBS M-12, 9 casos de uso, prefijo `EJE`)**.
Depende de entidades de Preinversión (`Proyecto`, `Institucion`, `Usuario`, `Departamento`, `Municipio`)
y de Programación (`Producto`, `IndicadorProducto`, `PeriodoProgramacionPaip`, `PeriodoProgramacionPap`).

## Tabla de trazabilidad

| Entidad JPA | Tabla Oracle | Caso de uso de origen |
|---|---|---|
| `EjecucionFinancieraMensual` | `EJECUCION_FINANCIERA_MENSUAL` | CU-EJE-01 |
| `ObservacionEjecucionFinanciera` | `OBSERVACION_EJECUCION_FINANCIERA` | CU-EJE-01 |
| `AvanceFisicoMensual` | `AVANCE_FISICO_MENSUAL` | CU-EJE-02 |
| `EjecucionFinancieraUbicacion` | `EJECUCION_FINANCIERA_UBICACION` | CU-EJE-03 |
| `ProcesoAdministrativo` | `PROCESO_ADMINISTRATIVO` | CU-EJE-04 |
| `ContratoProcesoAdministrativo` | `CONTRATO_PROCESO_ADMINISTRATIVO` | CU-EJE-04 |
| `RevisionEjecucionPaip` | `REVISION_EJECUCION_PAIP` | CU-EJE-05 |
| `InformeVisitaCampo` | `INFORME_VISITA_CAMPO` | CU-EJE-06 |
| `AcompananteVisitaCampo` | `ACOMPANANTE_VISITA_CAMPO` | CU-EJE-06 |
| `ArchivoAdjuntoVisitaCampo` | `ARCHIVO_ADJUNTO_VISITA_CAMPO` | CU-EJE-06 |
| `CierreProyecto` | `CIERRE_PROYECTO` | CU-EJE-07 |
| `DocumentoRespaldoCierre` | `DOCUMENTO_RESPALDO_CIERRE` | CU-EJE-07 |
| `HistorialCierre` | `HISTORIAL_CIERRE` | CU-EJE-07 |
| `RevisionAvanceCuatriPap` | `REVISION_AVANCE_CUATRI_PAP` | CU-EJE-10 |
| `SeguimientoMensualEstatus` | `SEGUIMIENTO_MENSUAL_ESTATUS` | CU-EJE-11 |

## Supuestos explícitos [SUPUESTO — pendientes de validación]

1. **CU-EJE-11 (Seguimiento Mensual de Estatus)**: los bloques "Avance Financiero" y "Avance Físico" que muestra la pantalla se cargan automáticamente desde `EjecucionFinancieraMensual` (CU-EJE-01) y `AvanceFisicoMensual` (CU-EJE-02) — **no se duplican** en `SeguimientoMensualEstatus`, que solo persiste el campo editable `Estatus`. Si el consolidado necesita historizarse de forma independiente (por ejemplo, si el avance financiero pudiera cambiar retroactivamente sin que el estatus lo refleje), se necesitaría una tabla de snapshot adicional.
2. **CU-EJE-10 vs CU-PRO-25**: ambos monitorean el PAP pero en momentos distintos del ciclo — CU-PRO-25 sobre la *programación* cuatrimestral (ya modelado como `RevisionTecnicaPap` en el módulo Programación) y CU-EJE-10 sobre el *avance/ejecución* cuatrimestral. Se crearon como **entidades separadas** (`RevisionTecnicaPap` vs `RevisionAvanceCuatriPap`) para no mezclar ambos estados; si en la práctica es la misma tabla con una columna adicional de "etapa", se puede consolidar.
3. **Calendario de Eventos / CU-ADM-04**: varios CU (EJE-01, EJE-10) referencian un "Calendario de Eventos del PAIP" gestionado en CU-ADM-04, que pertenece al módulo M-00 (Administración) — **no documentado como caso de uso propio** en el corpus recibido. Los períodos de apertura/cierre se modelaron de forma autónoma en `PeriodoProgramacionPaip`/`PeriodoProgramacionPap` (módulo Programación); cuando se procese M-00 se debe evaluar si conviene una tabla `CALENDARIO_EVENTO` transversal en su lugar.
4. **Mensajes de correo de cierre de período** (mencionados en CU-EJE-05, CU-EJE-10) y **"Respaldo/Histórico de información"** no se modelaron como tablas — se asume que son acciones de servicio (envío de correo, snapshot) sin necesidad de persistencia relacional propia, salvo que se requiera trazabilidad de envíos.
5. **`ProcesoAdministrativo.estado`** se dejó como `VARCHAR2` libre (no enum) porque el documento no especifica una lista cerrada de estados — a diferir hasta tener el catálogo real de estados de procesos de adquisición (probablemente proveniente de un sistema externo de compras públicas, no de SIIP).

## Pendiente para siguiente iteración

- **M-13 — Operación y Mantenimiento**: 1 CU (`OYM`)
- **M-14 — Convenios de Financiamiento**: 3 CU (`MPD`) — resolverá los supuestos de Convenio pendientes desde el módulo Programación
- **M-00 / M-15 — Administración e Interfaces**: sin CU documentado (incluye el "Calendario de Eventos" de CU-ADM-04 referenciado aquí)
