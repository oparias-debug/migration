-- =====================================================================
-- Script de inicialización de datos — catalog_record - catalog_record_value
-- Dialecto: SQL ANSI (SQL:2003). Probado sobre PostgreSQL.
-- Origen: catalogos__inicializacion.json  (solo items con "nivel" = 1)
--
-- Prerrequisitos:
--   * Las tablas catalog y catalog_field ya contienen los catálogos y sus
--     campos (codigo, nombre y los campos adicionales por catálogo).
--
-- Estrategia:
--   1) Se crea la tabla temporal tmp_catalog_field que resuelve, una sola
--      vez, (catalog.code, catalog_field.name) -> (catalog_id, field_id).
--   2) Se crea la tabla temporal tmp_campo_requerido con todos los pares
--      (catálogo, campo) que el script necesita, y se consulta cuáles NO
--      existen en catalog_field. Esa consulta debe devolver 0 filas; de lo
--      contrario los valores de esos campos NO se cargarían.
--   3) Por cada item: INSERT en catalog_record y, a continuación, un único
--      INSERT ... SELECT en catalog_record_value con todos sus valores.
--      El record_id se obtiene como MAX(id) de catalog_record para ese
--      catalog_id, es decir, el registro recién generado.
--
-- IMPORTANTE:
--   * Ejecutar en una sola sesión, sin cargas concurrentes sobre
--     catalog_record (la obtención del id se basa en MAX(id)).
--   * El script no es idempotente: ejecutarlo dos veces duplica registros.
--
-- Resumen: 1146 registros en catalog_record, 4429 valores en catalog_record_value.
-- Valores omitidos por no existir en el JSON (valor es NOT NULL):
--   TIPO_AJUSTE_CONVENIO - codigo Adenda - campo descripcion
--   TIPO_AJUSTE_CONVENIO - codigo Reestructuración - campo descripcion
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) Tabla temporal de resolución catálogo-campo
-- ---------------------------------------------------------------------
CREATE GLOBAL TEMPORARY TABLE tmp_catalog_field (
    catalog_code  VARCHAR(64)  NOT NULL,
    field_name    VARCHAR(128) NOT NULL,
    catalog_id    BIGINT       NOT NULL,
    field_id      BIGINT       NOT NULL,
    PRIMARY KEY (catalog_code, field_name)
) ON COMMIT PRESERVE ROWS;

INSERT INTO tmp_catalog_field (catalog_code, field_name, catalog_id, field_id)
SELECT c.code, f.name, c.id, f.id
FROM catalog c
JOIN catalog_field f ON f.catalog_id = c.id;

-- ---------------------------------------------------------------------
-- 2) Validación previa: campos requeridos por el script
-- ---------------------------------------------------------------------
CREATE GLOBAL TEMPORARY TABLE tmp_campo_requerido (
    catalog_code  VARCHAR(64)  NOT NULL,
    field_name    VARCHAR(128) NOT NULL,
    PRIMARY KEY (catalog_code, field_name)
) ON COMMIT PRESERVE ROWS;

INSERT INTO tmp_campo_requerido (catalog_code, field_name) VALUES
    ('GRD', 'codigo'),
    ('GRD', 'nombre'),
    ('GRC', 'codigo'),
    ('GRC', 'nombre'),
    ('ACC', 'codigo'),
    ('ACC', 'nombre'),
    ('EJE_PLAN_GOBIERNO', 'codigo'),
    ('EJE_PLAN_GOBIERNO', 'nombre'),
    ('PLAN_SECTORIAL', 'codigo'),
    ('PLAN_SECTORIAL', 'nombre'),
    ('PLAN_SECTORIAL', 'sector_asociado'),
    ('MACROSECTOR', 'codigo'),
    ('MACROSECTOR', 'nombre'),
    ('EJE_TEMATICO', 'codigo'),
    ('EJE_TEMATICO', 'nombre'),
    ('TIPO_EJECUTORA', 'codigo'),
    ('TIPO_EJECUTORA', 'nombre'),
    ('INSTITUCION', 'codigo'),
    ('INSTITUCION', 'nombre'),
    ('ESTADO_PROYECTO', 'codigo'),
    ('ESTADO_PROYECTO', 'nombre'),
    ('INTERESADO_TIPO', 'codigo'),
    ('INTERESADO_TIPO', 'nombre'),
    ('REGION', 'codigo'),
    ('REGION', 'nombre'),
    ('TIPO_UNIDAD_MEDIDA', 'codigo'),
    ('TIPO_UNIDAD_MEDIDA', 'nombre'),
    ('COMPONENTE_PROYECTO', 'codigo'),
    ('COMPONENTE_PROYECTO', 'nombre'),
    ('PROPIETARIO_TERRENO', 'codigo'),
    ('PROPIETARIO_TERRENO', 'nombre'),
    ('MEDIO_AMBIENTAL', 'codigo'),
    ('MEDIO_AMBIENTAL', 'nombre'),
    ('IMPACTO_AMBIENTAL', 'codigo'),
    ('IMPACTO_AMBIENTAL', 'nombre'),
    ('MAGNITUD_AMBIENTAL', 'codigo'),
    ('MAGNITUD_AMBIENTAL', 'nombre'),
    ('DURACION_AMBIENTAL', 'codigo'),
    ('DURACION_AMBIENTAL', 'nombre'),
    ('REVERSIBILIDAD_AMBIENTAL', 'codigo'),
    ('REVERSIBILIDAD_AMBIENTAL', 'nombre'),
    ('PROBABILIDAD_RIESGO', 'codigo'),
    ('PROBABILIDAD_RIESGO', 'nombre'),
    ('IMPACTO_RIESGO', 'codigo'),
    ('IMPACTO_RIESGO', 'nombre'),
    ('CALIFICACION_RIESGO_MATRIZ', 'codigo'),
    ('CALIFICACION_RIESGO_MATRIZ', 'nombre'),
    ('CALIFICACION_RIESGO_MATRIZ', 'probabilidad'),
    ('CALIFICACION_RIESGO_MATRIZ', 'impacto'),
    ('CALIFICACION_RIESGO_MATRIZ', 'calificacion_resultante'),
    ('INSUMO_TIPO', 'codigo'),
    ('INSUMO_TIPO', 'nombre'),
    ('INSUMO_TIPO', 'factor_correccion'),
    ('FUENTE_FINANCIAMIENTO', 'codigo'),
    ('FUENTE_FINANCIAMIENTO', 'nombre'),
    ('FUENTE_RECURSOS', 'codigo'),
    ('FUENTE_RECURSOS', 'nombre'),
    ('FUENTE_RECURSOS', 'codigo_pais'),
    ('FUENTE_RECURSOS', 'codigo_clase_recurso'),
    ('CONVENIO_FINANCIAMIENTO', 'codigo'),
    ('CONVENIO_FINANCIAMIENTO', 'nombre'),
    ('CONVENIO_FINANCIAMIENTO', 'moneda'),
    ('CONVENIO_FINANCIAMIENTO', 'monto'),
    ('CONVENIO_FINANCIAMIENTO', 'numero_sigade'),
    ('CONVENIO_FINANCIAMIENTO', 'codigo_tipo_convenio'),
    ('PARAMETRO_EVALUACION_SOCIAL', 'codigo'),
    ('PARAMETRO_EVALUACION_SOCIAL', 'nombre'),
    ('PARAMETRO_EVALUACION_SOCIAL', 'factor_correccion'),
    ('TIPO_BENEFICIO', 'codigo'),
    ('TIPO_BENEFICIO', 'nombre'),
    ('TIPO_BIEN_RESCATE', 'codigo'),
    ('TIPO_BIEN_RESCATE', 'nombre'),
    ('TIPO_BIEN_RESCATE', 'factor_correccion'),
    ('UNIDAD_EJECUTORA_EVALUACION_FINANCIERA', 'codigo'),
    ('UNIDAD_EJECUTORA_EVALUACION_FINANCIERA', 'nombre'),
    ('UNIDAD_EJECUTORA_EVALUACION_FINANCIERA', 'item_sigla'),
    ('UNIDAD_EJECUTORA_EVALUACION_FINANCIERA', 'clasificacion_institucional'),
    ('UNIDAD_EJECUTORA_EVALUACION_FINANCIERA', 'referencia_cruzada'),
    ('PRODUCTO', 'codigo'),
    ('PRODUCTO', 'nombre'),
    ('RESULTADO', 'codigo'),
    ('RESULTADO', 'nombre'),
    ('ODS_ONU', 'codigo'),
    ('ODS_ONU', 'nombre'),
    ('ODS_ONU', 'numero_ods'),
    ('MEDIO_AMBIENTE_COMPONENTE', 'codigo'),
    ('MEDIO_AMBIENTE_COMPONENTE', 'nombre'),
    ('MEDIO_AMBIENTE_COMPONENTE', 'numero'),
    ('GRUPO_POBLACIONAL_VULNERABLE', 'codigo'),
    ('GRUPO_POBLACIONAL_VULNERABLE', 'nombre'),
    ('GRUPO_POBLACIONAL_VULNERABLE', 'numero'),
    ('MEJORA_CALIDAD_VIDA', 'codigo'),
    ('MEJORA_CALIDAD_VIDA', 'nombre'),
    ('MEJORA_CALIDAD_VIDA', 'numero'),
    ('DIMENSION_ELEGIBILIDAD', 'codigo'),
    ('DIMENSION_ELEGIBILIDAD', 'nombre'),
    ('CRITERIO_PRIORIZACION', 'codigo'),
    ('CRITERIO_PRIORIZACION', 'nombre'),
    ('RANGO_PRIORIZACION', 'codigo'),
    ('RANGO_PRIORIZACION', 'nombre'),
    ('RANGO_PRIORIZACION', 'rango_puntaje'),
    ('RANGO_PRIORIZACION', 'implicacion'),
    ('TIPO_CAPITAL', 'codigo'),
    ('TIPO_CAPITAL', 'nombre'),
    ('TIPO_COSTO', 'codigo'),
    ('TIPO_COSTO', 'nombre'),
    ('TAMANO_PROYECTO', 'codigo'),
    ('TAMANO_PROYECTO', 'nombre'),
    ('TAMANO_PROYECTO', 'rango_monto'),
    ('COMPLEJIDAD_PROYECTO', 'codigo'),
    ('COMPLEJIDAD_PROYECTO', 'nombre'),
    ('RUTA_PREINVERSION', 'codigo'),
    ('RUTA_PREINVERSION', 'nombre'),
    ('RUTA_PREINVERSION', 'segun_tipo_capital'),
    ('RUTA_PREINVERSION', 'segun_tamano'),
    ('RUTA_PREINVERSION', 'segun_complejidad'),
    ('ETAPA_PROYECTO', 'codigo'),
    ('ETAPA_PROYECTO', 'nombre'),
    ('HABILITACION_CAMPOS', 'codigo'),
    ('HABILITACION_CAMPOS', 'nombre'),
    ('ENTREGABLE_ETAPA', 'codigo'),
    ('ENTREGABLE_ETAPA', 'nombre'),
    ('ESTADO_ESTUDIO_PREINVERSION', 'codigo'),
    ('ESTADO_ESTUDIO_PREINVERSION', 'nombre'),
    ('ESTADO_ESTUDIO_PREINVERSION', 'criterio'),
    ('TIPO_FINANCIAMIENTO', 'codigo'),
    ('TIPO_FINANCIAMIENTO', 'nombre'),
    ('CLASIFICACION_ETAPA_FINANCIAMIENTO', 'codigo'),
    ('CLASIFICACION_ETAPA_FINANCIAMIENTO', 'nombre'),
    ('ESTADO_PRIPME', 'codigo'),
    ('ESTADO_PRIPME', 'nombre'),
    ('NIVEL_PRIORIZACION_PRIPME', 'codigo'),
    ('NIVEL_PRIORIZACION_PRIPME', 'nombre'),
    ('ESTADO_REVISION_TECNICA', 'codigo'),
    ('ESTADO_REVISION_TECNICA', 'nombre'),
    ('FORMATO_REPORTE_PRIPME', 'codigo'),
    ('FORMATO_REPORTE_PRIPME', 'nombre'),
    ('ESTADOS_PROGRAMACION', 'codigo'),
    ('ESTADOS_PROGRAMACION', 'nombre'),
    ('TIPO_TRANSACCION', 'codigo'),
    ('TIPO_TRANSACCION', 'nombre'),
    ('TIPO_APROBACION', 'codigo'),
    ('TIPO_APROBACION', 'nombre'),
    ('AGRUPACION_OPERACIONAL', 'codigo'),
    ('AGRUPACION_OPERACIONAL', 'nombre'),
    ('FORMATO_EXPORTACION_UBICACION', 'codigo'),
    ('FORMATO_EXPORTACION_UBICACION', 'nombre'),
    ('ESTADO_PROPUESTA_PAIP', 'codigo'),
    ('ESTADO_PROPUESTA_PAIP', 'nombre'),
    ('ESTADO_COMPARACION_PROYECTO_PRO11', 'codigo'),
    ('ESTADO_COMPARACION_PROYECTO_PRO11', 'nombre'),
    ('ESTADO_COMPARACION_PROYECTO_PRO09', 'codigo'),
    ('ESTADO_COMPARACION_PROYECTO_PRO09', 'nombre'),
    ('INSTANCIA_APROBACION', 'codigo'),
    ('INSTANCIA_APROBACION', 'nombre'),
    ('DIMENSION_PRIORIZACION_ESCENARIOS', 'codigo'),
    ('DIMENSION_PRIORIZACION_ESCENARIOS', 'nombre'),
    ('ATRIBUTO_OBLIGATORIO_ESCENARIOS', 'codigo'),
    ('ATRIBUTO_OBLIGATORIO_ESCENARIOS', 'nombre'),
    ('ESTADO_ESCENARIO', 'codigo'),
    ('ESTADO_ESCENARIO', 'nombre'),
    ('HISTORICO_AUTORIZACION_TECHO', 'codigo'),
    ('HISTORICO_AUTORIZACION_TECHO', 'nombre'),
    ('ESTADO_SEMAFORO_AVANCE', 'codigo'),
    ('ESTADO_SEMAFORO_AVANCE', 'nombre'),
    ('ESTADO_SEMAFORO_AVANCE', 'color'),
    ('ESTADO_SEMAFORO_AVANCE', 'criterio'),
    ('METODO_CONTRATACION', 'codigo'),
    ('METODO_CONTRATACION', 'nombre'),
    ('CATEGORIA_PROCESO_ADMINISTRATIVO', 'codigo'),
    ('CATEGORIA_PROCESO_ADMINISTRATIVO', 'nombre'),
    ('PROCESO_ADMINISTRATIVO_HITO', 'codigo'),
    ('PROCESO_ADMINISTRATIVO_HITO', 'nombre'),
    ('PROCESO_ADMINISTRATIVO_HITO', 'orden'),
    ('FUENTE_FINANCIAMIENTO_SEGUIMIENTO', 'codigo'),
    ('FUENTE_FINANCIAMIENTO_SEGUIMIENTO', 'nombre'),
    ('ESTADO_CONDICION_PREVIA', 'codigo'),
    ('ESTADO_CONDICION_PREVIA', 'nombre'),
    ('ESTADO_CONDICION_PREVIA', 'descripcion'),
    ('TIPO_AJUSTE_CONVENIO', 'codigo'),
    ('TIPO_AJUSTE_CONVENIO', 'nombre'),
    ('TIPO_AJUSTE_CONVENIO', 'descripcion');

-- Debe devolver 0 filas. Si devuelve filas, crear esos catálogos-campos
-- en catalog - catalog_field antes de continuar.
SELECT r.catalog_code, r.field_name
FROM tmp_campo_requerido r
LEFT JOIN tmp_catalog_field t
       ON t.catalog_code = r.catalog_code AND t.field_name = r.field_name
WHERE t.field_id IS NULL
ORDER BY r.catalog_code, r.field_name;

-- ---------------------------------------------------------------------
-- 3) Carga de registros y valores
-- ---------------------------------------------------------------------

-- =====================================================================
-- Catálogo GRD — Catálogo de Gestión de Riesgo de Desastres (GRD)
-- Registros de nivel 1: 4   Campos: codigo, nombre
-- =====================================================================
-- [GRD] 1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRD';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1'),
    ('nombre', 'Reducción del riesgo existente')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRD' AND t.field_name = v.field_name;
-- [GRD] 2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRD';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2'),
    ('nombre', 'Prospectivos para evitar nuevos riesgos y generación de conocimiento')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRD' AND t.field_name = v.field_name;
-- [GRD] 3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRD';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3'),
    ('nombre', 'Preparación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRD' AND t.field_name = v.field_name;
-- [GRD] 4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRD';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4'),
    ('nombre', 'Respuesta y Recuperación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRD' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo GRC — Catálogo de Gestión de Riesgo Climático (GRC)
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [GRC] 1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRC';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1'),
    ('nombre', 'Prevención')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRC' AND t.field_name = v.field_name;
-- [GRC] 2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRC';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2'),
    ('nombre', 'Preparación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRC' AND t.field_name = v.field_name;
-- [GRC] 3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRC';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3'),
    ('nombre', 'Gestión de desastres')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRC' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ACC — Catálogo de Adaptación al Cambio Climático (ACC)
-- Registros de nivel 1: 2   Campos: codigo, nombre
-- =====================================================================
-- [ACC] 1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ACC';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1'),
    ('nombre', 'Mitigación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ACC' AND t.field_name = v.field_name;
-- [ACC] 2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ACC';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2'),
    ('nombre', 'Adaptación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ACC' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo EJE_PLAN_GOBIERNO — Ejes del Plan de Gobierno (Plan Cuscatlán)
-- Registros de nivel 1: 9   Campos: codigo, nombre
-- =====================================================================
-- [EJE_PLAN_GOBIERNO] EJE_1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_PLAN_GOBIERNO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EJE_1'),
    ('nombre', 'Eje 1: Carreteras')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_PLAN_GOBIERNO' AND t.field_name = v.field_name;
-- [EJE_PLAN_GOBIERNO] EJE_2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_PLAN_GOBIERNO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EJE_2'),
    ('nombre', 'Eje 2: Transporte')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_PLAN_GOBIERNO' AND t.field_name = v.field_name;
-- [EJE_PLAN_GOBIERNO] EJE_3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_PLAN_GOBIERNO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EJE_3'),
    ('nombre', 'Eje 3: Infraestructura de salud y educación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_PLAN_GOBIERNO' AND t.field_name = v.field_name;
-- [EJE_PLAN_GOBIERNO] EJE_4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_PLAN_GOBIERNO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EJE_4'),
    ('nombre', 'Eje 4: Puertos, aeropuertos y aduanas')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_PLAN_GOBIERNO' AND t.field_name = v.field_name;
-- [EJE_PLAN_GOBIERNO] EJE_5
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_PLAN_GOBIERNO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EJE_5'),
    ('nombre', 'Eje 5: Agua potable y saneamiento')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_PLAN_GOBIERNO' AND t.field_name = v.field_name;
-- [EJE_PLAN_GOBIERNO] EJE_6
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_PLAN_GOBIERNO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EJE_6'),
    ('nombre', 'Eje 6: Vivienda y desarrollo urbano')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_PLAN_GOBIERNO' AND t.field_name = v.field_name;
-- [EJE_PLAN_GOBIERNO] EJE_7
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_PLAN_GOBIERNO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EJE_7'),
    ('nombre', 'Eje 7: Infraestructura penitenciaria')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_PLAN_GOBIERNO' AND t.field_name = v.field_name;
-- [EJE_PLAN_GOBIERNO] EJE_8
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_PLAN_GOBIERNO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EJE_8'),
    ('nombre', 'Eje 8: Asocios públicos-privados')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_PLAN_GOBIERNO' AND t.field_name = v.field_name;
-- [EJE_PLAN_GOBIERNO] EJE_9
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_PLAN_GOBIERNO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EJE_9'),
    ('nombre', 'Eje 9: Energía')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_PLAN_GOBIERNO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo PLAN_SECTORIAL — Catálogo de Planes Sectoriales
-- Registros de nivel 1: 7   Campos: codigo, nombre, sector_asociado
-- =====================================================================
-- [PLAN_SECTORIAL] Plan Control Territorial
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PLAN_SECTORIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Plan Control Territorial'),
    ('nombre', 'Plan Control Territorial'),
    ('sector_asociado', 'Seguridad')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PLAN_SECTORIAL' AND t.field_name = v.field_name;
-- [PLAN_SECTORIAL] Plan Nacional de Turismo - 2030
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PLAN_SECTORIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Plan Nacional de Turismo - 2030'),
    ('nombre', 'Plan Nacional de Turismo - 2030'),
    ('sector_asociado', 'Turismo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PLAN_SECTORIAL' AND t.field_name = v.field_name;
-- [PLAN_SECTORIAL] Plan Sectorial de Educación 2022-2030
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PLAN_SECTORIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Plan Sectorial de Educación 2022-2030'),
    ('nombre', 'Plan Sectorial de Educación 2022-2030'),
    ('sector_asociado', 'Educación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PLAN_SECTORIAL' AND t.field_name = v.field_name;
-- [PLAN_SECTORIAL] Planes Sectoriales para la implementación de las Contribuciones Nacionalmente Determinadas de El Salvador
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PLAN_SECTORIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Planes Sectoriales para la implementación de las Contribuciones Nacionalmente Determinadas de El Salvador'),
    ('nombre', 'Planes Sectoriales para la implementación de las Contribuciones Nacionalmente Determinadas de El Salvador'),
    ('sector_asociado', 'Medio Ambiente')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PLAN_SECTORIAL' AND t.field_name = v.field_name;
-- [PLAN_SECTORIAL] Plan Nacional de Cambio Climático 2022-2026
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PLAN_SECTORIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Plan Nacional de Cambio Climático 2022-2026'),
    ('nombre', 'Plan Nacional de Cambio Climático 2022-2026'),
    ('sector_asociado', 'Medio Ambiente')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PLAN_SECTORIAL' AND t.field_name = v.field_name;
-- [PLAN_SECTORIAL] Plan Nacional para la Gestión Integral de Residuos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PLAN_SECTORIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Plan Nacional para la Gestión Integral de Residuos'),
    ('nombre', 'Plan Nacional para la Gestión Integral de Residuos'),
    ('sector_asociado', 'Medio Ambiente')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PLAN_SECTORIAL' AND t.field_name = v.field_name;
-- [PLAN_SECTORIAL] Política Crecer Juntos 2020-2030
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PLAN_SECTORIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Política Crecer Juntos 2020-2030'),
    ('nombre', 'Política Crecer Juntos 2020-2030'),
    ('sector_asociado', 'Salud-Educación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PLAN_SECTORIAL' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo MACROSECTOR — Catálogo de Sectores y MACROSECTORes
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [MACROSECTOR] Desarrollo Social
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MACROSECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Desarrollo Social'),
    ('nombre', 'Desarrollo Social')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MACROSECTOR' AND t.field_name = v.field_name;
-- [MACROSECTOR] Desarrollo Económico
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MACROSECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Desarrollo Económico'),
    ('nombre', 'Desarrollo Económico')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MACROSECTOR' AND t.field_name = v.field_name;
-- [MACROSECTOR] Seguridad Pública y Justicia
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MACROSECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Seguridad Pública y Justicia'),
    ('nombre', 'Seguridad Pública y Justicia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MACROSECTOR' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo EJE_TEMATICO — Catálogo de Ejes Temáticos
-- Registros de nivel 1: 22   Campos: codigo, nombre
-- =====================================================================
-- [EJE_TEMATICO] Infraestructura Educativa (Construcción y Mejoramiento)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Infraestructura Educativa (Construcción y Mejoramiento)'),
    ('nombre', 'Infraestructura Educativa (Construcción y Mejoramiento)')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Equipamiento, Tecnología y Fortalecimiento Pedagógico en Centros Escolares
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Equipamiento, Tecnología y Fortalecimiento Pedagógico en Centros Escolares'),
    ('nombre', 'Equipamiento, Tecnología y Fortalecimiento Pedagógico en Centros Escolares')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Educación Superior e Investigación
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Educación Superior e Investigación'),
    ('nombre', 'Educación Superior e Investigación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Construcción y Mejoramiento de Infraestructura de Salud
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Construcción y Mejoramiento de Infraestructura de Salud'),
    ('nombre', 'Construcción y Mejoramiento de Infraestructura de Salud')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Construcción y Mejoramiento de Infraestructura Vial
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Construcción y Mejoramiento de Infraestructura Vial'),
    ('nombre', 'Construcción y Mejoramiento de Infraestructura Vial')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Transporte Público y Movilidad Urbana
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Transporte Público y Movilidad Urbana'),
    ('nombre', 'Transporte Público y Movilidad Urbana')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Infraestructura Turística
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Infraestructura Turística'),
    ('nombre', 'Infraestructura Turística')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Espacios Públicos y Desarrollo Urbano
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Espacios Públicos y Desarrollo Urbano'),
    ('nombre', 'Espacios Públicos y Desarrollo Urbano')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Gestión Ambiental y Restauración
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Gestión Ambiental y Restauración'),
    ('nombre', 'Gestión Ambiental y Restauración')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Infraestructura para Gestión de Riesgo y Adaptación Climática
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Infraestructura para Gestión de Riesgo y Adaptación Climática'),
    ('nombre', 'Infraestructura para Gestión de Riesgo y Adaptación Climática')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Infraestructura Agrícola y Seguridad Alimentaria
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Infraestructura Agrícola y Seguridad Alimentaria'),
    ('nombre', 'Infraestructura Agrícola y Seguridad Alimentaria')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Generación, Transmisión o Distribución de Energía
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Generación, Transmisión o Distribución de Energía'),
    ('nombre', 'Generación, Transmisión o Distribución de Energía')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Infraestructura Aeroportuaria o Portuaria
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Infraestructura Aeroportuaria o Portuaria'),
    ('nombre', 'Infraestructura Aeroportuaria o Portuaria')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Conectividad y Comunicaciones
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Conectividad y Comunicaciones'),
    ('nombre', 'Conectividad y Comunicaciones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Infraestructura y Servicios para Grupos Vulnerables
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Infraestructura y Servicios para Grupos Vulnerables'),
    ('nombre', 'Infraestructura y Servicios para Grupos Vulnerables')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Equipamiento y Formación de Capital Humano
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Equipamiento y Formación de Capital Humano'),
    ('nombre', 'Equipamiento y Formación de Capital Humano')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Seguridad Ciudadana y Convivencia Comunitaria
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Seguridad Ciudadana y Convivencia Comunitaria'),
    ('nombre', 'Seguridad Ciudadana y Convivencia Comunitaria')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Fortalecimiento y Equipamiento Institucional
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Fortalecimiento y Equipamiento Institucional'),
    ('nombre', 'Fortalecimiento y Equipamiento Institucional')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Vivienda y Mejoramiento Habitacional
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Vivienda y Mejoramiento Habitacional'),
    ('nombre', 'Vivienda y Mejoramiento Habitacional')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Sistemas de Agua y Saneamiento Básico
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Sistemas de Agua y Saneamiento Básico'),
    ('nombre', 'Sistemas de Agua y Saneamiento Básico')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Infraestructura Cultural y Patrimonio
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Infraestructura Cultural y Patrimonio'),
    ('nombre', 'Infraestructura Cultural y Patrimonio')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;
-- [EJE_TEMATICO] Infraestructura Deportiva y Recreativa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'EJE_TEMATICO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Infraestructura Deportiva y Recreativa'),
    ('nombre', 'Infraestructura Deportiva y Recreativa')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'EJE_TEMATICO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo TIPO_EJECUTORA — Catálogo "Tipo Ejecutoras"
-- Registros de nivel 1: 5   Campos: codigo, nombre
-- =====================================================================
-- [TIPO_EJECUTORA] 1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1'),
    ('nombre', 'GOBIERNO CENTRAL')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_EJECUTORA' AND t.field_name = v.field_name;
-- [TIPO_EJECUTORA] 2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2'),
    ('nombre', 'INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_EJECUTORA' AND t.field_name = v.field_name;
-- [TIPO_EJECUTORA] 3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3'),
    ('nombre', 'EMPRESAS PUBLICAS NO FINANCIERAS')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_EJECUTORA' AND t.field_name = v.field_name;
-- [TIPO_EJECUTORA] 4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4'),
    ('nombre', 'INSTITUCIONES DE SEGURIDAD SOCIAL')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_EJECUTORA' AND t.field_name = v.field_name;
-- [TIPO_EJECUTORA] 5
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '5'),
    ('nombre', 'OTRAS')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_EJECUTORA' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo INSTITUCION — Catálogo "Instituciones"
-- Registros de nivel 1: 40   Campos: codigo, nombre
-- =====================================================================
-- [INSTITUCION] 0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '0'),
    ('nombre', 'NINGUNA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 100
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '100'),
    ('nombre', 'ORGANO LEGISLATIVO')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 200
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '200'),
    ('nombre', 'CORTE DE CUENTAS DE LA REPUBLICA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 300
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '300'),
    ('nombre', 'TRIBUNAL SUPREMO ELECTORAL')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 400
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '400'),
    ('nombre', 'TRIBUNAL DE SERVICIO CIVIL')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 500
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '500'),
    ('nombre', 'PRESIDENCIA DE LA REPUBLICA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 556
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '556'),
    ('nombre', 'DIRECCIÓN NACIONAL DE OBRAS MUNICIPALES')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 600
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '600'),
    ('nombre', 'TRIBUNAL DE ETICA GUBERNAMENTAL')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 700
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '700'),
    ('nombre', 'RAMO DE HACIENDA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 800
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '800'),
    ('nombre', 'RAMO DE RELACIONES EXTERIORES')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 900
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '900'),
    ('nombre', 'RAMO DE LA DEFENSA NACIONAL')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 1500
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1500'),
    ('nombre', 'CONSEJO NACIONAL DE LA JUDICATURA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 1600
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1600'),
    ('nombre', 'ORGANO JUDICIAL')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 1700
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1700'),
    ('nombre', 'FISCALIA GENERAL DE LA REPUBLICA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 1800
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1800'),
    ('nombre', 'PROCURADURIA GENERAL DE LA REPUBLICA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 1900
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1900'),
    ('nombre', 'PROCURADURIA PARA LA DEFENSA DE LOS DERECHOS HUMANOS')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 2000
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2000'),
    ('nombre', 'MINISTERIO DE GOBERNACION')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 2100
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2100'),
    ('nombre', 'RAMO DE SEGURIDAD PUBLICA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 2200
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2200'),
    ('nombre', 'RAMO DE JUSTICIA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 2300
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2300'),
    ('nombre', 'RAMO DE GOBERNACION Y DESARROLLO TERRITORIAL')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 2306
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2306'),
    ('nombre', 'CUERPO DE BOMBEROS DE EL SALVADOR')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 2400
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2400'),
    ('nombre', 'RAMO DE SEGURIDAD PUBLICA Y JUSTICIA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 3100
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3100'),
    ('nombre', 'RAMO DE EDUCACION, CIENCIA Y TECNOLOGIA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 3200
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3200'),
    ('nombre', 'RAMO DE SALUD')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 3237
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3237'),
    ('nombre', 'Hospital Nacional El Salvador')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 3300
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3300'),
    ('nombre', 'RAMO DE TRABAJO Y PREVISION SOCIAL')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 3400
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3400'),
    ('nombre', 'RAMO DE VIVIENDA Y DESARROLLO URBANO')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 3500
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3500'),
    ('nombre', 'RAMO DE CULTURA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 3600
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3600'),
    ('nombre', 'RAMO DE VIVIENDA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 3700
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3700'),
    ('nombre', 'RAMO DE DESARROLLO LOCAL')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 4100
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4100'),
    ('nombre', 'RAMO DE ECONOMIA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 4127
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4127'),
    ('nombre', 'INSTITUTO NACIONAL DE CAPACITACION Y FORMACION')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 4200
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4200'),
    ('nombre', 'RAMO DE AGRICULTURA Y GANADERIA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 4300
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4300'),
    ('nombre', 'RAMO DE OBRAS PUBLICAS Y TRANSPORTE')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 4400
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4400'),
    ('nombre', 'RAMO DE MEDIO AMBIENTE Y RECURSOS NATURALES')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 4500
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4500'),
    ('nombre', 'RAMO DE TRANSPORTE')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 4600
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4600'),
    ('nombre', 'RAMO DE TURISMO')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 4700
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4700'),
    ('nombre', 'HABITAT')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 4800
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4800'),
    ('nombre', 'MINISTERIO DE SEGURIDA PUBLICA Y JUSTICIA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;
-- [INSTITUCION] 4900
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTITUCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4900'),
    ('nombre', 'FUNDASAL')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTITUCION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo UNIDAD_EJECUTORA — Catálogo "Unidades Ejecutoras"
-- Registros de nivel 1: 145   Campos: codigo, nombre
-- =====================================================================
-- [UNIDAD_EJECUTORA] 1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1'),
    ('nombre', 'Ministerio de Seguridad Pública')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2'),
    ('nombre', 'Dirección General de Urbanismo y Arquitectura')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3'),
    ('nombre', 'Ministerio de Educación, Ciencia y Tecnología')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4'),
    ('nombre', 'Ministerio de Agricultura y Ganadería')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 5
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '5'),
    ('nombre', 'Corte Suprema de Justicia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 6
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '6'),
    ('nombre', 'Fondo de Inversión Social para el Desarrollo Local')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 7
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '7'),
    ('nombre', 'Instituto Nacional de los Deportes de El Salvador')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 8
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '8'),
    ('nombre', 'Secretaría Nacional de la Familia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 9
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '9'),
    ('nombre', 'Policia Nacional Civil')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 10
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '10'),
    ('nombre', 'Ministerio de Seguridad Pública y Justicia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 11
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '11'),
    ('nombre', 'Ministerio de Salud')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 12
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '12'),
    ('nombre', 'Ministerio de Hacienda')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 13
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '13'),
    ('nombre', 'Ministerio de Gobernación y Desarrollo Territorial')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 14
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '14'),
    ('nombre', 'Ministerio de Defensa')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 15
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '15'),
    ('nombre', 'Academia Nacional de Seguridad Pública')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 16
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '16'),
    ('nombre', 'Ministerio de Relaciones Exteriores')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 17
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '17'),
    ('nombre', 'Fiscalía General de la República')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 18
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '18'),
    ('nombre', 'Ministerio de Economía')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 19
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '19'),
    ('nombre', 'Ministerio de Trabajo y Previsión Social')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 20
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '20'),
    ('nombre', 'Asamblea Legislativa')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 21
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '21'),
    ('nombre', 'Viceministerio de Vivienda y Desarrollo Urbano')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 22
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '22'),
    ('nombre', 'Universidad de El Salvador')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 23
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '23'),
    ('nombre', 'Instituto Salvadoreño de Turismo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 24
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '24'),
    ('nombre', 'Instituto Salvadoreño del Seguro Social')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 25
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '25'),
    ('nombre', 'Comisión Ejecutiva Hidroeléctrica del Río Lempa')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 26
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '26'),
    ('nombre', 'Administración Nacional de Acueductos y Alcant.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 27
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '27'),
    ('nombre', 'Comisión Ejecutiva Portuaria Autónoma')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 28
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '28'),
    ('nombre', 'Instituto Salvadoreño de Desarrollo Municipal')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 29
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '29'),
    ('nombre', 'Comisión Presid. para la Modern.del Sector Público')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 30
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '30'),
    ('nombre', 'Unidad Técnica Ejecutiva del Sector Justicia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 31
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '31'),
    ('nombre', 'Centro Nacional de Registros')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 32
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '32'),
    ('nombre', 'Instituto Salvadoreño para el Desa de la Mujer')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 33
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '33'),
    ('nombre', 'Viceministerio de Obras Públicas - MOP')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 34
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '34'),
    ('nombre', 'Centro Internacional de Ferias y Convenciones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 35
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '35'),
    ('nombre', 'Dirección General de Caminos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 36
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '36'),
    ('nombre', 'Instituto Salvadoreño de Rehabilitación Integral')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 37
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '37'),
    ('nombre', 'Dirección General de Correos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 38
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '38'),
    ('nombre', 'Cuerpo de Bomberos de El Salvador')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 39
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '39'),
    ('nombre', 'Dirección de Desarrollo de la Comunidad')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 40
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '40'),
    ('nombre', 'Radio Nacional de El Salvador')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 41
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '41'),
    ('nombre', 'Instituto Salvadoreño del Protección al Menor')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 42
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '42'),
    ('nombre', 'Viceministerio de Transporte')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 43
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '43'),
    ('nombre', 'Administración de Maquinaria y Equipo -AME-')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 44
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '44'),
    ('nombre', 'Procuraduria General de la República')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 45
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '45'),
    ('nombre', 'Dirección General de Centros Penales - MSPJ')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 46
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '46'),
    ('nombre', 'Ministerio de Medio Ambiente y Recursos Naturales')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 47
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '47'),
    ('nombre', 'Comité de Emergencia Nacional')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 48
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '48'),
    ('nombre', 'Caja Mutual de los Empleados del Min. de Educación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 49
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '49'),
    ('nombre', 'Fondo Nacional de Vivienda Popular')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 50
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '50'),
    ('nombre', 'Corporación Salvadoreña de Turismo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 51
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '51'),
    ('nombre', 'Procuraduria p-la Defensa de los Derechos Humanos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 52
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '52'),
    ('nombre', 'Consejo Nacional de Judicatura')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 53
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '53'),
    ('nombre', 'Corte de Cuentas de la República')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 54
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '54'),
    ('nombre', 'Fundacion Salvador del Mundo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 55
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '55'),
    ('nombre', 'Fondo de Conservación Vial')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 56
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '56'),
    ('nombre', 'Instituto Salvadoreño de Formación Profesional')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 57
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '57'),
    ('nombre', 'Consejo Superior de Salud Pública')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 58
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '58'),
    ('nombre', 'Fondo Social Para la Vivienda')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 59
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '59'),
    ('nombre', 'Consejo Nacional para la Cultura y el Arte')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 60
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '60'),
    ('nombre', 'Instituto para el Desarrollo de la Niñez y la Adolescencia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 61
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '61'),
    ('nombre', 'Fundación Salvadoreña de Desarrollo y Vivienda Mínima')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 62
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '62'),
    ('nombre', 'Instituto Salvadoreño para el Desarrollo de la Niñez y la Adolescencia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 63
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '63'),
    ('nombre', 'Fondo de Protección de Lisiados y Discap. a Consecuencia del Conflicto Armado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 64
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '64'),
    ('nombre', 'Servicio Nacional de Estudios Territoriales')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 65
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '65'),
    ('nombre', 'Secretaría Técnica y de Planificación de la Presidencia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 66
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '66'),
    ('nombre', 'Fondo Ambiental de El Salvador')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 67
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '67'),
    ('nombre', 'Superintendencia de Pensiones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 68
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '68'),
    ('nombre', 'Alcaldia Municipal de San Salvador')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 69
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '69'),
    ('nombre', 'Banco Multisectorial de Inversiones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 70
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '70'),
    ('nombre', 'Instituto Libertad y Progreso')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 71
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '71'),
    ('nombre', 'Fondo de Desarrollo Económico y Social')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 72
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '72'),
    ('nombre', 'Comisión Nacional de la Micro y Pequeña Empresa')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 73
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '73'),
    ('nombre', 'Superintendencia General de Electricidad y Telecomunicaciones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 74
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '74'),
    ('nombre', 'Consejo Salvadoreño del Café')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 75
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '75'),
    ('nombre', 'Defensoría del Consumidor')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 76
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '76'),
    ('nombre', 'Loteria Nacional de Beneficencia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 77
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '77'),
    ('nombre', 'Secretaría de la Juventud')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 78
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '78'),
    ('nombre', 'Comisión Nacional de Promoción de Exportaciones e Inversiones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 79
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '79'),
    ('nombre', 'Superintendencia del Sistema Financiero')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 80
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '80'),
    ('nombre', 'Superintendencia de Valores')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 81
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '81'),
    ('nombre', 'Tribunal Supremo Electoral')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 82
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '82'),
    ('nombre', 'Centros Intermedios')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 83
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '83'),
    ('nombre', 'Secretaría de Estado - MSPJ')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 84
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '84'),
    ('nombre', 'CONACYT')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 87
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '87'),
    ('nombre', 'Fondo del Milenio')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 88
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '88'),
    ('nombre', 'Academia Internacional para el Cumplimiento de la Ley')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 89
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '89'),
    ('nombre', 'Superintendencia de Competencia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 90
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '90'),
    ('nombre', 'Protección Civil')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 91
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '91'),
    ('nombre', 'MINISTERIO DE TURISMO')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 92
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '92'),
    ('nombre', 'Registro Nacional de las Personas Naturales')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 93
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '93'),
    ('nombre', 'Instituto Nacional de Pensiones de los Empleados Públicos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 94
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '94'),
    ('nombre', 'Consejo de Vigilancia de la Contaduría Pública y Auditoría')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 95
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '95'),
    ('nombre', 'Instituto Salvadoreño de Transformación Agraria')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 96
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '96'),
    ('nombre', 'Ministerio de Cultura')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 97
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '97'),
    ('nombre', 'Centro Nacional de Tecnología Agropecuaria y Forestal')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 98
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '98'),
    ('nombre', 'Secretaría de Inclusión Social')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 99
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '99'),
    ('nombre', 'PENDIENTE')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 100
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '100'),
    ('nombre', 'Secretaría de Participación Ciudadana, Transparencia y Anticorrupción')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 101
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '101'),
    ('nombre', 'Escuela Nacional de Agricultura "Roberto Quiñonez"')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 102
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '102'),
    ('nombre', 'Consejo Nacional de la Seguridad Pública')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 103
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '103'),
    ('nombre', 'Fondo Solidario para la Familia Microempresaria')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 104
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '104'),
    ('nombre', 'Instituto Salvadoreño de Fomento Cooperativo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 105
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '105'),
    ('nombre', 'Instituto Salvadoreño de Bienestar Magisterial')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 106
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '106'),
    ('nombre', 'Consejo Nacional de Energía')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 107
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '107'),
    ('nombre', 'Instituto Nacional de la Juventud')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 108
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '108'),
    ('nombre', 'Fondo Solidario para la Salud')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 109
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '109'),
    ('nombre', 'Consejo Nacional de Calidad')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 110
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '110'),
    ('nombre', 'Dirección Nacional de Medicamentos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 192
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '192'),
    ('nombre', 'Secretaría de Gobernabilidad y Comunicaciones de la de Presidencia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 193
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '193'),
    ('nombre', 'Centro Farmacéutico de la Fuerza Armada')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 194
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '194'),
    ('nombre', 'Cruz Roja Salvadoreña')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 195
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '195'),
    ('nombre', 'Ministerio de Vivienda')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 196
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '196'),
    ('nombre', 'Ministerio de Desarrollo Local')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 197
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '197'),
    ('nombre', 'Dirección General de Migración y Extranjería')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 198
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '198'),
    ('nombre', 'Instituto Administrador de los Beneficios de los Veteranos Militares y Excombatientes')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 199
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '199'),
    ('nombre', 'Entidad del Milenio')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 200
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '200'),
    ('nombre', 'Dirección de Reconstrucción del Tejido Social')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 201
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '201'),
    ('nombre', 'Vicepresidencia de la República')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 202
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '202'),
    ('nombre', 'Secretaría de Innovación de la Presidencia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 203
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '203'),
    ('nombre', 'Dirección Nacional de Obras Municipales')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 204
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '204'),
    ('nombre', 'Autoridad Salvadoreña del Agua-')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 205
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '205'),
    ('nombre', 'Secretaría Privada de la Presidencia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 206
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '206'),
    ('nombre', 'Agencia de El Salvador para la Cooperación Internacional')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 500
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '500'),
    ('nombre', 'Presidencia de la República')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 551
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '551'),
    ('nombre', 'Agencia de Promoción de Exportaciones e Inversiones de El Salvador')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 557
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '557'),
    ('nombre', 'Dirección Nacional de Compras Públicas')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 903
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '903'),
    ('nombre', 'MINED-FOSEDU')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 909
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '909'),
    ('nombre', 'PNC-FOSEDU')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 915
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '915'),
    ('nombre', 'ANSP-FOSEDU')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 917
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '917'),
    ('nombre', 'FGR-FOSEDU')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 944
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '944'),
    ('nombre', 'PGR-FOSEDU')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 945
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '945'),
    ('nombre', 'MJSP-DGCP-FOSEDU')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 2307
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2307'),
    ('nombre', 'Dirección de Integración')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 2308
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2308'),
    ('nombre', 'Dirección de Ordenamiento Territorial y Construcción')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 3108
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3108'),
    ('nombre', 'Consejo Nacional de la Niñez y de la Adolescencia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 3109
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3109'),
    ('nombre', 'Consejo Nacional de la Primera Infancia, Niñez y Adolescencia')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 3110
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3110'),
    ('nombre', 'Instituto Crecer Juntos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 3237
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3237'),
    ('nombre', 'Hospital Nacional El Salvador')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 4115
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4115'),
    ('nombre', 'Fondo de Inversión Nacional en Electricidad y Telefonía')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 4127
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4127'),
    ('nombre', 'Instituto Nacional de Capacitación y Formación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 4404
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4404'),
    ('nombre', 'Autoridad Salvadoreña del Agua')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 4603
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4603'),
    ('nombre', 'Autoridad de Planificación del Centro Histórico de San Salvador')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 99999
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '99999'),
    ('nombre', 'Banco Central de Reserva')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 207
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '207'),
    ('nombre', 'Organismo de Mejora Regulatoria')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA] 3241
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3241'),
    ('nombre', 'Superintendencia de Regulación Sanitaria')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ESTADO_PROYECTO — Estados de Proyecto (RN04)
-- Registros de nivel 1: 13   Campos: codigo, nombre
-- =====================================================================
-- [ESTADO_PROYECTO] En Elaboración
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'En Elaboración'),
    ('nombre', 'En Elaboración')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] Enviado a DGICP (Registro)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Enviado a DGICP (Registro)'),
    ('nombre', 'Enviado a DGICP (Registro)')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] Observado DGICP (Registro)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Observado DGICP (Registro)'),
    ('nombre', 'Observado DGICP (Registro)')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] CUP asignado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CUP asignado'),
    ('nombre', 'CUP asignado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] En F&E
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'En F&E'),
    ('nombre', 'En F&E')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] Proyecto formulado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Proyecto formulado'),
    ('nombre', 'Proyecto formulado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] Observado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Observado'),
    ('nombre', 'Observado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] En viabilidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'En viabilidad'),
    ('nombre', 'En viabilidad')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] Proyecto viable
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Proyecto viable'),
    ('nombre', 'Proyecto viable')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] En elegibilidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'En elegibilidad'),
    ('nombre', 'En elegibilidad')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] Proyecto elegible
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Proyecto elegible'),
    ('nombre', 'Proyecto elegible')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] En OT
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'En OT'),
    ('nombre', 'En OT')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;
-- [ESTADO_PROYECTO] Proyecto con O.T.
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Proyecto con O.T.'),
    ('nombre', 'Proyecto con O.T.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROYECTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo INTERESADO_TIPO — Catálogo de interesados (Tipo)
-- Registros de nivel 1: 4   Campos: codigo, nombre
-- =====================================================================
-- [INTERESADO_TIPO] Cooperante
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INTERESADO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Cooperante'),
    ('nombre', 'Cooperante')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INTERESADO_TIPO' AND t.field_name = v.field_name;
-- [INTERESADO_TIPO] Oponente
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INTERESADO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Oponente'),
    ('nombre', 'Oponente')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INTERESADO_TIPO' AND t.field_name = v.field_name;
-- [INTERESADO_TIPO] Beneficiario
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INTERESADO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Beneficiario'),
    ('nombre', 'Beneficiario')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INTERESADO_TIPO' AND t.field_name = v.field_name;
-- [INTERESADO_TIPO] Perjudicado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INTERESADO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Perjudicado'),
    ('nombre', 'Perjudicado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INTERESADO_TIPO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo REGION — Catálogo de ubicaciones geográficas (Región > Departamento > Distrito)
-- Registros de nivel 1: 4   Campos: codigo, nombre
-- =====================================================================
-- [REGION] Occidental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'REGION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Occidental'),
    ('nombre', 'Occidental')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'REGION' AND t.field_name = v.field_name;
-- [REGION] Central
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'REGION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Central'),
    ('nombre', 'Central')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'REGION' AND t.field_name = v.field_name;
-- [REGION] Oriental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'REGION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Oriental'),
    ('nombre', 'Oriental')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'REGION' AND t.field_name = v.field_name;
-- [REGION] Nivel nacional
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'REGION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Nivel nacional'),
    ('nombre', 'Nivel nacional')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'REGION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo TIPO_UNIDAD_MEDIDA — Catálogo de unidades de medida (Tipo > Categoría > Unidad)
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [TIPO_UNIDAD_MEDIDA] Bien
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Bien'),
    ('nombre', 'Bien')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_UNIDAD_MEDIDA' AND t.field_name = v.field_name;
-- [TIPO_UNIDAD_MEDIDA] Servicio
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Servicio'),
    ('nombre', 'Servicio')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_UNIDAD_MEDIDA' AND t.field_name = v.field_name;
-- [TIPO_UNIDAD_MEDIDA] Mixta
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Mixta'),
    ('nombre', 'Mixta')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_UNIDAD_MEDIDA' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo COMPONENTE_PROYECTO — Catálogo de componentes del proyecto
-- Registros de nivel 1: 7   Campos: codigo, nombre
-- =====================================================================
-- [COMPONENTE_PROYECTO] Infraestructura
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'COMPONENTE_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Infraestructura'),
    ('nombre', 'Infraestructura')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'COMPONENTE_PROYECTO' AND t.field_name = v.field_name;
-- [COMPONENTE_PROYECTO] Equipamiento
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'COMPONENTE_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Equipamiento'),
    ('nombre', 'Equipamiento')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'COMPONENTE_PROYECTO' AND t.field_name = v.field_name;
-- [COMPONENTE_PROYECTO] Capacitaciones
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'COMPONENTE_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Capacitaciones'),
    ('nombre', 'Capacitaciones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'COMPONENTE_PROYECTO' AND t.field_name = v.field_name;
-- [COMPONENTE_PROYECTO] Administración
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'COMPONENTE_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Administración'),
    ('nombre', 'Administración')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'COMPONENTE_PROYECTO' AND t.field_name = v.field_name;
-- [COMPONENTE_PROYECTO] Consultorías
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'COMPONENTE_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Consultorías'),
    ('nombre', 'Consultorías')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'COMPONENTE_PROYECTO' AND t.field_name = v.field_name;
-- [COMPONENTE_PROYECTO] Terrenos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'COMPONENTE_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Terrenos'),
    ('nombre', 'Terrenos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'COMPONENTE_PROYECTO' AND t.field_name = v.field_name;
-- [COMPONENTE_PROYECTO] Otros
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'COMPONENTE_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Otros'),
    ('nombre', 'Otros')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'COMPONENTE_PROYECTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo PROPIETARIO_TERRENO — Catálogo de propietarios
-- Registros de nivel 1: 5   Campos: codigo, nombre
-- =====================================================================
-- [PROPIETARIO_TERRENO] La Institución propietaria del proyecto
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROPIETARIO_TERRENO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'La Institución propietaria del proyecto'),
    ('nombre', 'La Institución propietaria del proyecto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROPIETARIO_TERRENO' AND t.field_name = v.field_name;
-- [PROPIETARIO_TERRENO] Otra Institución pública
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROPIETARIO_TERRENO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Otra Institución pública'),
    ('nombre', 'Otra Institución pública')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROPIETARIO_TERRENO' AND t.field_name = v.field_name;
-- [PROPIETARIO_TERRENO] La Municipalidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROPIETARIO_TERRENO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'La Municipalidad'),
    ('nombre', 'La Municipalidad')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROPIETARIO_TERRENO' AND t.field_name = v.field_name;
-- [PROPIETARIO_TERRENO] Comodato
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROPIETARIO_TERRENO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Comodato'),
    ('nombre', 'Comodato')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROPIETARIO_TERRENO' AND t.field_name = v.field_name;
-- [PROPIETARIO_TERRENO] Otros
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROPIETARIO_TERRENO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Otros'),
    ('nombre', 'Otros')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROPIETARIO_TERRENO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo MEDIO_AMBIENTAL — Catálogo Medio (componente ambiental afectado)
-- Registros de nivel 1: 7   Campos: codigo, nombre
-- =====================================================================
-- [MEDIO_AMBIENTAL] Físico - Agua
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEDIO_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Físico - Agua'),
    ('nombre', 'Físico - Agua')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEDIO_AMBIENTAL' AND t.field_name = v.field_name;
-- [MEDIO_AMBIENTAL] Físico - Tierra
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEDIO_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Físico - Tierra'),
    ('nombre', 'Físico - Tierra')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEDIO_AMBIENTAL' AND t.field_name = v.field_name;
-- [MEDIO_AMBIENTAL] Físico - Aire
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEDIO_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Físico - Aire'),
    ('nombre', 'Físico - Aire')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEDIO_AMBIENTAL' AND t.field_name = v.field_name;
-- [MEDIO_AMBIENTAL] Biológico - Flora
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEDIO_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Biológico - Flora'),
    ('nombre', 'Biológico - Flora')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEDIO_AMBIENTAL' AND t.field_name = v.field_name;
-- [MEDIO_AMBIENTAL] Biológico - Fauna
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEDIO_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Biológico - Fauna'),
    ('nombre', 'Biológico - Fauna')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEDIO_AMBIENTAL' AND t.field_name = v.field_name;
-- [MEDIO_AMBIENTAL] Social - Estructura Social
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEDIO_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Social - Estructura Social'),
    ('nombre', 'Social - Estructura Social')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEDIO_AMBIENTAL' AND t.field_name = v.field_name;
-- [MEDIO_AMBIENTAL] Social - Patrimonio Cultural
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEDIO_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Social - Patrimonio Cultural'),
    ('nombre', 'Social - Patrimonio Cultural')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEDIO_AMBIENTAL' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo IMPACTO_AMBIENTAL — Catálogo Impacto (ambiental)
-- Registros de nivel 1: 2   Campos: codigo, nombre
-- =====================================================================
-- [IMPACTO_AMBIENTAL] Positivo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'IMPACTO_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Positivo'),
    ('nombre', 'Positivo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'IMPACTO_AMBIENTAL' AND t.field_name = v.field_name;
-- [IMPACTO_AMBIENTAL] Negativo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'IMPACTO_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Negativo'),
    ('nombre', 'Negativo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'IMPACTO_AMBIENTAL' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo MAGNITUD_AMBIENTAL — Catálogo Magnitud (ambiental)
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [MAGNITUD_AMBIENTAL] Leve
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MAGNITUD_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Leve'),
    ('nombre', 'Leve')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MAGNITUD_AMBIENTAL' AND t.field_name = v.field_name;
-- [MAGNITUD_AMBIENTAL] Moderado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MAGNITUD_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Moderado'),
    ('nombre', 'Moderado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MAGNITUD_AMBIENTAL' AND t.field_name = v.field_name;
-- [MAGNITUD_AMBIENTAL] Fuerte
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MAGNITUD_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Fuerte'),
    ('nombre', 'Fuerte')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MAGNITUD_AMBIENTAL' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo DURACION_AMBIENTAL — Catálogo Duración (impacto ambiental)
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [DURACION_AMBIENTAL] Corto Plazo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DURACION_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Corto Plazo'),
    ('nombre', 'Corto Plazo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DURACION_AMBIENTAL' AND t.field_name = v.field_name;
-- [DURACION_AMBIENTAL] Mediano Plazo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DURACION_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Mediano Plazo'),
    ('nombre', 'Mediano Plazo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DURACION_AMBIENTAL' AND t.field_name = v.field_name;
-- [DURACION_AMBIENTAL] Largo Plazo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DURACION_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Largo Plazo'),
    ('nombre', 'Largo Plazo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DURACION_AMBIENTAL' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo REVERSIBILIDAD_AMBIENTAL — Catálogo Reversibilidad (impacto ambiental)
-- Registros de nivel 1: 2   Campos: codigo, nombre
-- =====================================================================
-- [REVERSIBILIDAD_AMBIENTAL] Reversible
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'REVERSIBILIDAD_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Reversible'),
    ('nombre', 'Reversible')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'REVERSIBILIDAD_AMBIENTAL' AND t.field_name = v.field_name;
-- [REVERSIBILIDAD_AMBIENTAL] Irreversible
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'REVERSIBILIDAD_AMBIENTAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Irreversible'),
    ('nombre', 'Irreversible')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'REVERSIBILIDAD_AMBIENTAL' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo PROBABILIDAD_RIESGO — Catálogo Probabilidad (análisis de riesgo)
-- Registros de nivel 1: 4   Campos: codigo, nombre
-- =====================================================================
-- [PROBABILIDAD_RIESGO] Improbable
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROBABILIDAD_RIESGO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Improbable'),
    ('nombre', 'Improbable')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROBABILIDAD_RIESGO' AND t.field_name = v.field_name;
-- [PROBABILIDAD_RIESGO] Probable
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROBABILIDAD_RIESGO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Probable'),
    ('nombre', 'Probable')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROBABILIDAD_RIESGO' AND t.field_name = v.field_name;
-- [PROBABILIDAD_RIESGO] Muy probable
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROBABILIDAD_RIESGO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Muy probable'),
    ('nombre', 'Muy probable')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROBABILIDAD_RIESGO' AND t.field_name = v.field_name;
-- [PROBABILIDAD_RIESGO] Casi seguro
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROBABILIDAD_RIESGO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Casi seguro'),
    ('nombre', 'Casi seguro')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROBABILIDAD_RIESGO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo IMPACTO_RIESGO — Catálogo Impacto (análisis de riesgo)
-- Registros de nivel 1: 5   Campos: codigo, nombre
-- =====================================================================
-- [IMPACTO_RIESGO] Insignificante
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'IMPACTO_RIESGO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Insignificante'),
    ('nombre', 'Insignificante')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'IMPACTO_RIESGO' AND t.field_name = v.field_name;
-- [IMPACTO_RIESGO] Bajo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'IMPACTO_RIESGO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Bajo'),
    ('nombre', 'Bajo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'IMPACTO_RIESGO' AND t.field_name = v.field_name;
-- [IMPACTO_RIESGO] Moderado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'IMPACTO_RIESGO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Moderado'),
    ('nombre', 'Moderado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'IMPACTO_RIESGO' AND t.field_name = v.field_name;
-- [IMPACTO_RIESGO] Alto
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'IMPACTO_RIESGO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Alto'),
    ('nombre', 'Alto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'IMPACTO_RIESGO' AND t.field_name = v.field_name;
-- [IMPACTO_RIESGO] Extremo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'IMPACTO_RIESGO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Extremo'),
    ('nombre', 'Extremo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'IMPACTO_RIESGO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo CALIFICACION_RIESGO_MATRIZ — Calificación del riesgo (matriz Probabilidad x Impacto)
-- Registros de nivel 1: 20   Campos: codigo, nombre, probabilidad, impacto, calificacion_resultante
-- =====================================================================
-- [CALIFICACION_RIESGO_MATRIZ] Improbable::Extremo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Improbable::Extremo'),
    ('nombre', 'Improbable x Extremo = Medio'),
    ('probabilidad', 'Improbable'),
    ('impacto', 'Extremo'),
    ('calificacion_resultante', 'Medio')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Improbable::Alto
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Improbable::Alto'),
    ('nombre', 'Improbable x Alto = Bajo'),
    ('probabilidad', 'Improbable'),
    ('impacto', 'Alto'),
    ('calificacion_resultante', 'Bajo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Improbable::Moderado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Improbable::Moderado'),
    ('nombre', 'Improbable x Moderado = Bajo'),
    ('probabilidad', 'Improbable'),
    ('impacto', 'Moderado'),
    ('calificacion_resultante', 'Bajo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Improbable::Bajo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Improbable::Bajo'),
    ('nombre', 'Improbable x Bajo = Bajo'),
    ('probabilidad', 'Improbable'),
    ('impacto', 'Bajo'),
    ('calificacion_resultante', 'Bajo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Improbable::Insignificante
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Improbable::Insignificante'),
    ('nombre', 'Improbable x Insignificante = Bajo'),
    ('probabilidad', 'Improbable'),
    ('impacto', 'Insignificante'),
    ('calificacion_resultante', 'Bajo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Probable::Extremo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Probable::Extremo'),
    ('nombre', 'Probable x Extremo = Alto'),
    ('probabilidad', 'Probable'),
    ('impacto', 'Extremo'),
    ('calificacion_resultante', 'Alto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Probable::Alto
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Probable::Alto'),
    ('nombre', 'Probable x Alto = Medio'),
    ('probabilidad', 'Probable'),
    ('impacto', 'Alto'),
    ('calificacion_resultante', 'Medio')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Probable::Moderado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Probable::Moderado'),
    ('nombre', 'Probable x Moderado = Medio'),
    ('probabilidad', 'Probable'),
    ('impacto', 'Moderado'),
    ('calificacion_resultante', 'Medio')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Probable::Bajo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Probable::Bajo'),
    ('nombre', 'Probable x Bajo = Bajo'),
    ('probabilidad', 'Probable'),
    ('impacto', 'Bajo'),
    ('calificacion_resultante', 'Bajo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Probable::Insignificante
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Probable::Insignificante'),
    ('nombre', 'Probable x Insignificante = Bajo'),
    ('probabilidad', 'Probable'),
    ('impacto', 'Insignificante'),
    ('calificacion_resultante', 'Bajo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Muy probable::Extremo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Muy probable::Extremo'),
    ('nombre', 'Muy probable x Extremo = Muy alto'),
    ('probabilidad', 'Muy probable'),
    ('impacto', 'Extremo'),
    ('calificacion_resultante', 'Muy alto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Muy probable::Alto
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Muy probable::Alto'),
    ('nombre', 'Muy probable x Alto = Alto'),
    ('probabilidad', 'Muy probable'),
    ('impacto', 'Alto'),
    ('calificacion_resultante', 'Alto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Muy probable::Moderado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Muy probable::Moderado'),
    ('nombre', 'Muy probable x Moderado = Alto'),
    ('probabilidad', 'Muy probable'),
    ('impacto', 'Moderado'),
    ('calificacion_resultante', 'Alto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Muy probable::Bajo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Muy probable::Bajo'),
    ('nombre', 'Muy probable x Bajo = Medio'),
    ('probabilidad', 'Muy probable'),
    ('impacto', 'Bajo'),
    ('calificacion_resultante', 'Medio')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Muy probable::Insignificante
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Muy probable::Insignificante'),
    ('nombre', 'Muy probable x Insignificante = Bajo'),
    ('probabilidad', 'Muy probable'),
    ('impacto', 'Insignificante'),
    ('calificacion_resultante', 'Bajo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Casi seguro::Extremo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Casi seguro::Extremo'),
    ('nombre', 'Casi seguro x Extremo = Muy alto'),
    ('probabilidad', 'Casi seguro'),
    ('impacto', 'Extremo'),
    ('calificacion_resultante', 'Muy alto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Casi seguro::Alto
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Casi seguro::Alto'),
    ('nombre', 'Casi seguro x Alto = Muy alto'),
    ('probabilidad', 'Casi seguro'),
    ('impacto', 'Alto'),
    ('calificacion_resultante', 'Muy alto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Casi seguro::Moderado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Casi seguro::Moderado'),
    ('nombre', 'Casi seguro x Moderado = Alto'),
    ('probabilidad', 'Casi seguro'),
    ('impacto', 'Moderado'),
    ('calificacion_resultante', 'Alto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Casi seguro::Bajo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Casi seguro::Bajo'),
    ('nombre', 'Casi seguro x Bajo = Medio'),
    ('probabilidad', 'Casi seguro'),
    ('impacto', 'Bajo'),
    ('calificacion_resultante', 'Medio')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;
-- [CALIFICACION_RIESGO_MATRIZ] Casi seguro::Insignificante
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Casi seguro::Insignificante'),
    ('nombre', 'Casi seguro x Insignificante = Bajo'),
    ('probabilidad', 'Casi seguro'),
    ('impacto', 'Insignificante'),
    ('calificacion_resultante', 'Bajo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CALIFICACION_RIESGO_MATRIZ' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo INSUMO_TIPO — Catálogo "Insumo Tipo"
-- Registros de nivel 1: 8   Campos: codigo, nombre, factor_correccion
-- =====================================================================
-- [INSUMO_TIPO] Mano de obra calificada
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSUMO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Mano de obra calificada'),
    ('nombre', 'Mano de obra calificada'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSUMO_TIPO' AND t.field_name = v.field_name;
-- [INSUMO_TIPO] Mano de obra semi calificada
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSUMO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Mano de obra semi calificada'),
    ('nombre', 'Mano de obra semi calificada'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSUMO_TIPO' AND t.field_name = v.field_name;
-- [INSUMO_TIPO] Mano de obra no calificada
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSUMO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Mano de obra no calificada'),
    ('nombre', 'Mano de obra no calificada'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSUMO_TIPO' AND t.field_name = v.field_name;
-- [INSUMO_TIPO] Bienes importados
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSUMO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Bienes importados'),
    ('nombre', 'Bienes importados'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSUMO_TIPO' AND t.field_name = v.field_name;
-- [INSUMO_TIPO] Bienes nacionales
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSUMO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Bienes nacionales'),
    ('nombre', 'Bienes nacionales'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSUMO_TIPO' AND t.field_name = v.field_name;
-- [INSUMO_TIPO] Combustibles-Energía
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSUMO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Combustibles-Energía'),
    ('nombre', 'Combustibles-Energía'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSUMO_TIPO' AND t.field_name = v.field_name;
-- [INSUMO_TIPO] Servicios
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSUMO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Servicios'),
    ('nombre', 'Servicios'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSUMO_TIPO' AND t.field_name = v.field_name;
-- [INSUMO_TIPO] Otros
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSUMO_TIPO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Otros'),
    ('nombre', 'Otros'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSUMO_TIPO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo FUENTE_FINANCIAMIENTO — Catálogo "Fuentes de Financiamiento"
-- Registros de nivel 1: 7   Campos: codigo, nombre
-- =====================================================================
-- [FUENTE_FINANCIAMIENTO] 0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '0'),
    ('nombre', 'Sin Financiamiento')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [FUENTE_FINANCIAMIENTO] 1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1'),
    ('nombre', 'Fondo General')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [FUENTE_FINANCIAMIENTO] 2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2'),
    ('nombre', 'Recursos Propios')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [FUENTE_FINANCIAMIENTO] 3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3'),
    ('nombre', 'Préstamos Externos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [FUENTE_FINANCIAMIENTO] 4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4'),
    ('nombre', 'Préstamos Internos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [FUENTE_FINANCIAMIENTO] 5
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '5'),
    ('nombre', 'Donaciones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [FUENTE_FINANCIAMIENTO] 9
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '9'),
    ('nombre', 'Otros')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_FINANCIAMIENTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo FUENTE_RECURSOS — Catálogo "Fuentes de Recursos"
-- Registros de nivel 1: 191   Campos: codigo, nombre, codigo_pais, codigo_clase_recurso
-- =====================================================================
-- [FUENTE_RECURSOS] -5
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '-5'),
    ('nombre', 'Donación'),
    ('codigo_pais', '1'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] -4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '-4'),
    ('nombre', 'Préstamo Interno'),
    ('codigo_pais', '1'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] -3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '-3'),
    ('nombre', 'Préstamo Externo'),
    ('codigo_pais', '1'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '0'),
    ('nombre', 'S-D'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1'),
    ('nombre', 'AGENCIA PARA EL DESARROLLO INTERNACIONAL'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2'),
    ('nombre', 'BANCO CENTROAMERICANO DE INTEGRACION ECONOMICA'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3'),
    ('nombre', 'BANCO INTERAMERICANO DE DESARROLLO'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4'),
    ('nombre', 'BANCO INTERNACIONAL DE RECONSTRUCCION Y FOMENTO'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 5
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '5'),
    ('nombre', 'BANCO VILBAO VIZCAYA S.A. (ESPAÑA)'),
    ('codigo_pais', '8'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 6
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '6'),
    ('nombre', 'DUCADO DE LUXEMBURGO'),
    ('codigo_pais', '16'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 7
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '7'),
    ('nombre', 'FONDO GENERAL'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 8
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '8'),
    ('nombre', 'RECURSOS PROPIOS SETEFE'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 9
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '9'),
    ('nombre', 'RECURSOS PROPIOS'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 10
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '10'),
    ('nombre', 'JAPAN BANK FOR INTERNATIONAL CORPORATION'),
    ('codigo_pais', '14'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 11
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '11'),
    ('nombre', 'FONDO SALVADOREÑO PARA ESTUDIOS DE PREINVERSION'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 12
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '12'),
    ('nombre', 'GOBIERNO DE HOLANDA'),
    ('codigo_pais', '10'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 13
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '13'),
    ('nombre', 'GOBIERNO DE JAPON'),
    ('codigo_pais', '14'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 14
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '14'),
    ('nombre', 'GOBIERNO DE SUIZA'),
    ('codigo_pais', '19'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 15
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '15'),
    ('nombre', 'PL-480-93'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 16
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '16'),
    ('nombre', 'FONDO INTERNACIONAL DE DESARROLLO AGRICOLA'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 17
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '17'),
    ('nombre', 'UNION EUROPEA'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 18
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '18'),
    ('nombre', 'KfW - BANCO ALEMAN DE FOMENTO Y RECONSTRUCCION'),
    ('codigo_pais', '1'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 19
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '19'),
    ('nombre', 'GOBIERNO DE FRANCIA-CREDIT NATIONAL'),
    ('codigo_pais', '9'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 20
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '20'),
    ('nombre', 'RECURSOS PROVENIENTES DE LA VENTA DE CEL'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 21
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '21'),
    ('nombre', 'FONDO DE LAS NACIONES UNIDAS P-LA INFANCIA'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 22
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '22'),
    ('nombre', 'COMUNIDAD'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 23
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '23'),
    ('nombre', 'OTRAS FUENTES INTERNAS'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 24
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '24'),
    ('nombre', 'GOBIERNO DE ESPAÑA'),
    ('codigo_pais', '8'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 25
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '25'),
    ('nombre', 'GOBIERNO DE CHINA'),
    ('codigo_pais', '24'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 26
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '26'),
    ('nombre', 'FONDO DE INVERSIONES DE VENEZUELA'),
    ('codigo_pais', '22'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 27
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '27'),
    ('nombre', 'GOBIERNO DE COREA'),
    ('codigo_pais', '15'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 28
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '28'),
    ('nombre', 'PROCHALATE'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 29
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '29'),
    ('nombre', 'JUNTA DE ANDALUCIA'),
    ('codigo_pais', '8'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 30
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '30'),
    ('nombre', 'PROGRAMA DE LAS NACIONES UNIDAS PARA EL DESARROLLO'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 31
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '31'),
    ('nombre', 'GOBIERNO DE COLOMBIA'),
    ('codigo_pais', '6'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 32
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '32'),
    ('nombre', 'GOBIERNO DE BRASIL'),
    ('codigo_pais', '3'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 33
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '33'),
    ('nombre', 'GOBIERNO DE NORUEGA'),
    ('codigo_pais', '18'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 34
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '34'),
    ('nombre', 'ORGANIZACION PANAMERICANA DE SALUD'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 35
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '35'),
    ('nombre', 'UNFPA'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 36
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '36'),
    ('nombre', 'CONGRESO -USA-'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 37
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '37'),
    ('nombre', 'FONDOS PROV.DE LA RECONST.NACIONAL'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 38
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '38'),
    ('nombre', 'FONDO DE MICROEMPRESAS BCR-BMI'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 39
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '39'),
    ('nombre', 'ORGANISMO DE ESTADOS AMERICANOS'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 40
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '40'),
    ('nombre', 'AGENCIA DE EE.UU. DEL COMERCIO Y DESARROLLO.'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 41
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '41'),
    ('nombre', 'COOPERATIVA AMERICANA DE AYUDA AL EXTERIOR'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 42
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '42'),
    ('nombre', 'FUNDACION PARA LA SALUD'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 43
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '43'),
    ('nombre', 'OTRAS FUENTES EXTERNAS'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 44
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '44'),
    ('nombre', 'FONDOS TAIWAN'),
    ('codigo_pais', '20'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 45
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '45'),
    ('nombre', 'DEPARTAMENTO DE AGRICULTURA DE LOS ESTADOS UNIDOS'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 46
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '46'),
    ('nombre', 'CAPITAL FANTEL'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 47
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '47'),
    ('nombre', 'INTERESES FANTEL'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 48
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '48'),
    ('nombre', 'Fondo de Inversión Nacional en Electricidad y Telefonía'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 49
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '49'),
    ('nombre', 'AGENCIA DE LOS EEUU PARA EL DESARROLLO INTERNAC.'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 61
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '61'),
    ('nombre', 'BONOS DE EMERGENCIA - RECONSTRUCCION'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 64
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '64'),
    ('nombre', 'GOBIERNO DE ALEMANIA'),
    ('codigo_pais', '1'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 65
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '65'),
    ('nombre', 'GOBIERNO DE ITALIA'),
    ('codigo_pais', '13'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 66
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '66'),
    ('nombre', 'AGENCIA DE COOPERACION INTERNACIONAL DEL JAPAN'),
    ('codigo_pais', '14'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 70
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '70'),
    ('nombre', 'PL-480-2001'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 71
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '71'),
    ('nombre', 'GOBIERNO DE PUERTO RICO'),
    ('codigo_pais', '23'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 75
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '75'),
    ('nombre', 'ORGANIZACION DE LAS NAC. UNIDAS PARA LA AGRIC. Y LA ALIME'),
    ('codigo_pais', '17'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 76
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '76'),
    ('nombre', 'FONDO DE DESARROLLO ECONOMICO Y SOCIAL'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 78
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '78'),
    ('nombre', 'FONDO DE CONVERSION DE DEUDA FRANCO SALVADOREÑO'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 79
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '79'),
    ('nombre', 'BONOS'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 80
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '80'),
    ('nombre', 'INSTITUTO SALVADOREÑO DE FORMACION PROFESIONAL'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 81
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '81'),
    ('nombre', 'COMISION NACIONAL DE LA MICRO Y PEQUEÑA EMPRESA'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 82
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '82'),
    ('nombre', 'BENEFICIARIOS'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 83
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '83'),
    ('nombre', 'FIDEICOMISO MAG-BFA-PRODAP'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 84
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '84'),
    ('nombre', 'DEUTSCHE BANK'),
    ('codigo_pais', '1'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 85
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '85'),
    ('nombre', 'EXPORT-IMPORT BANK OF CHINA'),
    ('codigo_pais', '20'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 86
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '86'),
    ('nombre', 'FONDO INTERNACIONAL DE COOPERACION Y DESARROLLO DE LA REPUBLICA DE CHINA'),
    ('codigo_pais', '20'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 87
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '87'),
    ('nombre', 'INSTITUTO DE CREDITO OFICIAL'),
    ('codigo_pais', '8'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 88
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '88'),
    ('nombre', 'FONDO MUNDIAL PARA EL MEDIO AMBIENTE'),
    ('codigo_pais', '14'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 89
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '89'),
    ('nombre', 'RECURSOS PROPIOS EN ESPECIE'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 90
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '90'),
    ('nombre', 'ESPAÑA CANJE DE DEUDA'),
    ('codigo_pais', '8'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 91
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '91'),
    ('nombre', 'INTERVIDA EL SALVADOR (ONG)'),
    ('codigo_pais', '99'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 92
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '92'),
    ('nombre', 'PLAN EL SALVADOR (ONG)'),
    ('codigo_pais', '99'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 93
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '93'),
    ('nombre', 'MILENIUM CHALLENGE CORPORATION'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 94
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '94'),
    ('nombre', 'BELL HELICOPTER TEXTRON'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 95
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '95'),
    ('nombre', 'EXPORT DEVELOPMENT CORPORATION'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 96
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '96'),
    ('nombre', 'TEXTRON FINANCIAL CORPORATION'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 97
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '97'),
    ('nombre', 'Agencia Española de Cooperación Internacional para el Desarrollo'),
    ('codigo_pais', '8'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 99
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '99'),
    ('nombre', 'SIN FINANCIAMIENTO'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 101
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '101'),
    ('nombre', 'Fdo. Especial de los Rec. de la Priv. de ANTEL'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 109
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '109'),
    ('nombre', 'THE J. PAUL GETTY TRUST'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 110
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '110'),
    ('nombre', 'FODES 20% Gasto Corriente'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 112
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '112'),
    ('nombre', 'Fondos para Inversión FISDL'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 113
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '113'),
    ('nombre', 'Viceministerio de Vivienda y Desarrollo Urbano'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 117
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '117'),
    ('nombre', 'Fideicomiso para Inversión en Educación, Paz Soci'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 119
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '119'),
    ('nombre', 'Fundación privada CETEMMSA-España'),
    ('codigo_pais', '8'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 120
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '120'),
    ('nombre', 'Fondo Salvadoreño para la Cooperación Mixta España'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 121
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '121'),
    ('nombre', 'FONDO DE DESARROLLO CANADIENSE'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 122
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '122'),
    ('nombre', 'COMMODITY CREDIT CORPORATION'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 124
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '124'),
    ('nombre', 'Comision Centroaméricana de Ambiente y Desarrollo'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 125
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '125'),
    ('nombre', 'Radda Barnen de Suecia'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 126
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '126'),
    ('nombre', 'Caja de Ahorro y Pensiones de Barcelona "LA CAIXA"'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 127
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '127'),
    ('nombre', 'Comision para el Desarrollo Cientifico y Tecnológi'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 128
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '128'),
    ('nombre', 'Programa de las Naciones Unidas para el Medio Ambi'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 129
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '129'),
    ('nombre', 'Agencia Sueca de Cooperacion para el Desarrollo In'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 130
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '130'),
    ('nombre', 'Gobierno de Inglaterra'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 131
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '131'),
    ('nombre', 'Gobierno de Irlanda'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 133
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '133'),
    ('nombre', 'Asociación de Fomento Internacional'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 134
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '134'),
    ('nombre', 'Asociación Multilateral de Garantía a las Inversio'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 135
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '135'),
    ('nombre', 'Banco de Comercio de Mexico'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 136
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '136'),
    ('nombre', 'Bancos Franceses'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 137
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '137'),
    ('nombre', 'Corporación Financiera Internacional'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 138
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '138'),
    ('nombre', 'Corporación Interamericana de Inversiones'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 139
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '139'),
    ('nombre', 'Comunidad Económica Europea'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 140
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '140'),
    ('nombre', 'Eximbank del Japón'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 141
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '141'),
    ('nombre', 'Fondo de Finaciamiento para la Exportación'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 142
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '142'),
    ('nombre', 'Gobierno de Argentina'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 143
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '143'),
    ('nombre', 'Gobierno de Mexico'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 145
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '145'),
    ('nombre', 'Organización de Paises Exportadores de Petróleo OPEP'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 146
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '146'),
    ('nombre', 'Societe Generale de Banque'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 147
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '147'),
    ('nombre', 'United States Trust Co. of New York'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 149
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '149'),
    ('nombre', 'Agencia Alemana de Cooperación Técnica (GTZ)'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 150
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '150'),
    ('nombre', 'Organización Internacional del Trabajo'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 151
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '151'),
    ('nombre', 'Programa Mundial de Alimentos'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 152
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '152'),
    ('nombre', 'City Bank'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 153
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '153'),
    ('nombre', 'Chemical Bank New York, U.S.A.'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 154
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '154'),
    ('nombre', 'Gobierno de Bélgica'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 156
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '156'),
    ('nombre', 'Banco Paribas'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 157
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '157'),
    ('nombre', 'Nations Bank'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 158
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '158'),
    ('nombre', 'General Bank Belgica'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 159
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '159'),
    ('nombre', 'Eximbank de Estados Unidos'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 160
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '160'),
    ('nombre', 'Coordinadora Educativa Cultural Centroamericana'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 161
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '161'),
    ('nombre', 'Organización de las Naciones Unidas para la Educación, la Ciencia y la Cultura'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 162
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '162'),
    ('nombre', 'C. Itom Japon'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 163
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '163'),
    ('nombre', 'Francesa de Seguros para Comercio Exterior'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 164
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '164'),
    ('nombre', 'Otras Fuentes'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 165
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '165'),
    ('nombre', 'The Riggs National Bank (Weeber Inc.)'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 166
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '166'),
    ('nombre', 'Phlcorp inc.'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 168
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '168'),
    ('nombre', 'Alto Comisionado de las Naciones Unidas para los Refugiados'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 169
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '169'),
    ('nombre', 'Fundación Paz y Solidaridad de las Comisiones Obre'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 170
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '170'),
    ('nombre', 'Organizacion Holandesa para la Coop Internc. al D'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 171
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '171'),
    ('nombre', 'Gobierno de los Estados Unidos de América'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 172
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '172'),
    ('nombre', 'Fondo Español de Cooperacion para Agua y Saneamiento en América Latian y el Caribe'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 173
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '173'),
    ('nombre', 'OXFAM AMERICA'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 174
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '174'),
    ('nombre', 'Embajada de Canadá'),
    ('codigo_pais', '4'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 175
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '175'),
    ('nombre', 'Alianza en Energía y Ambiente con Centroamérica'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 176
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '176'),
    ('nombre', 'Fondo Común de Apoyo Programático - FOCAP'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 177
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '177'),
    ('nombre', 'FIDEICOMISO PRODERNOR'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 178
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '178'),
    ('nombre', 'Facilidad de Inversión en América Latina (Latin American Investment Facility – LAIF)'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 179
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '179'),
    ('nombre', 'Fundacion para la Hemofilia Novo Nordisk FHNN'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 180
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '180'),
    ('nombre', 'Philip Morris International'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 181
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '181'),
    ('nombre', 'Foreing Affairs and International Trade Canada'),
    ('codigo_pais', '4'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 182
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '182'),
    ('nombre', 'Centro Internacional de Agricultura Tropical'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 183
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '183'),
    ('nombre', 'Health Focus GmbH'),
    ('codigo_pais', '1'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 184
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '184'),
    ('nombre', 'Banco de Desarrollo de El Salvador'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 185
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '185'),
    ('nombre', 'Agencia Australiana de Cooperación Internacional'),
    ('codigo_pais', '25'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 186
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '186'),
    ('nombre', 'Estado de Qatar'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 187
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '187'),
    ('nombre', 'FIDEICOMISO DE APOYO A LA PRODUCCION DE CAFE'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 188
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '188'),
    ('nombre', 'Institución Financiera Italiana (IFI) Artigiancassa S.p.A.'),
    ('codigo_pais', '13'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 189
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '189'),
    ('nombre', 'Agencia Alemana de Cooperación International (GIZ)'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 190
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '190'),
    ('nombre', 'Fondo de la OPEP para el Desarrollo Internacional (OFID)'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 191
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '191'),
    ('nombre', 'Fundación FORD'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 192
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '192'),
    ('nombre', 'Organización Internacional de las Migraciones'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 194
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '194'),
    ('nombre', 'Asociación Enfants Du Monde'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 195
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '195'),
    ('nombre', 'TEFEX S.A de C.V.'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 196
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '196'),
    ('nombre', 'Fondo Mundial de lucha contra el SIDA, la tuberculosis y la malaria'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 197
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '197'),
    ('nombre', 'Entidad de la ONU para la Igualdad de Género y el Empoderamiento de la Mujer'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 198
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '198'),
    ('nombre', 'Comisión Ejecutiva Portuaria Autónoma'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 199
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '199'),
    ('nombre', 'Consejo Internacional para la Ciencia'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 200
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '200'),
    ('nombre', 'Fondo de Compensación Ambiental'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 201
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '201'),
    ('nombre', 'FIDEICOMISO FIDA'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 202
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '202'),
    ('nombre', 'Cassa Depositi e Prestiti S.p.A.'),
    ('codigo_pais', '13'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 203
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '203'),
    ('nombre', 'Fundación Howard G. Buffett'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 204
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '204'),
    ('nombre', 'Fondos de Sentencias Judiciales por Daños Ambientales'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 206
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '206'),
    ('nombre', 'Agencia Catalana de Cooperación al Desarrollo'),
    ('codigo_pais', '8'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 207
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '207'),
    ('nombre', 'Departamento de Justicia de los Estados Unidos de América'),
    ('codigo_pais', '21'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 208
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '208'),
    ('nombre', 'Contribución Especial para la Seguridad Ciudadana'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 209
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '209'),
    ('nombre', 'Contribución Especial a los Grandes Contribuyentes'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 210
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '210'),
    ('nombre', 'Fondo de Protección Civil, Prevención y Mitigación de Desastres'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 211
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '211'),
    ('nombre', 'Reino de Marruecos'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 212
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '212'),
    ('nombre', 'Organización Internacional de Entidades Fiscalizadoras Superiores'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 213
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '213'),
    ('nombre', 'Agencia Internacional de Energías Renovables'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 214
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '214'),
    ('nombre', 'Principado de Andorra'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 215
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '215'),
    ('nombre', 'Banco Hipotecario de El Salvador'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 216
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '216'),
    ('nombre', 'Corporación Andina de Fomento - CAF'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 217
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '217'),
    ('nombre', 'Alianza Mundial para la Educación'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 218
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '218'),
    ('nombre', 'Reino de Arabia Saudita'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 219
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '219'),
    ('nombre', 'Banco de America Central'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 220
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '220'),
    ('nombre', 'Consejo Nacional para la Protección y Desarrollo de la Persona Migrante y su Familia'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 221
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '221'),
    ('nombre', 'Embajada de Japón'),
    ('codigo_pais', '14'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 222
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '222'),
    ('nombre', 'Agencia Italiana de Cooperación para el Desarrollo'),
    ('codigo_pais', '13'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 223
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '223'),
    ('nombre', 'Fondo Saudita para el Desarrollo'),
    ('codigo_pais', '26'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 224
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '224'),
    ('nombre', 'Yutong Bus Co. LTD.'),
    ('codigo_pais', '27'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 225
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '225'),
    ('nombre', 'Programa Adopting El Salvador'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 227
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '227'),
    ('nombre', 'Gobierno de la India'),
    ('codigo_pais', '0'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 229
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '229'),
    ('nombre', 'Banco de Fomento Agropecuario'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 230
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '230'),
    ('nombre', 'Agencia Luxemburguesa para la Cooperación al Desarrollo'),
    ('codigo_pais', '16'),
    ('codigo_clase_recurso', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;
-- [FUENTE_RECURSOS] 901
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_RECURSOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '901'),
    ('nombre', 'RECURSOS PROPIOS - CEPA'),
    ('codigo_pais', '7'),
    ('codigo_clase_recurso', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_RECURSOS' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo CONVENIO_FINANCIAMIENTO — Catálogo "Convenios"
-- Registros de nivel 1: 392   Campos: codigo, nombre, moneda, monto, numero_sigade, codigo_tipo_convenio
-- =====================================================================
-- [CONVENIO_FINANCIAMIENTO] 132-0A1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '132-0A1'),
    ('nombre', 'EXPLOTAC. DE LOS REC.PES.'),
    ('moneda', 'FRF'),
    ('monto', '32664400'),
    ('numero_sigade', 'BFR0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] 2003-65-718
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2003-65-718'),
    ('nombre', 'PROG. FOMENTO DEL DESARROLLO LOCAL Y GOBERNANZA - FISDL IV'),
    ('moneda', 'EU'),
    ('monto', '13994257.02'),
    ('numero_sigade', 'BAL0018'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] 519-HG-006-AQ1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '519-HG-006-AQ1'),
    ('nombre', 'FINAN.DEUDA CONTR-PAINE WEBBER'),
    ('moneda', 'USD'),
    ('monto', '8415312'),
    ('numero_sigade', 'BEU0015'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] 519-HG-007
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '519-HG-007'),
    ('nombre', 'FINAN.DEUDA CON PAINE WEBBER'),
    ('moneda', 'USD'),
    ('monto', '5500000'),
    ('numero_sigade', 'BEU0016'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] 519-I-049 30% NO CON
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '519-I-049 30% NO CON'),
    ('nombre', '519-I-049 30% NO CONDONADA'),
    ('moneda', 'USD'),
    ('monto', '83764047'),
    ('numero_sigade', 'BEU0012'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] 880-SAL-8555
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '880-SAL-8555'),
    ('nombre', 'COMPRA DE 6 HELIC. FUERZA ARMA'),
    ('moneda', 'USD'),
    ('monto', '33501559'),
    ('numero_sigade', 'PC00001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] ADMINISTRACIÓN DEL PROGRAMA DE BECAS DE EDUCACIÓN SUPERIOR
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ADMINISTRACIÓN DEL PROGRAMA DE BECAS DE EDUCACIÓN SUPERIOR'),
    ('nombre', 'ADMINISTRACIÓN DEL PROGRAMA DE BECAS DE EDUCACIÓN SUPERIOR ENTRE FANTEL Y PRESIDENCIA DE LA REPÚBLICA DE EL SALVADOR'),
    ('moneda', 'USD'),
    ('monto', '21000000'),
    ('numero_sigade', '-'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] AID 9962
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'AID 9962'),
    ('nombre', 'Programa de prevención y rehabilitación para jóvenes en situación de riesgo y conflicto con la ley en El Salvador'),
    ('moneda', 'EUR'),
    ('monto', '5550000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] AID-519-0462
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'AID-519-0462'),
    ('nombre', 'CRECIMIENTO ECONOMICO PARA EL SIGLO XXI'),
    ('moneda', 'USD'),
    ('monto', '10000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] AID-519-HR-001 AQ1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'AID-519-HR-001 AQ1'),
    ('nombre', 'AID-519-HR-001 AQ1'),
    ('moneda', 'USD'),
    ('monto', '3478404.9'),
    ('numero_sigade', 'BEU0013'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] AIF-0031-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'AIF-0031-0 ES'),
    ('nombre', 'Tercer Proyecto de Carreteras'),
    ('moneda', 'USD'),
    ('monto', '9645203'),
    ('numero_sigade', 'MAI0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] AIF-0517-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'AIF-0517-0 ES'),
    ('nombre', 'Proyecto de Lotes con Servicio'),
    ('moneda', 'USD'),
    ('monto', '6000000'),
    ('numero_sigade', 'MAI0003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] AIF-0726-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'AIF-0726-0 ES'),
    ('nombre', 'SEG. PROY. DE DES. URB.'),
    ('moneda', 'USD'),
    ('monto', '6000000'),
    ('numero_sigade', 'MAI0004'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] AIF-227-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'AIF-227-0 ES'),
    ('nombre', 'CREDITO PARA DESARROLLO'),
    ('moneda', 'USD'),
    ('monto', '6343811'),
    ('numero_sigade', 'MAI0002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] ALA-2005-17-587
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ALA-2005-17-587'),
    ('nombre', 'ALA-2005-17-587'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BANCO-COMERCIAL
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BANCO-COMERCIAL'),
    ('nombre', 'BCOM-HUELLA'),
    ('moneda', 'USD'),
    ('monto', '787194'),
    ('numero_sigade', 'PBC0007'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BBV 2130-SV-3773
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BBV 2130-SV-3773'),
    ('nombre', 'SEMAFOR.ELECTRON.MULT.'),
    ('moneda', 'USD'),
    ('monto', '4182092'),
    ('numero_sigade', 'PBC00001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BBV 2130-SV-4045
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BBV 2130-SV-4045'),
    ('nombre', 'EQUIP.HOSP. MED.QUIRUR1,2,3NIV'),
    ('moneda', 'USD'),
    ('monto', '3554876'),
    ('numero_sigade', 'PBC00003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BBV2130-SV-3935
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BBV2130-SV-3935'),
    ('nombre', 'EQUIP. MED.IND.RED HOSPIT.'),
    ('moneda', 'USD'),
    ('monto', '3554731'),
    ('numero_sigade', 'PBC00002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BBV2130-SV-4003
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BBV2130-SV-4003'),
    ('nombre', 'sum.bienese transporte Eq.PNc'),
    ('moneda', 'USD'),
    ('monto', '6803472'),
    ('numero_sigade', 'PBC00004'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 1132-0-1 ANDA
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 1132-0-1 ANDA'),
    ('nombre', 'BCIE 1132-0-1 ANDA'),
    ('moneda', 'USD'),
    ('monto', '14698690'),
    ('numero_sigade', 'MBCT001'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 1769
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 1769'),
    ('nombre', 'PROGRAMA CONECTATE'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', 'MBC0039'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 1773
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 1773'),
    ('nombre', 'CENTRAL TERMICA DE TALNIQUE (50 MW)'),
    ('moneda', 'USD'),
    ('monto', '60000000'),
    ('numero_sigade', 'DMBC0001'),
    ('codigo_tipo_convenio', '6')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 1865
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 1865'),
    ('nombre', 'BCIE-1865'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', 'MBCCEL1865'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 1886
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 1886'),
    ('nombre', 'MEJOR. Y CONSERVACION DE VIAS NO PAVIMENTADAS DEL PROGR. DE VIAS SUBURBANAS Y CAMINOS RURALES'),
    ('moneda', 'USD'),
    ('monto', '60000000'),
    ('numero_sigade', 'DMBC0002'),
    ('codigo_tipo_convenio', '6')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 1888
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 1888'),
    ('nombre', 'MODERNIZACION DEL REGISTRO INMOBILIARIO Y DEL CATASTRO - FASE II'),
    ('moneda', 'USD'),
    ('monto', '55000000'),
    ('numero_sigade', 'MBCCNR'),
    ('codigo_tipo_convenio', '6')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2-0242-0 ISDEM
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2-0242-0 ISDEM'),
    ('nombre', 'BCIE 2-0242-0 ISDEM ALUMBRADO'),
    ('moneda', 'USD'),
    ('monto', '2729925'),
    ('numero_sigade', 'FMBC001'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2015
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2015'),
    ('nombre', 'Programa de Infraestructura Social y Prevención de Vulnerabilidades'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', 'MBC0038'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2015, 2139
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2015, 2139'),
    ('nombre', 'BCIE 2015, 2139'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', '-'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2031
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2031'),
    ('nombre', 'Proyecto Apertura del Boulevard Diego de Holgin, Santa Tecla'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', 'MBC0036'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2059
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2059'),
    ('nombre', '--'),
    ('moneda', 'USD'),
    ('monto', '57500000'),
    ('numero_sigade', 'MBC2059'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2067
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2067'),
    ('nombre', 'PROGRAMA DE CONECTIVIDAD DE LA INFRAESTRUCTURA VIAL PARA EL DESARROLLO'),
    ('moneda', 'USD'),
    ('monto', '48200000'),
    ('numero_sigade', 'MBC0040'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2077
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2077'),
    ('nombre', 'Plan de Agricultura Familiar y Emprededurismo Rural para la Seguridad Alimentaria y Nutricional'),
    ('moneda', 'USD'),
    ('monto', '2000000'),
    ('numero_sigade', 'MBC0042'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2077, 2139
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2077, 2139'),
    ('nombre', 'BCIE 2077, 2139'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', '-'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2102
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2102'),
    ('nombre', 'Programa de Fortalecimiento del Sistema Penitenciario en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '71000000'),
    ('numero_sigade', 'MBC0043'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2102, 2139
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2102, 2139'),
    ('nombre', 'BCIE 2102, 2139'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', '-'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2114
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2114'),
    ('nombre', 'Proyecto Construcción de Planta Fotovoltaica 15 de Septiembre'),
    ('moneda', 'USD'),
    ('monto', '15000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '6')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2120
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2120'),
    ('nombre', 'Ampliación de la Carretera al Puerto de La Libertad Tramos II y III, Construcción del Puente General Manuel José Arce sobre el Río Paz en la Frontera de La Hachadura, y Construcción del Puente sobre el Río Anguiatú en la Frontera de Anguiatú'),
    ('moneda', 'USD'),
    ('monto', '144708600'),
    ('numero_sigade', 'MBC0046'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2127
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2127'),
    ('nombre', 'Construcción y Equipamiento de Edificio para Oficinas de Diputados y Grupos Parlamentarios de la Asamblea Legislativa de la República de El Salvador'),
    ('moneda', 'USD'),
    ('monto', '32000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2139
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2139'),
    ('nombre', 'Apoyo a Proyectos de Inversión Productiva y Social'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'MBC0045'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2143
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2143'),
    ('nombre', 'Construcción de la Central Hidroeléctrica el Chaparral'),
    ('moneda', 'USD'),
    ('monto', '125000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2146
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2146'),
    ('nombre', 'Construcción, Equipamiento y Modernización de las Oficinas Centrales de la FGR de El Salvador'),
    ('moneda', 'USD'),
    ('monto', '44887500'),
    ('numero_sigade', 'MBC0044'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2152
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2152'),
    ('nombre', 'Rehabilitación de la Planta Potabilizadora de Las Pavas'),
    ('moneda', 'USD'),
    ('monto', '16982500'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2234
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2234'),
    ('nombre', 'Construcción, Equipamiento y Modernización de las Oficinas Centrales de la Fiscalía General de la República'),
    ('moneda', 'USD'),
    ('monto', '25338586.48'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2237
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2237'),
    ('nombre', 'Prog. de Desarrollo Soc en el Marco del Prog. de Finan. del Plan Control Territorial en su Fase II'),
    ('moneda', 'USD'),
    ('monto', '91000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2240
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2240'),
    ('nombre', 'Programa de Modernización de las Instituciones de Seguridad Ciudadana en el Marco del Financiamiento del Plan Control Territorial en su Fase III'),
    ('moneda', 'USD'),
    ('monto', '109000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2243
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2243'),
    ('nombre', 'Proyecto Construcción de Viaducto y Ampliación de Carretera CA01W (Tramo Los Chorros), entre Autopista Monseñor Romero y CA01W; Municipios de Santa Tecla, Colón y San Juan Opico, Departamento de La Libertad'),
    ('moneda', 'USD'),
    ('monto', '245824129'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2254
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2254'),
    ('nombre', 'Programa de Construcción de Infraestructura y Rescate de Escenarios Deportivos a Nivel Nacional (PRODEPORTE)'),
    ('moneda', 'USD'),
    ('monto', '115200000'),
    ('numero_sigade', 'MBC0054'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2256
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2256'),
    ('nombre', 'Programa Mi Nueva Escuela'),
    ('moneda', 'USD'),
    ('monto', '200000000'),
    ('numero_sigade', 'MBC0058'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2306
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2306'),
    ('nombre', 'Proyecto Construcción de Viaducto y Ampliación de Carretera CA01W (Tramo Los Chorros)'),
    ('moneda', 'USD'),
    ('monto', '166000000'),
    ('numero_sigade', 'MBC0061'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE 2337
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE 2337'),
    ('nombre', 'Programa Surf City Fase I'),
    ('moneda', 'USD'),
    ('monto', '113000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1099-0 ISDEM
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1099-0 ISDEM'),
    ('nombre', 'BCIE-2-1099-0 ISDEM CONS.MER.'),
    ('moneda', 'USD'),
    ('monto', '9283726'),
    ('numero_sigade', 'FMBC004'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0003-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0003-0'),
    ('nombre', 'CONST.CARRET LA UNION-FRONT.HO'),
    ('moneda', 'USD'),
    ('monto', '2400000'),
    ('numero_sigade', 'MBC0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0003-1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0003-1'),
    ('nombre', 'PROY.CONST.CARR.LA CUCHILLA'),
    ('moneda', 'USD'),
    ('monto', '2295699'),
    ('numero_sigade', 'MBC0002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0007-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0007-0'),
    ('nombre', '1era.ETAPA DESAR-COMUNAL-CABAÑ'),
    ('moneda', 'USD'),
    ('monto', '3998812.1'),
    ('numero_sigade', 'MBC0006'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0015-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0015-0'),
    ('nombre', 'DESARR.AGR.P´PEQ.PRODUCTORES'),
    ('moneda', 'USD'),
    ('monto', '3270000'),
    ('numero_sigade', 'MBC0014'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0017-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0017-0'),
    ('nombre', 'PROG.PROD.COLEG.VOCACIONALES'),
    ('moneda', 'USD'),
    ('monto', '2500000'),
    ('numero_sigade', 'MBC0016'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0024-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0024-0'),
    ('nombre', 'PROY.RESTAURAC. DE LA UES'),
    ('moneda', 'USD'),
    ('monto', '475000'),
    ('numero_sigade', 'MBC0020'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0029-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0029-0'),
    ('nombre', 'PROG.INFR.SOC. EDUC.PARV.BÁSIC'),
    ('moneda', 'USD'),
    ('monto', '1500000'),
    ('numero_sigade', 'MBC0021'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0030-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0030-0'),
    ('nombre', 'PROG.INFR.SOCIAL AGUA POTABLE'),
    ('moneda', 'USD'),
    ('monto', '1499895'),
    ('numero_sigade', 'MBC0022'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0112-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0112-0'),
    ('nombre', 'Const.Carrt.SSalv.-San Miguel'),
    ('moneda', 'USD'),
    ('monto', '11000000'),
    ('numero_sigade', 'BMC0003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0136-1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0136-1'),
    ('nombre', 'Carr.CA-1CA-12, Santa Ana'),
    ('moneda', 'USD'),
    ('monto', '3840000'),
    ('numero_sigade', 'MBC0011'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0148-1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0148-1'),
    ('nombre', 'Construc.Carret. Km.52 La Herr'),
    ('moneda', 'USD'),
    ('monto', '3225627'),
    ('numero_sigade', 'MBC0012'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0213-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0213-0'),
    ('nombre', 'REHAB.CARRET.LA HACHADURA-CA12'),
    ('moneda', 'USD'),
    ('monto', '10000000'),
    ('numero_sigade', 'MBC0009'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0219-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0219-0'),
    ('nombre', 'REHAB.CARR.S.ANA-METAPÁN CA-12'),
    ('moneda', 'USD'),
    ('monto', '10199184'),
    ('numero_sigade', 'MBC0008'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0225-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0225-0'),
    ('nombre', 'Reconst. Carr. SS-Sn Miguel II'),
    ('moneda', 'USD'),
    ('monto', '7745859'),
    ('numero_sigade', 'MBC0010'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0278-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0278-0'),
    ('nombre', 'REHABILITACION VIAS PAVIMENT.'),
    ('moneda', 'USD'),
    ('monto', '20000000'),
    ('numero_sigade', 'MBC0017'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0279-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0279-0'),
    ('nombre', 'PLAN D´RECONST.NAC.REP.DE E.S.'),
    ('moneda', 'USD'),
    ('monto', '30000000'),
    ('numero_sigade', 'MBC0018'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0282-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0282-0'),
    ('nombre', 'PLAN NACNAL. SEÑALAMIENTO VIAL'),
    ('moneda', 'USD'),
    ('monto', '7935410'),
    ('numero_sigade', 'MBC0024'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0300-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0300-0'),
    ('nombre', 'CONST.PUENTE BAILEY S.RIO LEMP'),
    ('moneda', 'USD'),
    ('monto', '4900000'),
    ('numero_sigade', 'MBC0026'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-0623-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-0623-0'),
    ('nombre', 'PROG.NAC.DE RIEGO Y DRENAJE'),
    ('moneda', 'USD'),
    ('monto', '3159616.4'),
    ('numero_sigade', 'MBC0007'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1005-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1005-0'),
    ('nombre', 'PROY.DESARROLLO RURAL INTEGRAD'),
    ('moneda', 'USD'),
    ('monto', '11100000'),
    ('numero_sigade', 'MBC0023'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1144-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1144-0'),
    ('nombre', 'PROY.RECONST.HOSPIT.GRAL. ISSS'),
    ('moneda', 'USD'),
    ('monto', '33300000'),
    ('numero_sigade', 'MBC0025'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1152-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1152-0'),
    ('nombre', 'PROG.NAC.D´REHAB.VIAS PAVIMENT'),
    ('moneda', 'USD'),
    ('monto', '32900000'),
    ('numero_sigade', 'MBC0028'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1180-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1180-0'),
    ('nombre', 'PROG.ESCUELA SALUDABL.II ETAPA'),
    ('moneda', 'USD'),
    ('monto', '20000000'),
    ('numero_sigade', 'MBC0027'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1250-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1250-0'),
    ('nombre', 'PROY.INFRAEST.ECON.Y SOCIAL B.'),
    ('moneda', 'USD'),
    ('monto', '40000000'),
    ('numero_sigade', 'MBC0029'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1306 -MAG
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1306 -MAG'),
    ('nombre', 'Desarr.Rural S.F.Ecol.Trifinio'),
    ('moneda', 'USD'),
    ('monto', '6971000'),
    ('numero_sigade', 'MBC0030'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1417-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1417-0'),
    ('nombre', 'Primera Etapa Anillo Periféric'),
    ('moneda', 'USD'),
    ('monto', '62700000'),
    ('numero_sigade', 'MBC0034'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1417-0,BCIE 2031,BCIE 2015
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1417-0,BCIE 2031,BCIE 2015'),
    ('nombre', 'Proyecto Apertura del Boulevard Diego de Holgin, Santa Tecla'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', '-'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1496-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1496-0'),
    ('nombre', 'Progr. Desarrollo Local FISDL'),
    ('moneda', 'USD'),
    ('monto', '30000000'),
    ('numero_sigade', 'MBC0032'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1510-0 INDES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1510-0 INDES'),
    ('nombre', 'Apoyo Desarr.Educ.Integ.El Sal'),
    ('moneda', 'USD'),
    ('monto', '50000000'),
    ('numero_sigade', 'MBC0031'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1517-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1517-0'),
    ('nombre', 'Multisectorial de Emergencia'),
    ('moneda', 'USD'),
    ('monto', '75000000'),
    ('numero_sigade', 'MBC0033'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1531
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1531'),
    ('nombre', 'CONTRATO DE PREST. PARA MERC C'),
    ('moneda', 'USD'),
    ('monto', '7400000'),
    ('numero_sigade', 'MBCGES'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1556-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1556-0'),
    ('nombre', 'PROG. NACIONAL DE CARRETERAS'),
    ('moneda', 'USD'),
    ('monto', '135000000'),
    ('numero_sigade', 'MBC0035'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCIE-2-1663-0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCIE-2-1663-0'),
    ('nombre', '--'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', 'S-N'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BCO.BBV-2130-SV-5047
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BCO.BBV-2130-SV-5047'),
    ('nombre', 'ANDA -LEMPA'),
    ('moneda', 'USD'),
    ('monto', '15438600'),
    ('numero_sigade', 'GBES002'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BFCE-AC # 1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BFCE-AC # 1'),
    ('nombre', 'Construcción del Nuevo H.Ros.'),
    ('moneda', 'FRF'),
    ('monto', '21362712'),
    ('numero_sigade', 'BFR0005'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BFCE-AC # 2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BFCE-AC # 2'),
    ('nombre', 'Construcción del Comp.G.Chi.'),
    ('moneda', 'FRF'),
    ('monto', '17389398'),
    ('numero_sigade', 'BFR0006'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIC-015-CD-ES-CEPA
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIC-015-CD-ES-CEPA'),
    ('nombre', 'BIC-015-CD-ES-CEPA AMPLIACION'),
    ('moneda', 'CAD'),
    ('monto', '2000000'),
    ('numero_sigade', 'GMBI002'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID - ATN-5977
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID - ATN-5977'),
    ('nombre', 'BID - ATN-5977'),
    ('moneda', 'DON'),
    ('monto', '0'),
    ('numero_sigade', 'BID - ATN-59'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID - ATN-5981
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID - ATN-5981'),
    ('nombre', 'BID - ATN-5981'),
    ('moneda', 'DON'),
    ('monto', '0'),
    ('numero_sigade', 'BID - ATN-59'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1041-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1041-OC-ES'),
    ('nombre', 'PROG.MODERNIZ.SECTOR PUBLIC.'),
    ('moneda', 'USD'),
    ('monto', '70000000'),
    ('numero_sigade', 'MBI0057'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1067-OC-ES-1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1067-OC-ES-1'),
    ('nombre', 'PROGRAMA DE DESARROLLO LOCAL'),
    ('moneda', 'USD'),
    ('monto', '19769937'),
    ('numero_sigade', 'MBI0049'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1067-OC-ES-2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1067-OC-ES-2'),
    ('nombre', 'PROGRAMA DESARROLLO LOCAL'),
    ('moneda', 'USD'),
    ('monto', '13965000'),
    ('numero_sigade', 'MBI0050'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1084-OC-ES-1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1084-OC-ES-1'),
    ('nombre', 'PROG.APOY.A TECNOLOG.EDUCATIV.'),
    ('moneda', 'USD'),
    ('monto', '43051125'),
    ('numero_sigade', 'MBI0058'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1084-OC-ES-2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1084-OC-ES-2'),
    ('nombre', 'PROG.APOY.A TEC.EDUCATIV.'),
    ('moneda', 'USD'),
    ('monto', '29892810'),
    ('numero_sigade', 'MBI0059'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1092-OC-ES-1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1092-OC-ES-1'),
    ('nombre', 'APOY.A LA MODERNIZ.MSPAS'),
    ('moneda', 'USD'),
    ('monto', '13934495'),
    ('numero_sigade', 'MBI0072'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1092-OC-ES-2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1092-OC-ES-2'),
    ('nombre', 'APOYO A LA MODERNIZ. DEL MSPAS'),
    ('moneda', 'USD'),
    ('monto', '6670225'),
    ('numero_sigade', 'MBI0073'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1100-OC-ES-1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1100-OC-ES-1'),
    ('nombre', 'Programa de Infraestructura Educativa'),
    ('moneda', 'USD'),
    ('monto', '34919000'),
    ('numero_sigade', 'MBI0052'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1100-OC-ES-2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1100-OC-ES-2'),
    ('nombre', 'PROGRAMA INFRAESTRUCTURA EDUCA'),
    ('moneda', 'USD'),
    ('monto', '34759000'),
    ('numero_sigade', 'MBI0053'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1102-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1102-OC-ES'),
    ('nombre', 'PROG.REFORMA SECTOR HIDRICO'),
    ('moneda', 'USD'),
    ('monto', '43625705'),
    ('numero_sigade', 'MBI0066'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 119-TF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 119-TF-ES'),
    ('nombre', 'PROG. CRED. AGRP. PEQ PRODUCTO'),
    ('moneda', 'USD'),
    ('monto', '12313292'),
    ('numero_sigade', 'MBI0006'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1203-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1203-OC-ES'),
    ('nombre', 'PROG.MODERNIZ.Y FORT.ASAMB.LEG'),
    ('moneda', 'USD'),
    ('monto', '3528774'),
    ('numero_sigade', 'MBI0060'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1209-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1209-OC-ES'),
    ('nombre', 'PROG. DESCONTAM DE AREAS CRI'),
    ('moneda', 'USD'),
    ('monto', '29765084'),
    ('numero_sigade', 'MBI0063'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 124-TF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 124-TF-ES'),
    ('nombre', 'PROG CRED GLOB SEC AGROP REFOR'),
    ('moneda', 'USD'),
    ('monto', '3422874'),
    ('numero_sigade', 'MBI0017'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1310-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1310-OC-ES'),
    ('nombre', 'APOYO RECONST.EMERG.TERR 13.01'),
    ('moneda', 'USD'),
    ('monto', '19682834'),
    ('numero_sigade', 'MBI0062'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1314-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1314-OC-ES'),
    ('nombre', 'PROG MULTIF CAM RURAL SOST'),
    ('moneda', 'USD'),
    ('monto', '57712030'),
    ('numero_sigade', 'MBI0067'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1315-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1315-OC-ES'),
    ('nombre', 'APOY RECONST EMERG.TERR 13-OC'),
    ('moneda', 'USD'),
    ('monto', '18435371'),
    ('numero_sigade', 'MBI0068'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1327-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1327-OC-ES'),
    ('nombre', 'PROY.RECONVERSION AGROEMPRESAR'),
    ('moneda', 'USD'),
    ('monto', '24921505'),
    ('numero_sigade', 'MBI0070'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1352-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1352-OC-ES'),
    ('nombre', 'DESARROLLO LOCAL II FIS'),
    ('moneda', 'USD'),
    ('monto', '69793465'),
    ('numero_sigade', 'MBI0071'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1379-OC-ES-1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1379-OC-ES-1'),
    ('nombre', 'PROG.VIVIENDA-FASE I- MOP'),
    ('moneda', 'USD'),
    ('monto', '30300000'),
    ('numero_sigade', 'MBI0074'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1379-OC-ES-2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1379-OC-ES-2'),
    ('nombre', 'PROG.VIVIENDA FASE I-MOP'),
    ('moneda', 'USD'),
    ('monto', '39700000'),
    ('numero_sigade', 'MBI0075'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1492-OC (IFF)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1492-OC (IFF)'),
    ('nombre', 'PROG. DE APOYO A LA COMPETITIVIDAD'),
    ('moneda', 'USD'),
    ('monto', '77900000'),
    ('numero_sigade', 'MBI0078'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1782-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1782-OC-ES'),
    ('nombre', 'PROGRAMA DE APOYO A LA POLITICA SOCIAL'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', 'MBI0081'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 1782-OC-ES,BID 2068-BL-ES,BID 2069-OC-ES,BID 2070-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 1782-OC-ES,BID 2068-BL-ES,BID 2069-OC-ES,BID 2070-OC-ES'),
    ('nombre', 'Programa de Apoyo a la Politica Social'),
    ('moneda', 'USD'),
    ('monto', '500000000'),
    ('numero_sigade', '-'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2296-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2296-OC-ES'),
    ('nombre', 'PROGRAMA DE FORTALECIMIENTO FISCAL'),
    ('moneda', 'USD'),
    ('monto', '200000000'),
    ('numero_sigade', 'MBI000084'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 234-IC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 234-IC-ES'),
    ('nombre', 'PROG. REHAB EMERG SECTOR TELEF'),
    ('moneda', 'USD'),
    ('monto', '5544425'),
    ('numero_sigade', 'MBI0051'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2347-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2347-OC-ES'),
    ('nombre', 'Programa Integrado de Salud'),
    ('moneda', 'USD'),
    ('monto', '60000000'),
    ('numero_sigade', 'MBI00085'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2358-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2358-OC-ES'),
    ('nombre', 'Programa de Agua y Saneamiento Rural'),
    ('moneda', 'USD'),
    ('monto', '20000000'),
    ('numero_sigade', 'MBI0084'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2369-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2369-OC-ES'),
    ('nombre', 'Programa de Caminos Rurales para el Desarrollo'),
    ('moneda', 'USD'),
    ('monto', '35000000'),
    ('numero_sigade', 'MBI00086'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2373 OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2373 OC-ES'),
    ('nombre', 'Programa de Vivienda y Mejoramiento Integral de Asentamientos Urbanos Precarios Fase II'),
    ('moneda', 'USD'),
    ('monto', '70000000'),
    ('numero_sigade', 'MBI00087'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2375-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2375-OC-ES'),
    ('nombre', 'Apoyo a Comunidades Solidarias Urbanas'),
    ('moneda', 'USD'),
    ('monto', '35000000'),
    ('numero_sigade', 'MBI2375-OC-ES'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2492-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2492-OC-ES'),
    ('nombre', 'Modernización del Órgano Legislativo II'),
    ('moneda', 'USD'),
    ('monto', '5000000'),
    ('numero_sigade', 'MBI0096'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2525-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2525-OC-ES'),
    ('nombre', 'PROYECTO CIUDAD MUJER'),
    ('moneda', 'USD'),
    ('monto', '20000000'),
    ('numero_sigade', 'MBI0097'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2570-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2570-OC-ES'),
    ('nombre', 'PROGRAMA DE APOYO PROGRAMATICO A LA AGENDA DE REFORMAS ESTRUCTURALES DEL SECTOR DE ENERGIA ELECTRICA'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'MBI0099'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2572-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2572-OC-ES'),
    ('nombre', 'Programa de Transporte del AMSS'),
    ('moneda', 'USD'),
    ('monto', '45000000'),
    ('numero_sigade', 'MBI0100'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2581-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2581-OC-ES'),
    ('nombre', 'Programa de Conectividad Rural en Zona Norte y Oriente'),
    ('moneda', 'USD'),
    ('monto', '15000000'),
    ('numero_sigade', 'MBI0101'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2583-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2583-OC-ES'),
    ('nombre', 'Programa de Apoyo al Desarrollo Productivo para Inserción Internacional'),
    ('moneda', 'USD'),
    ('monto', '30000000'),
    ('numero_sigade', 'MBI0103'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2630-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2630-OC-ES'),
    ('nombre', 'Reducción de Vulnerabilidad en Asentamenientos Urbanos Precarios en el AMSS'),
    ('moneda', 'USD'),
    ('monto', '50000000'),
    ('numero_sigade', 'MBI0104'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2710-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2710-OC-ES'),
    ('nombre', 'Programa Integral de Sostenibilidad Fiscal y Adaptación al Cambio Climatico para El Salvador'),
    ('moneda', 'USD'),
    ('monto', '20000000'),
    ('numero_sigade', 'MBI0102'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2881-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2881-OC-ES'),
    ('nombre', 'Programa de Apoyo Integral a la Estrategia de Prevención de la Violencia'),
    ('moneda', 'USD'),
    ('monto', '45000000'),
    ('numero_sigade', 'MBI0106'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 2966-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 2966-OC-ES'),
    ('nombre', 'Programa de Desarrollo Turístico de la Franja Costero-Marina'),
    ('moneda', 'USD'),
    ('monto', '25000000'),
    ('numero_sigade', 'MBI0105'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 3170-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 3170-OC-ES'),
    ('nombre', 'Programa de Corredores Productivos'),
    ('moneda', 'USD'),
    ('monto', '40000000'),
    ('numero_sigade', 'MBI0108'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 3271-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 3271-OC-ES'),
    ('nombre', 'Préstamo Global de Crédito para el Financiamiento del Desarrollo Productivo de El Salvador'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'MBI0107'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 352-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 352-SF-ES'),
    ('nombre', 'PROY. HIDROELEC CERRON GRANDE'),
    ('moneda', 'USD'),
    ('monto', '38100000'),
    ('numero_sigade', 'MBI0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 3608-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 3608-OC-ES'),
    ('nombre', 'Programa Integrado de Salud'),
    ('moneda', 'USD'),
    ('monto', '170000000'),
    ('numero_sigade', 'MBI0109'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 369-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 369-SF-ES'),
    ('nombre', 'PROY DE RIEGO Y DES. AGROPECUA'),
    ('moneda', 'USD'),
    ('monto', '7548267'),
    ('numero_sigade', 'MBI0002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 3852-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 3852-OC-ES'),
    ('nombre', 'Programa de Fortalecimiento de la Administración Tributaria'),
    ('moneda', 'USD'),
    ('monto', '30000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 393-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 393-SF-ES'),
    ('nombre', 'PROG. MEJOR. SERV. DE SALUD'),
    ('moneda', 'USD'),
    ('monto', '15000000'),
    ('numero_sigade', 'MBI0003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 426-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 426-SF-ES'),
    ('nombre', 'MJ. AMP. SIS. ABAS AGUA P. SS'),
    ('moneda', 'USD'),
    ('monto', '30000000'),
    ('numero_sigade', 'MBI0004'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 427-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 427-SF-ES'),
    ('nombre', 'APERT. AMP. VIAS URB. S.S.'),
    ('moneda', 'USD'),
    ('monto', '12983818'),
    ('numero_sigade', 'MBI0005'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 4542-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 4542-OC-ES'),
    ('nombre', 'Programa de Fortalecimiento Fiscal para el Crecimiento Inclusivo'),
    ('moneda', 'USD'),
    ('monto', '350000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 472-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 472-SF-ES'),
    ('nombre', 'PROG.DE CONST. DE CAM.RURALES'),
    ('moneda', 'USD'),
    ('monto', '10000000'),
    ('numero_sigade', 'MBI0008'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 480-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 480-OC-ES'),
    ('nombre', 'PROG.CREDITO AGROPECUARIO'),
    ('moneda', 'USD'),
    ('monto', '8274264'),
    ('numero_sigade', 'MBI0024'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 4807-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 4807-OC-ES'),
    ('nombre', 'Programa de Fortalecimiento Fiscal para el Crecimiento Inclusivo II'),
    ('moneda', 'USD'),
    ('monto', '200000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 481-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 481-OC-ES'),
    ('nombre', 'III-ET PROG GLOB.AGROP. PTEII'),
    ('moneda', 'USD'),
    ('monto', '29100000'),
    ('numero_sigade', 'MBI0025'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 4870-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 4870-OC-ES'),
    ('nombre', 'Fortalecimiento de la Resiliencia Climática de los Bosques Cafetaleros en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '45000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 502-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 502-SF-ES'),
    ('nombre', 'PROGRAMA DE DESARROLLO PESQUER'),
    ('moneda', 'USD'),
    ('monto', '4773149'),
    ('numero_sigade', 'MBI0009'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 504-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 504-SF-ES'),
    ('nombre', 'SEGUNDA ET PROG. ACUED. RURALE'),
    ('moneda', 'USD'),
    ('monto', '4661714'),
    ('numero_sigade', 'MBI0007'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5043-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5043-OC-ES'),
    ('nombre', 'Respuesta Inmediata de Salud Pública para Contener y Controlar el Coronavirus y Mitigar su Efecto en la Prestación del Servicio en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '50000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5046-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5046-OC-ES'),
    ('nombre', 'Programa de Fortalecimiento de la Politica Pública y Gestión Fiscal para la Atención de la Crisis Sanitaria y Económica causada por el COVID-19 en El Salvador.'),
    ('moneda', 'USD'),
    ('monto', '250000000'),
    ('numero_sigade', 'MBI0113'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5080-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5080-OC-ES'),
    ('nombre', 'Programa de Mejora de la Calidad y cobertura Educativa: Nacer, Crecer, Aprender'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 525-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 525-SF-ES'),
    ('nombre', 'PROY.HIDROEL.SN LORENZO EN LEM'),
    ('moneda', 'USD'),
    ('monto', '45400000'),
    ('numero_sigade', 'MBI0061'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5340-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5340-OC-ES'),
    ('nombre', 'Programa de Conectividad Digital Social'),
    ('moneda', 'USD'),
    ('monto', '50000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5341-KI-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5341-KI-ES'),
    ('nombre', 'Programa de Conectividad Digital Social'),
    ('moneda', 'USD'),
    ('monto', '35000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 537-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 537-SF-ES'),
    ('nombre', 'Proy.Seg. Etapa del Pro. de Ex'),
    ('moneda', 'USD'),
    ('monto', '8749282'),
    ('numero_sigade', 'MBI0010'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5454-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5454-OC-ES'),
    ('nombre', 'Programa de Modernización del Sistema Estadistico de El Salvador'),
    ('moneda', 'USD'),
    ('monto', '44000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5577-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5577-OC-ES'),
    ('nombre', 'Programa de Fortalecimiento del Sector Agua y Saneamiento en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'MBI0121'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5590-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5590-OC-ES'),
    ('nombre', 'Programa de Apoyo a la Recuperación y Expansión del Sector Turismo en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '106000000'),
    ('numero_sigade', 'MBI0122'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5620-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5620-OC-ES'),
    ('nombre', 'Programa de Caminos Rurales'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'MBI0123'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5785-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5785-OC-ES'),
    ('nombre', 'Programa para la Protección Social Responsiva a Choques en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'MBI0124'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5874-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5874-OC-ES'),
    ('nombre', 'PROGRAMA DE SALUD INTELIGENTE E INTEGRAL'),
    ('moneda', 'USD'),
    ('monto', '235000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 596-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 596-SF-ES'),
    ('nombre', 'PROGRAMA DE PREINVERSION'),
    ('moneda', 'USD'),
    ('monto', '4500000'),
    ('numero_sigade', 'MBI0013'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 5977-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 5977-OC-ES'),
    ('nombre', 'Programa de Financiamiento para Vivienda Social, Inclusiva y Sostenible'),
    ('moneda', 'USD'),
    ('monto', '50000000'),
    ('numero_sigade', '-'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 605-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 605-SF-ES'),
    ('nombre', 'PROG. CRED. AGROP. PEQ. PROD.'),
    ('moneda', 'USD'),
    ('monto', '16500000'),
    ('numero_sigade', 'MBI0015'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 653-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 653-OC-ES'),
    ('nombre', 'PROGRAMA CARRETERAS TRONCALES'),
    ('moneda', 'USD'),
    ('monto', '94791630'),
    ('numero_sigade', 'MBI0034'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 665-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 665-SF-ES'),
    ('nombre', 'CONSTRUCCION CAMINOS RURALES'),
    ('moneda', 'USD'),
    ('monto', '21476369'),
    ('numero_sigade', 'MBI0018'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 676-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 676-SF-ES'),
    ('nombre', 'PROG COMERC INSUM PROD AGRIC'),
    ('moneda', 'USD'),
    ('monto', '4374317'),
    ('numero_sigade', 'MBI0019'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 714-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 714-OC-ES'),
    ('nombre', 'PROG REFORMA SECTOR INVERSIONE'),
    ('moneda', 'USD'),
    ('monto', '90000000'),
    ('numero_sigade', 'MBI0035'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 731-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 731-OC-ES'),
    ('nombre', 'PLAN DE RECONSTRUC. NACIONAL'),
    ('moneda', 'USD'),
    ('monto', '39331184'),
    ('numero_sigade', 'MBI0055'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 772-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 772-SF-ES'),
    ('nombre', 'TERC. ET. PROG. ACUEDUC RURALE'),
    ('moneda', 'USD'),
    ('monto', '15392413'),
    ('numero_sigade', 'MBI0026'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 813-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 813-SF-ES'),
    ('nombre', 'PROG.AGUA POTAB.Y ALCANTAR.SAN'),
    ('moneda', 'USD'),
    ('monto', '164300582'),
    ('numero_sigade', 'MBI0064'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 829-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 829-OC-ES'),
    ('nombre', 'PROG FONDO INV SOC SALV III ET'),
    ('moneda', 'USD'),
    ('monto', '60000000'),
    ('numero_sigade', 'MBI0039'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 837-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 837-SF-ES'),
    ('nombre', 'EDUC.TEC.SUPER.NO UNIVERSIT.'),
    ('moneda', 'USD'),
    ('monto', '14396645'),
    ('numero_sigade', 'MBI0029'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 838-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 838-OC-ES'),
    ('nombre', 'PROGRAMA DE DESAR. ELECT. II'),
    ('moneda', 'USD'),
    ('monto', '206755061'),
    ('numero_sigade', 'MBI0056'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 840-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 840-OC-ES'),
    ('nombre', 'PROG.REHAB.MEJORAM.VIAL E-II'),
    ('moneda', 'USD'),
    ('monto', '29597132'),
    ('numero_sigade', 'MBI0042'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 861-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 861-SF-ES'),
    ('nombre', 'PROG. FONDO INV. SOC. EL SALV'),
    ('moneda', 'USD'),
    ('monto', '33000000'),
    ('numero_sigade', 'MBI0032'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 870-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 870-SF-ES'),
    ('nombre', 'PROGRAMA DE CARRET.TRONC.'),
    ('moneda', 'USD'),
    ('monto', '24074662'),
    ('numero_sigade', 'MBI0033'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 879-0C-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 879-0C-ES'),
    ('nombre', 'PROY. MOD. EDUC. BASICA'),
    ('moneda', 'USD'),
    ('monto', '37300000'),
    ('numero_sigade', 'MBI0040'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 886-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 886-OC-ES'),
    ('nombre', 'PROGRAMA AMBIENTAL DE EL SALVA'),
    ('moneda', 'USD'),
    ('monto', '30000000'),
    ('numero_sigade', 'MBI0045'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 905-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 905-SF-ES'),
    ('nombre', 'PROGRAMA F.I.S.D.L., II ETAPA'),
    ('moneda', 'USD'),
    ('monto', '35000000'),
    ('numero_sigade', 'MBI0038'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 919-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 919-OC-ES'),
    ('nombre', 'PROG. APOYO A REFORMA JUSTICIA'),
    ('moneda', 'USD'),
    ('monto', '19200000'),
    ('numero_sigade', 'MBI0043'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 920-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 920-OC-ES'),
    ('nombre', 'PROG. APOYO REFOR SIST JUSTICI'),
    ('moneda', 'USD'),
    ('monto', '3000000'),
    ('numero_sigade', 'MBI0044'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 941-OC-ES-2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 941-OC-ES-2'),
    ('nombre', 'PROY MODERN ADMON FISCAL 2'),
    ('moneda', 'USD'),
    ('monto', '6636000'),
    ('numero_sigade', 'MBI0048'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID 980-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID 980-SF-ES'),
    ('nombre', 'PROY MODERN ADMON FISCAL (UTEC'),
    ('moneda', 'USD'),
    ('monto', '3774000'),
    ('numero_sigade', 'MBI0046'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID ES-G1001
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID ES-G1001'),
    ('nombre', 'Programa Salud Mesoamérica 2015 - El Salvador'),
    ('moneda', 'USD'),
    ('monto', '15666000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID ES-O0011
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID ES-O0011'),
    ('nombre', 'Préstamo Contingente para Emergencias por Desastres Naturales y de Salud Pública'),
    ('moneda', 'USD'),
    ('monto', '400000000'),
    ('numero_sigade', '-'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID ES-O0011; 5631-OC-ES; ES-L1161
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID ES-O0011; 5631-OC-ES; ES-L1161'),
    ('nombre', 'Préstamo Contingente para Emergencias por Desastres Naturales - Tormenta Tropical Julia'),
    ('moneda', 'USD'),
    ('monto', '26880000'),
    ('numero_sigade', '-'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID GRT-ER-19647-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID GRT-ER-19647-ES'),
    ('nombre', 'Programa de Conectividad Digital Social'),
    ('moneda', 'USD'),
    ('monto', '6431162'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID GRT-HE-12982-ES, GRT-HE-12983-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID GRT-HE-12982-ES, GRT-HE-12983-ES'),
    ('nombre', 'Convenio Individual de Financiamiento No Reembolsable de Inversión del Fondo Mesoamericano de Salud'),
    ('moneda', 'USD'),
    ('monto', '6500000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID GRT-HE-14650- ES, GRT-HE-14651-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID GRT-HE-14650- ES, GRT-HE-14651-ES'),
    ('nombre', 'Convenio Individual de Financiamiento No Reembolsable de Inversión del Fondo Mesoamericano de Salud'),
    ('moneda', 'USD'),
    ('monto', '3944645'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID GRT-HE-16714-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID GRT-HE-16714-ES'),
    ('nombre', 'Iniciativa Salud Mesoamérica 2015 – El Salvador Tercera Operacion individual'),
    ('moneda', 'USD'),
    ('monto', '850000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID GRT-HE-16714-ES, GRT-HE-16715-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID GRT-HE-16714-ES, GRT-HE-16715-ES'),
    ('nombre', 'Iniciativa Salud Mesoamérica 2015 – El Salvador Tercera Operacion individual'),
    ('moneda', 'USD'),
    ('monto', '1530000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-019-VF-ES-CEL
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-019-VF-ES-CEL'),
    ('nombre', 'BID-019-VF-ES-CEL-SAN LORENZO'),
    ('moneda', 'USD'),
    ('monto', '30000000'),
    ('numero_sigade', 'MBIT001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-1173-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-1173-OC-ES'),
    ('nombre', 'PROG.APOYO ALSEC FINANC EN E.S'),
    ('moneda', 'USD'),
    ('monto', '3782240'),
    ('numero_sigade', 'MBI0069'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-1492 (TASA AJUST
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-1492 (TASA AJUST'),
    ('nombre', 'PROG. DE APOYO A LA COMPETITIVIDAD'),
    ('moneda', 'USD'),
    ('monto', '22100000'),
    ('numero_sigade', 'MBI0079'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-349-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-349-OC-ES'),
    ('nombre', 'PROG. DESARROLLO GANADERO'),
    ('moneda', 'USD'),
    ('monto', '14802288'),
    ('numero_sigade', 'MBI0012'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-561-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-561-SF-ES'),
    ('nombre', 'PROG. DESAR. COMUNAL ZONA NORT'),
    ('moneda', 'USD'),
    ('monto', '8648673'),
    ('numero_sigade', 'MBI0011'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-5851-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-5851-OC-ES'),
    ('nombre', 'Programa de Facilitación Comercial y Modernización de Operación Portuaria en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '84000000'),
    ('numero_sigade', 'MBI0125'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-5937-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-5937-OC-ES'),
    ('nombre', 'Programa para el Desarrollo de Infraestructura de Datos de El Salvador'),
    ('moneda', 'USD'),
    ('monto', '60000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-604-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-604-SF-ES'),
    ('nombre', 'P.DE MEJ.DE LOS SERV.DE SALU.'),
    ('moneda', 'USD'),
    ('monto', '21516703'),
    ('numero_sigade', 'MBI0014'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-642-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-642-SF-ES'),
    ('nombre', 'PROG CRED. GLO. SEC. AGRO. REF'),
    ('moneda', 'USD'),
    ('monto', '40400000'),
    ('numero_sigade', 'MBI0016'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-683-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-683-SF-ES'),
    ('nombre', 'PROY.HIDRO.SAN LORENZO.R.LEMP.'),
    ('moneda', 'USD'),
    ('monto', '16500000'),
    ('numero_sigade', 'MBI0020'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-705-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-705-SF-ES'),
    ('nombre', 'Proy. Des. de Inv. y Ext. Agr.'),
    ('moneda', 'USD'),
    ('monto', '7657946'),
    ('numero_sigade', 'MBI0021'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-732-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-732-SF-ES'),
    ('nombre', 'PROGR.DE PREINVERSION II ETAPA'),
    ('moneda', 'USD'),
    ('monto', '6421458'),
    ('numero_sigade', 'MBI0022'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-765-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-765-OC-ES'),
    ('nombre', 'PROGRAMA DE INVERS. SOCIAL'),
    ('moneda', 'IDB'),
    ('monto', '15042972'),
    ('numero_sigade', 'MBI0037'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-801-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-801-SF-ES'),
    ('nombre', 'REHAB. SECT. DE SALUD Y AGUA P'),
    ('moneda', 'USD'),
    ('monto', '3136008'),
    ('numero_sigade', 'MBI0027'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-802-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-802-SF-ES'),
    ('nombre', 'DESARR.AGRICOLA LEMPA-ACAHUAPA'),
    ('moneda', 'USD'),
    ('monto', '10875000'),
    ('numero_sigade', 'MBI0028'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-844-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-844-SF-ES'),
    ('nombre', 'MEJORAM. CAMINOS RURALES'),
    ('moneda', 'USD'),
    ('monto', '43696792'),
    ('numero_sigade', 'MBI0030'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-860-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-860-SF-ES'),
    ('nombre', 'PROGRA.PREINVERSION III ETAPA'),
    ('moneda', 'USD'),
    ('monto', '7000000'),
    ('numero_sigade', 'MBI0031'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID-898-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID-898-SF-ES'),
    ('nombre', 'PROG.REHABILIT.AGUA POTABLE'),
    ('moneda', 'USD'),
    ('monto', '19000000'),
    ('numero_sigade', 'MBI0036'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID0004-SQ-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID0004-SQ-ES'),
    ('nombre', 'SIST.INT.ELEC.P.C.A.(SIEPAC)'),
    ('moneda', 'USD'),
    ('monto', '10000000'),
    ('numero_sigade', 'GMBI0004'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID004-SQ-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID004-SQ-ES'),
    ('nombre', 'SIST.INT.ELEC.P.C.A(SIEPAC)'),
    ('moneda', 'USD'),
    ('monto', '10000000'),
    ('numero_sigade', 'GMBI0003'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID1004-SF-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID1004-SF-ES'),
    ('nombre', 'PROGRAMA INFRAESTRUCTURA EDUC'),
    ('moneda', 'USD'),
    ('monto', '1100000'),
    ('numero_sigade', 'MBI0054'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID1369 OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID1369 OC-ES'),
    ('nombre', 'SIST.INT.ELEC.P.C.A.(SIEPAC)'),
    ('moneda', 'USD'),
    ('monto', '30000000'),
    ('numero_sigade', 'GMBI0004'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID839-OC-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID839-OC-ES'),
    ('nombre', 'PROG. REH. Y MEJ. VIAL. E. II'),
    ('moneda', 'USD'),
    ('monto', '195000000'),
    ('numero_sigade', 'MBI0041'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BID941-OC-ES-1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BID941-OC-ES-1'),
    ('nombre', 'PROY.MODER.ADMON.FISCAL'),
    ('moneda', 'USD'),
    ('monto', '9300000'),
    ('numero_sigade', 'MBI0047'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 3293-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 3293-0 ES'),
    ('nombre', 'PROGRAMA DE AJUSTE ESTRUCTURAL'),
    ('moneda', 'USD'),
    ('monto', '75000000'),
    ('numero_sigade', 'MBM0006'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 7135 - ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 7135 - ES'),
    ('nombre', 'PROYECTO DE MODERNIZACION DEL ORGANO JUDICIAL'),
    ('moneda', 'USD'),
    ('monto', '18200000'),
    ('numero_sigade', 'MBM0021'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 7635-FV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 7635-FV'),
    ('nombre', 'PRESTAMO PARA POLITICAS DE DESARROLLO DE LAS FINANZAS PUBLICAS Y DEL SECTOR SOCIAL'),
    ('moneda', 'USD'),
    ('monto', '450000000'),
    ('numero_sigade', 'MBM0022'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 7635-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 7635-SV'),
    ('nombre', 'PRESTAMO PARA POLITICAS DE DESARROLLO DE LAS FINANZAS PUBLICAS Y DEL SECTOR SOCIAL'),
    ('moneda', 'USD'),
    ('monto', '450000000'),
    ('numero_sigade', 'MBM0022'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 7806-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 7806-SV'),
    ('nombre', 'Sostenibilidad de los Logros Sociales para la Recuperación Económica'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'MBM0023'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 7811-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 7811-SV'),
    ('nombre', 'Protección de Ingresos y Empleabilidad'),
    ('moneda', 'USD'),
    ('monto', '50000000'),
    ('numero_sigade', 'MBM0035'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 7812-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 7812-SV'),
    ('nombre', 'PROYECTO DE ASISTENCIA TECNICA PARA ADMINISTRACION FISCAL Y DESEMPEÑO DEL SECTOR PUBLICO'),
    ('moneda', 'USD'),
    ('monto', '20000000'),
    ('numero_sigade', 'MBM0030'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 8110-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 8110-SV'),
    ('nombre', 'Proyecto de Mejoramiento de la Calidad de la Educación'),
    ('moneda', 'USD'),
    ('monto', '60000000'),
    ('numero_sigade', 'MBM0036'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 8948-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 8948-SV'),
    ('nombre', 'Proyecto de Desarrollo Económico Social Resiliente'),
    ('moneda', 'USD'),
    ('monto', '200000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 9065-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 9065-SV'),
    ('nombre', 'Proyecto Creciendo Saludables Juntos: Desarrollo Integral de la Primera Infancia en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '250000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 9067-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 9067-SV'),
    ('nombre', 'Proyecto y Aprender Juntos: Desarrollo Integral de la Primera Infancia en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '250000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 9100-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 9100-SV'),
    ('nombre', 'Proyecto de Respuesta de El Salvador ante el COVID-19'),
    ('moneda', 'USD'),
    ('monto', '20000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 9602-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 9602-SV'),
    ('nombre', 'Proyecto de Transporte Infraestructura Resiliente en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '150000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF 9612-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF 9612-SV'),
    ('nombre', 'Proyecto Promoviendo Oportunidades de Empleo y Desarrollo de Habilidades en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '150000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF NO-7084-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF NO-7084-ES'),
    ('nombre', 'RECONST.HOSP. DEST.POR EL TERR'),
    ('moneda', 'USD'),
    ('monto', '142600000'),
    ('numero_sigade', 'MBM0040'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF No. 9790-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF No. 9790-SV'),
    ('nombre', 'Proyecto de Mejora de la Atención de Salud en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '120000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF TF-099529
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF TF-099529'),
    ('nombre', 'Acuerdo para la Preparación de Propuesta de Readiness El Salvador'),
    ('moneda', 'USD'),
    ('monto', '3000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-1007-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-1007-0 ES'),
    ('nombre', 'SEGUNDO PROYECTO DE EDUCACION'),
    ('moneda', 'USD'),
    ('monto', '15456104'),
    ('numero_sigade', 'MBM0002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-1050-0 BES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-1050-0 BES'),
    ('nombre', 'PROYECTO DE LOT. CON SERVICIO'),
    ('moneda', 'USD'),
    ('monto', '2500000'),
    ('numero_sigade', 'MBM0003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-2873-S
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-2873-S'),
    ('nombre', 'PROG. DE REC.DE S.S. Y POB. AL'),
    ('moneda', 'USD'),
    ('monto', '63767434'),
    ('numero_sigade', 'MBM0004'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-3348-S ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-3348-S ES'),
    ('nombre', 'REHABILITACION DE LOS SECT.SOC'),
    ('moneda', 'USD'),
    ('monto', '25727480'),
    ('numero_sigade', 'MBM0007'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-3389-S ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-3389-S ES'),
    ('nombre', 'PROY ASIST.TEC.P´EL S.E.EL.'),
    ('moneda', 'USD'),
    ('monto', '9660412'),
    ('numero_sigade', 'MBM0018'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-3576-S-3576-A
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-3576-S-3576-A'),
    ('nombre', 'PROG. DE REF. E INV. SECT. AGR'),
    ('moneda', 'USD'),
    ('monto', '39550859'),
    ('numero_sigade', 'MBM0008'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-3646-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-3646-0 ES'),
    ('nombre', 'SEGUNDO PRESTAMO DE AJ.EST.'),
    ('moneda', 'USD'),
    ('monto', '50000000'),
    ('numero_sigade', 'MBM0010'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-3648-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-3648-0 ES'),
    ('nombre', 'PROYECTO DE ASISTENCIA TECNICA'),
    ('moneda', 'USD'),
    ('monto', '2407944'),
    ('numero_sigade', 'MBM0009'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-3920-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-3920-0 ES'),
    ('nombre', 'PROY.MODERN. DEL SECT. ENERGIA'),
    ('moneda', 'USD'),
    ('monto', '36038761'),
    ('numero_sigade', 'MBM0013'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-3945-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-3945-0 ES'),
    ('nombre', 'PROY. PARA LA MOD. DE LA ED.BA'),
    ('moneda', 'USD'),
    ('monto', '34000000'),
    ('numero_sigade', 'MBM0011'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-3946-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-3946-0 ES'),
    ('nombre', 'PROY. DE AS.TEC. PARA EL MEJ.'),
    ('moneda', 'USD'),
    ('monto', '16000000'),
    ('numero_sigade', 'MBM0012'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-3982-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-3982-0 ES'),
    ('nombre', 'PROY. ADMON. DE TIERRAS'),
    ('moneda', 'USD'),
    ('monto', '50000000'),
    ('numero_sigade', 'MBM0014'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-4082-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-4082-0 ES'),
    ('nombre', 'PROY. ASIST. TEC. P. LA M.S.P.'),
    ('moneda', 'USD'),
    ('monto', '24000000'),
    ('numero_sigade', 'MBM0016'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-4224-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-4224-0 ES'),
    ('nombre', 'PROYECTO DE EDUC. MEDIA'),
    ('moneda', 'USD'),
    ('monto', '58000000'),
    ('numero_sigade', 'MBM0017'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-4320-0 ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-4320-0 ES'),
    ('nombre', 'PROGRAMA REFORMA ED.FASE I'),
    ('moneda', 'USD'),
    ('monto', '88000000'),
    ('numero_sigade', 'MBM0019'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-7084- ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-7084- ES'),
    ('nombre', 'RECONST. HOSP. DEST.POR ELTERR'),
    ('moneda', 'USD'),
    ('monto', '142060000'),
    ('numero_sigade', 'MBM0040'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-7275-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-7275-ES'),
    ('nombre', 'Préstamo para Politica de Desarrollo en Base a un Crecimiento Amplio'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'MBM0020'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-7916-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-7916-SV'),
    ('nombre', 'PROYECTO DE FORTALECIMIENTO DE GOBIERNOS LOCALES'),
    ('moneda', 'USD'),
    ('monto', '80000000'),
    ('numero_sigade', 'MBM0025'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-7997-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-7997-SV'),
    ('nombre', 'Préstamo para Politicas de Desarrollo para Manejos de Riesgos de Desastres'),
    ('moneda', 'USD'),
    ('monto', '50000000'),
    ('numero_sigade', 'MBM0029'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-8048-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-8048-SV'),
    ('nombre', 'PRESTAMO DE POLITICAS DE DESARROLLO PARA FINANZAS PUBLICAS Y PROGRESO SOCIAL'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'MBM0031'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-8076-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-8076-SV'),
    ('nombre', 'Proyecto de Fortalacimiento del Sistema de Salud Pública'),
    ('moneda', 'USD'),
    ('monto', '80000000'),
    ('numero_sigade', 'MBM0034'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-9229-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-9229-SV'),
    ('nombre', 'Financiamiento Adicional para Proyecto de Respuesta de El Salvador ante el COVID-19'),
    ('moneda', 'USD'),
    ('monto', '50000000'),
    ('numero_sigade', 'S-N'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-9429-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-9429-SV'),
    ('nombre', 'Segundo Financiamiento Adicional para Proyecto de Respuesta de El Salvador ante el COVID-19'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'S-N'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BIRF-9513-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BIRF-9513-SV'),
    ('nombre', 'Proyecto de Resiliencia del Sector Agua en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'S-N'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BOCAFE0001-TENEDORES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BOCAFE0001-TENEDORES'),
    ('nombre', 'BOCAFE0001'),
    ('moneda', 'USD'),
    ('monto', '80000000'),
    ('numero_sigade', 'BOCAFE0001'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] BONOSBE2001
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'BONOSBE2001'),
    ('nombre', 'GASTOS DE CAPITAL DEL GOB.'),
    ('moneda', 'USD'),
    ('monto', '353500000'),
    ('numero_sigade', 'BONOSBE2001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] C.ITOH C.P.
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'C.ITOH C.P.'),
    ('nombre', 'C.ITOH C.P.'),
    ('moneda', 'USD'),
    ('monto', '692650'),
    ('numero_sigade', 'BJA0002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] CAIXA-PNC2000
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CAIXA-PNC2000'),
    ('nombre', 'CAIXA-PNC2000'),
    ('moneda', 'USD'),
    ('monto', '5741489'),
    ('numero_sigade', 'PIF00001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] CESCE C.P
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CESCE C.P'),
    ('nombre', 'CESCE C.P.'),
    ('moneda', 'USD'),
    ('monto', '999400'),
    ('numero_sigade', 'BES0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] CFA-12061
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CFA-12061'),
    ('nombre', 'Programa para la Transformación del Clima de Negocios de El Salvador, a través de la Facilitación del Comercio e Inversiones'),
    ('moneda', 'USD'),
    ('monto', '75000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] CFA-12251
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CFA-12251'),
    ('nombre', 'Programa de mejora ambiental, agua potable y saneamiento en la cuenca alta del río Lempa (Trifinio) y Puerto de la Libertad, en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '75000000'),
    ('numero_sigade', '-'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] CFA-12253
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CFA-12253'),
    ('nombre', 'Programa para el Fortalecimiento de Espacios Públicos para la sostenibilidad de la Seguridad y la Recuperación del Tejido Social'),
    ('moneda', 'USD'),
    ('monto', '68000000'),
    ('numero_sigade', '-'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] CFA-12279
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CFA-12279'),
    ('nombre', 'Programa de Apoyo a la Movilidad Urbana Baja en Emisiones'),
    ('moneda', 'USD'),
    ('monto', '75000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] CFA012205
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CFA012205'),
    ('nombre', 'Programa para la Implementación de un Sistema de Telemedicina en El Salvador”'),
    ('moneda', 'USD'),
    ('monto', '77000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] CFA012400
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CFA012400'),
    ('nombre', 'Programa de Desarrollo del sector Aeronáutico de El Salvador: El Salvador Vuela'),
    ('moneda', 'USD'),
    ('monto', '320000000'),
    ('numero_sigade', 'MCA0010'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] CFA012402
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CFA012402'),
    ('nombre', 'Operación de Fortalecimiento de la Soberanía de Conectividad de El Salvador: Cable Submarino'),
    ('moneda', 'USD'),
    ('monto', '145000000'),
    ('numero_sigade', 'MCA0011'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] CHINA AREAS CRITICAS
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CHINA AREAS CRITICAS'),
    ('nombre', 'CHINA-AREAS CRITICAS'),
    ('moneda', 'USD'),
    ('monto', '7682000'),
    ('numero_sigade', 'BCH0003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] COFACE CP
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'COFACE CP'),
    ('nombre', 'COFACE CP'),
    ('moneda', 'FRF'),
    ('monto', '99361076'),
    ('numero_sigade', 'BFR0004'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] CREDIT NATIONAL CP
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CREDIT NATIONAL CP'),
    ('nombre', 'CREDIT NATIONAL CP'),
    ('moneda', 'FRF'),
    ('monto', '9416451.7'),
    ('numero_sigade', 'BFR0003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] DCI-ALA-2009-020-152
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DCI-ALA-2009-020-152'),
    ('nombre', 'PROEDUCA'),
    ('moneda', 'EUR'),
    ('monto', '23000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] DCI-ALA-2011-282-216
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DCI-ALA-2011-282-216'),
    ('nombre', 'Contribución LAIF al Programa Caminos Rurales de El Salvador'),
    ('moneda', 'EUR'),
    ('monto', '5340000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] DCI-ALA-2014-341-133
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DCI-ALA-2014-341-133'),
    ('nombre', 'Promoción Derechos de Mujeres a través de'),
    ('moneda', 'EUR'),
    ('monto', '2250000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] DEUTSCHE BANK
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DEUTSCHE BANK'),
    ('nombre', 'CONTRATO COMERCIAL DE SUM'),
    ('moneda', 'USD'),
    ('monto', '2846993'),
    ('numero_sigade', 'PBC0005'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] DEUTSCHEBANK-RED
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DEUTSCHEBANK-RED'),
    ('nombre', 'SUMINS Y EQUI RED HOSPITALARIA'),
    ('moneda', 'USD'),
    ('monto', '2978554'),
    ('numero_sigade', 'PBC0008'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] DEUTSCHEBANK-UES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DEUTSCHEBANK-UES'),
    ('nombre', 'EQUIP.REAC.UNID INV.DES.(U.NAC'),
    ('moneda', 'USD'),
    ('monto', '3562138'),
    ('numero_sigade', 'PBC0006'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] EXIMBANK C.P.
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EXIMBANK C.P.'),
    ('nombre', 'EXIMBANK-JAPON CP'),
    ('moneda', 'JPY'),
    ('monto', '1630000000'),
    ('numero_sigade', 'BJA0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] EXIMBANK CEL.MODER.
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EXIMBANK CEL.MODER.'),
    ('nombre', 'EXIMBANK CEL.MODER.ENERGIA'),
    ('moneda', 'JPY'),
    ('monto', '1230000000'),
    ('numero_sigade', 'GBJA001'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] Exp.I Bank6020676001
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Exp.I Bank6020676001'),
    ('nombre', 'Rehabilit. agric.en El Salv.'),
    ('moneda', 'USD'),
    ('monto', '100000000'),
    ('numero_sigade', 'PEI0001'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] EXPORT IMPORT BANK O
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EXPORT IMPORT BANK O'),
    ('nombre', 'BALANZA DE PAGOS'),
    ('moneda', 'USD'),
    ('monto', '10000000'),
    ('numero_sigade', 'BCH0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] F.ROT-AID 11-005-00
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'F.ROT-AID 11-005-00'),
    ('nombre', 'Recalificación Socio- Económico y Cultural del Centro Histórico de San Salvador'),
    ('moneda', 'EUR'),
    ('monto', '12000000'),
    ('numero_sigade', 'FROT-AID01'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] F.ROT-AID 12-008-00
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'F.ROT-AID 12-008-00'),
    ('nombre', 'Ampliación de Oferta Educativa de Educación Media para Mejorar la Producción en 12 Dptos. del País'),
    ('moneda', 'EUR'),
    ('monto', '15000000'),
    ('numero_sigade', 'FROT-AID02'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] F.ROT-AID 13-003-00
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'F.ROT-AID 13-003-00'),
    ('nombre', 'Programa de Prevención y de Rehabilitación de Jovenes en Riesgo y en Conflicto con la Ley'),
    ('moneda', 'EUR'),
    ('monto', '5550000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FAES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FAES'),
    ('nombre', 'COMPRA DE 6 HELIC. FUERZA ARMA'),
    ('moneda', 'USD'),
    ('monto', '5025434'),
    ('numero_sigade', 'PC00001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FDO.INT.COOP.Y DESAR
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FDO.INT.COOP.Y DESAR'),
    ('nombre', 'CONST.VIVIENDAS MITCH'),
    ('moneda', 'USD'),
    ('monto', '4000000'),
    ('numero_sigade', 'BCH0002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA 2000001431
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA 2000001431'),
    ('nombre', 'Programa Nacional de Transformación Económica Rural para el Buen Vivir-Rural Adelante'),
    ('moneda', 'DEG'),
    ('monto', '3560000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA 2000001432
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA 2000001432'),
    ('nombre', 'Programa Nacional de Transformación Económica Rural para el Buen Vivir-Rural Adelante'),
    ('moneda', 'EUR'),
    ('monto', '10850000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA 579-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA 579-SV'),
    ('nombre', 'PROG DE RECONST. Y MODER. RURA'),
    ('moneda', 'SDR'),
    ('monto', '15650000'),
    ('numero_sigade', 'MFI0006'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA 728-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA 728-SV'),
    ('nombre', 'PROYECTO DE DESARROLLO Y MODERNIZACION RURAL PARA REGION CENTRAL Y PARACENTRAL'),
    ('moneda', 'DEG'),
    ('monto', '9500000'),
    ('numero_sigade', 'MFI008'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA 784-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA 784-SV'),
    ('nombre', 'Proyecto de Desarrollo y Modernización Rural para la Región Oriental (PRODEMORO)'),
    ('moneda', 'DEG'),
    ('monto', '963460.13'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA E-6-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA E-6-SV'),
    ('nombre', 'Proyecto de Des. y Moder. Rural para las reg. Central y Paracentral (PRODEMOR-Central)-Ampliación'),
    ('moneda', 'EUR'),
    ('monto', '11150000'),
    ('numero_sigade', 'MFI0009'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA I-828-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA I-828-SV'),
    ('nombre', 'Programa de Competitividad Territorial Rural (Amanecer Rural)'),
    ('moneda', 'DEG'),
    ('monto', '11150000'),
    ('numero_sigade', 'MFI00011'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA-163
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA-163'),
    ('nombre', 'CREDITO AGROPECUARIO III ETAPA'),
    ('moneda', 'SDR'),
    ('monto', '5050000'),
    ('numero_sigade', 'MFI0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA-267
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA-267'),
    ('nombre', 'DESARROLLO AGRICOLA PARA PEQUE'),
    ('moneda', 'SDR'),
    ('monto', '6314293'),
    ('numero_sigade', 'MFI0002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA-322
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA-322'),
    ('nombre', 'REHABILITACION Y DESARROLLO PO'),
    ('moneda', 'SDR'),
    ('monto', '9250000'),
    ('numero_sigade', 'MFI0003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA-465 SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA-465 SV'),
    ('nombre', 'PRODENOR-MAG'),
    ('moneda', 'SDR'),
    ('monto', '13050000'),
    ('numero_sigade', 'MFI0004'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA-666
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA-666'),
    ('nombre', 'Proyecto de Desarrollo y Modernización Rural de la Region Oriental (PRODEMORO)'),
    ('moneda', 'DEG'),
    ('monto', '9950000'),
    ('numero_sigade', 'MFI0007'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIDA508-SV
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIDA508-SV'),
    ('nombre', 'DES. RURAL REG.CTRAL PRODAP II'),
    ('moneda', 'SDR'),
    ('monto', '9550000'),
    ('numero_sigade', 'MFI0005'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FIVEN-REPROG.ARR II
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FIVEN-REPROG.ARR II'),
    ('nombre', 'REPROG.DE ADEUDOS ARREGLO II'),
    ('moneda', 'USD'),
    ('monto', '6120182'),
    ('numero_sigade', 'BVE0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FMS-ES-917D
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FMS-ES-917D'),
    ('nombre', 'FMS-ES-917D C.P'),
    ('moneda', 'USD'),
    ('monto', '41821389'),
    ('numero_sigade', 'BEU0014'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FOCAP - AECID
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FOCAP - AECID'),
    ('nombre', 'Fondo Común de Apoyo Programático al Programa Comunidades Solidarias'),
    ('moneda', 'EUR'),
    ('monto', '16000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] FOCAP - LUXEMBURGO
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'FOCAP - LUXEMBURGO'),
    ('nombre', 'Fondo Común de Apoyo Programático al Programa Comunidades Solidarias'),
    ('moneda', 'EUR'),
    ('monto', '10000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] GENERAL BANK CEL 90M
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'GENERAL BANK CEL 90M'),
    ('nombre', 'GENERAL BANK CEL 90M'),
    ('moneda', 'BEF'),
    ('monto', '90000000'),
    ('numero_sigade', 'BBET001'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] GOBIERNO DE JAPON - BIENES IMPORTADOS
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'GOBIERNO DE JAPON - BIENES IMPORTADOS'),
    ('nombre', 'Gobierno de Japón – Bienes Importados'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] GOJA-2KR
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'GOJA-2KR'),
    ('nombre', 'GOJA-2KR (DONACION)'),
    ('moneda', '-'),
    ('monto', '0'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] GRT-SW-12281-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'GRT-SW-12281-ES'),
    ('nombre', '--'),
    ('moneda', 'USD'),
    ('monto', '1950000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] ICO (16907.0)HUELLA
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ICO (16907.0)HUELLA'),
    ('nombre', 'HUELLA GENETICA'),
    ('moneda', 'USD'),
    ('monto', '787195'),
    ('numero_sigade', 'BES0021'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] ICO (016901.0)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ICO (016901.0)'),
    ('nombre', 'EQUI.MEDIC-INDUST RED HOSPITAL'),
    ('moneda', 'USD'),
    ('monto', '3388863'),
    ('numero_sigade', 'BES0002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] ICO (016902.0)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ICO (016902.0)'),
    ('nombre', 'EQUIPAM.MED-QUIR.1-2-3 NIVELES'),
    ('moneda', 'USD'),
    ('monto', '3554877'),
    ('numero_sigade', 'BES0003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] ICO (016903.0)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ICO (016903.0)'),
    ('nombre', 'INFORMATIZACION PNC-2000'),
    ('moneda', 'USD'),
    ('monto', '5741489'),
    ('numero_sigade', 'BES0004'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] ICO (16906.0)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ICO (16906.0)'),
    ('nombre', 'SUM.EQ. REAC. UNID. INV.DES.C'),
    ('moneda', 'USD'),
    ('monto', '3562138'),
    ('numero_sigade', 'BES0007'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] ICO (16908.0)POLIDEP
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ICO (16908.0)POLIDEP'),
    ('nombre', 'CONSTRUC. EQUI. C. POLIDEPORTI'),
    ('moneda', 'EUR'),
    ('monto', '26000000'),
    ('numero_sigade', 'BES0008'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] ICO 01069015.0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ICO 01069015.0'),
    ('nombre', 'Programa de Caminos Rurales Progresivos y Mejoramiento de Caminos a Nivel Nacional'),
    ('moneda', 'USD'),
    ('monto', '30000000'),
    ('numero_sigade', 'BES0022'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] ICO 016909.0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ICO 016909.0'),
    ('nombre', 'APOYO AL EQUIPAMIENTO'),
    ('moneda', 'USD'),
    ('monto', '2978554'),
    ('numero_sigade', 'BES0009'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] ICO BOMBERO
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ICO BOMBERO'),
    ('nombre', 'EQUIPAMIENTO DE RESCATE Y SALVAMENTO DEL CUERPO DE BOMBEROS'),
    ('moneda', 'USD'),
    ('monto', '2778072'),
    ('numero_sigade', 'BAL0006'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] INST DE CREDITO OFIC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'INST DE CREDITO OFIC'),
    ('nombre', 'CONTRATO COMERCIAL DE SUMINIST'),
    ('moneda', 'USD'),
    ('monto', '2846994'),
    ('numero_sigade', 'BES0006'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] INST.DE CREDITO OFIC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'INST.DE CREDITO OFIC'),
    ('nombre', 'MEJORAS RIO LEMPA'),
    ('moneda', 'USD'),
    ('monto', '15438600'),
    ('numero_sigade', 'GBES003'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] JBIC P5 (CEPA)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'JBIC P5 (CEPA)'),
    ('nombre', 'PROY.DES PTO.CUTUCO- CEPA'),
    ('moneda', 'JPY'),
    ('monto', '11200000000'),
    ('numero_sigade', 'BJAT005'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] JBIC-ES-P3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'JBIC-ES-P3'),
    ('nombre', 'RECONSTR.PUENTE CUSCATLAN'),
    ('moneda', 'JPY'),
    ('monto', '10300000000'),
    ('numero_sigade', 'BJA0003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] JICA ES SB1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'JICA ES SB1'),
    ('nombre', 'Préstamo Contingencial para la recuperacion ante desastres naturales'),
    ('moneda', 'YJP'),
    ('monto', '5000000000'),
    ('numero_sigade', 'BJA0004'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] JICA ES-F-P1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'JICA ES-F-P1'),
    ('nombre', 'Proyecto de Construcción del Bypass en la Ciudad de San Miguel'),
    ('moneda', 'USD'),
    ('monto', '51370075'),
    ('numero_sigade', 'S-N'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] JICA ES-P6
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'JICA ES-P6'),
    ('nombre', 'Proyecto de Construcción de ByPass en la Ciudad de San Salvador'),
    ('moneda', 'YJP'),
    ('monto', '12595000000'),
    ('numero_sigade', 'BJAT06'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] JPN 52119-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'JPN 52119-ES'),
    ('nombre', 'Don. Japonesa para la Preparacion de la Ref. Educativa Fase II'),
    ('moneda', 'USD'),
    ('monto', '400000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KfW 25488
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KfW 25488'),
    ('nombre', 'Expansión de Central Hidroeléctrica 5 Noviembre'),
    ('moneda', 'USD'),
    ('monto', '57500000'),
    ('numero_sigade', 'BALCEL001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 25815
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 25815'),
    ('nombre', 'Apoyo al Plan Nacional para el Mejoramiento del Manejo de los Desechos Solidos de El Salvador'),
    ('moneda', 'EUR'),
    ('monto', '15000000'),
    ('numero_sigade', 'BAL0019'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 26022
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 26022'),
    ('nombre', 'Promoción Energía Solar El Salvador'),
    ('moneda', 'USD'),
    ('monto', '22236000'),
    ('numero_sigade', 'BALCEL002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 84-65-650
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 84-65-650'),
    ('nombre', 'PROG.HABIT.VIV.MIN.POPOTLAN II'),
    ('moneda', 'DEM'),
    ('monto', '17500000'),
    ('numero_sigade', 'BAL0003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 85-67-687
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 85-67-687'),
    ('nombre', 'AYUDA EN MERCANCIAS II'),
    ('moneda', 'DEM'),
    ('monto', '20000000'),
    ('numero_sigade', 'BAL0002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 86-66-034
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 86-66-034'),
    ('nombre', 'AYUDA EN MERCANCIAS III'),
    ('moneda', 'DEM'),
    ('monto', '20000000'),
    ('numero_sigade', 'BAL0005'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 86-66-216
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 86-66-216'),
    ('nombre', 'TELECOMUNICAC.RURAL EN EL AREA'),
    ('moneda', 'DEM'),
    ('monto', '13300000'),
    ('numero_sigade', 'BAL0004'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 87-66-370
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 87-66-370'),
    ('nombre', 'AGUA POTAB.SANEAM.BASIC RURAL'),
    ('moneda', 'DEM'),
    ('monto', '14800000'),
    ('numero_sigade', 'BAL0008'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 91-65-473
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 91-65-473'),
    ('nombre', 'REHABILIT.PUERTO DE ACAJUTLA'),
    ('moneda', 'DEM'),
    ('monto', '21362904'),
    ('numero_sigade', 'BAL0011'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 92-65-927
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 92-65-927'),
    ('nombre', 'AYUDA EN MERCANCIAS V'),
    ('moneda', 'DEM'),
    ('monto', '9000000'),
    ('numero_sigade', 'BAL0009'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 92-65-943CITT
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 92-65-943CITT'),
    ('nombre', 'La Const.yEq.Taller.y Lab.CITT'),
    ('moneda', 'DEM'),
    ('monto', '6400000'),
    ('numero_sigade', 'BAL0010'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 93-65-966
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 93-65-966'),
    ('nombre', 'PROGRAM. AJUSTE ESTRUC. FASE I'),
    ('moneda', 'DEM'),
    ('monto', '20000000'),
    ('numero_sigade', 'BAL0013'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 94-65-665
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 94-65-665'),
    ('nombre', 'NUEVO CAMPUS UNIVERSITARIO'),
    ('moneda', 'DEM'),
    ('monto', '7000000'),
    ('numero_sigade', 'BAL0007'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 94-65-907
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 94-65-907'),
    ('nombre', 'CONSTRUC.Y AMPLIAC. ICAS-UCA'),
    ('moneda', 'DEM'),
    ('monto', '7834526'),
    ('numero_sigade', 'BAL0015'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW 95-66-563
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW 95-66-563'),
    ('nombre', 'AYUDA EN MERCANCIAS VI'),
    ('moneda', 'DEM'),
    ('monto', '11500000'),
    ('numero_sigade', 'BAL0014'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW CONVIVIR
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW CONVIVIR'),
    ('nombre', 'Espacios Seguros de Convivencia para Jóvenes en El Salvador (CONVIVIR)'),
    ('moneda', 'EUR'),
    ('monto', '17000000'),
    ('numero_sigade', 'BAL0020'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW ES 2003 65 718
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW ES 2003 65 718'),
    ('nombre', 'Programa Fomento del Desarrollo Local y Gobernanza FISDL IV'),
    ('moneda', 'EUR'),
    ('monto', '13994257.02'),
    ('numero_sigade', 'BAL0018'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW No. 193004900
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW No. 193004900'),
    ('nombre', 'Donacion de KFW por préstamo KFW -25815'),
    ('moneda', 'EUR'),
    ('monto', '1000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW-002-BMI9
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW-002-BMI9'),
    ('nombre', 'KFW-002'),
    ('moneda', 'DEM'),
    ('monto', '8000000'),
    ('numero_sigade', 'FBAL002'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW-2001-65-811
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW-2001-65-811'),
    ('nombre', 'RECT. Y DES. LOCAL (FISDL III)'),
    ('moneda', 'EUR'),
    ('monto', '2045167.5'),
    ('numero_sigade', 'BAL0017'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW-2017.6872.0
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW-2017.6872.0'),
    ('nombre', 'Aporte Financiero'),
    ('moneda', 'EUR'),
    ('monto', '12551536.65'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW-84-65-767-BFA
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW-84-65-767-BFA'),
    ('nombre', 'KFW-84-65-767-BFA FONDO'),
    ('moneda', 'DEM'),
    ('monto', '8200000'),
    ('numero_sigade', 'FBAL001'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW-84-67-672
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW-84-67-672'),
    ('nombre', 'AYUDA EN MERCANCIAS I'),
    ('moneda', 'DEM'),
    ('monto', '30000000'),
    ('numero_sigade', 'BAL0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW-87-65-356
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW-87-65-356'),
    ('nombre', 'AYUDA EN MERCANCIAS IV'),
    ('moneda', 'DEM'),
    ('monto', '20000000'),
    ('numero_sigade', 'BAL0006'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW-94-65-915
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW-94-65-915'),
    ('nombre', 'PROGRAMA FIS-III ETAPA'),
    ('moneda', 'DEM'),
    ('monto', '22500000'),
    ('numero_sigade', 'BAL0012'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW-97-65-819
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW-97-65-819'),
    ('nombre', 'AMPLIAC.FORTALEC.CITT-II'),
    ('moneda', 'DEM'),
    ('monto', '5000000'),
    ('numero_sigade', 'BAL0016'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] KFW-BMZ-No. 2017.6524.7
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'KFW-BMZ-No. 2017.6524.7'),
    ('nombre', 'Adaptación Urbana al Cambio Climático en Centroamérica - Componente El Salvado'),
    ('moneda', 'EUR'),
    ('monto', '23551536.65'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] MCC-CRM
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'MCC-CRM'),
    ('nombre', 'Convenio del Reto del Milenio entre el Gobierno de la República y los Estados Unidos de América, MCC'),
    ('moneda', 'USD'),
    ('monto', '460940000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] Modificar
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Modificar'),
    ('nombre', 'Modificar Convenio'),
    ('moneda', 'MON'),
    ('monto', '0'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] NA
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'NA'),
    ('nombre', '&nbsp;'),
    ('moneda', 'MON'),
    ('monto', '0'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] NATEXIS BANQ 799
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'NATEXIS BANQ 799'),
    ('nombre', 'NATEXIS BANQ 799-OA1'),
    ('moneda', 'FRF'),
    ('monto', '50000000'),
    ('numero_sigade', 'BFRT001'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] NATIXIS C34 0A1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'NATIXIS C34 0A1'),
    ('nombre', 'Proyecto de Rehabilitación de la Planta de Tratamiento de Agua Potable de Las Pavas y de su Red de Aducción'),
    ('moneda', 'EUR'),
    ('monto', '53000000'),
    ('numero_sigade', 'PR0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] OECF-ES-P1-CEL PROG.
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'OECF-ES-P1-CEL PROG.'),
    ('nombre', 'OECF-ES-P1-CEL PROG.EMERG.'),
    ('moneda', 'JPY'),
    ('monto', '8150000000'),
    ('numero_sigade', 'BJAT001'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] OECF-ES-P2 ANDA
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'OECF-ES-P2 ANDA'),
    ('nombre', 'OECF-ES-P2 ANDA AGUA POTABLE'),
    ('moneda', 'JPY'),
    ('monto', '1190000000'),
    ('numero_sigade', 'BJAT002'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] OECF-P4-CEL.DES-ELEC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'OECF-P4-CEL.DES-ELEC'),
    ('nombre', 'OECF-ES-P4-CEL DES.ELECTRICO'),
    ('moneda', 'JPY'),
    ('monto', '5500000000'),
    ('numero_sigade', 'BJAT003'),
    ('codigo_tipo_convenio', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] OFID 1433P
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'OFID 1433P'),
    ('nombre', 'Programa de Competitividad Territorial Rural (Amanecer Rural)'),
    ('moneda', 'USD'),
    ('monto', '15000000'),
    ('numero_sigade', 'MOFID001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] OFID 14611P
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'OFID 14611P'),
    ('nombre', 'Proyecto de Respuesta COVID-19 El Salvador'),
    ('moneda', 'USD'),
    ('monto', '15000000'),
    ('numero_sigade', '--'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PACSES - UE
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PACSES - UE'),
    ('nombre', 'Programa de Apoyo a Comunidades Solidarias (DCI-ALA-2011-022-647)'),
    ('moneda', 'EUR'),
    ('monto', '47400000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PAPSES - UE
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PAPSES - UE'),
    ('nombre', 'Plan Nacional de Desarrollo, Protección e Inclusión Social'),
    ('moneda', 'USD'),
    ('monto', '12000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480 20% D.NO COND
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480 20% D.NO COND'),
    ('nombre', 'PL-480 20% D.NO CONDONADA'),
    ('moneda', 'USD'),
    ('monto', '67101271'),
    ('numero_sigade', 'BEU0006'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480 CLUB DE PARIS
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480 CLUB DE PARIS'),
    ('nombre', 'PL-480 CLUB DE PARIS'),
    ('moneda', 'USD'),
    ('monto', '1023669'),
    ('numero_sigade', 'BEU0002'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480-1993 CCC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480-1993 CCC'),
    ('nombre', 'PL-480-1993 CCC'),
    ('moneda', 'USD'),
    ('monto', '33226729'),
    ('numero_sigade', 'BEU0007'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480-1995 CCC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480-1995 CCC'),
    ('nombre', 'PL-480-1995 CCC'),
    ('moneda', 'USD'),
    ('monto', '9640803'),
    ('numero_sigade', 'BEU0008'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480-1996 CCC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480-1996 CCC'),
    ('nombre', 'PL-480-1996 CCC'),
    ('moneda', 'USD'),
    ('monto', '12152231'),
    ('numero_sigade', 'BEU0009'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480-1997 CCC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480-1997 CCC'),
    ('nombre', 'PL-480-1997 CCC'),
    ('moneda', 'USD'),
    ('monto', '9172006'),
    ('numero_sigade', 'BEU0010'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480-1998 CCC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480-1998 CCC'),
    ('nombre', 'PL-480-1998 CCC'),
    ('moneda', 'USD'),
    ('monto', '4712050'),
    ('numero_sigade', 'BEU0011'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480-1999
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480-1999'),
    ('nombre', 'PL-480-1999 CCC'),
    ('moneda', 'USD'),
    ('monto', '3783883'),
    ('numero_sigade', 'BEU0017'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480-90 CCC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480-90 CCC'),
    ('nombre', 'PL-480-90 CCC'),
    ('moneda', 'USD'),
    ('monto', '39642650'),
    ('numero_sigade', 'BEU0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480-91 CCC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480-91 CCC'),
    ('nombre', 'PL-480-91 CCC'),
    ('moneda', 'USD'),
    ('monto', '34819338'),
    ('numero_sigade', 'BEU0003'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480-92 CCC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480-92 CCC'),
    ('nombre', 'PL-480-92 CCC'),
    ('moneda', 'USD'),
    ('monto', '26416686'),
    ('numero_sigade', 'BEU0004'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL-480-92-A CCC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL-480-92-A CCC'),
    ('nombre', 'PL-480-92-A CCC'),
    ('moneda', 'USD'),
    ('monto', '2804654'),
    ('numero_sigade', 'BEU0005'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] PL480-CCC
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PL480-CCC'),
    ('nombre', 'PL-480-2001 CCC'),
    ('moneda', 'USD'),
    ('monto', '2486367'),
    ('numero_sigade', 'PL480-CCC'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] S-N
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'S-N'),
    ('nombre', 'S-N'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] SFD 1-835
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'SFD 1-835'),
    ('nombre', 'Proyecto de Tratamiento de Aguas y Generación de Energía con Biogás a partir de Agua del Río Acelhuate'),
    ('moneda', 'USD'),
    ('monto', '83000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] SLV- 060-B
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'SLV- 060-B'),
    ('nombre', '---'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] SLV-001-B
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'SLV-001-B'),
    ('nombre', '--'),
    ('moneda', 'USD'),
    ('monto', '23280644.52'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] SLV-041-B
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'SLV-041-B'),
    ('nombre', '--'),
    ('moneda', 'USD'),
    ('monto', '4205472.45'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] SLV-056-B
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'SLV-056-B'),
    ('nombre', '--'),
    ('moneda', 'USD'),
    ('monto', '53000000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] SLV-059-B
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'SLV-059-B'),
    ('nombre', '---'),
    ('moneda', 'USD'),
    ('monto', '0'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] SVD-025
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'SVD-025'),
    ('nombre', 'Empleo Juvenil y Digitalización'),
    ('moneda', 'EUR'),
    ('monto', '4522000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] TF024109
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'TF024109'),
    ('nombre', '--'),
    ('moneda', 'USD'),
    ('monto', '350000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] TF026534
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'TF026534'),
    ('nombre', '--'),
    ('moneda', 'USD'),
    ('monto', '250000'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] TF0B9765-ES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'TF0B9765-ES'),
    ('nombre', 'Programa Crecer y Aprender Juntos: Desarrollo Integral de la Primera Infancia en El Salvador'),
    ('moneda', 'USD'),
    ('monto', '3915630'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] THE OPEC FUND
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'THE OPEC FUND'),
    ('nombre', 'AYUDA A LA BALANZA DE PAGOS'),
    ('moneda', 'USD'),
    ('monto', '1750000'),
    ('numero_sigade', 'MOP0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] TRANSTOOLS,S.A.DEC.V
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'TRANSTOOLS,S.A.DEC.V'),
    ('nombre', 'ELAB.Y SUMINISTRO LIBRETAS PAS'),
    ('moneda', 'USD'),
    ('monto', '8507330'),
    ('numero_sigade', 'PRP0001'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CONVENIO_FINANCIAMIENTO] YUTONG-BUS-1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'YUTONG-BUS-1'),
    ('nombre', 'Contratación de Suministro de Vehículos a través de los cuales se brindará el Servicio Esencial de Transporte Público de Pasajeros tipo Colectivo con Financiamiento Incluido'),
    ('moneda', 'CNY'),
    ('monto', '1301656680'),
    ('numero_sigade', 'NA'),
    ('codigo_tipo_convenio', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CONVENIO_FINANCIAMIENTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo PARAMETRO_EVALUACION_SOCIAL — Catálogo "Parámetros" (evaluación social de beneficios)
-- Registros de nivel 1: 10   Campos: codigo, nombre, factor_correccion
-- =====================================================================
-- [PARAMETRO_EVALUACION_SOCIAL] Valor social del tiempo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Valor social del tiempo'),
    ('nombre', 'Valor social del tiempo'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PARAMETRO_EVALUACION_SOCIAL' AND t.field_name = v.field_name;
-- [PARAMETRO_EVALUACION_SOCIAL] Valor social de la vida
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Valor social de la vida'),
    ('nombre', 'Valor social de la vida'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PARAMETRO_EVALUACION_SOCIAL' AND t.field_name = v.field_name;
-- [PARAMETRO_EVALUACION_SOCIAL] Precio social del combustible
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Precio social del combustible'),
    ('nombre', 'Precio social del combustible'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PARAMETRO_EVALUACION_SOCIAL' AND t.field_name = v.field_name;
-- [PARAMETRO_EVALUACION_SOCIAL] Precio social del carbono
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Precio social del carbono'),
    ('nombre', 'Precio social del carbono'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PARAMETRO_EVALUACION_SOCIAL' AND t.field_name = v.field_name;
-- [PARAMETRO_EVALUACION_SOCIAL] PS agua potable
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PS agua potable'),
    ('nombre', 'PS agua potable'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PARAMETRO_EVALUACION_SOCIAL' AND t.field_name = v.field_name;
-- [PARAMETRO_EVALUACION_SOCIAL] PS transporte
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PS transporte'),
    ('nombre', 'PS transporte'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PARAMETRO_EVALUACION_SOCIAL' AND t.field_name = v.field_name;
-- [PARAMETRO_EVALUACION_SOCIAL] PS energía
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PS energía'),
    ('nombre', 'PS energía'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PARAMETRO_EVALUACION_SOCIAL' AND t.field_name = v.field_name;
-- [PARAMETRO_EVALUACION_SOCIAL] PS agropecuario
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PS agropecuario'),
    ('nombre', 'PS agropecuario'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PARAMETRO_EVALUACION_SOCIAL' AND t.field_name = v.field_name;
-- [PARAMETRO_EVALUACION_SOCIAL] PS forestal
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PS forestal'),
    ('nombre', 'PS forestal'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PARAMETRO_EVALUACION_SOCIAL' AND t.field_name = v.field_name;
-- [PARAMETRO_EVALUACION_SOCIAL] PS servicios turísticos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PS servicios turísticos'),
    ('nombre', 'PS servicios turísticos'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PARAMETRO_EVALUACION_SOCIAL' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo TIPO_BENEFICIO — Catálogo "Tipo de Beneficio"
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [TIPO_BENEFICIO] Beneficios Directos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_BENEFICIO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Beneficios Directos'),
    ('nombre', 'Beneficios Directos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_BENEFICIO' AND t.field_name = v.field_name;
-- [TIPO_BENEFICIO] Beneficios Indirectos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_BENEFICIO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Beneficios Indirectos'),
    ('nombre', 'Beneficios Indirectos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_BENEFICIO' AND t.field_name = v.field_name;
-- [TIPO_BENEFICIO] Externalidades
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_BENEFICIO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Externalidades'),
    ('nombre', 'Externalidades')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_BENEFICIO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo TIPO_BIEN_RESCATE — Catálogo "Tipo de bienes (Valor de rescate)"
-- Registros de nivel 1: 4   Campos: codigo, nombre, factor_correccion
-- =====================================================================
-- [TIPO_BIEN_RESCATE] Equipos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_BIEN_RESCATE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Equipos'),
    ('nombre', 'Equipos'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_BIEN_RESCATE' AND t.field_name = v.field_name;
-- [TIPO_BIEN_RESCATE] Edificios
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_BIEN_RESCATE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Edificios'),
    ('nombre', 'Edificios'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_BIEN_RESCATE' AND t.field_name = v.field_name;
-- [TIPO_BIEN_RESCATE] Terrenos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_BIEN_RESCATE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Terrenos'),
    ('nombre', 'Terrenos'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_BIEN_RESCATE' AND t.field_name = v.field_name;
-- [TIPO_BIEN_RESCATE] Vehículos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_BIEN_RESCATE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Vehículos'),
    ('nombre', 'Vehículos'),
    ('factor_correccion', '1.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_BIEN_RESCATE' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo UNIDAD_EJECUTORA_EVALUACION_FINANCIERA — Instituciones (unidades ejecutoras) con acceso al botón "Evaluación Financiera"
-- Registros de nivel 1: 5   Campos: codigo, nombre, item_sigla, clasificacion_institucional, referencia_cruzada
-- =====================================================================
-- [UNIDAD_EJECUTORA_EVALUACION_FINANCIERA] CEPA
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CEPA'),
    ('nombre', 'Comisión Ejecutiva Portuaria Autónoma'),
    ('item_sigla', 'CEPA'),
    ('clasificacion_institucional', 'Empresa Pública No Financiera'),
    ('referencia_cruzada', 'sigla corresponde a un registro del catálogo UNIDAD_EJECUTORA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA_EVALUACION_FINANCIERA] CEL
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CEL'),
    ('nombre', 'Comisión Ejecutiva Hidroeléctrica del Río Lempa'),
    ('item_sigla', 'CEL'),
    ('clasificacion_institucional', 'Empresa Pública No Financiera'),
    ('referencia_cruzada', 'sigla corresponde a un registro del catálogo UNIDAD_EJECUTORA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA_EVALUACION_FINANCIERA] ANDA
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ANDA'),
    ('nombre', 'Administración Nacional de Acueductos y Alcantarillados'),
    ('item_sigla', 'ANDA'),
    ('clasificacion_institucional', 'Empresa Pública No Financiera'),
    ('referencia_cruzada', 'sigla corresponde a un registro del catálogo UNIDAD_EJECUTORA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA_EVALUACION_FINANCIERA] INDES
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'INDES'),
    ('nombre', 'Instituto Nacional de los Deportes de El Salvador'),
    ('item_sigla', 'INDES'),
    ('clasificacion_institucional', 'Institución Descentralizada No Empresarial'),
    ('referencia_cruzada', 'sigla corresponde a un registro del catálogo UNIDAD_EJECUTORA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA' AND t.field_name = v.field_name;
-- [UNIDAD_EJECUTORA_EVALUACION_FINANCIERA] ISTU
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'ISTU'),
    ('nombre', 'Instituto Salvadoreño de Turismo'),
    ('item_sigla', 'ISTU'),
    ('clasificacion_institucional', 'Institución Descentralizada No Empresarial'),
    ('referencia_cruzada', 'sigla corresponde a un registro del catálogo UNIDAD_EJECUTORA')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo PRODUCTO — Catálogo de Productos e Indicadores (Producto > Indicador de Producto)
-- Registros de nivel 1: 1   Campos: codigo, nombre
-- =====================================================================
-- [PRODUCTO] 2201021
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PRODUCTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2201021'),
    ('nombre', 'Infraestructura educativa construida')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PRODUCTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo RESULTADO — Catálogo de Resultados (Indicadores de Resultado)
-- Registros de nivel 1: 1   Campos: codigo, nombre
-- =====================================================================
-- [RESULTADO] R2200000
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'RESULTADO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'R2200000'),
    ('nombre', 'Mejoras en diversos temas')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'RESULTADO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ODS_ONU — Catálogo ¿A cuáles ODS contribuye? (Objetivos de Desarrollo Sostenible)
-- Registros de nivel 1: 17   Campos: codigo, nombre, numero_ods
-- =====================================================================
-- [ODS_ONU] Fin de la pobreza
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Fin de la pobreza'),
    ('nombre', 'Fin de la pobreza'),
    ('numero_ods', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Hambre cero
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Hambre cero'),
    ('nombre', 'Hambre cero'),
    ('numero_ods', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Salud y bienestar
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Salud y bienestar'),
    ('nombre', 'Salud y bienestar'),
    ('numero_ods', '3')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Educación de calidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Educación de calidad'),
    ('nombre', 'Educación de calidad'),
    ('numero_ods', '4')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Igualdad de género
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Igualdad de género'),
    ('nombre', 'Igualdad de género'),
    ('numero_ods', '5')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Agua limpia y saneamiento
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Agua limpia y saneamiento'),
    ('nombre', 'Agua limpia y saneamiento'),
    ('numero_ods', '6')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Energía asequible y no contaminante
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Energía asequible y no contaminante'),
    ('nombre', 'Energía asequible y no contaminante'),
    ('numero_ods', '7')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Trabajo decente y crecimiento económico
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Trabajo decente y crecimiento económico'),
    ('nombre', 'Trabajo decente y crecimiento económico'),
    ('numero_ods', '8')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Industria, innovación e infraestructura
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Industria, innovación e infraestructura'),
    ('nombre', 'Industria, innovación e infraestructura'),
    ('numero_ods', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Reducción de las desigualdades
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Reducción de las desigualdades'),
    ('nombre', 'Reducción de las desigualdades'),
    ('numero_ods', '10')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Ciudades y comunidades sostenibles
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Ciudades y comunidades sostenibles'),
    ('nombre', 'Ciudades y comunidades sostenibles'),
    ('numero_ods', '11')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Producción y consumo responsables
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Producción y consumo responsables'),
    ('nombre', 'Producción y consumo responsables'),
    ('numero_ods', '12')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Acción por el clima
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Acción por el clima'),
    ('nombre', 'Acción por el clima'),
    ('numero_ods', '13')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Vida submarina
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Vida submarina'),
    ('nombre', 'Vida submarina'),
    ('numero_ods', '14')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Vida de ecosistemas terrestres
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Vida de ecosistemas terrestres'),
    ('nombre', 'Vida de ecosistemas terrestres'),
    ('numero_ods', '15')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Paz, justicia e instituciones sólidas
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Paz, justicia e instituciones sólidas'),
    ('nombre', 'Paz, justicia e instituciones sólidas'),
    ('numero_ods', '16')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;
-- [ODS_ONU] Alianzas para lograr los objetivos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ODS_ONU';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Alianzas para lograr los objetivos'),
    ('nombre', 'Alianzas para lograr los objetivos'),
    ('numero_ods', '17')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ODS_ONU' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo MEDIO_AMBIENTE_COMPONENTE — Catálogo ¿A cuáles componentes del Medio Ambiente contribuye?
-- Registros de nivel 1: 2   Campos: codigo, nombre, numero
-- =====================================================================
-- [MEDIO_AMBIENTE_COMPONENTE] Medio Físico (agua, aire, suelos, otros)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEDIO_AMBIENTE_COMPONENTE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Medio Físico (agua, aire, suelos, otros)'),
    ('nombre', 'Medio Físico (agua, aire, suelos, otros)'),
    ('numero', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEDIO_AMBIENTE_COMPONENTE' AND t.field_name = v.field_name;
-- [MEDIO_AMBIENTE_COMPONENTE] Medio Biológico (flora, fauna, ecosistemas, áreas naturales protegidas)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEDIO_AMBIENTE_COMPONENTE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Medio Biológico (flora, fauna, ecosistemas, áreas naturales protegidas)'),
    ('nombre', 'Medio Biológico (flora, fauna, ecosistemas, áreas naturales protegidas)'),
    ('numero', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEDIO_AMBIENTE_COMPONENTE' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo GRUPO_POBLACIONAL_VULNERABLE — Catálogo de grupos poblacionales en situación de vulnerabilidad
-- Registros de nivel 1: 10   Campos: codigo, nombre, numero
-- =====================================================================
-- [GRUPO_POBLACIONAL_VULNERABLE] Personas en riesgo ambiental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Personas en riesgo ambiental'),
    ('nombre', 'Personas en riesgo ambiental'),
    ('numero', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRUPO_POBLACIONAL_VULNERABLE' AND t.field_name = v.field_name;
-- [GRUPO_POBLACIONAL_VULNERABLE] Personas con discapacidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Personas con discapacidad'),
    ('nombre', 'Personas con discapacidad'),
    ('numero', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRUPO_POBLACIONAL_VULNERABLE' AND t.field_name = v.field_name;
-- [GRUPO_POBLACIONAL_VULNERABLE] Tercera edad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Tercera edad'),
    ('nombre', 'Tercera edad'),
    ('numero', '3')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRUPO_POBLACIONAL_VULNERABLE' AND t.field_name = v.field_name;
-- [GRUPO_POBLACIONAL_VULNERABLE] Mujeres cabeza de hogar
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Mujeres cabeza de hogar'),
    ('nombre', 'Mujeres cabeza de hogar'),
    ('numero', '4')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRUPO_POBLACIONAL_VULNERABLE' AND t.field_name = v.field_name;
-- [GRUPO_POBLACIONAL_VULNERABLE] Primera infancia
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Primera infancia'),
    ('nombre', 'Primera infancia'),
    ('numero', '5')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRUPO_POBLACIONAL_VULNERABLE' AND t.field_name = v.field_name;
-- [GRUPO_POBLACIONAL_VULNERABLE] Personas retornadas
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Personas retornadas'),
    ('nombre', 'Personas retornadas'),
    ('numero', '6')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRUPO_POBLACIONAL_VULNERABLE' AND t.field_name = v.field_name;
-- [GRUPO_POBLACIONAL_VULNERABLE] Jóvenes vulnerables
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Jóvenes vulnerables'),
    ('nombre', 'Jóvenes vulnerables'),
    ('numero', '7')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRUPO_POBLACIONAL_VULNERABLE' AND t.field_name = v.field_name;
-- [GRUPO_POBLACIONAL_VULNERABLE] Desempleados
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Desempleados'),
    ('nombre', 'Desempleados'),
    ('numero', '8')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRUPO_POBLACIONAL_VULNERABLE' AND t.field_name = v.field_name;
-- [GRUPO_POBLACIONAL_VULNERABLE] Niñez y adolescencia
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Niñez y adolescencia'),
    ('nombre', 'Niñez y adolescencia'),
    ('numero', '9')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRUPO_POBLACIONAL_VULNERABLE' AND t.field_name = v.field_name;
-- [GRUPO_POBLACIONAL_VULNERABLE] Personas reinsertadas
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Personas reinsertadas'),
    ('nombre', 'Personas reinsertadas'),
    ('numero', '10')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'GRUPO_POBLACIONAL_VULNERABLE' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo MEJORA_CALIDAD_VIDA — Catálogo de mejoras en calidad de vida
-- Registros de nivel 1: 5   Campos: codigo, nombre, numero
-- =====================================================================
-- [MEJORA_CALIDAD_VIDA] Mayor disponibilidad de servicios (salud, educación, agua, energía, transporte, entre otros)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEJORA_CALIDAD_VIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Mayor disponibilidad de servicios (salud, educación, agua, energía, transporte, entre otros)'),
    ('nombre', 'Mayor disponibilidad de servicios (salud, educación, agua, energía, transporte, entre otros)'),
    ('numero', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEJORA_CALIDAD_VIDA' AND t.field_name = v.field_name;
-- [MEJORA_CALIDAD_VIDA] Mejoras en la calidad de servicios (salud, educación, agua, energía, transporte, entre otros)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEJORA_CALIDAD_VIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Mejoras en la calidad de servicios (salud, educación, agua, energía, transporte, entre otros)'),
    ('nombre', 'Mejoras en la calidad de servicios (salud, educación, agua, energía, transporte, entre otros)'),
    ('numero', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEJORA_CALIDAD_VIDA' AND t.field_name = v.field_name;
-- [MEJORA_CALIDAD_VIDA] Generación de empleo productivo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEJORA_CALIDAD_VIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Generación de empleo productivo'),
    ('nombre', 'Generación de empleo productivo'),
    ('numero', '3')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEJORA_CALIDAD_VIDA' AND t.field_name = v.field_name;
-- [MEJORA_CALIDAD_VIDA] Seguridad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEJORA_CALIDAD_VIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Seguridad'),
    ('nombre', 'Seguridad'),
    ('numero', '4')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEJORA_CALIDAD_VIDA' AND t.field_name = v.field_name;
-- [MEJORA_CALIDAD_VIDA] Acceso a vivienda
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MEJORA_CALIDAD_VIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Acceso a vivienda'),
    ('nombre', 'Acceso a vivienda'),
    ('numero', '5')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'MEJORA_CALIDAD_VIDA' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo DIMENSION_ELEGIBILIDAD — Tabla de Dimensiones, Criterios y Ponderación de Elegibilidad
-- Registros de nivel 1: 6   Campos: codigo, nombre
-- =====================================================================
-- [DIMENSION_ELEGIBILIDAD] DIM1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DIM1'),
    ('nombre', '1. Alineación estratégica')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_ELEGIBILIDAD' AND t.field_name = v.field_name;
-- [DIMENSION_ELEGIBILIDAD] DIM2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DIM2'),
    ('nombre', '2. Aspectos Sociales')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_ELEGIBILIDAD' AND t.field_name = v.field_name;
-- [DIMENSION_ELEGIBILIDAD] DIM3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DIM3'),
    ('nombre', '3. Rentabilidad social')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_ELEGIBILIDAD' AND t.field_name = v.field_name;
-- [DIMENSION_ELEGIBILIDAD] DIM4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DIM4'),
    ('nombre', '4. Aspectos Medioambientales')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_ELEGIBILIDAD' AND t.field_name = v.field_name;
-- [DIMENSION_ELEGIBILIDAD] DIM5
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DIM5'),
    ('nombre', '5. Sostenibilidad fiscal')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_ELEGIBILIDAD' AND t.field_name = v.field_name;
-- [DIMENSION_ELEGIBILIDAD] DIM6
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'DIM6'),
    ('nombre', '6. Madurez del Proyecto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_ELEGIBILIDAD' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo CRITERIO_PRIORIZACION — Criterios y Subcriterios de Priorización (versión coincidente con el PDF, Anexo A.1)
-- Registros de nivel 1: 4   Campos: codigo, nombre
-- =====================================================================
-- [CRITERIO_PRIORIZACION] CRIT1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_PRIORIZACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CRIT1'),
    ('nombre', 'CRITERIO 1 — MADUREZ TÉCNICA Y CUMPLIMIENTO REGULATORIO')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CRITERIO_PRIORIZACION' AND t.field_name = v.field_name;
-- [CRITERIO_PRIORIZACION] CRIT2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_PRIORIZACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CRIT2'),
    ('nombre', 'CRITERIO 2 — RENTABILIDAD SOCIAL O VALOR PÚBLICO')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CRITERIO_PRIORIZACION' AND t.field_name = v.field_name;
-- [CRITERIO_PRIORIZACION] CRIT3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_PRIORIZACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CRIT3'),
    ('nombre', 'CRITERIO 3 — IMPACTO TERRITORIAL Y CIERRE DE BRECHAS')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CRITERIO_PRIORIZACION' AND t.field_name = v.field_name;
-- [CRITERIO_PRIORIZACION] CRIT4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_PRIORIZACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CRIT4'),
    ('nombre', 'CRITERIO 4 — SOSTENIBILIDAD FISCAL Y FINANCIERA PARA LA ETAPA DE EJECUCIÓN')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CRITERIO_PRIORIZACION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo RANGO_PRIORIZACION — Rangos de Interpretación del puntaje de priorización
-- Registros de nivel 1: 4   Campos: codigo, nombre, rango_puntaje, implicacion
-- =====================================================================
-- [RANGO_PRIORIZACION] 85-100
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'RANGO_PRIORIZACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '85-100'),
    ('nombre', 'Priorizado para programación'),
    ('rango_puntaje', '85-100'),
    ('implicacion', 'Incorporación prioritaria al año n+1 del PRIPME. Elegible para PAIP con financiamiento.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'RANGO_PRIORIZACION' AND t.field_name = v.field_name;
-- [RANGO_PRIORIZACION] 70-84
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'RANGO_PRIORIZACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '70-84'),
    ('nombre', 'Priorizado condicional'),
    ('rango_puntaje', '70-84'),
    ('implicacion', 'Puede incorporarse al año n+1 del PRIPME sujeto a disponibilidad fiscal. Orientar ajustes con PAP.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'RANGO_PRIORIZACION' AND t.field_name = v.field_name;
-- [RANGO_PRIORIZACION] 50-69
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'RANGO_PRIORIZACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '50-69'),
    ('nombre', 'Elegible para fortalecimiento'),
    ('rango_puntaje', '50-69'),
    ('implicacion', 'No debe avanzar al PAIP en estado actual. Maduración mediante PAP.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'RANGO_PRIORIZACION' AND t.field_name = v.field_name;
-- [RANGO_PRIORIZACION] 0-49
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'RANGO_PRIORIZACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '0-49'),
    ('nombre', 'No priorizable en estado actual'),
    ('rango_puntaje', '0-49'),
    ('implicacion', 'Se recomienda reformulación o postergación.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'RANGO_PRIORIZACION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo TIPO_CAPITAL — Catálogo de tipo de capital
-- Registros de nivel 1: 4   Campos: codigo, nombre
-- =====================================================================
-- [TIPO_CAPITAL] Capital físico
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_CAPITAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Capital físico'),
    ('nombre', 'Capital físico')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_CAPITAL' AND t.field_name = v.field_name;
-- [TIPO_CAPITAL] Capital humano
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_CAPITAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Capital humano'),
    ('nombre', 'Capital humano')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_CAPITAL' AND t.field_name = v.field_name;
-- [TIPO_CAPITAL] Capital institucional
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_CAPITAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Capital institucional'),
    ('nombre', 'Capital institucional')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_CAPITAL' AND t.field_name = v.field_name;
-- [TIPO_CAPITAL] Otros capitales
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_CAPITAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Otros capitales'),
    ('nombre', 'Otros capitales')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_CAPITAL' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo TIPO_COSTO — Catálogo de Tipo de Costos
-- Registros de nivel 1: 12   Campos: codigo, nombre
-- =====================================================================
-- [TIPO_COSTO] Infraestructura
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Infraestructura'),
    ('nombre', 'Infraestructura')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;
-- [TIPO_COSTO] Infraestructura Informática
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Infraestructura Informática'),
    ('nombre', 'Infraestructura Informática')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;
-- [TIPO_COSTO] Equipamiento
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Equipamiento'),
    ('nombre', 'Equipamiento')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;
-- [TIPO_COSTO] Ambiental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Ambiental'),
    ('nombre', 'Ambiental')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;
-- [TIPO_COSTO] Capacitaciones
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Capacitaciones'),
    ('nombre', 'Capacitaciones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;
-- [TIPO_COSTO] Administración
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Administración'),
    ('nombre', 'Administración')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;
-- [TIPO_COSTO] Consultorías
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Consultorías'),
    ('nombre', 'Consultorías')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;
-- [TIPO_COSTO] Supervisión
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Supervisión'),
    ('nombre', 'Supervisión')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;
-- [TIPO_COSTO] Imprevistos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Imprevistos'),
    ('nombre', 'Imprevistos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;
-- [TIPO_COSTO] Derechos de Vía
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Derechos de Vía'),
    ('nombre', 'Derechos de Vía')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;
-- [TIPO_COSTO] Terrenos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Terrenos'),
    ('nombre', 'Terrenos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;
-- [TIPO_COSTO] Otros
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_COSTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Otros'),
    ('nombre', 'Otros')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_COSTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo TAMANO_PROYECTO — Catálogo tamaño del proyecto según monto
-- Registros de nivel 1: 3   Campos: codigo, nombre, rango_monto
-- =====================================================================
-- [TAMANO_PROYECTO] Pequeño
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TAMANO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Pequeño'),
    ('nombre', 'Pequeño'),
    ('rango_monto', 'hasta $1,000,000.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TAMANO_PROYECTO' AND t.field_name = v.field_name;
-- [TAMANO_PROYECTO] Mediano
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TAMANO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Mediano'),
    ('nombre', 'Mediano'),
    ('rango_monto', 'entre $1,000,001.00 hasta $5,000,000.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TAMANO_PROYECTO' AND t.field_name = v.field_name;
-- [TAMANO_PROYECTO] Grande
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TAMANO_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Grande'),
    ('nombre', 'Grande'),
    ('rango_monto', 'mayor a $5,000,001.00')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TAMANO_PROYECTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo COMPLEJIDAD_PROYECTO — Catálogo de complejidad del proyecto
-- Registros de nivel 1: 4   Campos: codigo, nombre
-- =====================================================================
-- [COMPLEJIDAD_PROYECTO] Complejidad baja
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'COMPLEJIDAD_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Complejidad baja'),
    ('nombre', 'Complejidad baja')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'COMPLEJIDAD_PROYECTO' AND t.field_name = v.field_name;
-- [COMPLEJIDAD_PROYECTO] Complejidad media
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'COMPLEJIDAD_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Complejidad media'),
    ('nombre', 'Complejidad media')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'COMPLEJIDAD_PROYECTO' AND t.field_name = v.field_name;
-- [COMPLEJIDAD_PROYECTO] Complejidad alta
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'COMPLEJIDAD_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Complejidad alta'),
    ('nombre', 'Complejidad alta')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'COMPLEJIDAD_PROYECTO' AND t.field_name = v.field_name;
-- [COMPLEJIDAD_PROYECTO] Todas las complejidades
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'COMPLEJIDAD_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Todas las complejidades'),
    ('nombre', 'Todas las complejidades')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'COMPLEJIDAD_PROYECTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo RUTA_PREINVERSION — Definición de Ruta de Preinversión (Tipo de capital x Tamaño x Complejidad -> Ruta)
-- Registros de nivel 1: 5   Campos: codigo, nombre, segun_tipo_capital, segun_tamano, segun_complejidad
-- =====================================================================
-- [RUTA_PREINVERSION] REGLA1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'RUTA_PREINVERSION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'REGLA1'),
    ('nombre', 'Perfil'),
    ('segun_tipo_capital', 'Capital Humano - Capital Institucional - Otros capitales'),
    ('segun_tamano', 'Todas las categorías'),
    ('segun_complejidad', 'Todas las Complejidades')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'RUTA_PREINVERSION' AND t.field_name = v.field_name;
-- [RUTA_PREINVERSION] REGLA2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'RUTA_PREINVERSION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'REGLA2'),
    ('nombre', 'Perfil con Diseño Básico'),
    ('segun_tipo_capital', 'Capital Físico'),
    ('segun_tamano', 'Pequeño'),
    ('segun_complejidad', 'Todas Complejidades')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'RUTA_PREINVERSION' AND t.field_name = v.field_name;
-- [RUTA_PREINVERSION] REGLA3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'RUTA_PREINVERSION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'REGLA3'),
    ('nombre', 'Perfil + Diseño'),
    ('segun_tipo_capital', 'Capital Físico'),
    ('segun_tamano', 'Mediano'),
    ('segun_complejidad', 'Complejidad Baja')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'RUTA_PREINVERSION' AND t.field_name = v.field_name;
-- [RUTA_PREINVERSION] REGLA4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'RUTA_PREINVERSION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'REGLA4'),
    ('nombre', 'Perfil + Prefactibilidad + Factibilidad + Diseño'),
    ('segun_tipo_capital', 'Capital Físico'),
    ('segun_tamano', 'Mediano'),
    ('segun_complejidad', 'Complejidad Media y Alta')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'RUTA_PREINVERSION' AND t.field_name = v.field_name;
-- [RUTA_PREINVERSION] REGLA5
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'RUTA_PREINVERSION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'REGLA5'),
    ('nombre', 'Perfil + Prefactibilidad + Factibilidad + Diseño'),
    ('segun_tipo_capital', 'Capital Físico'),
    ('segun_tamano', 'Grande'),
    ('segun_complejidad', 'Todas las Complejidades')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'RUTA_PREINVERSION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ETAPA_PROYECTO — Etapas del proyecto
-- Registros de nivel 1: 5   Campos: codigo, nombre
-- =====================================================================
-- [ETAPA_PROYECTO] Perfil
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ETAPA_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Perfil'),
    ('nombre', 'Perfil')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ETAPA_PROYECTO' AND t.field_name = v.field_name;
-- [ETAPA_PROYECTO] Prefactibilidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ETAPA_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Prefactibilidad'),
    ('nombre', 'Prefactibilidad')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ETAPA_PROYECTO' AND t.field_name = v.field_name;
-- [ETAPA_PROYECTO] Factibilidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ETAPA_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Factibilidad'),
    ('nombre', 'Factibilidad')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ETAPA_PROYECTO' AND t.field_name = v.field_name;
-- [ETAPA_PROYECTO] Diseño
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ETAPA_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Diseño'),
    ('nombre', 'Diseño')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ETAPA_PROYECTO' AND t.field_name = v.field_name;
-- [ETAPA_PROYECTO] Ejecución
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ETAPA_PROYECTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Ejecución'),
    ('nombre', 'Ejecución')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ETAPA_PROYECTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo HABILITACION_CAMPOS — Contenido de Iniciativas de Proyecto (habilitación de campos por etapa e iniciativa)
-- Registros de nivel 1: 5   Campos: codigo, nombre
-- =====================================================================
-- [HABILITACION_CAMPOS] G1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'HABILITACION_CAMPOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'G1'),
    ('nombre', '1. Identificación del proyecto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'HABILITACION_CAMPOS' AND t.field_name = v.field_name;
-- [HABILITACION_CAMPOS] G2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'HABILITACION_CAMPOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'G2'),
    ('nombre', '2. Formulación del proyecto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'HABILITACION_CAMPOS' AND t.field_name = v.field_name;
-- [HABILITACION_CAMPOS] G3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'HABILITACION_CAMPOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'G3'),
    ('nombre', '3. Evaluación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'HABILITACION_CAMPOS' AND t.field_name = v.field_name;
-- [HABILITACION_CAMPOS] G4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'HABILITACION_CAMPOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'G4'),
    ('nombre', '4. Programación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'HABILITACION_CAMPOS' AND t.field_name = v.field_name;
-- [HABILITACION_CAMPOS] G5
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'HABILITACION_CAMPOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'G5'),
    ('nombre', '5. Documentos anexos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'HABILITACION_CAMPOS' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ENTREGABLE_ETAPA — Catálogo "Entregable"
-- Registros de nivel 1: 4   Campos: codigo, nombre
-- =====================================================================
-- [ENTREGABLE_ETAPA] Estudio de perfil
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ENTREGABLE_ETAPA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Estudio de perfil'),
    ('nombre', 'Estudio de perfil')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ENTREGABLE_ETAPA' AND t.field_name = v.field_name;
-- [ENTREGABLE_ETAPA] Estudio de prefactibilidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ENTREGABLE_ETAPA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Estudio de prefactibilidad'),
    ('nombre', 'Estudio de prefactibilidad')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ENTREGABLE_ETAPA' AND t.field_name = v.field_name;
-- [ENTREGABLE_ETAPA] Estudio de factibilidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ENTREGABLE_ETAPA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Estudio de factibilidad'),
    ('nombre', 'Estudio de factibilidad')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ENTREGABLE_ETAPA' AND t.field_name = v.field_name;
-- [ENTREGABLE_ETAPA] Estudio de diseño
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ENTREGABLE_ETAPA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Estudio de diseño'),
    ('nombre', 'Estudio de diseño')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ENTREGABLE_ETAPA' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ESTADO_ESTUDIO_PREINVERSION — Catálogo de Estados del Estudio
-- Registros de nivel 1: 4   Campos: codigo, nombre, criterio
-- =====================================================================
-- [ESTADO_ESTUDIO_PREINVERSION] A tiempo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_ESTUDIO_PREINVERSION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'A tiempo'),
    ('nombre', 'A tiempo'),
    ('criterio', 'Presentan una ejecución igual al porcentaje programado al cuatrimestre.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_ESTUDIO_PREINVERSION' AND t.field_name = v.field_name;
-- [ESTADO_ESTUDIO_PREINVERSION] Atrasado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_ESTUDIO_PREINVERSION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Atrasado'),
    ('nombre', 'Atrasado'),
    ('criterio', 'El porcentaje registrado es menor al programado.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_ESTUDIO_PREINVERSION' AND t.field_name = v.field_name;
-- [ESTADO_ESTUDIO_PREINVERSION] Adelantado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_ESTUDIO_PREINVERSION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Adelantado'),
    ('nombre', 'Adelantado'),
    ('criterio', 'La ejecución es mayor a la programada al cuatrimestre, sin sobrepasar el porcentaje del campo ''Programado en el Año''.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_ESTUDIO_PREINVERSION' AND t.field_name = v.field_name;
-- [ESTADO_ESTUDIO_PREINVERSION] Finalizado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_ESTUDIO_PREINVERSION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Finalizado'),
    ('nombre', 'Finalizado'),
    ('criterio', 'Estudios concluidos conforme a la ejecución de metas físicas.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_ESTUDIO_PREINVERSION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo TIPO_FINANCIAMIENTO — Tipo de Financiamiento
-- Registros de nivel 1: 2   Campos: codigo, nombre
-- =====================================================================
-- [TIPO_FINANCIAMIENTO] INTERNO
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'INTERNO'),
    ('nombre', 'INTERNO')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [TIPO_FINANCIAMIENTO] EXTERNO
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EXTERNO'),
    ('nombre', 'EXTERNO')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_FINANCIAMIENTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo CLASIFICACION_ETAPA_FINANCIAMIENTO — Clasificación por Etapa (de financiamiento del proyecto)
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [CLASIFICACION_ETAPA_FINANCIAMIENTO] CON FINANCIAMIENTO
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CLASIFICACION_ETAPA_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'CON FINANCIAMIENTO'),
    ('nombre', 'CON FINANCIAMIENTO')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CLASIFICACION_ETAPA_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CLASIFICACION_ETAPA_FINANCIAMIENTO] EN GESTIÓN
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CLASIFICACION_ETAPA_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EN GESTIÓN'),
    ('nombre', 'EN GESTIÓN')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CLASIFICACION_ETAPA_FINANCIAMIENTO' AND t.field_name = v.field_name;
-- [CLASIFICACION_ETAPA_FINANCIAMIENTO] EN DEMANDA DE FINANCIAMIENTO
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CLASIFICACION_ETAPA_FINANCIAMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'EN DEMANDA DE FINANCIAMIENTO'),
    ('nombre', 'EN DEMANDA DE FINANCIAMIENTO')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CLASIFICACION_ETAPA_FINANCIAMIENTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ESTADO_PRIPME — Estados del PRIPME
-- Registros de nivel 1: 4   Campos: codigo, nombre
-- =====================================================================
-- [ESTADO_PRIPME] En elaboración
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PRIPME';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'En elaboración'),
    ('nombre', 'En elaboración')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PRIPME' AND t.field_name = v.field_name;
-- [ESTADO_PRIPME] En revisión (DGI)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PRIPME';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'En revisión (DGI)'),
    ('nombre', 'En revisión (DGI)')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PRIPME' AND t.field_name = v.field_name;
-- [ESTADO_PRIPME] Con observaciones
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PRIPME';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Con observaciones'),
    ('nombre', 'Con observaciones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PRIPME' AND t.field_name = v.field_name;
-- [ESTADO_PRIPME] Revisado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PRIPME';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Revisado'),
    ('nombre', 'Revisado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PRIPME' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo NIVEL_PRIORIZACION_PRIPME — Nivel de Priorización (PRIPME)
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [NIVEL_PRIORIZACION_PRIPME] Alta
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'NIVEL_PRIORIZACION_PRIPME';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Alta'),
    ('nombre', 'Alta')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'NIVEL_PRIORIZACION_PRIPME' AND t.field_name = v.field_name;
-- [NIVEL_PRIORIZACION_PRIPME] Media
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'NIVEL_PRIORIZACION_PRIPME';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Media'),
    ('nombre', 'Media')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'NIVEL_PRIORIZACION_PRIPME' AND t.field_name = v.field_name;
-- [NIVEL_PRIORIZACION_PRIPME] Baja
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'NIVEL_PRIORIZACION_PRIPME';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Baja'),
    ('nombre', 'Baja')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'NIVEL_PRIORIZACION_PRIPME' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ESTADO_REVISION_TECNICA — Estado de Revisión (Técnico)
-- Registros de nivel 1: 2   Campos: codigo, nombre
-- =====================================================================
-- [ESTADO_REVISION_TECNICA] Sin revisar
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_REVISION_TECNICA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Sin revisar'),
    ('nombre', 'Sin revisar')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_REVISION_TECNICA' AND t.field_name = v.field_name;
-- [ESTADO_REVISION_TECNICA] Revisado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_REVISION_TECNICA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Revisado'),
    ('nombre', 'Revisado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_REVISION_TECNICA' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo FORMATO_REPORTE_PRIPME — Formato de reporte (Generar reporte PRIPME)
-- Registros de nivel 1: 2   Campos: codigo, nombre
-- =====================================================================
-- [FORMATO_REPORTE_PRIPME] Hoja de cálculo - Excel
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FORMATO_REPORTE_PRIPME';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Hoja de cálculo - Excel'),
    ('nombre', 'Hoja de cálculo - Excel')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FORMATO_REPORTE_PRIPME' AND t.field_name = v.field_name;
-- [FORMATO_REPORTE_PRIPME] PDF-A
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FORMATO_REPORTE_PRIPME';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PDF-A'),
    ('nombre', 'PDF-A')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FORMATO_REPORTE_PRIPME' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ESTADOS_PROGRAMACION — Estados de Programación
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [ESTADOS_PROGRAMACION] Pendiente de Programar
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADOS_PROGRAMACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Pendiente de Programar'),
    ('nombre', 'Pendiente de Programar')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADOS_PROGRAMACION' AND t.field_name = v.field_name;
-- [ESTADOS_PROGRAMACION] En Programación
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADOS_PROGRAMACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'En Programación'),
    ('nombre', 'En Programación')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADOS_PROGRAMACION' AND t.field_name = v.field_name;
-- [ESTADOS_PROGRAMACION] Programado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADOS_PROGRAMACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Programado'),
    ('nombre', 'Programado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADOS_PROGRAMACION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo TIPO_TRANSACCION — Tipo de Transacción
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [TIPO_TRANSACCION] Refuerzo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_TRANSACCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Refuerzo'),
    ('nombre', 'Refuerzo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_TRANSACCION' AND t.field_name = v.field_name;
-- [TIPO_TRANSACCION] Incremento
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_TRANSACCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Incremento'),
    ('nombre', 'Incremento')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_TRANSACCION' AND t.field_name = v.field_name;
-- [TIPO_TRANSACCION] Disminución
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_TRANSACCION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Disminución'),
    ('nombre', 'Disminución')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_TRANSACCION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo TIPO_APROBACION — Tipo de Aprobación
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [TIPO_APROBACION] Acuerdo Ejecutivo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_APROBACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Acuerdo Ejecutivo'),
    ('nombre', 'Acuerdo Ejecutivo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_APROBACION' AND t.field_name = v.field_name;
-- [TIPO_APROBACION] Decreto Legislativo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_APROBACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Decreto Legislativo'),
    ('nombre', 'Decreto Legislativo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_APROBACION' AND t.field_name = v.field_name;
-- [TIPO_APROBACION] Nota de Autorización
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_APROBACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Nota de Autorización'),
    ('nombre', 'Nota de Autorización')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_APROBACION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo AGRUPACION_OPERACIONAL — Tabla de Agrupaciones Operacionales de la DGCG
-- Registros de nivel 1: 1   Campos: codigo, nombre
-- =====================================================================
-- [AGRUPACION_OPERACIONAL] 3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'AGRUPACION_OPERACIONAL';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3'),
    ('nombre', '3')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'AGRUPACION_OPERACIONAL' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo FORMATO_EXPORTACION_UBICACION — Formato de exportación de reportes (Distribución Financiera por Ubicación Geográfica)
-- Registros de nivel 1: 2   Campos: codigo, nombre
-- =====================================================================
-- [FORMATO_EXPORTACION_UBICACION] xlsx
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FORMATO_EXPORTACION_UBICACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'xlsx'),
    ('nombre', 'xlsx')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FORMATO_EXPORTACION_UBICACION' AND t.field_name = v.field_name;
-- [FORMATO_EXPORTACION_UBICACION] docx
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FORMATO_EXPORTACION_UBICACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'docx'),
    ('nombre', 'docx')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FORMATO_EXPORTACION_UBICACION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ESTADO_PROPUESTA_PAIP — Estados de la Propuesta PAIP
-- Registros de nivel 1: 5   Campos: codigo, nombre
-- =====================================================================
-- [ESTADO_PROPUESTA_PAIP] PAIP aceptado (URP)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROPUESTA_PAIP';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PAIP aceptado (URP)'),
    ('nombre', 'PAIP aceptado (URP)')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROPUESTA_PAIP' AND t.field_name = v.field_name;
-- [ESTADO_PROPUESTA_PAIP] En elaboración (URP)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROPUESTA_PAIP';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'En elaboración (URP)'),
    ('nombre', 'En elaboración (URP)')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROPUESTA_PAIP' AND t.field_name = v.field_name;
-- [ESTADO_PROPUESTA_PAIP] En revisión DGI
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROPUESTA_PAIP';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'En revisión DGI'),
    ('nombre', 'En revisión DGI')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROPUESTA_PAIP' AND t.field_name = v.field_name;
-- [ESTADO_PROPUESTA_PAIP] Aprobado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROPUESTA_PAIP';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Aprobado'),
    ('nombre', 'Aprobado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROPUESTA_PAIP' AND t.field_name = v.field_name;
-- [ESTADO_PROPUESTA_PAIP] Aprobado con ajustes
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_PROPUESTA_PAIP';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Aprobado con ajustes'),
    ('nombre', 'Aprobado con ajustes')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_PROPUESTA_PAIP' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ESTADO_COMPARACION_PROYECTO_PRO11 — Estados de Comparación de Proyecto (contrapropuesta institucional)
-- Registros de nivel 1: 4   Campos: codigo, nombre
-- =====================================================================
-- [ESTADO_COMPARACION_PROYECTO_PRO11] Nuevo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_COMPARACION_PROYECTO_PRO11';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Nuevo'),
    ('nombre', 'Nuevo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_COMPARACION_PROYECTO_PRO11' AND t.field_name = v.field_name;
-- [ESTADO_COMPARACION_PROYECTO_PRO11] Eliminado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_COMPARACION_PROYECTO_PRO11';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Eliminado'),
    ('nombre', 'Eliminado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_COMPARACION_PROYECTO_PRO11' AND t.field_name = v.field_name;
-- [ESTADO_COMPARACION_PROYECTO_PRO11] Modificado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_COMPARACION_PROYECTO_PRO11';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Modificado'),
    ('nombre', 'Modificado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_COMPARACION_PROYECTO_PRO11' AND t.field_name = v.field_name;
-- [ESTADO_COMPARACION_PROYECTO_PRO11] Sin cambio
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_COMPARACION_PROYECTO_PRO11';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Sin cambio'),
    ('nombre', 'Sin cambio')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_COMPARACION_PROYECTO_PRO11' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ESTADO_COMPARACION_PROYECTO_PRO09 — Estados de Comparación de Proyecto (propuesta de escenarios)
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [ESTADO_COMPARACION_PROYECTO_PRO09] Nuevo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_COMPARACION_PROYECTO_PRO09';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Nuevo'),
    ('nombre', 'Nuevo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_COMPARACION_PROYECTO_PRO09' AND t.field_name = v.field_name;
-- [ESTADO_COMPARACION_PROYECTO_PRO09] No propuesto
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_COMPARACION_PROYECTO_PRO09';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'No propuesto'),
    ('nombre', 'No propuesto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_COMPARACION_PROYECTO_PRO09' AND t.field_name = v.field_name;
-- [ESTADO_COMPARACION_PROYECTO_PRO09] Arrastre
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_COMPARACION_PROYECTO_PRO09';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Arrastre'),
    ('nombre', 'Arrastre')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_COMPARACION_PROYECTO_PRO09' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo INSTANCIA_APROBACION — Instancias de Aprobación
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [INSTANCIA_APROBACION] Director DGICP
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTANCIA_APROBACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Director DGICP'),
    ('nombre', 'Director DGICP')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTANCIA_APROBACION' AND t.field_name = v.field_name;
-- [INSTANCIA_APROBACION] Consejo de Ministros
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTANCIA_APROBACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Consejo de Ministros'),
    ('nombre', 'Consejo de Ministros')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTANCIA_APROBACION' AND t.field_name = v.field_name;
-- [INSTANCIA_APROBACION] Asamblea Legislativa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INSTANCIA_APROBACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Asamblea Legislativa'),
    ('nombre', 'Asamblea Legislativa')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'INSTANCIA_APROBACION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo DIMENSION_PRIORIZACION_ESCENARIOS — Dimensiones de Priorización (Generación de Escenarios)
-- Registros de nivel 1: 6   Campos: codigo, nombre
-- =====================================================================
-- [DIMENSION_PRIORIZACION_ESCENARIOS] 1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_PRIORIZACION_ESCENARIOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1'),
    ('nombre', 'Alineación estratégica')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_PRIORIZACION_ESCENARIOS' AND t.field_name = v.field_name;
-- [DIMENSION_PRIORIZACION_ESCENARIOS] 2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_PRIORIZACION_ESCENARIOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2'),
    ('nombre', 'Aspectos Sociales')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_PRIORIZACION_ESCENARIOS' AND t.field_name = v.field_name;
-- [DIMENSION_PRIORIZACION_ESCENARIOS] 3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_PRIORIZACION_ESCENARIOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3'),
    ('nombre', 'Rentabilidad social')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_PRIORIZACION_ESCENARIOS' AND t.field_name = v.field_name;
-- [DIMENSION_PRIORIZACION_ESCENARIOS] 4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_PRIORIZACION_ESCENARIOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4'),
    ('nombre', 'Aspectos Medioambientales')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_PRIORIZACION_ESCENARIOS' AND t.field_name = v.field_name;
-- [DIMENSION_PRIORIZACION_ESCENARIOS] 5
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_PRIORIZACION_ESCENARIOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '5'),
    ('nombre', 'Sostenibilidad fiscal')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_PRIORIZACION_ESCENARIOS' AND t.field_name = v.field_name;
-- [DIMENSION_PRIORIZACION_ESCENARIOS] 6
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_PRIORIZACION_ESCENARIOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '6'),
    ('nombre', 'Madurez del Proyecto')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'DIMENSION_PRIORIZACION_ESCENARIOS' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ATRIBUTO_OBLIGATORIO_ESCENARIOS — Atributos Obligatorios (Generación de Escenarios)
-- Registros de nivel 1: 2   Campos: codigo, nombre
-- =====================================================================
-- [ATRIBUTO_OBLIGATORIO_ESCENARIOS] Arrastre
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ATRIBUTO_OBLIGATORIO_ESCENARIOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Arrastre'),
    ('nombre', 'Arrastre')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ATRIBUTO_OBLIGATORIO_ESCENARIOS' AND t.field_name = v.field_name;
-- [ATRIBUTO_OBLIGATORIO_ESCENARIOS] Financiamiento asegurado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ATRIBUTO_OBLIGATORIO_ESCENARIOS';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Financiamiento asegurado'),
    ('nombre', 'Financiamiento asegurado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ATRIBUTO_OBLIGATORIO_ESCENARIOS' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ESTADO_ESCENARIO — Estados del Escenario
-- Registros de nivel 1: 3   Campos: codigo, nombre
-- =====================================================================
-- [ESTADO_ESCENARIO] En revisión
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_ESCENARIO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'En revisión'),
    ('nombre', 'En revisión')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_ESCENARIO' AND t.field_name = v.field_name;
-- [ESTADO_ESCENARIO] Revisado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_ESCENARIO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Revisado'),
    ('nombre', 'Revisado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_ESCENARIO' AND t.field_name = v.field_name;
-- [ESTADO_ESCENARIO] Aprobado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_ESCENARIO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Aprobado'),
    ('nombre', 'Aprobado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_ESCENARIO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo HISTORICO_AUTORIZACION_TECHO — Histórico de Autorizaciones (Consolidado de Techos)
-- Registros de nivel 1: 5   Campos: codigo, nombre
-- =====================================================================
-- [HISTORICO_AUTORIZACION_TECHO] Techo comunicado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'HISTORICO_AUTORIZACION_TECHO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Techo comunicado'),
    ('nombre', 'Techo comunicado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'HISTORICO_AUTORIZACION_TECHO' AND t.field_name = v.field_name;
-- [HISTORICO_AUTORIZACION_TECHO] Contrapropuesta institución
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'HISTORICO_AUTORIZACION_TECHO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Contrapropuesta institución'),
    ('nombre', 'Contrapropuesta institución')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'HISTORICO_AUTORIZACION_TECHO' AND t.field_name = v.field_name;
-- [HISTORICO_AUTORIZACION_TECHO] Techo aprobado y enviado a Consejo de Ministros
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'HISTORICO_AUTORIZACION_TECHO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Techo aprobado y enviado a Consejo de Ministros'),
    ('nombre', 'Techo aprobado y enviado a Consejo de Ministros')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'HISTORICO_AUTORIZACION_TECHO' AND t.field_name = v.field_name;
-- [HISTORICO_AUTORIZACION_TECHO] PAIP Autorizado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'HISTORICO_AUTORIZACION_TECHO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PAIP Autorizado'),
    ('nombre', 'PAIP Autorizado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'HISTORICO_AUTORIZACION_TECHO' AND t.field_name = v.field_name;
-- [HISTORICO_AUTORIZACION_TECHO] PAIP modificado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'HISTORICO_AUTORIZACION_TECHO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'PAIP modificado'),
    ('nombre', 'PAIP modificado')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'HISTORICO_AUTORIZACION_TECHO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ESTADO_SEMAFORO_AVANCE — Estados de Semáforo de Avance (Físico-Financiero)
-- Registros de nivel 1: 4   Campos: codigo, nombre, color, criterio
-- =====================================================================
-- [ESTADO_SEMAFORO_AVANCE] No iniciado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_SEMAFORO_AVANCE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'No iniciado'),
    ('nombre', 'No iniciado'),
    ('color', 'gris'),
    ('criterio', 'Ejecutado = 0 y Programado (Modificado) = 0.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_SEMAFORO_AVANCE' AND t.field_name = v.field_name;
-- [ESTADO_SEMAFORO_AVANCE] A Tiempo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_SEMAFORO_AVANCE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'A Tiempo'),
    ('nombre', 'A Tiempo'),
    ('color', 'amarillo'),
    ('criterio', 'Ejecutado igual al Programado (Modificado), o diferencia de hasta 5% menos.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_SEMAFORO_AVANCE' AND t.field_name = v.field_name;
-- [ESTADO_SEMAFORO_AVANCE] Adelantado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_SEMAFORO_AVANCE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Adelantado'),
    ('nombre', 'Adelantado'),
    ('color', 'verde'),
    ('criterio', 'Ejecutado superior al Programado (Modificado).')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_SEMAFORO_AVANCE' AND t.field_name = v.field_name;
-- [ESTADO_SEMAFORO_AVANCE] Atrasado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_SEMAFORO_AVANCE';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Atrasado'),
    ('nombre', 'Atrasado'),
    ('color', 'rojo'),
    ('criterio', 'Ejecutado inferior al Programado (Modificado) en más de un 5% (valor de dispersión configurable).')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_SEMAFORO_AVANCE' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo METODO_CONTRATACION — Método de Contratación
-- Registros de nivel 1: 15   Campos: codigo, nombre
-- =====================================================================
-- [METODO_CONTRATACION] Licitación Pública Internacional
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Licitación Pública Internacional'),
    ('nombre', 'Licitación Pública Internacional')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Licitación Competitiva
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Licitación Competitiva'),
    ('nombre', 'Licitación Competitiva')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Comparación de Precios
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Comparación de Precios'),
    ('nombre', 'Comparación de Precios')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Contratación Directa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Contratación Directa'),
    ('nombre', 'Contratación Directa')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Baja Cuantía
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Baja Cuantía'),
    ('nombre', 'Baja Cuantía')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Selección basada en calidad y costo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Selección basada en calidad y costo'),
    ('nombre', 'Selección basada en calidad y costo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Selección basada en calidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Selección basada en calidad'),
    ('nombre', 'Selección basada en calidad')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Selección basada en precio fijo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Selección basada en precio fijo'),
    ('nombre', 'Selección basada en precio fijo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Selección al menor costo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Selección al menor costo'),
    ('nombre', 'Selección al menor costo')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Selección basada en calificaciones de los consultores
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Selección basada en calificaciones de los consultores'),
    ('nombre', 'Selección basada en calificaciones de los consultores')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Selección de Fuente única
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Selección de Fuente única'),
    ('nombre', 'Selección de Fuente única')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Consultores individuales
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Consultores individuales'),
    ('nombre', 'Consultores individuales')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Compras en línea
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Compras en línea'),
    ('nombre', 'Compras en línea')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Catálogo electrónico
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Catálogo electrónico'),
    ('nombre', 'Catálogo electrónico')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;
-- [METODO_CONTRATACION] Subasta Electrónica inversa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'METODO_CONTRATACION';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Subasta Electrónica inversa'),
    ('nombre', 'Subasta Electrónica inversa')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'METODO_CONTRATACION' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo CATEGORIA_PROCESO_ADMINISTRATIVO — Categoría (Procesos Administrativos)
-- Registros de nivel 1: 13   Campos: codigo, nombre
-- =====================================================================
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Obra
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Obra'),
    ('nombre', 'Obra')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Supervisión
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Supervisión'),
    ('nombre', 'Supervisión')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Auditoría
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Auditoría'),
    ('nombre', 'Auditoría')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Equipo-Mobiliario
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Equipo-Mobiliario'),
    ('nombre', 'Equipo-Mobiliario')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Terrenos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Terrenos'),
    ('nombre', 'Terrenos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Materiales-insumos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Materiales-insumos'),
    ('nombre', 'Materiales-insumos')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Estudios
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Estudios'),
    ('nombre', 'Estudios')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Consultorías
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Consultorías'),
    ('nombre', 'Consultorías')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Capacitaciones
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Capacitaciones'),
    ('nombre', 'Capacitaciones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Becas
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Becas'),
    ('nombre', 'Becas')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Salarios
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Salarios'),
    ('nombre', 'Salarios')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Gastos de Funcionamiento
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Gastos de Funcionamiento'),
    ('nombre', 'Gastos de Funcionamiento')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;
-- [CATEGORIA_PROCESO_ADMINISTRATIVO] Otros
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Otros'),
    ('nombre', 'Otros')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'CATEGORIA_PROCESO_ADMINISTRATIVO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo PROCESO_ADMINISTRATIVO_HITO — Proceso Administrativo - Estado del Proceso (hitos)
-- Registros de nivel 1: 8   Campos: codigo, nombre, orden
-- =====================================================================
-- [PROCESO_ADMINISTRATIVO_HITO] 1
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROCESO_ADMINISTRATIVO_HITO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '1'),
    ('nombre', 'Elaboración de TDR'),
    ('orden', '1')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROCESO_ADMINISTRATIVO_HITO' AND t.field_name = v.field_name;
-- [PROCESO_ADMINISTRATIVO_HITO] 2
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROCESO_ADMINISTRATIVO_HITO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '2'),
    ('nombre', 'Elaboración de Bases de Licitación'),
    ('orden', '2')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROCESO_ADMINISTRATIVO_HITO' AND t.field_name = v.field_name;
-- [PROCESO_ADMINISTRATIVO_HITO] 3
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROCESO_ADMINISTRATIVO_HITO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '3'),
    ('nombre', 'Publicación'),
    ('orden', '3')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROCESO_ADMINISTRATIVO_HITO' AND t.field_name = v.field_name;
-- [PROCESO_ADMINISTRATIVO_HITO] 4
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROCESO_ADMINISTRATIVO_HITO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '4'),
    ('nombre', 'Elaboración de Ofertas'),
    ('orden', '4')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROCESO_ADMINISTRATIVO_HITO' AND t.field_name = v.field_name;
-- [PROCESO_ADMINISTRATIVO_HITO] 5
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROCESO_ADMINISTRATIVO_HITO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '5'),
    ('nombre', 'Evaluación de Ofertas'),
    ('orden', '5')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROCESO_ADMINISTRATIVO_HITO' AND t.field_name = v.field_name;
-- [PROCESO_ADMINISTRATIVO_HITO] 6
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROCESO_ADMINISTRATIVO_HITO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '6'),
    ('nombre', 'Adjudicación'),
    ('orden', '6')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROCESO_ADMINISTRATIVO_HITO' AND t.field_name = v.field_name;
-- [PROCESO_ADMINISTRATIVO_HITO] 7
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROCESO_ADMINISTRATIVO_HITO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '7'),
    ('nombre', 'Declaratoria de Desierto'),
    ('orden', '7')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROCESO_ADMINISTRATIVO_HITO' AND t.field_name = v.field_name;
-- [PROCESO_ADMINISTRATIVO_HITO] 8
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'PROCESO_ADMINISTRATIVO_HITO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', '8'),
    ('nombre', 'En Contratación'),
    ('orden', '8')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'PROCESO_ADMINISTRATIVO_HITO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo FUENTE_FINANCIAMIENTO_SEGUIMIENTO — Fuente de Financiamiento (Seguimiento Mensual de Proyectos - Estatus)
-- Registros de nivel 1: 4   Campos: codigo, nombre
-- =====================================================================
-- [FUENTE_FINANCIAMIENTO_SEGUIMIENTO] Fondo General
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_FINANCIAMIENTO_SEGUIMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Fondo General'),
    ('nombre', 'Fondo General')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_FINANCIAMIENTO_SEGUIMIENTO' AND t.field_name = v.field_name;
-- [FUENTE_FINANCIAMIENTO_SEGUIMIENTO] Préstamo(s) Externo(s)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_FINANCIAMIENTO_SEGUIMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Préstamo(s) Externo(s)'),
    ('nombre', 'Préstamo(s) Externo(s)')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_FINANCIAMIENTO_SEGUIMIENTO' AND t.field_name = v.field_name;
-- [FUENTE_FINANCIAMIENTO_SEGUIMIENTO] Donaciones
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_FINANCIAMIENTO_SEGUIMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Donaciones'),
    ('nombre', 'Donaciones')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_FINANCIAMIENTO_SEGUIMIENTO' AND t.field_name = v.field_name;
-- [FUENTE_FINANCIAMIENTO_SEGUIMIENTO] Otros
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'FUENTE_FINANCIAMIENTO_SEGUIMIENTO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Otros'),
    ('nombre', 'Otros')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'FUENTE_FINANCIAMIENTO_SEGUIMIENTO' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo ESTADO_CONDICION_PREVIA — Estado (Condición Previa del Convenio)
-- Registros de nivel 1: 2   Campos: codigo, nombre, descripcion
-- =====================================================================
-- [ESTADO_CONDICION_PREVIA] Pendiente
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_CONDICION_PREVIA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Pendiente'),
    ('nombre', 'Pendiente'),
    ('descripcion', 'Estado inicial por defecto. Indica que los requisitos de la condición previa del Anexo 5 aún no se han satisfecho o están en proceso de revisión.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_CONDICION_PREVIA' AND t.field_name = v.field_name;
-- [ESTADO_CONDICION_PREVIA] Cumplida
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'ESTADO_CONDICION_PREVIA';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Cumplida'),
    ('nombre', 'Cumplida'),
    ('descripcion', 'Indica que la condición ha sido validada y finalizada satisfactoriamente.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'ESTADO_CONDICION_PREVIA' AND t.field_name = v.field_name;

-- =====================================================================
-- Catálogo TIPO_AJUSTE_CONVENIO — Tipo de Ajuste (Modificaciones al Convenio)
-- Registros de nivel 1: 3   Campos: codigo, nombre, descripcion
-- =====================================================================
-- [TIPO_AJUSTE_CONVENIO] NA
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_AJUSTE_CONVENIO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'NA'),
    ('nombre', 'NA'),
    ('descripcion', 'Caso en que se ingresan las partes de los convenios BIRF como componentes.')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_AJUSTE_CONVENIO' AND t.field_name = v.field_name;
-- [TIPO_AJUSTE_CONVENIO] Adenda
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_AJUSTE_CONVENIO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Adenda'),
    ('nombre', 'Adenda')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_AJUSTE_CONVENIO' AND t.field_name = v.field_name;
-- [TIPO_AJUSTE_CONVENIO] Reestructuración
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_AJUSTE_CONVENIO';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = t.catalog_id), t.field_id, v.valor
FROM (VALUES
    ('codigo', 'Reestructuración'),
    ('nombre', 'Reestructuración')
) AS v (field_name, valor)
JOIN tmp_catalog_field t ON t.catalog_code = 'TIPO_AJUSTE_CONVENIO' AND t.field_name = v.field_name;

-- ---------------------------------------------------------------------
-- 4) Verificación posterior y limpieza
-- ---------------------------------------------------------------------
-- Registros y valores cargados por catálogo
SELECT c.code,
       COUNT(DISTINCT r.id) AS registros,
       COUNT(v.id)          AS valores
FROM catalog c
JOIN catalog_record r            ON r.catalog_id = c.id
LEFT JOIN catalog_record_value v ON v.record_id = r.id
GROUP BY c.code
ORDER BY c.code;

DROP TABLE tmp_campo_requerido;
DROP TABLE tmp_catalog_field;

COMMIT;
