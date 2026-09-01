# Trazabilidad — Módulo Preinversión (SIIP)

Generado a partir de: `project_siip_v3.xlsx` (WBS) + `use_cases-unificado.md` (61 casos de uso).
Módulo procesado: **Preinversión (WBS M-01 a M-08, 32 casos de uso, prefijo `PRE`)**.

## Tabla de trazabilidad

| Entidad JPA | Tabla Oracle | Módulo(s) WBS | Caso(s) de uso de origen |
|---|---|---|---|
| `Institucion` | `INSTITUCION` | M-00 (catálogo transversal) | CU-PRE-01 (catálogo referenciado) |
| `UnidadEjecutora` | `UNIDAD_EJECUTORA` | M-00 (catálogo transversal) | CU-PRE-01 (catálogo referenciado) |
| `Usuario` | `USUARIO` | M-00 (catálogo transversal) | Todos (actor principal/secundario) |
| `Departamento` / `Municipio` | `DEPARTAMENTO` / `MUNICIPIO` | M-00 (catálogo transversal) | CU-PRE-08, CU-PRE-12 |
| `FuenteFinanciamiento` | `FUENTE_FINANCIAMIENTO` | M-00 (catálogo transversal) | CU-PRE-17 |
| `Proyecto` | `PROYECTO` | M-01, M-02 | CU-PRE-01, CU-PRE-01.5, CU-PRE-02, CU-PRE-03 |
| `EtapaPreinversion` | `ETAPA_PREINVERSION` | M-02 | CU-PRE-3.5 |
| `SolicitudPreinversion` | `SOLICITUD_PREINVERSION` | M-01 | CU-PRE-01, CU-PRE-01.5, CU-PRE-02 |
| `ComentarioSolicitud` | `COMENTARIO_SOLICITUD` | M-01 | CU-PRE-01, CU-PRE-01.5 |
| `Identificacion` + `ObjetivoEspecifico` | `IDENTIFICACION` / `OBJETIVO_ESPECIFICO` | M-02 | CU-PRE-04 |
| `AlternativaSolucion` | `ALTERNATIVA_SOLUCION` | M-02 | CU-PRE-05 |
| `Interesado` | `INTERESADO` | M-02 | CU-PRE-06 |
| `PoblacionObjetivo` | `POBLACION_OBJETIVO` | M-02 | CU-PRE-07 |
| `AreaInfluencia` | `AREA_INFLUENCIA` | M-02 | CU-PRE-08 |
| `AnalisisMercado` | `ANALISIS_MERCADO` | M-02 | CU-PRE-09 |
| `DescripcionTecnica` | `DESCRIPCION_TECNICA` | M-03 | CU-PRE-11 |
| `Localizacion` | `LOCALIZACION` | M-03 | CU-PRE-12 |
| `AnalisisAmbiental` | `ANALISIS_AMBIENTAL` | M-03 | CU-PRE-14 |
| `AnalisisRiesgo` | `ANALISIS_RIESGO` | M-03 | CU-PRE-15 |
| `AnalisisLegal` | `ANALISIS_LEGAL` | M-03 | CU-PRE-16 |
| `Componente` | `COMPONENTE` | M-04 | CU-PRE-17 |
| `PresupuestoInversion` | `PRESUPUESTO_INVERSION` | M-04 | CU-PRE-17 |
| `FlujoCostoOM` | `FLUJO_COSTO_OM` | M-04 | CU-PRE-18 |
| `FlujoBeneficio` | `FLUJO_BENEFICIO` | M-05 | CU-PRE-20 |
| `FlujoCajaIndicador` | `FLUJO_CAJA_INDICADOR` | M-05 | CU-PRE-21 |
| `FlujoCajaFinanciero` | `FLUJO_CAJA_FINANCIERO` | M-05 | CU-PRE-21.5 |
| `ProgramacionFinPreinversion` | `PROGRAMACION_FIN_PREINVERSION` | M-05 | CU-PRE-22.1 |
| `IndicadorEvaluacion` | `INDICADOR_EVALUACION` | M-05 | CU-PRE-21, CU-PRE-23 |
| `Viabilidad` | `VIABILIDAD` | M-06 | CU-PRE-24 |
| `Elegibilidad` | `ELEGIBILIDAD` | M-06 | CU-PRE-25 |
| `OpinionTecnica` | `OPINION_TECNICA` | M-06 | CU-PRE-26 |
| `Priorizacion` | `PRIORIZACION` | M-06 | CU-PRE-26.5 |
| `VW_BANCO_PROYECTOS` (vista, sin entidad JPA propia) | vista sobre `PROYECTO` | M-07 | CU-PRE-29 **[SUPUESTO — validar]** |
| `ProgCuatrimestralFinanciera` | `PROG_CUATRIMESTRAL_FINANCIERA` | M-08 | CU-PRE-30 |
| `ProgCuatrimestralMetaFisica` | `PROG_CUATRIMESTRAL_META_FISICA` | M-08 | CU-PRE-31 |
| `AvanceFinancieroCuatrimestral` | `AVANCE_FINANCIERO_CUATRIMESTRAL` | M-08 | CU-PRE-32 |
| `AvanceCuatriMetaFisica` | `AVANCE_CUATRI_META_FISICA` | M-08 | CU-PRE-33 |

## Supuestos explícitos [SUPUESTO — pendientes de validación]

1. **Banco de Proyectos (CU-PRE-29)** se modeló como una **vista** (`VW_BANCO_PROYECTOS`) sobre `PROYECTO`, no como tabla propia, porque el caso de uso no describe campos/atributos nuevos, solo listado y filtros sobre proyectos ya viables/elegibles. Si en la realidad se requiere guardar historial de ingreso al banco (fecha, motivo, usuario que lo incorpora), se necesita una tabla `BANCO_PROYECTOS` adicional.
2. **Opinión Técnica (CU-PRE-26)** se vinculó a un `Proyecto` existente. El documento menciona una pantalla "Definición del proyecto" propia de este CU — si en la práctica permite crear proyectos *sin pasar por CU-PRE-01* (solo para Opinión Técnica, sin CUP), se debe revisar si `Proyecto.cup` debe manejarse como verdaderamente opcional (ya está nullable) y si se requieren reglas adicionales de validación a nivel de servicio.
3. **Localización (CU-PRE-12) y Área de Influencia (CU-PRE-08)** se modelaron como colecciones 1:N independientes (pueden abarcar varios municipios/departamentos). Si en la práctica un proyecto tiene una única ubicación puntual, se puede simplificar a relación 1:1.
4. **Auditoría**: las entidades transversales (`Institucion`, `UnidadEjecutora`, `Usuario`) y `Proyecto` heredan de `Auditable` (Spring Data JPA Auditing). El resto de entidades hijas de `Proyecto` (formulación, estudios, financiero) usan campos de auditoría mínimos o ninguno, asumiendo que su ciclo de vida sigue al de `Proyecto`. Confirmar si se requiere auditoría completa en todas.
5. Los roles de usuario (`RolUsuario`) se derivaron de los actores mencionados en los 32 CU (Técnico URP, Técnico PRE, Coordinador PRE, Viabilizador, etc.) — deben contrastarse contra el catálogo real de roles/permisos del sistema (Anexo C, no incluido como archivo separado).

## Pendiente para siguiente iteración

Módulos aún no procesados (a definir orden de continuación):
- **M-09, M-10, M-11 — Programación** (PRIPME, Escenarios, PAIP): 16 CU (`PRO`)
- **M-12 — Ejecución y Seguimiento**: 9 CU (`EJE`)
- **M-13 — Operación y Mantenimiento**: 1 CU (`OYM`)
- **M-14 — Convenios de Financiamiento**: 3 CU (`MPD`)
- **M-00 / M-15 — Administración e Interfaces**: sin CU documentado, solo mencionados en WBS
