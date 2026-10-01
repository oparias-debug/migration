-- =====================================================================
-- Inicialización de catalog_record y catalog_record_value
-- Jerarquía de catálogos: TIPO_EJECUTORA -> UNIDAD_EJECUTORA
-- Fuente : catalogos__inicializacion.json, grupo code = "UNIDAD_EJECUTORA"
-- Ítems  : 145 de nivel 1 (119 ACTIVE, 26 INACTIVE)
-- Valores: 4 por registro (codigo, nombre, sigla, codigo_institucion) = 580
-- Dialecto: SQL ANSI (SQL:2003). Codificación: UTF-8.
--
-- Prerrequisitos:
--   * Existen en catalog los códigos 'UNIDAD_EJECUTORA' y 'TIPO_EJECUTORA'.
--   * UNIDAD_EJECUTORA tiene en catalog_field los campos name = 'codigo',
--     'nombre', 'sigla' y 'codigo_institucion'; TIPO_EJECUTORA tiene 'codigo'.
--   * Los registros de TIPO_EJECUTORA ya están cargados, con los códigos
--     1, 2, 3, 5 (los que referencian los ítems).
--   Si falta alguno, el script falla al poblar las tablas temporales (NOT NULL).
--
-- parent_record_id: es el catalog_record de TIPO_EJECUTORA cuyo valor del
--   campo 'codigo' es igual al atributo_extra "codigo_tipo_ejecutora".
--
-- Id generado: SQL ANSI no ofrece una función portable para obtener el
--   último valor IDENTITY; se usa MAX(id) de catalog_record filtrado por
--   catalog_id, justo después de cada INSERT. Ejecutar en una sola sesión
--   sin cargas concurrentes sobre catalog_record.
--
-- Todos los ítems tienen "codigo_tipo_ejecutora": ningún registro queda sin padre.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Tablas temporales
-- ---------------------------------------------------------------------
CREATE GLOBAL TEMPORARY TABLE tmp_catalog_ref (
    catalog_code              VARCHAR(64) NOT NULL PRIMARY KEY,
    catalog_id                BIGINT      NOT NULL,
    field_codigo_id           BIGINT      NOT NULL,
    field_nombre_id           BIGINT      NOT NULL,
    field_sigla_id            BIGINT      NOT NULL,
    field_codigo_inst_id      BIGINT      NOT NULL
) ON COMMIT PRESERVE ROWS;

CREATE GLOBAL TEMPORARY TABLE tmp_tipo_ejecutora_ref (
    codigo                    VARCHAR(64) NOT NULL PRIMARY KEY,
    record_id                 BIGINT      NOT NULL
) ON COMMIT PRESERVE ROWS;

CREATE GLOBAL TEMPORARY TABLE tmp_record_map (
    codigo                    VARCHAR(64) NOT NULL PRIMARY KEY,
    record_id                 BIGINT      NOT NULL
) ON COMMIT PRESERVE ROWS;

INSERT INTO tmp_catalog_ref (catalog_code, catalog_id, field_codigo_id, field_nombre_id,
                             field_sigla_id, field_codigo_inst_id)
VALUES (
    'UNIDAD_EJECUTORA',
    (SELECT c.id FROM catalog c WHERE c.code = 'UNIDAD_EJECUTORA'),
    (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id
      WHERE c.code = 'UNIDAD_EJECUTORA' AND f.name = 'codigo'),
    (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id
      WHERE c.code = 'UNIDAD_EJECUTORA' AND f.name = 'nombre'),
    (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id
      WHERE c.code = 'UNIDAD_EJECUTORA' AND f.name = 'sigla'),
    (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id
      WHERE c.code = 'UNIDAD_EJECUTORA' AND f.name = 'codigo_institucion')
);

-- Registros padre de TIPO_EJECUTORA, resueltos por su valor 'codigo'

INSERT INTO tmp_tipo_ejecutora_ref (codigo, record_id)
VALUES ('1', (SELECT cr.id
                  FROM catalog_record cr
                  JOIN catalog c              ON c.id = cr.catalog_id AND c.code = 'TIPO_EJECUTORA'
                  JOIN catalog_field f        ON f.catalog_id = c.id AND f.name = 'codigo'
                  JOIN catalog_record_value v ON v.record_id = cr.id AND v.field_id = f.id
                 WHERE v.valor = '1'));

INSERT INTO tmp_tipo_ejecutora_ref (codigo, record_id)
VALUES ('2', (SELECT cr.id
                  FROM catalog_record cr
                  JOIN catalog c              ON c.id = cr.catalog_id AND c.code = 'TIPO_EJECUTORA'
                  JOIN catalog_field f        ON f.catalog_id = c.id AND f.name = 'codigo'
                  JOIN catalog_record_value v ON v.record_id = cr.id AND v.field_id = f.id
                 WHERE v.valor = '2'));

INSERT INTO tmp_tipo_ejecutora_ref (codigo, record_id)
VALUES ('3', (SELECT cr.id
                  FROM catalog_record cr
                  JOIN catalog c              ON c.id = cr.catalog_id AND c.code = 'TIPO_EJECUTORA'
                  JOIN catalog_field f        ON f.catalog_id = c.id AND f.name = 'codigo'
                  JOIN catalog_record_value v ON v.record_id = cr.id AND v.field_id = f.id
                 WHERE v.valor = '3'));

INSERT INTO tmp_tipo_ejecutora_ref (codigo, record_id)
VALUES ('5', (SELECT cr.id
                  FROM catalog_record cr
                  JOIN catalog c              ON c.id = cr.catalog_id AND c.code = 'TIPO_EJECUTORA'
                  JOIN catalog_field f        ON f.catalog_id = c.id AND f.name = 'codigo'
                  JOIN catalog_record_value v ON v.record_id = cr.id AND v.field_id = f.id
                 WHERE v.valor = '5'));

-- ---------------------------------------------------------------------
-- 1 - Ministerio de Seguridad Pública  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '1', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '1'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '1';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Seguridad Pública'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '1';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MSP'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '1';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '1';

-- ---------------------------------------------------------------------
-- 2 - Dirección General de Urbanismo y Arquitectura  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '2', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección General de Urbanismo y Arquitectura'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DUA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2';

-- ---------------------------------------------------------------------
-- 3 - Ministerio de Educación, Ciencia y Tecnología  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '3', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Educación, Ciencia y Tecnología'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MINEDUCYT'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3100'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3';

-- ---------------------------------------------------------------------
-- 4 - Ministerio de Agricultura y Ganadería  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '4', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Agricultura y Ganadería'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MAG'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4200'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4';

-- ---------------------------------------------------------------------
-- 5 - Corte Suprema de Justicia  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '5', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '5'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '5';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Corte Suprema de Justicia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '5';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CSJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '5';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '1600'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '5';

-- ---------------------------------------------------------------------
-- 6 - Fondo de Inversión Social para el Desarrollo Local  (tipo 2, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '6', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '6'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '6';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fondo de Inversión Social para el Desarrollo Local'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '6';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FISDL'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '6';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '505'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '6';

-- ---------------------------------------------------------------------
-- 7 - Instituto Nacional de los Deportes de El Salvador  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '7', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '7'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '7';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Nacional de los Deportes de El Salvador'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '7';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'INDES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '7';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '501'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '7';

-- ---------------------------------------------------------------------
-- 8 - Secretaría Nacional de la Familia  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '8', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '8'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '8';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Secretaría Nacional de la Familia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '8';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SNF'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '8';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '8';

-- ---------------------------------------------------------------------
-- 9 - Policia Nacional Civil  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '9', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '9'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '9';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Policia Nacional Civil'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '9';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'PNC - MSPJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '9';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '9';

-- ---------------------------------------------------------------------
-- 10 - Ministerio de Seguridad Pública y Justicia  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '10', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '10'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '10';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Seguridad Pública y Justicia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '10';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MSPJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '10';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '10';

-- ---------------------------------------------------------------------
-- 11 - Ministerio de Salud  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '11', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '11'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '11';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Salud'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '11';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MINSAL'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '11';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3200'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '11';

-- ---------------------------------------------------------------------
-- 12 - Ministerio de Hacienda  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '12', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '12'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '12';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Hacienda'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '12';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MH'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '12';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '700'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '12';

-- ---------------------------------------------------------------------
-- 13 - Ministerio de Gobernación y Desarrollo Territorial  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '13', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '13'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '13';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Gobernación y Desarrollo Territorial'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '13';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MIGOBDT'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '13';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '13';

-- ---------------------------------------------------------------------
-- 14 - Ministerio de Defensa  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '14', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '14'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '14';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Defensa'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '14';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MINDEF'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '14';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '900'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '14';

-- ---------------------------------------------------------------------
-- 15 - Academia Nacional de Seguridad Pública  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '15', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '15'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '15';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Academia Nacional de Seguridad Pública'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '15';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ANSP - MSPJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '15';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2401'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '15';

-- ---------------------------------------------------------------------
-- 16 - Ministerio de Relaciones Exteriores  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '16', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '16'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '16';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Relaciones Exteriores'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '16';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MIREX'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '16';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '800'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '16';

-- ---------------------------------------------------------------------
-- 17 - Fiscalía General de la República  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '17', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '17'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '17';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fiscalía General de la República'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '17';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FGR'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '17';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '1700'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '17';

-- ---------------------------------------------------------------------
-- 18 - Ministerio de Economía  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '18', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '18'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '18';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Economía'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '18';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MINEC'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '18';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4100'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '18';

-- ---------------------------------------------------------------------
-- 19 - Ministerio de Trabajo y Previsión Social  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '19', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '19'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '19';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Trabajo y Previsión Social'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '19';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MTYPS'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '19';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '19';

-- ---------------------------------------------------------------------
-- 20 - Asamblea Legislativa  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '20', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '20'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '20';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Asamblea Legislativa'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '20';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'AL'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '20';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '100'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '20';

-- ---------------------------------------------------------------------
-- 21 - Viceministerio de Vivienda y Desarrollo Urbano  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '21', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '21'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '21';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Viceministerio de Vivienda y Desarrollo Urbano'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '21';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'VMVDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '21';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '21';

-- ---------------------------------------------------------------------
-- 22 - Universidad de El Salvador  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '22', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '22'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '22';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Universidad de El Salvador'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '22';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'UES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '22';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3100'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '22';

-- ---------------------------------------------------------------------
-- 23 - Instituto Salvadoreño de Turismo  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '23', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '23'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '23';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Salvadoreño de Turismo'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '23';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ISTU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '23';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4601'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '23';

-- ---------------------------------------------------------------------
-- 24 - Instituto Salvadoreño del Seguro Social  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '24', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '24'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '24';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Salvadoreño del Seguro Social'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '24';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ISSS'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '24';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3303'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '24';

-- ---------------------------------------------------------------------
-- 25 - Comisión Ejecutiva Hidroeléctrica del Río Lempa  (tipo 3, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '3';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '25', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '25'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '25';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Comisión Ejecutiva Hidroeléctrica del Río Lempa'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '25';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CEL'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '25';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4106'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '25';

-- ---------------------------------------------------------------------
-- 26 - Administración Nacional de Acueductos y Alcant.  (tipo 3, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '3';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '26', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '26'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '26';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Administración Nacional de Acueductos y Alcant.'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '26';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ANDA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '26';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4301'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '26';

-- ---------------------------------------------------------------------
-- 27 - Comisión Ejecutiva Portuaria Autónoma  (tipo 3, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '3';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '27', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '27'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '27';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Comisión Ejecutiva Portuaria Autónoma'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '27';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CEPA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '27';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4303'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '27';

-- ---------------------------------------------------------------------
-- 28 - Instituto Salvadoreño de Desarrollo Municipal  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '28', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '28'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '28';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Salvadoreño de Desarrollo Municipal'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '28';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ISDEM'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '28';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2303'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '28';

-- ---------------------------------------------------------------------
-- 29 - Comisión Presid. para la Modern.del Sector Público  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '29', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '29'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '29';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Comisión Presid. para la Modern.del Sector Público'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '29';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CPMSP - CAPRES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '29';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '29';

-- ---------------------------------------------------------------------
-- 30 - Unidad Técnica Ejecutiva del Sector Justicia  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '30', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '30'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '30';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Unidad Técnica Ejecutiva del Sector Justicia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '30';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'UTE'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '30';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2402'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '30';

-- ---------------------------------------------------------------------
-- 31 - Centro Nacional de Registros  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '31', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '31'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '31';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Centro Nacional de Registros'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '31';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CNR'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '31';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4114'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '31';

-- ---------------------------------------------------------------------
-- 32 - Instituto Salvadoreño para el Desa de la Mujer  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '32', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '32'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '32';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Salvadoreño para el Desa de la Mujer'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '32';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ISDEMU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '32';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '504'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '32';

-- ---------------------------------------------------------------------
-- 33 - Viceministerio de Obras Públicas - MOP  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '33', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '33'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '33';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Viceministerio de Obras Públicas - MOP'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '33';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MOPT'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '33';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '33';

-- ---------------------------------------------------------------------
-- 34 - Centro Internacional de Ferias y Convenciones  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '34', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '34'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '34';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Centro Internacional de Ferias y Convenciones'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '34';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CIFCO'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '34';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4101'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '34';

-- ---------------------------------------------------------------------
-- 35 - Dirección General de Caminos  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '35', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '35'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '35';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección General de Caminos'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '35';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DGC'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '35';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '35';

-- ---------------------------------------------------------------------
-- 36 - Instituto Salvadoreño de Rehabilitación Integral  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '36', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '36'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '36';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Salvadoreño de Rehabilitación Integral'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '36';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ISRI'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '36';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3232'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '36';

-- ---------------------------------------------------------------------
-- 37 - Dirección General de Correos  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '37', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '37'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '37';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección General de Correos'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '37';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CORREO - MIGOBDT'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '37';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '37';

-- ---------------------------------------------------------------------
-- 38 - Cuerpo de Bomberos de El Salvador  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '38', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '38'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '38';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Cuerpo de Bomberos de El Salvador'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '38';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'BOMBEROS - MIGOBDT'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '38';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2306'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '38';

-- ---------------------------------------------------------------------
-- 39 - Dirección de Desarrollo de la Comunidad  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '39', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '39'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '39';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección de Desarrollo de la Comunidad'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '39';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DIDECO - MIGOBDT'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '39';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '39';

-- ---------------------------------------------------------------------
-- 40 - Radio Nacional de El Salvador  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '40', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '40'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '40';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Radio Nacional de El Salvador'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '40';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'RADIO'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '40';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '40';

-- ---------------------------------------------------------------------
-- 41 - Instituto Salvadoreño del Protección al Menor  (tipo 5, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '41', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '41'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '41';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Salvadoreño del Protección al Menor'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '41';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ISPM'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '41';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '41';

-- ---------------------------------------------------------------------
-- 42 - Viceministerio de Transporte  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '42', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '42'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '42';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Viceministerio de Transporte'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '42';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'VMT'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '42';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '42';

-- ---------------------------------------------------------------------
-- 43 - Administración de Maquinaria y Equipo -AME-  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '43', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '43'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '43';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Administración de Maquinaria y Equipo -AME-'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '43';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'AME'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '43';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '43';

-- ---------------------------------------------------------------------
-- 44 - Procuraduria General de la República  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '44', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '44'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '44';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Procuraduria General de la República'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '44';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'PGR'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '44';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '1800'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '44';

-- ---------------------------------------------------------------------
-- 45 - Dirección General de Centros Penales - MSPJ  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '45', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '45'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '45';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección General de Centros Penales - MSPJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '45';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DGCP - MSPJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '45';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '45';

-- ---------------------------------------------------------------------
-- 46 - Ministerio de Medio Ambiente y Recursos Naturales  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '46', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '46'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '46';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Medio Ambiente y Recursos Naturales'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '46';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MARN'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '46';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '46';

-- ---------------------------------------------------------------------
-- 47 - Comité de Emergencia Nacional  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '47', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '47'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '47';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Comité de Emergencia Nacional'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '47';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'COEN'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '47';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '47';

-- ---------------------------------------------------------------------
-- 48 - Caja Mutual de los Empleados del Min. de Educación  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '48', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '48'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '48';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Caja Mutual de los Empleados del Min. de Educación'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '48';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CM'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '48';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3105'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '48';

-- ---------------------------------------------------------------------
-- 49 - Fondo Nacional de Vivienda Popular  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '49', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '49'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '49';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fondo Nacional de Vivienda Popular'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '49';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FONAVIPO'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '49';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4305'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '49';

-- ---------------------------------------------------------------------
-- 50 - Corporación Salvadoreña de Turismo  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '50', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '50'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '50';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Corporación Salvadoreña de Turismo'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '50';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CORSATUR'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '50';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4602'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '50';

-- ---------------------------------------------------------------------
-- 51 - Procuraduria p/la Defensa de los Derechos Humanos  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '51', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '51'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '51';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Procuraduria p/la Defensa de los Derechos Humanos'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '51';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'PDDH'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '51';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '1900'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '51';

-- ---------------------------------------------------------------------
-- 52 - Consejo Nacional de Judicatura  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '52', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '52'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '52';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Consejo Nacional de Judicatura'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '52';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CNJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '52';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '1500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '52';

-- ---------------------------------------------------------------------
-- 53 - Corte de Cuentas de la República  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '53', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '53'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '53';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Corte de Cuentas de la República'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '53';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CCR'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '53';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '200'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '53';

-- ---------------------------------------------------------------------
-- 54 - Fundacion Salvador del Mundo  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '54', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '54'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '54';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fundacion Salvador del Mundo'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '54';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FUSALMO'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '54';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '54';

-- ---------------------------------------------------------------------
-- 55 - Fondo de Conservación Vial  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '55', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '55'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '55';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fondo de Conservación Vial'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '55';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FOVIAL'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '55';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4306'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '55';

-- ---------------------------------------------------------------------
-- 56 - Instituto Salvadoreño de Formación Profesional  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '56', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '56'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '56';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Salvadoreño de Formación Profesional'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '56';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'INSAFORP'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '56';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3302'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '56';

-- ---------------------------------------------------------------------
-- 57 - Consejo Superior de Salud Pública  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '57', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '57'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '57';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Consejo Superior de Salud Pública'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '57';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CSSP'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '57';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3231'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '57';

-- ---------------------------------------------------------------------
-- 58 - Fondo Social Para la Vivienda  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '58', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '58'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '58';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fondo Social Para la Vivienda'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '58';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FSV'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '58';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '58';

-- ---------------------------------------------------------------------
-- 59 - Consejo Nacional para la Cultura y el Arte  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '59', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '59'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '59';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Consejo Nacional para la Cultura y el Arte'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '59';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CONCULTURA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '59';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3100'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '59';

-- ---------------------------------------------------------------------
-- 60 - Instituto para el Desarrollo de la Niñez y la Adolescencia  (tipo 2, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '60', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '60'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '60';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto para el Desarrollo de la Niñez y la Adolescencia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '60';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'IDNA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '60';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '60';

-- ---------------------------------------------------------------------
-- 61 - Fundación Salvadoreña de Desarrollo y Vivienda Mínima  (tipo 3, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '3';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '61', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '61'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '61';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fundación Salvadoreña de Desarrollo y Vivienda Mínima'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '61';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FUNDASAL'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '61';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '61';

-- ---------------------------------------------------------------------
-- 62 - Instituto Salvadoreño para el Desarrollo de la Niñez y la Adolescencia  (tipo 2, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '62', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '62'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '62';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Salvadoreño para el Desarrollo de la Niñez y la Adolescencia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '62';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ISNA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '62';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3106'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '62';

-- ---------------------------------------------------------------------
-- 63 - Fondo de Protección de Lisiados y Discap. a Consecuencia del Conflicto Armado  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '63', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '63'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '63';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fondo de Protección de Lisiados y Discap. a Consecuencia del Conflicto Armado'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '63';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FOPROLYD'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '63';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3304'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '63';

-- ---------------------------------------------------------------------
-- 64 - Servicio Nacional de Estudios Territoriales  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '64', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '64'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '64';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Servicio Nacional de Estudios Territoriales'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '64';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SNET'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '64';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '64';

-- ---------------------------------------------------------------------
-- 65 - Secretaría Técnica y de Planificación de la Presidencia  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '65', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '65'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '65';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Secretaría Técnica y de Planificación de la Presidencia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '65';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'STPP - CAPRES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '65';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '65';

-- ---------------------------------------------------------------------
-- 66 - Fondo Ambiental de El Salvador  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '66', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '66'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '66';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fondo Ambiental de El Salvador'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '66';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FONAES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '66';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4401'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '66';

-- ---------------------------------------------------------------------
-- 67 - Superintendencia de Pensiones  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '67', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '67'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '67';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Superintendencia de Pensiones'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '67';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SP'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '67';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4111'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '67';

-- ---------------------------------------------------------------------
-- 68 - Alcaldia Municipal de San Salvador  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '68', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '68'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '68';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Alcaldia Municipal de San Salvador'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '68';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'AMSS'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '68';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '68';

-- ---------------------------------------------------------------------
-- 69 - Banco Multisectorial de Inversiones  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '69', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '69'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '69';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Banco Multisectorial de Inversiones'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '69';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'BMI'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '69';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '69';

-- ---------------------------------------------------------------------
-- 70 - Instituto Libertad y Progreso  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '70', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '70'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '70';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Libertad y Progreso'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '70';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ILP'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '70';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '70';

-- ---------------------------------------------------------------------
-- 71 - Fondo de Desarrollo Económico y Social  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '71', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '71'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '71';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fondo de Desarrollo Económico y Social'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '71';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FODES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '71';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2303'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '71';

-- ---------------------------------------------------------------------
-- 72 - Comisión Nacional de la Micro y Pequeña Empresa  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '72', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '72'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '72';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Comisión Nacional de la Micro y Pequeña Empresa'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '72';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CONAMYPE'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '72';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4122'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '72';

-- ---------------------------------------------------------------------
-- 73 - Superintendencia General de Electricidad y Telecomunicaciones  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '73', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '73'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '73';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Superintendencia General de Electricidad y Telecomunicaciones'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '73';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SIGET'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '73';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4109'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '73';

-- ---------------------------------------------------------------------
-- 74 - Consejo Salvadoreño del Café  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '74', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '74'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '74';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Consejo Salvadoreño del Café'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '74';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CSC'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '74';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4105'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '74';

-- ---------------------------------------------------------------------
-- 75 - Defensoría del Consumidor  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '75', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '75'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '75';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Defensoría del Consumidor'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '75';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DC'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '75';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4118'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '75';

-- ---------------------------------------------------------------------
-- 76 - Loteria Nacional de Beneficencia  (tipo 3, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '3';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '76', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '76'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '76';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Loteria Nacional de Beneficencia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '76';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'LNB'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '76';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '701'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '76';

-- ---------------------------------------------------------------------
-- 77 - Secretaría de la Juventud  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '77', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '77'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '77';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Secretaría de la Juventud'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '77';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SJ - CAPRES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '77';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '77';

-- ---------------------------------------------------------------------
-- 78 - Comisión Nacional de Promoción de Exportaciones e Inversiones  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '78', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '78'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '78';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Comisión Nacional de Promoción de Exportaciones e Inversiones'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '78';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CONADEI - CAPRES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '78';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '78';

-- ---------------------------------------------------------------------
-- 79 - Superintendencia del Sistema Financiero  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '79', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '79'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '79';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Superintendencia del Sistema Financiero'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '79';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SSF'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '79';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '79';

-- ---------------------------------------------------------------------
-- 80 - Superintendencia de Valores  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '80', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '80'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '80';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Superintendencia de Valores'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '80';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SV'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '80';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4110'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '80';

-- ---------------------------------------------------------------------
-- 81 - Tribunal Supremo Electoral  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '81', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '81'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '81';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Tribunal Supremo Electoral'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '81';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'TSE'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '81';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '81';

-- ---------------------------------------------------------------------
-- 82 - Centros Intermedios  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '82', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '82'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '82';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Centros Intermedios'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '82';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CI - MSPJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '82';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '82';

-- ---------------------------------------------------------------------
-- 83 - Secretaría de Estado - MSPJ  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '83', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '83'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '83';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Secretaría de Estado - MSPJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '83';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SE - MSPJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '83';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '83';

-- ---------------------------------------------------------------------
-- 84 - CONACYT  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '84', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '84'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '84';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CONACYT'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '84';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CONACYT'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '84';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4102'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '84';

-- ---------------------------------------------------------------------
-- 87 - Fondo del Milenio  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '87', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '87'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '87';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fondo del Milenio'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '87';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FOMILENIO'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '87';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '507'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '87';

-- ---------------------------------------------------------------------
-- 88 - Academia Internacional para el Cumplimiento de la Ley  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '88', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '88'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '88';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Academia Internacional para el Cumplimiento de la Ley'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '88';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ILEA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '88';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '88';

-- ---------------------------------------------------------------------
-- 89 - Superintendencia de Competencia  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '89', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '89'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '89';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Superintendencia de Competencia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '89';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SC'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '89';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4117'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '89';

-- ---------------------------------------------------------------------
-- 90 - Protección Civil  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '90', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '90'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '90';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Protección Civil'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '90';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'PC'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '90';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '90';

-- ---------------------------------------------------------------------
-- 91 - MINISTERIO DE TURISMO  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '91', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '91'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '91';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'MINISTERIO DE TURISMO'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '91';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MITUR'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '91';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4600'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '91';

-- ---------------------------------------------------------------------
-- 92 - Registro Nacional de las Personas Naturales  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '92', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '92'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '92';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Registro Nacional de las Personas Naturales'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '92';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'RNPN'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '92';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '301'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '92';

-- ---------------------------------------------------------------------
-- 93 - Instituto Nacional de Pensiones de los Empleados Públicos  (tipo 3, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '3';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '93', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '93'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '93';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Nacional de Pensiones de los Empleados Públicos'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '93';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'INPEP'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '93';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '702'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '93';

-- ---------------------------------------------------------------------
-- 94 - Consejo de Vigilancia de la Contaduría Pública y Auditoría  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '94', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '94'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '94';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Consejo de Vigilancia de la Contaduría Pública y Auditoría'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '94';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CVCPA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '94';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4103'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '94';

-- ---------------------------------------------------------------------
-- 95 - Instituto Salvadoreño de Transformación Agraria  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '95', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '95'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '95';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Salvadoreño de Transformación Agraria'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '95';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ISTA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '95';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4201'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '95';

-- ---------------------------------------------------------------------
-- 96 - Ministerio de Cultura  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '96', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '96'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '96';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Cultura'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '96';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MICULTURA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '96';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '96';

-- ---------------------------------------------------------------------
-- 97 - Centro Nacional de Tecnología Agropecuaria y Forestal  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '97', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '97'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '97';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Centro Nacional de Tecnología Agropecuaria y Forestal'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '97';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CENTA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '97';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4202'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '97';

-- ---------------------------------------------------------------------
-- 98 - Secretaría de Inclusión Social  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '98', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '98'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '98';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Secretaría de Inclusión Social'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '98';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SIS - CAPRES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '98';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '98';

-- ---------------------------------------------------------------------
-- 99 - PENDIENTE  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '99', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '99'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '99';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'PENDIENTE'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '99';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'PENDIENTE'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '99';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '99';

-- ---------------------------------------------------------------------
-- 100 - Secretaría de Participación Ciudadana, Transparencia y Anticorrupción  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '100', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '100'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '100';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Secretaría de Participación Ciudadana, Transparencia y Anticorrupción'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '100';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SPCTA - CAPRES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '100';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '100';

-- ---------------------------------------------------------------------
-- 101 - Escuela Nacional de Agricultura "Roberto Quiñonez"  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '101', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '101'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '101';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Escuela Nacional de Agricultura "Roberto Quiñonez"'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '101';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ENA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '101';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4203'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '101';

-- ---------------------------------------------------------------------
-- 102 - Consejo Nacional de la Seguridad Pública  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '102', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '102'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '102';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Consejo Nacional de la Seguridad Pública'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '102';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CNSP'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '102';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '102';

-- ---------------------------------------------------------------------
-- 103 - Fondo Solidario para la Familia Microempresaria  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '103', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '103'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '103';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fondo Solidario para la Familia Microempresaria'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '103';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FOSOFAMILIA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '103';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '537'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '103';

-- ---------------------------------------------------------------------
-- 104 - Instituto Salvadoreño de Fomento Cooperativo  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '104', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '104'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '104';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Salvadoreño de Fomento Cooperativo'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '104';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'INSAFOCOOP'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '104';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3301'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '104';

-- ---------------------------------------------------------------------
-- 105 - Instituto Salvadoreño de Bienestar Magisterial  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '105', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '105'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '105';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Salvadoreño de Bienestar Magisterial'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '105';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ISBM'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '105';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3107'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '105';

-- ---------------------------------------------------------------------
-- 106 - Consejo Nacional de Energía  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '106', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '106'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '106';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Consejo Nacional de Energía'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '106';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CNE'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '106';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4119'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '106';

-- ---------------------------------------------------------------------
-- 107 - Instituto Nacional de la Juventud  (tipo 2, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '107', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '107'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '107';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Nacional de la Juventud'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '107';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'INJUVE'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '107';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '107';

-- ---------------------------------------------------------------------
-- 108 - Fondo Solidario para la Salud  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '108', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '108'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '108';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fondo Solidario para la Salud'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '108';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FOSALUD'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '108';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3235'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '108';

-- ---------------------------------------------------------------------
-- 109 - Consejo Nacional de Calidad  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '109', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '109'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '109';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Consejo Nacional de Calidad'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '109';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CNC'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '109';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '109';

-- ---------------------------------------------------------------------
-- 110 - Dirección Nacional de Medicamentos  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '110', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '110'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '110';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección Nacional de Medicamentos'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '110';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DNM'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '110';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3236'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '110';

-- ---------------------------------------------------------------------
-- 192 - Secretaría de Gobernabilidad y Comunicaciones de la de Presidencia  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '192', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '192'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '192';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Secretaría de Gobernabilidad y Comunicaciones de la de Presidencia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '192';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SGYCP - CAPRES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '192';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '192';

-- ---------------------------------------------------------------------
-- 193 - Centro Farmacéutico de la Fuerza Armada  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '193', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '193'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '193';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Centro Farmacéutico de la Fuerza Armada'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '193';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CEFAFA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '193';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '902'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '193';

-- ---------------------------------------------------------------------
-- 194 - Cruz Roja Salvadoreña  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '194', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '194'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '194';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Cruz Roja Salvadoreña'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '194';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CRUZ ROJA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '194';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3234'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '194';

-- ---------------------------------------------------------------------
-- 195 - Ministerio de Vivienda  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '195', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '195'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '195';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Vivienda'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '195';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MIVI'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '195';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3600'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '195';

-- ---------------------------------------------------------------------
-- 196 - Ministerio de Desarrollo Local  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '196', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '196'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '196';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Ministerio de Desarrollo Local'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '196';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MINDEL'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '196';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3700'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '196';

-- ---------------------------------------------------------------------
-- 197 - Dirección General de Migración y Extranjería  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '197', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '197'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '197';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección General de Migración y Extranjería'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '197';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DGME - MSPJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '197';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '197';

-- ---------------------------------------------------------------------
-- 198 - Instituto Administrador de los Beneficios de los Veteranos Militares y Excombatientes  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '198', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '198'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '198';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Administrador de los Beneficios de los Veteranos Militares y Excombatientes'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '198';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'INABVE'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '198';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2300'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '198';

-- ---------------------------------------------------------------------
-- 199 - Entidad del Milenio  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '199', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '199'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '199';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Entidad del Milenio'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '199';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'EDM'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '199';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '599'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '199';

-- ---------------------------------------------------------------------
-- 200 - Dirección de Reconstrucción del Tejido Social  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '200', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '200'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '200';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección de Reconstrucción del Tejido Social'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '200';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DRTS - MSPJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '200';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '200';

-- ---------------------------------------------------------------------
-- 201 - Vicepresidencia de la República  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '201', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '201'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '201';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Vicepresidencia de la República'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '201';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'VICEPRESIDENCIA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '201';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '201';

-- ---------------------------------------------------------------------
-- 202 - Secretaría de Innovación de la Presidencia  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '202', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '202'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '202';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Secretaría de Innovación de la Presidencia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '202';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SI - CAPRES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '202';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '202';

-- ---------------------------------------------------------------------
-- 203 - Dirección Nacional de Obras Municipales  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '203', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '203'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '203';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección Nacional de Obras Municipales'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '203';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DOM'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '203';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '556'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '203';

-- ---------------------------------------------------------------------
-- 204 - Autoridad Salvadoreña del Agua-  (tipo 5, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '204', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '204'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '204';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Autoridad Salvadoreña del Agua-'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '204';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ASA-'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '204';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4404'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '204';

-- ---------------------------------------------------------------------
-- 205 - Secretaría Privada de la Presidencia  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '205', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '205'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '205';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Secretaría Privada de la Presidencia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '205';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SPP - CAPRES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '205';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '205';

-- ---------------------------------------------------------------------
-- 206 - Agencia de El Salvador para la Cooperación Internacional  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '206', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '206'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '206';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Agencia de El Salvador para la Cooperación Internacional'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '206';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ESCO - CAPRES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '206';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '206';

-- ---------------------------------------------------------------------
-- 500 - Presidencia de la República  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '500', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '500';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Presidencia de la República'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '500';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'PRESIDENCIA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '500';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '500';

-- ---------------------------------------------------------------------
-- 551 - Agencia de Promoción de Exportaciones e Inversiones de El Salvador  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '551', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '551'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '551';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Agencia de Promoción de Exportaciones e Inversiones de El Salvador'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '551';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'PROESA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '551';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '551'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '551';

-- ---------------------------------------------------------------------
-- 557 - Dirección Nacional de Compras Públicas  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '557', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '557'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '557';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección Nacional de Compras Públicas'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '557';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DINAC'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '557';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '557'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '557';

-- ---------------------------------------------------------------------
-- 903 - MINED-FOSEDU  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '903', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '903'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '903';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'MINED-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '903';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MINED-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '903';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3100'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '903';

-- ---------------------------------------------------------------------
-- 909 - PNC-FOSEDU  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '909', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '909'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '909';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'PNC-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '909';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'PNC-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '909';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '909';

-- ---------------------------------------------------------------------
-- 915 - ANSP-FOSEDU  (tipo 2, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '915', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '915'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '915';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'ANSP-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '915';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ANSP-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '915';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2401'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '915';

-- ---------------------------------------------------------------------
-- 917 - FGR-FOSEDU  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '917', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '917'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '917';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FGR-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '917';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FGR-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '917';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '1700'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '917';

-- ---------------------------------------------------------------------
-- 944 - PGR-FOSEDU  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '944', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '944'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '944';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'PGR-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '944';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'PGR-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '944';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '1800'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '944';

-- ---------------------------------------------------------------------
-- 945 - MJSP/DGCP-FOSEDU  (tipo 1, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '945', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '945'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '945';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'MJSP/DGCP-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '945';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'MJSP/DGCP-FOSEDU'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '945';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2400'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '945';

-- ---------------------------------------------------------------------
-- 2307 - Dirección de Integración  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '2307', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2307'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2307';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección de Integración'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2307';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DI'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2307';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2307'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2307';

-- ---------------------------------------------------------------------
-- 2308 - Dirección de Ordenamiento Territorial y Construcción  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '2308', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2308'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2308';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Dirección de Ordenamiento Territorial y Construcción'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2308';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'DOT'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2308';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '2308'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '2308';

-- ---------------------------------------------------------------------
-- 3108 - Consejo Nacional de la Niñez y de la Adolescencia  (tipo 2, INACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'INACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '3108', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3108'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3108';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Consejo Nacional de la Niñez y de la Adolescencia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3108';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CONNA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3108';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3108'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3108';

-- ---------------------------------------------------------------------
-- 3109 - Consejo Nacional de la Primera Infancia, Niñez y Adolescencia  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '3109', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3109'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3109';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Consejo Nacional de la Primera Infancia, Niñez y Adolescencia'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3109';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'CONAPINA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3109';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3109'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3109';

-- ---------------------------------------------------------------------
-- 3110 - Instituto Crecer Juntos  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '3110', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3110'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3110';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Crecer Juntos'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3110';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ICJ'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3110';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3100'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3110';

-- ---------------------------------------------------------------------
-- 3237 - Hospital Nacional El Salvador  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '3237', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3237'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3237';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Hospital Nacional El Salvador'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3237';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'HES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3237';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3237'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3237';

-- ---------------------------------------------------------------------
-- 4115 - Fondo de Inversión Nacional en Electricidad y Telefonía  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '4115', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4115'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4115';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Fondo de Inversión Nacional en Electricidad y Telefonía'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4115';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'FINET'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4115';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4115'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4115';

-- ---------------------------------------------------------------------
-- 4127 - Instituto Nacional de Capacitación y Formación  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '4127', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4127'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4127';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Instituto Nacional de Capacitación y Formación'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4127';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'INCAF'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4127';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4127'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4127';

-- ---------------------------------------------------------------------
-- 4404 - Autoridad Salvadoreña del Agua  (tipo 2, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '2';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '4404', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4404'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4404';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Autoridad Salvadoreña del Agua'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4404';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'ASA'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4404';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4404'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4404';

-- ---------------------------------------------------------------------
-- 4603 - Autoridad de Planificación del Centro Histórico de San Salvador  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '4603', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4603'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4603';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Autoridad de Planificación del Centro Histórico de San Salvador'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4603';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'Centro Histórico'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4603';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '4603'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '4603';

-- ---------------------------------------------------------------------
-- 99999 - Banco Central de Reserva  (tipo 1, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '1';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '99999', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '99999'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '99999';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Banco Central de Reserva'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '99999';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'BCR'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '99999';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '0'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '99999';

-- ---------------------------------------------------------------------
-- 207 - Organismo de Mejora Regulatoria  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '207', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '207'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '207';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Organismo de Mejora Regulatoria'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '207';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'OMR - CAPRES'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '207';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '500'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '207';

-- ---------------------------------------------------------------------
-- 3241 - Superintendencia de Regulación Sanitaria  (tipo 5, ACTIVE)
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_tipo_ejecutora_ref p
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND p.codigo = '5';
INSERT INTO tmp_record_map (codigo, record_id)
SELECT '3241', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'UNIDAD_EJECUTORA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3241'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3241';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Superintendencia de Regulación Sanitaria'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3241';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_sigla_id, 'SRS'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3241';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_inst_id, '3200'
  FROM tmp_record_map m, tmp_catalog_ref r
 WHERE r.catalog_code = 'UNIDAD_EJECUTORA' AND m.codigo = '3241';

-- ---------------------------------------------------------------------
-- Verificación (esperado: 145 registros = 119 ACTIVE + 26 INACTIVE;
-- 0 sin padre; 580 valores)
-- ---------------------------------------------------------------------
SELECT cr.active, COUNT(*) AS registros,
       SUM(CASE WHEN cr.parent_record_id IS NULL THEN 1 ELSE 0 END) AS sin_padre
  FROM catalog_record cr JOIN tmp_record_map m ON m.record_id = cr.id
 GROUP BY cr.active;

SELECT COUNT(*) AS valores
  FROM catalog_record_value v JOIN tmp_record_map m ON m.record_id = v.record_id;

COMMIT;

-- ---------------------------------------------------------------------
-- Limpieza
-- ---------------------------------------------------------------------
DROP TABLE tmp_record_map;
DROP TABLE tmp_tipo_ejecutora_ref;
DROP TABLE tmp_catalog_ref;
