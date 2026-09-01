# Trazabilidad — Módulo Convenios de Financiamiento (SIIP)

Generado a partir de: `project_siip_v3.xlsx` (WBS) + `use_cases-unificado.md`.
Módulo procesado: **Convenios de Financiamiento (WBS M-14, 3 casos de uso, prefijo `MPD`)**.
Depende de `Institucion` y `Proyecto` (módulo Preinversión).

Este módulo también **cierra el supuesto pendiente** del módulo Programación: las columnas
`ID_CONVENIO` de `FINANCIAMIENTO_PRIPME` y `PROGRAMACION_FINANCIERA_PAIP` ahora tienen FK real
hacia `CONVENIO` (ver sección 5 de `schema_convenios.sql`), y las entidades JPA correspondientes
se actualizaron para usar `@ManyToOne Convenio` en vez de un `Long idConvenio` suelto.

## Tabla de trazabilidad

| Entidad JPA | Tabla Oracle | Caso de uso de origen |
|---|---|---|
| `OrganismoAcreedor` | `ORGANISMO_ACREEDOR` | CU-MPD-01 (catálogo) |
| `Convenio` | `CONVENIO` | CU-MPD-01 |
| `InstitucionCoejecutora` | `INSTITUCION_COEJECUTORA` | CU-MPD-01 |
| `CondicionPrevia` | `CONDICION_PREVIA` | CU-MPD-01, CU-MPD-04 |
| `ComponenteConvenio` | `COMPONENTE_CONVENIO` | CU-MPD-01 |
| `DocumentoAdjuntoConvenio` | `DOCUMENTO_ADJUNTO_CONVENIO` | CU-MPD-01 |
| `AjusteConvenio` | `AJUSTE_CONVENIO` | CU-MPD-03 |
| `ComponenteConvenioAjuste` | `COMPONENTE_CONVENIO_AJUSTE` | CU-MPD-03 |
| `AmpliacionPlazoConvenio` | `AMPLIACION_PLAZO_CONVENIO` | CU-MPD-03 |
| `ProyectoConvenio` | `PROYECTO_CONVENIO` | CU-MPD-04 |

## Supuestos explícitos [SUPUESTO — pendientes de validación]

1. **Contradicciones del propio documento fuente sobre "Editable"**: varios campos de `Convenio` (Financiamiento, Organismo Acreedor, Institución Ejecutora) están marcados como "Campo Requerido" (el usuario debe completarlo) pero también como "Editable: No" en el Anexo 9 — una contradicción que el propio corpus de casos de uso señala sin resolver. Se modelaron como **editables en el registro inicial** (`Financiamiento`, `Organismo Acreedor`, `Institución Ejecutora` son parte del alta de `Convenio`), asumiendo que "Editable: No" aplica a *ediciones posteriores* del convenio ya creado, no al registro inicial. **Requiere confirmación del equipo de negocio.**
2. **`Financiamiento` (tipo de convenio)** por defecto es `"Préstamo Externo"` según el mockup del Anexo 3 (RN implícita) — se refleja como `DEFAULT 'PRESTAMO_EXTERNO'` en el DDL.
3. **`ComponenteConvenioAjuste.componenteOrigen`** es nullable porque un ajuste podría introducir un componente nuevo que no existía en la distribución inicial (no confirmado explícitamente en el documento, pero es la interpretación más flexible).
4. **Ficha del Convenio (CU-MPD-04)** es mayormente una pantalla de consulta consolidada — no genera tablas propias más allá de `ProyectoConvenio` (que sí registra datos nuevos: monto comprometido y bandera de Programa Principal). El resto de la ficha (montos, fechas, componentes) se resuelve mediante consultas a `Convenio`, `ComponenteConvenio`/`ComponenteConvenioAjuste` (el que tenga el ajuste más reciente) y `CondicionPrevia`.
5. **`ID_PROCESO_EXTERNO` / `ID_CONTRATO_EXTERNO`** (del módulo Ejecución) y el "Monto Comprometido" en `ProyectoConvenio` sugieren una futura integración con SIAF o un sistema de compras públicas externo — no modelada aquí porque no hay caso de uso de interfaz documentado (ver M-15 pendiente).

## Módulos restantes

- **M-00 / M-15 — Administración e Interfaces**: sin caso de uso documentado en el corpus (58 CU cubiertos en su totalidad entre Preinversión, Programación, Ejecución, Operación y Mantenimiento, y Convenios).

Con este módulo se completan **5 de 6 grandes módulos funcionales** del SIIP. Solo falta administración transversal (usuarios/roles/seguridad, catálogos base, calendario de eventos) e interfaces externas (SIAF), que el WBS menciona (M-00, M-15) pero de las cuales no se recibió documentación de casos de uso.
