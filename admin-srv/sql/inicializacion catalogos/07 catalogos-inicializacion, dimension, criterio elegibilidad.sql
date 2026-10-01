-- =====================================================================
-- Inicialización de catalog_record y catalog_record_value
-- Jerarquía DIMENSION_ELEGIBILIDAD > CRITERIO_ELEGIBILIDAD
-- Origen : catalogos__inicializacion.json, grupo code = 'CRITERIO_ELEGIBILIDAD'
--          22 items: 6 dimensiones (nivel 1), 16 criterios (nivel 2)
-- Dialecto: SQL ANSI (SQL:2003)
-- ---------------------------------------------------------------------
-- Prerrequisitos
--   * catalog contiene los code 'DIMENSION_ELEGIBILIDAD', 'CRITERIO_ELEGIBILIDAD'
--     (CRITERIO_ELEGIBILIDAD.parent_id -> DIMENSION_ELEGIBILIDAD; Regla 23).
--   * catalog_field define name = 'codigo', 'nombre' y 'ponderacion' en ambos,
--     y además name = 'pregunta' en CRITERIO_ELEGIBILIDAD.
--
-- Mecánica ("id más recientemente generado")
--   SQL ANSI no tiene una función portable para leer el último id generado.
--   El script la sustituye con subconsultas escalares:
--     * registro recién creado de un catálogo X:
--         (SELECT MAX(r.id) FROM catalog_record r
--           WHERE r.catalog_id = <id de X> AND r.id > <MAX previo a la carga>)
--   Como cada item se inserta con su propia sentencia y en el orden del JSON:
--     * el padre de un CRITERIO es la última DIMENSION insertada;
--     * los valores de cada item se asignan al registro recién creado:
--       DIMENSION: 'codigo', 'nombre', 'ponderacion' (atributos_extra.ponderacion_dimension)
--       CRITERIO : 'codigo', 'nombre', 'pregunta' (atributos_extra.pregunta_guia),
--                  'ponderacion' (atributos_extra.ponderacion_criterio)
--       Las ponderaciones son numéricas en el JSON y se guardan como texto ('10').
--   Condición: una sola sesión, sin inserciones concurrentes en
--   catalog_record. Se recomienda ejecutarlo dentro de una única transacción.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Paso 1. Tablas temporales
--   tmp_el_control : MAX(id) de catalog_record antes de la carga.
--   tmp_el_item    : copia de los items del JSON, usada solo para
--                   verificar la jerarquía al final (paso 5).
-- ---------------------------------------------------------------------
CREATE LOCAL TEMPORARY TABLE tmp_el_control (
    max_record_id_previo BIGINT NOT NULL
) ON COMMIT PRESERVE ROWS;

CREATE LOCAL TEMPORARY TABLE tmp_el_item (
    orden          INTEGER       NOT NULL PRIMARY KEY,
    nivel          INTEGER       NOT NULL,
    codigo         VARCHAR(4000) NOT NULL,
    nombre         VARCHAR(4000) NOT NULL,
    padre_codigo   VARCHAR(4000)
) ON COMMIT PRESERVE ROWS;

INSERT INTO tmp_el_item (orden, nivel, codigo, nombre, padre_codigo) VALUES
    (1, 1, 'DIM1', '1. Alineación estratégica', NULL),
    (2, 2, 'DIM1::C1', 'Contribución a metas Objetivos de Desarrollo Sostenible', 'DIM1'),
    (3, 2, 'DIM1::C2', 'Coherencia con Plan de Gobierno', 'DIM1'),
    (4, 2, 'DIM1::C3', 'Contribución a Planes Regionales', 'DIM1'),
    (5, 2, 'DIM1::C4', 'Contribución a Planes Sectoriales o Institucionales', 'DIM1'),
    (6, 1, 'DIM2', '2. Aspectos Sociales', NULL),
    (7, 2, 'DIM2::C1', 'Beneficia a grupos poblacionales en situación de vulnerabilidad: en riesgo ambiental, discapacitados, tercera edad, mujeres cabeza de hogar, primera infancia, otros.', 'DIM2'),
    (8, 2, 'DIM2::C2', 'Contribución a mejorar la calidad de vida (mayor disponibilidad y calidad de servicios, generación de empleo productivo, seguridad)', 'DIM2'),
    (9, 1, 'DIM3', '3. Rentabilidad social', NULL),
    (10, 2, 'DIM3::C1', 'Indicadores de evaluación (VAN, TIR, B/C, CAE, VAC) indican la rentabilidad social', 'DIM3'),
    (11, 1, 'DIM4', '4. Aspectos Medioambientales', NULL),
    (12, 2, 'DIM4::C1', 'Contribución a conservar, restablecer o mejorar el medio ambiente (reforestación, preservación, conservación de especies, energías verdes, mejoramiento de recursos naturales, plantas de tratamiento de agua, entre otros).', 'DIM4'),
    (13, 2, 'DIM4::C2', 'Apoya el cumplimiento de metas de Gestión de Riesgo de Desastres (GRD).', 'DIM4'),
    (14, 2, 'DIM4::C3', 'Contribución a la adaptación (prevención) o mitigación (reacción ante daños), en materia de Adaptación al Cambio Climático (ACC).', 'DIM4'),
    (15, 1, 'DIM5', '5. Sostenibilidad fiscal', NULL),
    (16, 2, 'DIM5::C1', 'Cuenta con financiamiento autorizado', 'DIM5'),
    (17, 2, 'DIM5::C2', 'Se encuentra en gestión de financiamiento', 'DIM5'),
    (18, 2, 'DIM5::C3', 'Cuenta con apoyo de las autoridades Institucionales para asignar recursos, dentro del techo de funcionamiento, para su operación', 'DIM5'),
    (19, 1, 'DIM6', '6. Madurez del Proyecto', NULL),
    (20, 2, 'DIM6::C1', 'Proyecto en ejecución (arrastre)', 'DIM6'),
    (21, 2, 'DIM6::C2', 'Cuenta con estudios de preinversión', 'DIM6'),
    (22, 2, 'DIM6::C3', 'Capacidad institución ejecutora', 'DIM6');

-- ---------------------------------------------------------------------
-- Paso 2. Verificación previa — DEBE DEVOLVER 0 FILAS antes de continuar
-- ---------------------------------------------------------------------
SELECT x.code, x.problema
FROM (
    SELECT e.code,
           CASE WHEN c.id  IS NULL THEN 'catálogo inexistente'
                WHEN fc.id IS NULL THEN 'falta campo codigo'
                WHEN fn.id IS NULL THEN 'falta campo nombre'
                WHEN fp.id IS NULL THEN 'falta campo ponderacion'
                WHEN e.code = 'CRITERIO_ELEGIBILIDAD' AND fu.id IS NULL THEN 'falta campo pregunta'
                WHEN e.code = 'CRITERIO_ELEGIBILIDAD'
                 AND (c.parent_id IS NULL OR c.parent_id <> (SELECT id FROM catalog WHERE code = 'DIMENSION_ELEGIBILIDAD'))
                     THEN 'parent_id no apunta a DIMENSION_ELEGIBILIDAD (Regla 23)'
           END AS problema
    FROM (VALUES ('DIMENSION_ELEGIBILIDAD'), ('CRITERIO_ELEGIBILIDAD')) AS e (code)
    LEFT JOIN catalog c        ON c.code = e.code
    LEFT JOIN catalog_field fc ON fc.catalog_id = c.id AND fc.name = 'codigo'
    LEFT JOIN catalog_field fn ON fn.catalog_id = c.id AND fn.name = 'nombre'
    LEFT JOIN catalog_field fu ON fu.catalog_id = c.id AND fu.name = 'pregunta'
    LEFT JOIN catalog_field fp ON fp.catalog_id = c.id AND fp.name = 'ponderacion'
) x
WHERE x.problema IS NOT NULL;

-- ---------------------------------------------------------------------
-- Paso 3. Punto de partida de la identidad
-- ---------------------------------------------------------------------
INSERT INTO tmp_el_control (max_record_id_previo)
SELECT COALESCE(MAX(id), 0) FROM catalog_record;

-- ---------------------------------------------------------------------
-- Paso 4. Carga jerárquica, item por item, en el orden del JSON
-- ---------------------------------------------------------------------

-- =============================== DIMENSION_ELEGIBILIDAD: 1. Alineación estratégica
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM1' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '1. Alineación estratégica' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '10' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Contribución a metas Objetivos de Desarrollo Sostenible
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM1::C1' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Contribución a metas Objetivos de Desarrollo Sostenible' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿A cuáles ODS contribuye?' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '2' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Coherencia con Plan de Gobierno
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM1::C2' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Coherencia con Plan de Gobierno' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿A cuál eje o pilar del Plan de Gobierno contribuye?' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '5' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Contribución a Planes Regionales
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM1::C3' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Contribución a Planes Regionales' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿A cuál Plan Regional contribuye? Especifique' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '1' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Contribución a Planes Sectoriales o Institucionales
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM1::C4' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Contribución a Planes Sectoriales o Institucionales' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿A cuál Plan Institucional o Sectorial contribuye? Especifique' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '2' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';

-- =============================== DIMENSION_ELEGIBILIDAD: 2. Aspectos Sociales
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM2' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '2. Aspectos Sociales' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '20' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Beneficia a grupos poblacionales en situación de vulnerabilidad: en riesgo ambiental, discapacitados, tercera edad, mujeres cabeza de hogar, primera infancia, otros.
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM2::C1' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Beneficia a grupos poblacionales en situación de vulnerabilidad: en riesgo ambiental, discapacitados, tercera edad, mujeres cabeza de hogar, primera infancia, otros.' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿A cuáles grupos en situación de vulnerabilidad contribuye?' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '10' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Contribución a mejorar la calidad de vida (mayor disponibilidad y calidad de servicios, generación de empleo productivo, seguridad)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM2::C2' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Contribución a mejorar la calidad de vida (mayor disponibilidad y calidad de servicios, generación de empleo productivo, seguridad)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿Cómo el proyecto contribuye a mejorar la calidad de vida?' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '10' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';

-- =============================== DIMENSION_ELEGIBILIDAD: 3. Rentabilidad social
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM3' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '3. Rentabilidad social' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '20' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Indicadores de evaluación (VAN, TIR, B/C, CAE, VAC) indican la rentabilidad social
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM3::C1' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Indicadores de evaluación (VAN, TIR, B/C, CAE, VAC) indican la rentabilidad social' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿El proyecto demuestra rentabilidad social? (Sí - No)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '20' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';

-- =============================== DIMENSION_ELEGIBILIDAD: 4. Aspectos Medioambientales
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM4' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '4. Aspectos Medioambientales' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '15' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Contribución a conservar, restablecer o mejorar el medio ambiente (reforestación, preservación, conservación de especies, energías verdes, mejoramiento de recursos naturales, plantas de tratamiento de agua, entre otros).
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM4::C1' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Contribución a conservar, restablecer o mejorar el medio ambiente (reforestación, preservación, conservación de especies, energías verdes, mejoramiento de recursos naturales, plantas de tratamiento de agua, entre otros).' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿A cuáles componentes del Medio Ambiente contribuye?' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '5' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Apoya el cumplimiento de metas de Gestión de Riesgo de Desastres (GRD).
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM4::C2' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Apoya el cumplimiento de metas de Gestión de Riesgo de Desastres (GRD).' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿Qué tipo de medidas para GRD incluye el proyecto?' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '5' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Contribución a la adaptación (prevención) o mitigación (reacción ante daños), en materia de Adaptación al Cambio Climático (ACC).
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM4::C3' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Contribución a la adaptación (prevención) o mitigación (reacción ante daños), en materia de Adaptación al Cambio Climático (ACC).' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿El proyecto incluye medidas de adaptación o de mitigación al Cambio Climático?' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '5' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';

-- =============================== DIMENSION_ELEGIBILIDAD: 5. Sostenibilidad fiscal
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM5' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '5. Sostenibilidad fiscal' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '15' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Cuenta con financiamiento autorizado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM5::C1' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Cuenta con financiamiento autorizado' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿Cuenta con financiamiento autorizado? (Sí - No)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '6' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Se encuentra en gestión de financiamiento
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM5::C2' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Se encuentra en gestión de financiamiento' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿Se encuentra en gestión el financiamiento? (Sí - No)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '3' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Cuenta con apoyo de las autoridades Institucionales para asignar recursos, dentro del techo de funcionamiento, para su operación
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM5::C3' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Cuenta con apoyo de las autoridades Institucionales para asignar recursos, dentro del techo de funcionamiento, para su operación' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿Cuenta con apoyo de las autoridades Institucionales para asignar recursos para su operación y mantenimiento? (Sí - No)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '6' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';

-- =============================== DIMENSION_ELEGIBILIDAD: 6. Madurez del Proyecto
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM6' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '6. Madurez del Proyecto' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '20' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Proyecto en ejecución (arrastre)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM6::C1' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Proyecto en ejecución (arrastre)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿Es proyecto de arrastre? (Sí - No)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '10' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Cuenta con estudios de preinversión
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM6::C2' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Cuenta con estudios de preinversión' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿Cuenta con estudios de preinversión para ejecutar? (Sí - No)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '5' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';
-- CRITERIO_ELEGIBILIDAD: Capacidad institución ejecutora
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'DIMENSION_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'DIM6::C3' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, 'Capacidad institución ejecutora' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '¿La institución ejecutora demuestra buena capacidad de ejecución histórica? (Sí - No)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'pregunta';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)), f.id, '5' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CRITERIO_ELEGIBILIDAD' AND f.name = 'ponderacion';

-- ---------------------------------------------------------------------
-- Paso 5. Verificación posterior
-- ---------------------------------------------------------------------
-- 5.1 Conteos. Esperado: DIMENSION_ELEGIBILIDAD 6-18, CRITERIO_ELEGIBILIDAD 16-64
SELECT c.code,
       COUNT(DISTINCT r.id) AS registros,
       COUNT(v.id)          AS valores
FROM catalog c
JOIN catalog_record r            ON r.catalog_id = c.id
LEFT JOIN catalog_record_value v ON v.record_id = r.id
WHERE c.code IN ('DIMENSION_ELEGIBILIDAD', 'CRITERIO_ELEGIBILIDAD')
  AND r.id > (SELECT max_record_id_previo FROM tmp_el_control)
GROUP BY c.code;

-- 5.2 Jerarquía: el 'codigo' del registro padre debe coincidir con el
--     item_padre_codigo del JSON. DEBE DEVOLVER 0 FILAS.
SELECT t.orden, t.codigo, t.padre_codigo AS padre_esperado, vp.valor AS padre_cargado
FROM tmp_el_item t
JOIN catalog_record_value vh ON vh.valor = t.codigo
JOIN catalog_field fh        ON fh.id = vh.field_id AND fh.name = 'codigo'
JOIN catalog ch              ON ch.id = fh.catalog_id
                            AND ch.code IN ('DIMENSION_ELEGIBILIDAD', 'CRITERIO_ELEGIBILIDAD')
JOIN catalog_record rh       ON rh.id = vh.record_id
                            AND rh.id > (SELECT max_record_id_previo FROM tmp_el_control)
LEFT JOIN catalog_record_value vp ON vp.record_id = rh.parent_record_id
LEFT JOIN catalog_field fp        ON fp.id = vp.field_id
WHERE (fp.name = 'codigo' OR fp.id IS NULL)
  AND ( (t.padre_codigo IS NULL AND rh.parent_record_id IS NOT NULL)
     OR (t.padre_codigo IS NOT NULL AND (vp.valor IS NULL OR vp.valor <> t.padre_codigo)) );

-- ---------------------------------------------------------------------
-- Paso 6. Limpieza
-- ---------------------------------------------------------------------
DROP TABLE tmp_el_item;
DROP TABLE tmp_el_control;
