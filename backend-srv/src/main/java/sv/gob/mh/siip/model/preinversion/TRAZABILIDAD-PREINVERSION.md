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
| `EtapaPreinversion` | `ETAPA_PREINVERSION` | M-02 | CU-PRE-03.5 |
| `SolicitudPreinversion` | `SOLICITUD_PREINVERSION` | M-01 | CU-PRE-01, CU-PRE-01.5, CU-PRE-02 |
| `ComentarioSolicitud` | `COMENTARIO_SOLICITUD` | M-01 | CU-PRE-01, CU-PRE-01.5 |
| `Identificacion` + `ObjetivoEspecifico` | `IDENTIFICACION` / `OBJETIVO_ESPECIFICO` | M-02 | CU-PRE-04 |
| `AlternativaSolucion` | `ALTERNATIVA_SOLUCION` | M-02 | CU-PRE-05 |
| `Interesado` | `INTERESADO` | M-02 | CU-PRE-06 |
| `Poblacion` | `POBLACION` | M-02 | CU-PRE-07 |
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
| `Viabilidad` | `VIABILIDAD` | M-06 | CU-PRE-24 (resultado de cada cierre de revisión: `OBSERVADO` al devolver, `VIABLE` al emitir) |
| `RevisionViabilidad` + `ComentarioCampoViabilidad` (embebido) | `REVISION_VIABILIDAD` / `COMENTARIO_REVISION_VIABILIDAD` | M-06 | CU-PRE-24 (una revisión por solicitud; comentarios por campo y justificación; historial de devoluciones, RN10) |
| `DocumentoViabilidad` | `DOCUMENTO_VIABILIDAD` | M-06 | CU-PRE-24 (Documento de Preinversión y otros documentos, RN02) |
| `Elegibilidad` | `ELEGIBILIDAD` | M-06 | CU-PRE-25 (una fila por emisión: la primera cambia el estado a "Proyecto elegible"; las siguientes responden a comentarios de la OT, FB2) |
| `CalificacionCriterioElegibilidad` + códigos de opción (colección) | `CALIFICACION_CRITERIO_ELEGIBILIDAD` / `CALIFICACION_ELEG_OPCION` | M-06 | CU-PRE-25 (columnas "¿Aplica?" y "Especificar" del Anexo A.1 por criterio; RN03, RN08) |
| `OpinionTecnica` | `OPINION_TECNICA` | M-06 | CU-PRE-26 (una fila por gestión: solicitud o ronda de revisión; `OBSERVADO` al enviar comentarios, `FAVORABLE` al emitir) |
| `DocumentoOpinionTecnica` | `DOCUMENTO_OPINION_TECNICA` | M-06 | CU-PRE-26 ("Nota de solicitud de OT" y "Nota de OT") |
| `ComentarioOpinionTecnica` | `COMENTARIO_OPINION_TECNICA` | M-06 | CU-PRE-26 (un comentario DGICP por apartado del Anexo A.1, documentos anexos o Elegibilidad, con su "Justificación Institución"; CU-PRE-24 RN11 y CU-PRE-25 RN15 exigen que estén respondidos) |
| `Priorizacion` | `PRIORIZACION` | M-06 | CU-PRE-26.5 (una fila por priorización completada, con año y cuatrimestre, para CU-PRE-29) |
| `PriorizacionProyecto` + `TramoCalificacionPriorizacion` (embebido ×2) | `PRIORIZACION_PROYECTO` | M-06 | CU-PRE-26.5 (una calificación por OT favorable; estado de los tramos PRE —criterios 1 a 4— y SYMP —criterio 5—, "Prioridad del proyecto" y categoría al completarse) |
| `CalificacionSubcriterioPriorizacion` | `CALIFICACION_SUBCRITERIO_PRIORIZ` | M-06 | CU-PRE-26.5 (columna "Calificación" del Anexo A.1 por subcriterio) |
| `CriterioPriorizacion`, `SubcriterioPriorizacion`, `EscalaCalificacionSubcriterio`, `RangoInterpretacionPriorizacion` | `CRITERIO_PRIORIZACION`, `SUBCRITERIO_PRIORIZACION`, `ESCALA_CALIFICACION_SUBCRITERIO`, `RANGO_INTERPRETACION_PRIORIZACION` | M-06 | CU-ADM-02 (catálogo) usado por CU-PRE-26.5 (Anexos A.1, A.2 y C) |
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
5. **Viabilidad (CU-PRE-24)**:
   - Solicitar Viabilidad pasa el proyecto a "En viabilidad" (el CU no indica el estado, Observación 4). Ese estado bloquea la formulación (CU-PRE-04 a CU-PRE-23, RN04): la regla vive en `EstadoProyecto#bloqueaFormulacion()` y la aplica `EdicionFormulacion.exigirEditable(...)` en las operaciones que modifican datos de esos servicios, con error 409 `FORMULACION_BLOQUEADA`. Si CU-PRE-25/26 necesitan bloquear la formulación en sus estados, basta con agregarlos en `bloqueaFormulacion()`.
   - La solicitud responde a una observación de la OT (RN11) cuando la última `OpinionTecnica` del proyecto quedó `OBSERVADO`. Tras emitir la Viabilidad, la ficha solo vuelve a admitir solicitud si esa OT observada, con comentarios a los campos del proyecto, es posterior a la emisión (RN03; RN14 de CU-PRE-26), o si después se inició una Actualización de OT (FA04 de CU-PRE-26).
   - Con comentarios de la OT también a los criterios de elegibilidad, la Viabilidad reemitida vuelve a pasar por Elegibilidad (`elegibilidadHabilitada`, RN14 de CU-PRE-26).
   - No se valida el "registro completo de CU-PRE-04 a CU-PRE-23" (FB1 paso 4): el CU no define qué se exige ni el mensaje.
   - Los proyectos de emergencia que CU-PRE-03.5 pasa a "En viabilidad" no abren una revisión de CU-PRE-24; queda pendiente definir cómo entran al flujo del Viabilizador.
6. **Opinión Técnica (CU-PRE-26)**:
   - La OT se solicita con el botón "Solicitar OT" y la "Nota de solicitud de OT" (RN04), no automáticamente al emitir Viabilidad/Elegibilidad (FB paso 1): se sigue el contrato. Solo se admite con la Viabilidad y la Elegibilidad emitidas y sin otra gestión abierta.
   - Cada solicitud abre una gestión (`OPINION_TECNICA`) y una solicitud de tipo `OPINION_TECNICA` en la Bandeja de Preinversión (CU-PRE-02), donde el Coordinador PRE la ve. La asignación se hace con el endpoint de CU-PRE-26; asignar desde la bandeja de CU-PRE-02 no actualiza la gestión. Asignada la gestión, solo ese Técnico PRE la revisa, comenta y emite (RN07 b); el Coordinador PRE conserva el acceso. Sin asignación, el primer Técnico PRE que envía comentarios o emite queda como responsable.
   - Cada devolución deja su gestión `OBSERVADO` con sus comentarios; la siguiente revisión abre otra gestión, que ya no es "la primera" (RN 12) y no muestra "Comentarios Elegibilidad". Así queda el historial de devoluciones (RN15); la pantalla muestra cuántas hubo (`numeroDevoluciones`, cuenta también las gestiones archivadas). Una gestión archivada por vencimiento no cuenta como "la primera": la siguiente vuelve a mostrar "Comentarios Elegibilidad".
   - RN14: los comentarios a los apartados o documentos reabren la Viabilidad; los comentarios a Elegibilidad reabren CU-PRE-25. FA03 paso 3.2 dice que se habilitan siempre ambas pantallas; se siguió RN14.
   - El Técnico URP responde los comentarios a los apartados y documentos (RN03) y el Viabilizador los de la Elegibilidad (Anexo B.1 "Respuesta Institución"), mientras no la reemita. Así se resolvió la contradicción RN03 vs. Anexo B.1 (ítem 8), que dejaba al Viabilizador dependiendo del Técnico URP en el tercer caso de RN14. Cada validación de "comentarios sin responder" cuenta solo su sección: los del proyecto para volver a solicitar Viabilidad (RN11 de CU-PRE-24) y los de Elegibilidad para reemitirla (RN15 de CU-PRE-25). **[Validar con negocio]**
   - "Enviar ajustes" no tiene endpoint: el Técnico URP envía los ajustes al dar clic en "Solicitar Viabilidad" de CU-PRE-24 (FA03.1), que registra la "Fecha de ajustes" y envía el correo A2 d al Técnico PRE y al Coordinador PRE (no al Viabilizador, pendiente RN07 d vs. Anexo A2 d).
   - RN08/RN09: días hábiles de lunes a viernes, sin feriados (los lineamientos no están incluidos). El job corre a las 06:30 de El Salvador y evalúa cada gestión en su propia transacción: si una falla, las demás conservan su advertencia o su archivo. Al vencer el plazo se archivan la gestión y su solicitud de la bandeja, y el proyecto vuelve a "En Formulación" para solicitar otra vez la Viabilidad. No se borran la Viabilidad ni sus documentos, que el CU dice "eliminar" **[Validar con negocio]**. Si la OT solo comentó la Elegibilidad y el Viabilizador ya la reemitió, el plazo no vence.
   - La reemisión de la Elegibilidad deja el proyecto en "Proyecto elegible" (FA03.1 paso 3.1.3; CU-PRE-25 no lo definía).
   - La OT se emite sobre la etapa actual (queda con `tieneOpinionTecnica`) y habilita la etapa futura. RN11 ("OT para la etapa de Ejecución") se evalúa con la etapa futura, como el texto del correo A2 e.
   - Actualización de OT (FA04): solo el Técnico URP, como el contrato (FA04 vs. Anexo A – RN3, ítem 48). La hoja "Campos a habilitar para Actualización de O.T." no está incluida: se devuelven las pantallas de formulación que revisa la OT.
   - La "Nota de OT" y el "N° de nota de OT" son obligatorios al dar clic en "OT favorable" (FA01 paso 1.5; Anexo B.1 "Editable: Sí", ítem 15). La "Fecha de emisión de OT" es la del clic. La emisión guarda también la "Inversión estimada" del momento para el Histórico (Anexo A1.5).
   - El Informe OT (Anexo A.6) toma los datos de la gestión de la emisión y los del proyecto vigentes: el Sistema no guarda una copia del proyecto al emitir. "Tamaño", "Convenio" y "Costo del estudio" no tienen un origen registrado todavía y se devuelven nulos.
   - Los correos a los Viabilizadores van al que revisó la Viabilidad del proyecto (todos los activos si ninguno la revisó). Con comentarios a la Elegibilidad, el Viabilizador recibe solo el correo A2 h y la institución el A2 c.
   - La columna "Apartados" solo muestra texto para la Identificación (CU-PRE-04) y la ficha de emergencia (RN13); el resto de apartados muestra el enlace a su pantalla (RN16).
7. **Priorización (CU-PRE-26.5)**:
   - La priorización se abre con la última OT `FAVORABLE` del proyecto; sin ella responde 409 `FILTRO_HABILITANTE_INCOMPLETO`. Cada OT favorable tiene su propia calificación ("cada vez que se emita Opinión Técnica").
   - Cada tramo sigue el ciclo pendiente → enviado a revisión → revisado. El Técnico edita mientras está pendiente o con ajustes habilitados por su Coordinador (RN05, RN10, RN11). El Técnico SYMP califica el criterio 5 una vez que el Coordinador PRE revisó los criterios 1 a 4 (FB1 Técnico PRE paso 8).
   - La priorización se completa cuando ambos tramos están revisados: se guardan la "Prioridad del proyecto" y su categoría, se registra una fila en `PRIORIZACION` para el Banco de Proyectos y se notifica al Técnico PRE y al Coordinador PRE. Si se habilitan ajustes y se vuelve a revisar, se recalcula y se agrega otra fila.
   - RN13 no dice cómo se reparte la ponderación de un subcriterio "N/A": se reparte en proporción a las ponderaciones de los demás del criterio. RN14 (criterio completo en "N/A") se reparte en partes iguales, como dice el CU, también cuando hay varios criterios en "N/A".
   - Un puntaje decimal entre dos rangos (p. ej. 84.5) cae en el rango cuyo mínimo no supera el puntaje (el inferior).
   - El resultado del Anexo A.2 solo se devuelve con la priorización completa: queda abierto qué mostrar tras revisar solo los criterios 1 a 4 (Observaciones ítem 41). Tampoco se resolvió si el criterio 5 es obligatorio (ítem 40): se exige, como RN07 y el mensaje A.3.
   - La priorización no cambia el estado del proyecto (el CU no lo indica).
   - Cualquier Técnico PRE/SYMP activo puede calificar, y se notifica a todos los Coordinadores del tramo: no existe todavía la asignación de un "encargado del caso".
   - El catálogo de los CSV de `data/seed` (solo perfil dev) es el de la versión "Criterios de Priorización_Agosto-2" del CU; la DGI debe confirmar la versión vigente. La escala y los rangos se consultan con los endpoints de CU-ADM-02, que CU-PRE-26.5 no duplica.
8. Los roles de usuario (`RolUsuario`) se derivaron de los actores mencionados en los 32 CU (Técnico URP, Técnico PRE, Coordinador PRE, Viabilizador, etc.) — deben contrastarse contra el catálogo real de roles/permisos del sistema (Anexo C, no incluido como archivo separado).

## Pendiente para siguiente iteración

Módulos aún no procesados (a definir orden de continuación):
- **M-09, M-10, M-11 — Programación** (PRIPME, Escenarios, PAIP): 16 CU (`PRO`)
- **M-12 — Ejecución y Seguimiento**: 9 CU (`EJE`)
- **M-13 — Operación y Mantenimiento**: 1 CU (`OYM`)
- **M-14 — Convenios de Financiamiento**: 3 CU (`MPD`)
- **M-00 / M-15 — Administración e Interfaces**: sin CU documentado, solo mencionados en WBS
