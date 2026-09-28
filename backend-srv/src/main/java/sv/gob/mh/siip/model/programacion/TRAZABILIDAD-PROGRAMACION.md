# Trazabilidad — Módulo Programación (SIIP)

Generado a partir de: `project_siip_v3.xlsx` (WBS) + `use_cases-unificado.md`.
Módulo procesado: **Programación (WBS M-09, M-10, M-11, 16 casos de uso, prefijo `PRO`)**.
Depende de las entidades transversales y de `Proyecto`/`EtapaPreinversion` generadas en el módulo Preinversión.

## Tabla de trazabilidad

| Entidad JPA | Tabla Oracle | Módulo WBS | Caso(s) de uso de origen |
|---|---|---|---|
| `MacroSector` | `MACROSECTOR` | M-09 (catálogo) | CU-PRO-05 |
| `SectorActividad` | `SECTOR_ACTIVIDAD` | M-09 (catálogo) | CU-PRO-06 |
| `DimensionPriorizacion` | `DIMENSION_PRIORIZACION` | M-10 (catálogo) | CU-PRO-08 |
| `PeriodoProgramacionPripme` | `PERIODO_PROGRAMACION_PRIPME` | M-09 | CU-PRO-07 |
| `Pripme` | `PRIPME` | M-09 | CU-PRO-01, CU-PRO-02 |
| `ClasificacionFinanciamiento` | `CLASIFICACION_FINANCIAMIENTO` | M-09 | CU-PRO-01 |
| `FinanciamientoPripme` | `FINANCIAMIENTO_PRIPME` | M-09 | CU-PRO-01 |
| `ObservacionPripme` | `OBSERVACION_PRIPME` | M-09 | CU-PRO-02 |
| `VW_CONSOLIDADO_PRIPME_INSTITUCION` (vista) | vista sobre `PRIPME`/`FINANCIAMIENTO_PRIPME` | M-09 | CU-PRO-03 **[SUPUESTO]** |
| `VW_CONSOLIDADO_PRIPME_FUENTE` (vista) | vista sobre `FINANCIAMIENTO_PRIPME` | M-09 | CU-PRO-04 **[SUPUESTO]** |
| — (pendiente) | — | M-09 | CU-PRO-05, CU-PRO-06 **[SUPUESTO — requiere normalizar Macrosector/Sector en Institución]** |
| `EscenarioCortoPlazo` | `ESCENARIO_CORTO_PLAZO` | M-10 | CU-PRO-08, CU-PRO-09 |
| `ProyectoEscenario` | `PROYECTO_ESCENARIO` | M-10 | CU-PRO-08, CU-PRO-09 |
| `CalificacionProyectoEscenario` | `CALIFICACION_PROYECTO_ESCENARIO` | M-10 | CU-PRO-08 |
| `FuenteFinanciamientoEscenario` | `FUENTE_FINANCIAMIENTO_ESCENARIO` | M-10 | CU-PRO-09 |
| `ObservacionEscenario` | `OBSERVACION_ESCENARIO` | M-10 | CU-PRO-09 |
| `ContrapropuestaInstitucional` | `CONTRAPROPUESTA_INSTITUCIONAL` | M-10 | CU-PRO-11 |
| `ComparativoEscenario` | `COMPARATIVO_ESCENARIO` | M-10 | CU-PRO-09, CU-PRO-11 |
| `RegistroAprobacionInstancia` | `REGISTRO_APROBACION_INSTANCIA` | M-10 | CU-PRO-11 |
| `HistoricoAutorizacionEscenario` | `HISTORICO_AUTORIZACION_ESCENARIO` | M-10 | CU-PRO-10 |
| `PeriodoProgramacionPaip` | `PERIODO_PROGRAMACION_PAIP` | M-11 | CU-PRO-21 |
| `ProgramacionFinancieraPaip` | `PROGRAMACION_FINANCIERA_PAIP` | M-11 | CU-PRO-17 |
| `ProvisionFinanciera` | `PROVISION_FINANCIERA` | M-11 | CU-PRO-17 |
| `ModificacionPresupuestaria` | `MODIFICACION_PRESUPUESTARIA` | M-11 | CU-PRO-17 |
| `Producto` | `PRODUCTO` | M-11 | CU-PRO-18 |
| `IndicadorProducto` | `INDICADOR_PRODUCTO` | M-11 | CU-PRO-18 |
| `ProgramacionFisicaMensual` | `PROGRAMACION_FISICA_MENSUAL` | M-11 | CU-PRO-18 |
| `DistribucionFinUbicacion` | `DISTRIBUCION_FIN_UBICACION` | M-11 | CU-PRO-19 |
| `RevisionTecnicaPaip` | `REVISION_TECNICA_PAIP` | M-11 | CU-PRO-21 |
| `PeriodoProgramacionPap` | `PERIODO_PROGRAMACION_PAP` | M-08 (monitorea CU-PRE-30/31) | CU-PRO-25 |
| `RevisionTecnicaPap` | `REVISION_TECNICA_PAP` | M-08 | CU-PRO-25 |

## Supuestos explícitos [SUPUESTO — pendientes de validación]

1. **CU-PRO-03, 04, 05, 06 (consolidados PRIPME)** se modelaron como **vistas Oracle**, no tablas, ya que son agregaciones de `FINANCIAMIENTO_PRIPME` por institución/fuente/macrosector/sector. Solo se implementaron las vistas por Institución y por Fuente; las de Macrosector y Sector de Actividad **no se pudieron completar** porque `INSTITUCION` guarda `MACROSECTOR`/`SECTOR_ACTIVIDAD` como texto libre (columnas `VARCHAR2` del módulo Preinversión), no como FK a los nuevos catálogos `MACROSECTOR`/`SECTOR_ACTIVIDAD` de este módulo. **Se requiere decidir**: (a) normalizar `INSTITUCION` para que referencie estos catálogos por FK, o (b) mantener el join por coincidencia de texto (frágil, no recomendado).
2. **Convenio**: tanto `FINANCIAMIENTO_PRIPME.ID_CONVENIO` como `PROGRAMACION_FINANCIERA_PAIP.ID_CONVENIO` se dejaron como columnas simples sin FK, en espera del módulo M-14 (Convenios). **[RESUELTO]** Al procesar M-14 se agregaron las FK reales (`FK_FIN_PRIPME_CONVENIO`, `FK_PROG_FIN_PAIP_CONVENIO`) y se actualizaron `FinanciamientoPripme.java` y `ProgramacionFinancieraPaip.java` para usar `@ManyToOne Convenio` en vez de un `Long idConvenio` suelto. Ver `siip-convenios/sql/schema_convenios.sql`, sección 5.
3. **Reportes** (Reporte PRIPME, Reporte Consolidado, Reporte Institucional del PAIP/PAP, mensajes de correo de cierre de período) **no se modelaron como tablas** — se asumen generados on-demand por el backend (PDF/Excel) a partir de las tablas transaccionales, no como entidades persistentes. Si se requiere guardar un histórico de "reportes generados" (quién, cuándo, qué filtros), se necesitaría una tabla adicional `REPORTE_GENERADO`.
4. **`RevisionTecnicaPaip.estado` y `RevisionTecnicaPap.estado`** reutilizan el enum `EstadoRevisionPripme` (`REVISADO`/`SIN_REVISAR`) por tener el mismo dominio de valores. Si en el futuro divergen, conviene separarlos en enums propios.
5. **`ComparativoEscenario`** se modeló solo del lado de `ContrapropuestaInstitucional` (CU-PRO-11). El comparativo interno de CU-PRO-09 (escenario ajustado vs. banco de proyectos) se asume cubierto por el campo `ProyectoEscenario.clasificacion` (Nuevo/No propuesto/Arrastre) sin necesitar tabla adicional.
6. **Histórico/Respaldo de programación del PAIP** mencionado en CU-PRO-21 ("Respaldo/Histórico de programación") no se modeló como tabla explícita — se asume que Oracle Flashback / auditoría a nivel de aplicación cubre este requisito, o que se requiere una tabla de snapshot adicional a definir con el equipo.

## Pendiente para siguiente iteración

- **M-12 — Ejecución y Seguimiento del PAIP**: 9 CU (`EJE`)
- **M-13 — Operación y Mantenimiento**: 1 CU (`OYM`)
- **M-14 — Convenios de Financiamiento**: 3 CU (`MPD`) — resolverá los supuestos de Convenio pendientes en este módulo
- **M-00 / M-15 — Administración e Interfaces**: sin CU documentado
