-- =====================================================================
-- Inicialización de catalog_record y catalog_record_value
-- Jerarquía de catálogos: INSTITUCION -> INSTITUCION_ADSCRITA
-- Fuente : catalogos__inicializacion.json, grupo code = "INSTITUCION"
-- Ítems  : 144 (40 de nivel 1 -> INSTITUCION, 104 de nivel 2 -> INSTITUCION_ADSCRITA)
-- Dialecto: SQL ANSI (SQL:2003). Codificación: UTF-8.
--
-- Prerrequisitos:
--   * Existen en catalog los códigos 'INSTITUCION' e 'INSTITUCION_ADSCRITA'.
--   * Cada uno tiene en catalog_field los campos name = 'codigo' y 'nombre'.
--   Si falta alguno, el script falla al poblar tmp_catalog_ref (NOT NULL).
--
-- Id generado: SQL ANSI no ofrece una función portable para obtener el
--   último valor IDENTITY; se usa MAX(id) de catalog_record filtrado por
--   catalog_id, justo después de cada INSERT. Ejecutar en una sola sesión
--   sin cargas concurrentes sobre catalog_record.
--
-- Padre de nivel 2: se resuelve por "item_padre_codigo" del JSON a través
--   de tmp_record_map. Coincide con "el nivel 1 más reciente" en 100 de 104
--   casos; difiere en 4 (557 y 599 -> 500; 2307 y 2308 -> 2300), que en el
--   JSON aparecen tras un ítem de nivel 1 marcado como anomalía
--   (556 y 2306). Esos casos se señalan en el script.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Tablas temporales
-- ---------------------------------------------------------------------
CREATE GLOBAL TEMPORARY TABLE tmp_catalog_ref (
    catalog_code     VARCHAR(64) NOT NULL PRIMARY KEY,
    catalog_id       BIGINT      NOT NULL,
    field_codigo_id  BIGINT      NOT NULL,
    field_nombre_id  BIGINT      NOT NULL
) ON COMMIT PRESERVE ROWS;

CREATE GLOBAL TEMPORARY TABLE tmp_record_map (
    catalog_code     VARCHAR(64) NOT NULL,
    codigo           VARCHAR(64) NOT NULL,
    record_id        BIGINT      NOT NULL,
    PRIMARY KEY (catalog_code, codigo)
) ON COMMIT PRESERVE ROWS;

INSERT INTO tmp_catalog_ref (catalog_code, catalog_id, field_codigo_id, field_nombre_id)
VALUES (
    'INSTITUCION',
    (SELECT c.id FROM catalog c WHERE c.code = 'INSTITUCION'),
    (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id
      WHERE c.code = 'INSTITUCION' AND f.name = 'codigo'),
    (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id
      WHERE c.code = 'INSTITUCION' AND f.name = 'nombre')
);

INSERT INTO tmp_catalog_ref (catalog_code, catalog_id, field_codigo_id, field_nombre_id)
VALUES (
    'INSTITUCION_ADSCRITA',
    (SELECT c.id FROM catalog c WHERE c.code = 'INSTITUCION_ADSCRITA'),
    (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id
      WHERE c.code = 'INSTITUCION_ADSCRITA' AND f.name = 'codigo'),
    (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id
      WHERE c.code = 'INSTITUCION_ADSCRITA' AND f.name = 'nombre')
);

-- ---------------------------------------------------------------------
-- [N1] 0 - NINGUNA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '0', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '0'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '0';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'NINGUNA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '0';

-- ---------------------------------------------------------------------
-- [N1] 100 - ORGANO LEGISLATIVO
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '100', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '100'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '100';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'ORGANO LEGISLATIVO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '100';

-- ---------------------------------------------------------------------
-- [N1] 200 - CORTE DE CUENTAS DE LA REPUBLICA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '200', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '200'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '200';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CORTE DE CUENTAS DE LA REPUBLICA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '200';

-- ---------------------------------------------------------------------
-- [N1] 300 - TRIBUNAL SUPREMO ELECTORAL
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '300', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '300'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '300';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'TRIBUNAL SUPREMO ELECTORAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '300';

-- [N2] 301 - REGISTRO NACIONAL DE LAS PERSONAS NATURALES  (padre 300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '301', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '301'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '301';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'REGISTRO NACIONAL DE LAS PERSONAS NATURALES'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '301';

-- ---------------------------------------------------------------------
-- [N1] 400 - TRIBUNAL DE SERVICIO CIVIL
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '400', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '400'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '400';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'TRIBUNAL DE SERVICIO CIVIL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '400';

-- ---------------------------------------------------------------------
-- [N1] 500 - PRESIDENCIA DE LA REPUBLICA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '500', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '500'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '500';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'PRESIDENCIA DE LA REPUBLICA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '500';

-- [N2] 501 - INSTITUTO NACIONAL DE LOS DEPORTES DE EL SALVADOR  (padre 500)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '500';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '501', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '501'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '501';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO NACIONAL DE LOS DEPORTES DE EL SALVADOR'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '501';

-- [N2] 502 - INSTITUTO SALVADOREÑO DE PROTECCION AL MENOR  (padre 500)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '500';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '502', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '502'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '502';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO DE PROTECCION AL MENOR'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '502';

-- [N2] 503 - ADMINISTRACION NACIONAL DE TELECOMUNICACIONES  (padre 500)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '500';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '503', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '503'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '503';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'ADMINISTRACION NACIONAL DE TELECOMUNICACIONES'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '503';

-- [N2] 504 - INSTITUTO SALVADOREÑO PARA EL DESARROLLO DE LA MUJER  (padre 500)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '500';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '504', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '504'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '504';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO PARA EL DESARROLLO DE LA MUJER'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '504';

-- [N2] 505 - FONDO DE INVERSION SOCIAL PARA EL DESARROLLO LOCAL  (padre 500)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '500';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '505', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '505'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '505';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FONDO DE INVERSION SOCIAL PARA EL DESARROLLO LOCAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '505';

-- [N2] 507 - FONDO DEL MILENIO  (padre 500)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '500';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '507', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '507'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '507';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FONDO DEL MILENIO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '507';

-- [N2] 537 - FONDO SOLIDARIO PARA LA FAMILIA MICROEMPRESARIA  (padre 500)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '500';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '537', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '537'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '537';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FONDO SOLIDARIO PARA LA FAMILIA MICROEMPRESARIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '537';

-- [N2] 551 - AGENCIA DE PROMOCION DE EXPORTACIONES E INVERSIONES DE EL SALVADOR  (padre 500)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '500';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '551', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '551'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '551';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'AGENCIA DE PROMOCION DE EXPORTACIONES E INVERSIONES DE EL SALVADOR'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '551';

-- ---------------------------------------------------------------------
-- [N1] 556 - DIRECCIÓN NACIONAL DE OBRAS MUNICIPALES  -- ANOMALÍA en JSON: código padre igual al propio
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '556', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '556'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '556';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'DIRECCIÓN NACIONAL DE OBRAS MUNICIPALES'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '556';

-- [N2] 557 - DIRECCION NACIONAL DE COMPRAS PUBLICAS  (padre 500)
-- NOTA: el nivel 1 inmediatamente anterior es 556; item_padre_codigo = 500
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '500';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '557', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '557'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '557';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'DIRECCION NACIONAL DE COMPRAS PUBLICAS'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '557';

-- [N2] 599 - ENTIDAD DEL MILENIO  (padre 500)
-- NOTA: el nivel 1 inmediatamente anterior es 556; item_padre_codigo = 500
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '500';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '599', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '599'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '599';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'ENTIDAD DEL MILENIO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '599';

-- ---------------------------------------------------------------------
-- [N1] 600 - TRIBUNAL DE ETICA GUBERNAMENTAL
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '600', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '600'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '600';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'TRIBUNAL DE ETICA GUBERNAMENTAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '600';

-- ---------------------------------------------------------------------
-- [N1] 700 - RAMO DE HACIENDA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '700', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '700'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '700';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE HACIENDA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '700';

-- [N2] 701 - LOTERIA NACIONAL DE BENEFICENCIA  (padre 700)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '700';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '701', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '701'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '701';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'LOTERIA NACIONAL DE BENEFICENCIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '701';

-- [N2] 702 - INSTITUTO NACIONAL DE PENSIONES DE LOS EMPLEADOS PUBLICOS  (padre 700)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '700';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '702', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '702'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '702';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO NACIONAL DE PENSIONES DE LOS EMPLEADOS PUBLICOS'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '702';

-- ---------------------------------------------------------------------
-- [N1] 800 - RAMO DE RELACIONES EXTERIORES
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '800', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '800'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '800';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE RELACIONES EXTERIORES'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '800';

-- ---------------------------------------------------------------------
-- [N1] 900 - RAMO DE LA DEFENSA NACIONAL
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '900', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '900'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '900';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE LA DEFENSA NACIONAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '900';

-- [N2] 902 - CEFAFA  (padre 900)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '900';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '902', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '902'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '902';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CEFAFA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '902';

-- ---------------------------------------------------------------------
-- [N1] 1500 - CONSEJO NACIONAL DE LA JUDICATURA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '1500', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '1500'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '1500';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CONSEJO NACIONAL DE LA JUDICATURA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '1500';

-- ---------------------------------------------------------------------
-- [N1] 1600 - ORGANO JUDICIAL
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '1600', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '1600'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '1600';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'ORGANO JUDICIAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '1600';

-- ---------------------------------------------------------------------
-- [N1] 1700 - FISCALIA GENERAL DE LA REPUBLICA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '1700', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '1700'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '1700';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FISCALIA GENERAL DE LA REPUBLICA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '1700';

-- ---------------------------------------------------------------------
-- [N1] 1800 - PROCURADURIA GENERAL DE LA REPUBLICA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '1800', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '1800'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '1800';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'PROCURADURIA GENERAL DE LA REPUBLICA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '1800';

-- ---------------------------------------------------------------------
-- [N1] 1900 - PROCURADURIA PARA LA DEFENSA DE LOS DERECHOS HUMANOS
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '1900', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '1900'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '1900';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'PROCURADURIA PARA LA DEFENSA DE LOS DERECHOS HUMANOS'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '1900';

-- ---------------------------------------------------------------------
-- [N1] 2000 - MINISTERIO DE GOBERNACION
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '2000', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2000'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2000';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'MINISTERIO DE GOBERNACION'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2000';

-- [N2] 2002 - INSTITUTO SALVADOREÑO DE DESARROLLO MUNICIPAL  (padre 2000)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '2000';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '2002', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2002'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2002';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO DE DESARROLLO MUNICIPAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2002';

-- ---------------------------------------------------------------------
-- [N1] 2100 - RAMO DE SEGURIDAD PUBLICA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '2100', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2100'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2100';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE SEGURIDAD PUBLICA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2100';

-- [N2] 2101 - ACADEMIA NACIONAL DE SEGURIDAD PUBLICA  (padre 2100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '2100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '2101', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2101'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2101';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'ACADEMIA NACIONAL DE SEGURIDAD PUBLICA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2101';

-- ---------------------------------------------------------------------
-- [N1] 2200 - RAMO DE JUSTICIA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '2200', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2200'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2200';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE JUSTICIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2200';

-- [N2] 2201 - CENTRO NACIONAL DE REGISTROS  (padre 2200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '2200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '2201', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2201'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2201';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CENTRO NACIONAL DE REGISTROS'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2201';

-- [N2] 2202 - UNIDAD TECNICA EJECUTIVA  (padre 2200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '2200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '2202', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2202'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2202';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'UNIDAD TECNICA EJECUTIVA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2202';

-- ---------------------------------------------------------------------
-- [N1] 2300 - RAMO DE GOBERNACION Y DESARROLLO TERRITORIAL
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '2300', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2300'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2300';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE GOBERNACION Y DESARROLLO TERRITORIAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2300';

-- [N2] 2303 - INSTITUTO SALVADOREÑO DE DESARROLLO MUNICIPAL  (padre 2300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '2300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '2303', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2303'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2303';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO DE DESARROLLO MUNICIPAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2303';

-- ---------------------------------------------------------------------
-- [N1] 2306 - CUERPO DE BOMBEROS DE EL SALVADOR  -- ANOMALÍA en JSON: código padre igual al propio
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '2306', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2306'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2306';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CUERPO DE BOMBEROS DE EL SALVADOR'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2306';

-- [N2] 2307 - DIRECCION DE INTEGRACION  (padre 2300)
-- NOTA: el nivel 1 inmediatamente anterior es 2306; item_padre_codigo = 2300
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '2300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '2307', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2307'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2307';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'DIRECCION DE INTEGRACION'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2307';

-- [N2] 2308 - DIRECCIÓN DE ORDENAMIENTO TERRITORIAL Y CONSTRUCCIÓN  (padre 2300)
-- NOTA: el nivel 1 inmediatamente anterior es 2306; item_padre_codigo = 2300
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '2300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '2308', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2308'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2308';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'DIRECCIÓN DE ORDENAMIENTO TERRITORIAL Y CONSTRUCCIÓN'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2308';

-- ---------------------------------------------------------------------
-- [N1] 2400 - RAMO DE SEGURIDAD PUBLICA Y JUSTICIA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '2400', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2400'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2400';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE SEGURIDAD PUBLICA Y JUSTICIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '2400';

-- [N2] 2401 - ACADEMIA NACIONAL DE SEGURIDAD PUBLICA  (padre 2400)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '2400';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '2401', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2401'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2401';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'ACADEMIA NACIONAL DE SEGURIDAD PUBLICA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2401';

-- [N2] 2402 - UNIDAD TECNICA EJECUTIVA  (padre 2400)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '2400';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '2402', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '2402'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2402';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'UNIDAD TECNICA EJECUTIVA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '2402';

-- ---------------------------------------------------------------------
-- [N1] 3100 - RAMO DE EDUCACION, CIENCIA Y TECNOLOGIA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '3100', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3100'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3100';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE EDUCACION, CIENCIA Y TECNOLOGIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3100';

-- [N2] 3101 - UNIVERSIDAD DE EL SALVADOR  (padre 3100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3101', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3101'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3101';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'UNIVERSIDAD DE EL SALVADOR'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3101';

-- [N2] 3102 - FEDERACION SALVADOREÑA DE FUTBOL  (padre 3100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3102', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3102'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3102';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FEDERACION SALVADOREÑA DE FUTBOL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3102';

-- [N2] 3105 - CAJA MUTUAL DE LOS EMPLEADOS DEL MINISTERIO DE EDUCACION  (padre 3100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3105', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3105'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3105';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CAJA MUTUAL DE LOS EMPLEADOS DEL MINISTERIO DE EDUCACION'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3105';

-- [N2] 3106 - INSTITUTO NACIONAL PARA EL DESARROLLO INTEGRAL DE LA NIÑEZ Y LA ADOLESCENCIA  (padre 3100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3106', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3106'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3106';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO NACIONAL PARA EL DESARROLLO INTEGRAL DE LA NIÑEZ Y LA ADOLESCENCIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3106';

-- [N2] 3107 - INSTITUTO SALVADOREÑO DE BIENESTAR MAGISTERIAL  (padre 3100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3107', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3107'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3107';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO DE BIENESTAR MAGISTERIAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3107';

-- [N2] 3108 - CONSEJO NACIONAL DE LA NIÑEZ Y DE LA ADOLESCENCIA  (padre 3100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3108', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3108'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3108';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CONSEJO NACIONAL DE LA NIÑEZ Y DE LA ADOLESCENCIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3108';

-- [N2] 3109 - CONSEJO NACIONAL DE LA PRIMERA INFANCIA, NIÑEZ Y ADOLESCENCIA  (padre 3100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3109', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3109'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3109';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CONSEJO NACIONAL DE LA PRIMERA INFANCIA, NIÑEZ Y ADOLESCENCIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3109';

-- [N2] 3110 - INSTITUTO CRECER JUNTOS  (padre 3100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3110', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3110'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3110';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO CRECER JUNTOS'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3110';

-- ---------------------------------------------------------------------
-- [N1] 3200 - RAMO DE SALUD
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '3200', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3200'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3200';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE SALUD'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3200';

-- [N2] 3201 - HOSPITAL NACIONAL ROSALES  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3201', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3201'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3201';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL ROSALES'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3201';

-- [N2] 3202 - HOSPITAL NACIONAL "BENJAMIN BLOOM"  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3202', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3202'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3202';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL "BENJAMIN BLOOM"'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3202';

-- [N2] 3203 - HOSPITAL NACIONAL DE MATERNIDAD "DR. RAUL ARGUELLO  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3203', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3203'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3203';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE MATERNIDAD "DR. RAUL ARGUELLO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3203';

-- [N2] 3204 - HOSPITAL NACIONAL PSIQUIATRICO "DR. JOSE MOLINA MA  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3204', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3204'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3204';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL PSIQUIATRICO "DR. JOSE MOLINA MA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3204';

-- [N2] 3205 - HOSPITAL NACIONAL NEUMOLOGICO "DR. JOSE ANTONIO ZA  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3205', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3205'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3205';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL NEUMOLOGICO "DR. JOSE ANTONIO ZA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3205';

-- [N2] 3206 - HOSPITAL NACIONAL "SAN JUAN DE DIOS", SANTA ANA  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3206', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3206'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3206';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL "SAN JUAN DE DIOS", SANTA ANA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3206';

-- [N2] 3207 - HOSPITAL NACIONAL "FRANCISCO MENENDEZ",AHUACHAPAN  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3207', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3207'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3207';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL "FRANCISCO MENENDEZ",AHUACHAPAN'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3207';

-- [N2] 3208 - HOSPITAL NACIONAL "SAN JUAN DE DIOS", SONSONATE  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3208', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3208'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3208';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL "SAN JUAN DE DIOS", SONSONATE'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3208';

-- [N2] 3209 - HOSPITAL NACIONAL "DR. LUIS EDMUNDO VASQUEZ", CHAL  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3209', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3209'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3209';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL "DR. LUIS EDMUNDO VASQUEZ", CHAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3209';

-- [N2] 3210 - HOSPITAL NACIONAL "SAN RAFAEL", NUEVA SAN SALVADOR  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3210', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3210'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3210';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL "SAN RAFAEL", NUEVA SAN SALVADOR'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3210';

-- [N2] 3211 - HOSPITAL NACIONAL "SANTA GERTRUDIS", SAN VICENTE  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3211', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3211'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3211';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL "SANTA GERTRUDIS", SAN VICENTE'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3211';

-- [N2] 3212 - HOSPITAL NACIONAL "SANTA TERESA", ZACATECOLUCA  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3212', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3212'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3212';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL "SANTA TERESA", ZACATECOLUCA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3212';

-- [N2] 3213 - HOSPITAL NACIONAL "SAN JUAN DE DIOS", SAN MIGUEL  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3213', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3213'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3213';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL "SAN JUAN DE DIOS", SAN MIGUEL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3213';

-- [N2] 3214 - HOSPITAL NACIONAL "SAN PEDRO", USULUTAN  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3214', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3214'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3214';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL "SAN PEDRO", USULUTAN'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3214';

-- [N2] 3215 - HOSPITAL NACIONAL "DR. JUAN JOSE FERNANDEZ", ZACAM  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3215', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3215'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3215';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL "DR. JUAN JOSE FERNANDEZ", ZACAM'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3215';

-- [N2] 3216 - HOSPITAL NACIONAL DE SAN BARTOLO  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3216', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3216'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3216';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE SAN BARTOLO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3216';

-- [N2] 3217 - HOSPITAL NACIONAL DE COJUTEPEQUE  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3217', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3217'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3217';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE COJUTEPEQUE'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3217';

-- [N2] 3218 - HOSPITAL NACIONAL DE LA UNION  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3218', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3218'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3218';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE LA UNION'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3218';

-- [N2] 3219 - HOSPITAL NACIONAL DE ILOBASCO  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3219', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3219'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3219';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE ILOBASCO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3219';

-- [N2] 3220 - HOSPITAL NACIONAL DE NUEVA GUADALUPE  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3220', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3220'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3220';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE NUEVA GUADALUPE'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3220';

-- [N2] 3221 - HOSPITAL NACIONAL DE CIUDAD BARRIOS  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3221', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3221'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3221';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE CIUDAD BARRIOS'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3221';

-- [N2] 3222 - HOSPITAL NACIONAL DE SENSUNTEPEQUE  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3222', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3222'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3222';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE SENSUNTEPEQUE'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3222';

-- [N2] 3223 - HOSPITAL NACIONAL DE CHALCHUAPA  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3223', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3223'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3223';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE CHALCHUAPA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3223';

-- [N2] 3224 - HOSPITAL NACIONAL DE METAPAN  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3224', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3224'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3224';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE METAPAN'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3224';

-- [N2] 3225 - HOSPITAL NACIONAL DE SAN FRANCISCO GOTERA  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3225', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3225'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3225';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE SAN FRANCISCO GOTERA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3225';

-- [N2] 3226 - HOSPITAL NACIONAL DE SANTA ROSA DE LIMA  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3226', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3226'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3226';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE SANTA ROSA DE LIMA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3226';

-- [N2] 3227 - HOSPITAL NACIONAL DE NUEVA CONCEPCION  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3227', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3227'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3227';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE NUEVA CONCEPCION'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3227';

-- [N2] 3228 - HOSPITAL NACIONAL DE SANTIAGO DE MARIA  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3228', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3228'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3228';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE SANTIAGO DE MARIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3228';

-- [N2] 3229 - HOSPITAL NACIONAL DE JIQUILISCO  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3229', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3229'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3229';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE JIQUILISCO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3229';

-- [N2] 3230 - HOSPITAL NACIONAL DE SUCHITOTO  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3230', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3230'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3230';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOSPITAL NACIONAL DE SUCHITOTO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3230';

-- [N2] 3231 - CONSEJO SUPERIOR DE SALUD PUBLICA  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3231', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3231'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3231';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CONSEJO SUPERIOR DE SALUD PUBLICA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3231';

-- [N2] 3232 - INSTITUTO SALVADOREÑO DE REHABILITACION INTEGRAL  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3232', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3232'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3232';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO DE REHABILITACION INTEGRAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3232';

-- [N2] 3233 - HOGAR DE ANCIANOS "NARCISA CASTILLO", SANTA ANA  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3233', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3233'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3233';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HOGAR DE ANCIANOS "NARCISA CASTILLO", SANTA ANA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3233';

-- [N2] 3234 - CRUZ ROJA SALVADOREÑA  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3234', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3234'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3234';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CRUZ ROJA SALVADOREÑA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3234';

-- [N2] 3235 - FONDO SOLIDARIO PARA LA SALUD  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3235', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3235'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3235';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FONDO SOLIDARIO PARA LA SALUD'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3235';

-- [N2] 3236 - DIRECCION NACIONAL DE MEDICAMENTOS  (padre 3200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3236', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3236'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3236';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'DIRECCION NACIONAL DE MEDICAMENTOS'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3236';

-- ---------------------------------------------------------------------
-- [N1] 3237 - Hospital Nacional El Salvador  -- ANOMALÍA en JSON: código padre igual al propio
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '3237', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3237'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3237';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'Hospital Nacional El Salvador'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3237';

-- ---------------------------------------------------------------------
-- [N1] 3300 - RAMO DE TRABAJO Y PREVISION SOCIAL
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '3300', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3300'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3300';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE TRABAJO Y PREVISION SOCIAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3300';

-- [N2] 3301 - INSTITUTO SALVADOREÑO DE FOMENTO COOPERATIVO  (padre 3300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3301', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3301'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3301';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO DE FOMENTO COOPERATIVO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3301';

-- [N2] 3302 - INSTITUTO SALVADOREÑO DE FORMACION PROFESIONAL  (padre 3300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3302', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3302'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3302';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO DE FORMACION PROFESIONAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3302';

-- [N2] 3303 - INSTITUTO SALVADOREÑO DEL SEGURO SOCIAL  (padre 3300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3303', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3303'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3303';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO DEL SEGURO SOCIAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3303';

-- [N2] 3304 - FONDO DE PROTECCION DE LISIADOS Y DISCAPACITADOS A CONSECUENCIA DEL CONFLICTO ARMADO  (padre 3300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3304', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3304'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3304';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FONDO DE PROTECCION DE LISIADOS Y DISCAPACITADOS A CONSECUENCIA DEL CONFLICTO ARMADO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3304';

-- ---------------------------------------------------------------------
-- [N1] 3400 - RAMO DE VIVIENDA Y DESARROLLO URBANO
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '3400', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3400'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3400';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE VIVIENDA Y DESARROLLO URBANO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3400';

-- [N2] 3401 - FONDO NACIONAL DE VIVIENDA POPULAR  (padre 3400)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '3400';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '3401', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3401'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3401';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FONDO NACIONAL DE VIVIENDA POPULAR'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '3401';

-- ---------------------------------------------------------------------
-- [N1] 3500 - RAMO DE CULTURA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '3500', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3500'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3500';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE CULTURA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3500';

-- ---------------------------------------------------------------------
-- [N1] 3600 - RAMO DE VIVIENDA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '3600', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3600'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3600';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE VIVIENDA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3600';

-- ---------------------------------------------------------------------
-- [N1] 3700 - RAMO DE DESARROLLO LOCAL
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '3700', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '3700'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3700';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE DESARROLLO LOCAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '3700';

-- ---------------------------------------------------------------------
-- [N1] 4100 - RAMO DE ECONOMIA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '4100', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4100'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4100';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE ECONOMIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4100';

-- [N2] 4101 - CENTRO INTERNACIONAL DE FERIAS Y CONVENCIONES  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4101', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4101'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4101';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CENTRO INTERNACIONAL DE FERIAS Y CONVENCIONES'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4101';

-- [N2] 4102 - CONSEJO NACIONAL DE CIENCIA Y TECNOLOGIA  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4102', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4102'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4102';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CONSEJO NACIONAL DE CIENCIA Y TECNOLOGIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4102';

-- [N2] 4103 - CONSEJO DE VIGILANCIA DE LA CONTADURIA PUBLICA Y AUDITORIA  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4103', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4103'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4103';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CONSEJO DE VIGILANCIA DE LA CONTADURIA PUBLICA Y AUDITORIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4103';

-- [N2] 4104 - INSTITUTO SALVADOREÑO DE TURISMO  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4104', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4104'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4104';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO DE TURISMO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4104';

-- [N2] 4105 - CONSEJO SALVADOREÑO DEL CAFE  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4105', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4105'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4105';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CONSEJO SALVADOREÑO DEL CAFE'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4105';

-- [N2] 4106 - COMISION EJECUTIVA HIDROELECTRICA DEL RIO LEMPA  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4106', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4106'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4106';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'COMISION EJECUTIVA HIDROELECTRICA DEL RIO LEMPA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4106';

-- [N2] 4108 - CORPORACION SALVADOREÑA DE TURISMO  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4108', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4108'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4108';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CORPORACION SALVADOREÑA DE TURISMO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4108';

-- [N2] 4109 - SUPERINTENDENCIA GENERAL DE ELECTRICIDAD Y TELECOMUNICACIONES  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4109', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4109'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4109';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'SUPERINTENDENCIA GENERAL DE ELECTRICIDAD Y TELECOMUNICACIONES'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4109';

-- [N2] 4110 - SUPERINTENDENCIA DE VALORES  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4110', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4110'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4110';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'SUPERINTENDENCIA DE VALORES'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4110';

-- [N2] 4111 - SUPERINTENDENCIA DE PENSIONES  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4111', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4111'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4111';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'SUPERINTENDENCIA DE PENSIONES'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4111';

-- [N2] 4112 - CORPORACION SALVADOREÑA DE INVERSIONES  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4112', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4112'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4112';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CORPORACION SALVADOREÑA DE INVERSIONES'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4112';

-- [N2] 4114 - CENTRO NACIONAL DE REGISTROS  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4114', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4114'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4114';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CENTRO NACIONAL DE REGISTROS'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4114';

-- [N2] 4115 - FINET  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4115', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4115'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4115';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FINET'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4115';

-- [N2] 4116 - CONSEJO SALVADOREÑO DE LA AGROINDUSTRIA AZUCARERA  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4116', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4116'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4116';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CONSEJO SALVADOREÑO DE LA AGROINDUSTRIA AZUCARERA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4116';

-- [N2] 4117 - SUPERINTENDENCIA DE COMPETENCIA  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4117', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4117'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4117';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'SUPERINTENDENCIA DE COMPETENCIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4117';

-- [N2] 4118 - DEFENSORIA DEL CONSUMIDOR  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4118', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4118'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4118';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'DEFENSORIA DEL CONSUMIDOR'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4118';

-- [N2] 4119 - CONSEJO NACIONAL DE ENERGIA  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4119', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4119'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4119';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CONSEJO NACIONAL DE ENERGIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4119';

-- [N2] 4122 - COMISION NACIONAL PARA LA MICRO Y PEQUEÑA EMPRESA  (padre 4100)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4100';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4122', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4122'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4122';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'COMISION NACIONAL PARA LA MICRO Y PEQUEÑA EMPRESA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4122';

-- ---------------------------------------------------------------------
-- [N1] 4127 - INSTITUTO NACIONAL DE CAPACITACION Y FORMACION  -- ANOMALÍA en JSON: código padre igual al propio
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '4127', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4127'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4127';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO NACIONAL DE CAPACITACION Y FORMACION'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4127';

-- ---------------------------------------------------------------------
-- [N1] 4200 - RAMO DE AGRICULTURA Y GANADERIA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '4200', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4200'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4200';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE AGRICULTURA Y GANADERIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4200';

-- [N2] 4201 - INSTITUTO SALVADOREÑO DE TRANSFORMACION AGRARIA  (padre 4200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4201', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4201'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4201';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO DE TRANSFORMACION AGRARIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4201';

-- [N2] 4202 - CENTRO NACIONAL DE TECNOLOGIA AGROPECUARIA Y FORESTAL  (padre 4200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4202', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4202'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4202';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CENTRO NACIONAL DE TECNOLOGIA AGROPECUARIA Y FORESTAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4202';

-- [N2] 4203 - ESCUELA NACIONAL DE AGRICULTURA  (padre 4200)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4200';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4203', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4203'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4203';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'ESCUELA NACIONAL DE AGRICULTURA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4203';

-- ---------------------------------------------------------------------
-- [N1] 4300 - RAMO DE OBRAS PUBLICAS Y TRANSPORTE
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '4300', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4300'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4300';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE OBRAS PUBLICAS Y TRANSPORTE'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4300';

-- [N2] 4301 - ADMINISTRACION NACIONAL DE ACUEDUCTOS Y ALCANTARILLADOS  (padre 4300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4301', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4301'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4301';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'ADMINISTRACION NACIONAL DE ACUEDUCTOS Y ALCANTARILLADOS'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4301';

-- [N2] 4302 - FONDO SOCIAL PARA LA VIVIENDA  (padre 4300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4302', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4302'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4302';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FONDO SOCIAL PARA LA VIVIENDA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4302';

-- [N2] 4303 - COMISION EJECUTIVA PORTUARIA AUTONOMA  (padre 4300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4303', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4303'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4303';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'COMISION EJECUTIVA PORTUARIA AUTONOMA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4303';

-- [N2] 4304 - AUTORIDAD DE AVIACION CIVIL  (padre 4300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4304', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4304'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4304';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'AUTORIDAD DE AVIACION CIVIL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4304';

-- [N2] 4305 - FONDO NACIONAL DE VIVIENDA POPULAR  (padre 4300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4305', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4305'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4305';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FONDO NACIONAL DE VIVIENDA POPULAR'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4305';

-- [N2] 4306 - FONDO DE CONSERVACION VIAL  (padre 4300)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4300';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4306', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4306'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4306';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FONDO DE CONSERVACION VIAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4306';

-- ---------------------------------------------------------------------
-- [N1] 4400 - RAMO DE MEDIO AMBIENTE Y RECURSOS NATURALES
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '4400', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4400'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4400';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE MEDIO AMBIENTE Y RECURSOS NATURALES'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4400';

-- [N2] 4401 - FONDO AMBIENTAL DE EL SALVADOR  (padre 4400)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4400';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4401', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4401'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4401';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FONDO AMBIENTAL DE EL SALVADOR'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4401';

-- [N2] 4404 - AUTORIDAD SALVADOREÑA DEL AGUA  (padre 4400)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4400';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4404', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4404'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4404';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'AUTORIDAD SALVADOREÑA DEL AGUA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4404';

-- ---------------------------------------------------------------------
-- [N1] 4500 - RAMO DE TRANSPORTE
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '4500', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4500'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4500';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE TRANSPORTE'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4500';

-- ---------------------------------------------------------------------
-- [N1] 4600 - RAMO DE TURISMO
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '4600', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4600'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4600';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'RAMO DE TURISMO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4600';

-- [N2] 4601 - INSTITUTO SALVADOREÑO DE TURISMO  (padre 4600)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4600';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4601', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4601'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4601';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'INSTITUTO SALVADOREÑO DE TURISMO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4601';

-- [N2] 4602 - CORPORACION SALVADOREÑA DE TURISMO  (padre 4600)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4600';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4602', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4602'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4602';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'CORPORACION SALVADOREÑA DE TURISMO'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4602';

-- [N2] 4603 - AUTORIDAD DE PLANIFICACION DEL CENTRO HISTORICO DE SAN SALVADOR  (padre 4600)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, p.record_id, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r, tmp_record_map p
 WHERE r.catalog_code = 'INSTITUCION_ADSCRITA' AND p.catalog_code = 'INSTITUCION' AND p.codigo = '4600';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION_ADSCRITA', '4603', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION_ADSCRITA');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4603'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4603';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'AUTORIDAD DE PLANIFICACION DEL CENTRO HISTORICO DE SAN SALVADOR'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION_ADSCRITA' AND m.codigo = '4603';

-- ---------------------------------------------------------------------
-- [N1] 4700 - HABITAT
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '4700', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4700'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4700';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'HABITAT'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4700';

-- ---------------------------------------------------------------------
-- [N1] 4800 - MINISTERIO DE SEGURIDA PUBLICA Y JUSTICIA
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '4800', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4800'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4800';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'MINISTERIO DE SEGURIDA PUBLICA Y JUSTICIA'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4800';

-- ---------------------------------------------------------------------
-- [N1] 4900 - FUNDASAL
-- ---------------------------------------------------------------------
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date)
SELECT r.catalog_id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'
  FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION';
INSERT INTO tmp_record_map (catalog_code, codigo, record_id)
SELECT 'INSTITUCION', '4900', MAX(cr.id) FROM catalog_record cr
 WHERE cr.catalog_id = (SELECT r.catalog_id FROM tmp_catalog_ref r WHERE r.catalog_code = 'INSTITUCION');
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_codigo_id, '4900'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4900';
INSERT INTO catalog_record_value (record_id, field_id, valor)
SELECT m.record_id, r.field_nombre_id, 'FUNDASAL'
  FROM tmp_record_map m JOIN tmp_catalog_ref r ON r.catalog_code = m.catalog_code
 WHERE m.catalog_code = 'INSTITUCION' AND m.codigo = '4900';

-- ---------------------------------------------------------------------
-- Verificación (esperado: 40 registros INSTITUCION, 104 INSTITUCION_ADSCRITA,
-- 288 valores)
-- ---------------------------------------------------------------------
SELECT r.catalog_code, COUNT(*) AS registros
  FROM tmp_record_map r GROUP BY r.catalog_code;

SELECT COUNT(*) AS valores
  FROM catalog_record_value v JOIN tmp_record_map m ON m.record_id = v.record_id;

COMMIT;

-- ---------------------------------------------------------------------
-- Limpieza
-- ---------------------------------------------------------------------
DROP TABLE tmp_record_map;
DROP TABLE tmp_catalog_ref;
