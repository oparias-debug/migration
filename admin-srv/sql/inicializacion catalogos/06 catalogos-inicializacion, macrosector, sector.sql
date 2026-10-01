-- =====================================================================
-- Inicialización de catalog_record y catalog_record_value
-- Jerarquía MACROSECTOR > SECTOR
-- Origen : catalogos__inicializacion.json, grupo code = 'SECTOR_MACROSECTOR'
--          20 items: 3 macrosectores (nivel 1), 17 sectores (nivel 2)
-- Dialecto: SQL ANSI (SQL:2003)
-- ---------------------------------------------------------------------
-- Prerrequisitos
--   * catalog contiene los code 'MACROSECTOR' y 'SECTOR'
--     (SECTOR.parent_id -> MACROSECTOR, Regla 23).
--   * catalog_field define name = 'codigo' y name = 'nombre' en cada uno.
--
-- Mecánica ("id más recientemente generado")
--   SQL ANSI no tiene una función portable para leer el último id generado.
--   El script la sustituye con subconsultas escalares:
--     * registro recién creado de un catálogo X:
--         (SELECT MAX(r.id) FROM catalog_record r
--           WHERE r.catalog_id = <id de X> AND r.id > <MAX previo a la carga>)
--   Como cada item se inserta con su propia sentencia y en el orden del JSON:
--     * el padre de un SECTOR es el último MACROSECTOR insertado;
--     * los dos valores de cada item se asignan al registro recién creado.
--   Condición: una sola sesión, sin inserciones concurrentes en
--   catalog_record. Se recomienda ejecutarlo dentro de una única transacción.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Paso 1. Tablas temporales
--   tmp_sec_control : MAX(id) de catalog_record antes de la carga.
--   tmp_sec_item    : copia de los items del JSON, usada solo para
--                     verificar la jerarquía al final (paso 5).
-- ---------------------------------------------------------------------
CREATE LOCAL TEMPORARY TABLE tmp_sec_control (
    max_record_id_previo BIGINT NOT NULL
) ON COMMIT PRESERVE ROWS;

CREATE LOCAL TEMPORARY TABLE tmp_sec_item (
    orden          INTEGER       NOT NULL PRIMARY KEY,
    nivel          INTEGER       NOT NULL,
    codigo         VARCHAR(4000) NOT NULL,
    nombre         VARCHAR(4000) NOT NULL,
    padre_codigo   VARCHAR(4000)
) ON COMMIT PRESERVE ROWS;

INSERT INTO tmp_sec_item (orden, nivel, codigo, nombre, padre_codigo) VALUES
    (1, 1, 'Desarrollo Social', 'Desarrollo Social', NULL),
    (2, 2, 'Desarrollo Social::Previsión social', 'Previsión social', 'Desarrollo Social'),
    (3, 2, 'Desarrollo Social::Deporte y recreación', 'Deporte y recreación', 'Desarrollo Social'),
    (4, 2, 'Desarrollo Social::Vivienda', 'Vivienda', 'Desarrollo Social'),
    (5, 2, 'Desarrollo Social::Medio ambiente', 'Medio ambiente', 'Desarrollo Social'),
    (6, 2, 'Desarrollo Social::Asistencia social', 'Asistencia social', 'Desarrollo Social'),
    (7, 2, 'Desarrollo Social::Agua potable y alcantarillado', 'Agua potable y alcantarillado', 'Desarrollo Social'),
    (8, 2, 'Desarrollo Social::Multisectorial', 'Multisectorial', 'Desarrollo Social'),
    (9, 2, 'Desarrollo Social::Salud', 'Salud', 'Desarrollo Social'),
    (10, 2, 'Desarrollo Social::Desarrollo urbano y comunal', 'Desarrollo urbano y comunal', 'Desarrollo Social'),
    (11, 2, 'Desarrollo Social::Educación y cultura', 'Educación y cultura', 'Desarrollo Social'),
    (12, 1, 'Desarrollo Económico', 'Desarrollo Económico', NULL),
    (13, 2, 'Desarrollo Económico::Energía', 'Energía', 'Desarrollo Económico'),
    (14, 2, 'Desarrollo Económico::Industria/comercio y turismo', 'Industria/comercio y turismo', 'Desarrollo Económico'),
    (15, 2, 'Desarrollo Económico::Silvoagropecuario', 'Silvoagropecuario', 'Desarrollo Económico'),
    (16, 2, 'Desarrollo Económico::Comunicación', 'Comunicación', 'Desarrollo Económico'),
    (17, 2, 'Desarrollo Económico::Transporte y almacenaje', 'Transporte y almacenaje', 'Desarrollo Económico'),
    (18, 1, 'Seguridad Pública y Justicia', 'Seguridad Pública y Justicia', NULL),
    (19, 2, 'Seguridad Pública y Justicia::Seguridad', 'Seguridad', 'Seguridad Pública y Justicia'),
    (20, 2, 'Seguridad Pública y Justicia::Justicia', 'Justicia', 'Seguridad Pública y Justicia');

-- ---------------------------------------------------------------------
-- Paso 2. Verificación previa — DEBE DEVOLVER 0 FILAS antes de continuar
-- ---------------------------------------------------------------------
SELECT x.code, x.problema
FROM (
    SELECT e.code,
           CASE WHEN c.id  IS NULL THEN 'catálogo inexistente'
                WHEN fc.id IS NULL THEN 'falta campo codigo'
                WHEN fn.id IS NULL THEN 'falta campo nombre'
                WHEN e.code = 'SECTOR'
                 AND (c.parent_id IS NULL OR c.parent_id <> (SELECT id FROM catalog WHERE code = 'MACROSECTOR'))
                     THEN 'parent_id no apunta a MACROSECTOR (Regla 23)'
           END AS problema
    FROM (VALUES ('MACROSECTOR'), ('SECTOR')) AS e (code)
    LEFT JOIN catalog c        ON c.code = e.code
    LEFT JOIN catalog_field fc ON fc.catalog_id = c.id AND fc.name = 'codigo'
    LEFT JOIN catalog_field fn ON fn.catalog_id = c.id AND fn.name = 'nombre'
) x
WHERE x.problema IS NOT NULL;

-- ---------------------------------------------------------------------
-- Paso 3. Punto de partida de la identidad
-- ---------------------------------------------------------------------
INSERT INTO tmp_sec_control (max_record_id_previo)
SELECT COALESCE(MAX(id), 0) FROM catalog_record;

-- ---------------------------------------------------------------------
-- Paso 4. Carga jerárquica, item por item, en el orden del JSON
-- ---------------------------------------------------------------------

-- =============================== MACROSECTOR: Desarrollo Social
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MACROSECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'MACROSECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'MACROSECTOR' AND f.name = 'nombre';
-- SECTOR: Previsión social
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social::Previsión social' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Previsión social' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Deporte y recreación
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social::Deporte y recreación' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Deporte y recreación' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Vivienda
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social::Vivienda' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Vivienda' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Medio ambiente
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social::Medio ambiente' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Medio ambiente' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Asistencia social
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social::Asistencia social' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Asistencia social' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Agua potable y alcantarillado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social::Agua potable y alcantarillado' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Agua potable y alcantarillado' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Multisectorial
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social::Multisectorial' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Multisectorial' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Salud
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social::Salud' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Salud' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Desarrollo urbano y comunal
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social::Desarrollo urbano y comunal' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo urbano y comunal' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Educación y cultura
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Social::Educación y cultura' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Educación y cultura' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';

-- =============================== MACROSECTOR: Desarrollo Económico
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MACROSECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Económico' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'MACROSECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Económico' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'MACROSECTOR' AND f.name = 'nombre';
-- SECTOR: Energía
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Económico::Energía' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Energía' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Industria/comercio y turismo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Económico::Industria/comercio y turismo' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Industria/comercio y turismo' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Silvoagropecuario
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Económico::Silvoagropecuario' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Silvoagropecuario' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Comunicación
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Económico::Comunicación' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Comunicación' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Transporte y almacenaje
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Desarrollo Económico::Transporte y almacenaje' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Transporte y almacenaje' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';

-- =============================== MACROSECTOR: Seguridad Pública y Justicia
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'MACROSECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Seguridad Pública y Justicia' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'MACROSECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Seguridad Pública y Justicia' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'MACROSECTOR' AND f.name = 'nombre';
-- SECTOR: Seguridad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Seguridad Pública y Justicia::Seguridad' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Seguridad' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';
-- SECTOR: Justicia
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'MACROSECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'SECTOR';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Seguridad Pública y Justicia::Justicia' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'SECTOR' AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)), f.id, 'Justicia' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'SECTOR' AND f.name = 'nombre';

-- ---------------------------------------------------------------------
-- Paso 5. Verificación posterior
-- ---------------------------------------------------------------------
-- 5.1 Conteos. Esperado: MACROSECTOR 3/6, SECTOR 17/34
SELECT c.code,
       COUNT(DISTINCT r.id) AS registros,
       COUNT(v.id)          AS valores
FROM catalog c
JOIN catalog_record r            ON r.catalog_id = c.id
LEFT JOIN catalog_record_value v ON v.record_id = r.id
WHERE c.code IN ('MACROSECTOR', 'SECTOR')
  AND r.id > (SELECT max_record_id_previo FROM tmp_sec_control)
GROUP BY c.code;

-- 5.2 Jerarquía: el 'codigo' del registro padre debe coincidir con el
--     item_padre_codigo del JSON. DEBE DEVOLVER 0 FILAS.
SELECT t.orden, t.codigo, t.padre_codigo AS padre_esperado, vp.valor AS padre_cargado
FROM tmp_sec_item t
JOIN catalog_record_value vh ON vh.valor = t.codigo
JOIN catalog_field fh        ON fh.id = vh.field_id AND fh.name = 'codigo'
JOIN catalog ch              ON ch.id = fh.catalog_id
                            AND ch.code IN ('MACROSECTOR', 'SECTOR')
JOIN catalog_record rh       ON rh.id = vh.record_id
                            AND rh.id > (SELECT max_record_id_previo FROM tmp_sec_control)
LEFT JOIN catalog_record_value vp ON vp.record_id = rh.parent_record_id
LEFT JOIN catalog_field fp        ON fp.id = vp.field_id
WHERE (fp.name = 'codigo' OR fp.id IS NULL)
  AND ( (t.padre_codigo IS NULL AND rh.parent_record_id IS NOT NULL)
     OR (t.padre_codigo IS NOT NULL AND (vp.valor IS NULL OR vp.valor <> t.padre_codigo)) );

-- ---------------------------------------------------------------------
-- Paso 6. Limpieza
-- ---------------------------------------------------------------------
DROP TABLE tmp_sec_item;
DROP TABLE tmp_sec_control;
