-- =====================================================================
-- Inicialización de datos: jerarquía REGION > DEPARTAMENTO > DISTRITO
-- Tablas destino : catalog_record, catalog_record_value
-- Origen         : catalogos__inicializacion.json, grupo "UBICACION_GEOGRAFICA"
--                  (Catálogo de ubicaciones geográficas (Región > Departamento > Distrito))
-- Dialecto       : SQL ANSI (SQL:2003)
-- Volumen        : 293 registros (4 regiones, 14 departamentos, 275 distritos)
--                  861 valores de campo
--
-- Prerrequisitos:
--   * Existen en catalog los códigos 'REGION', 'DEPARTAMENTO' y 'DISTRITO'.
--   * Existen en catalog_field los campos:
--       REGION       : codigo, nombre
--       DEPARTAMENTO : codigo, nombre
--       DISTRITO     : codigo, nombre, marcador_nivel_departamental
--     (el nombre del tercer campo de DISTRITO se ajusta en la sección 1.2)
--   * El script se ejecuta en una sola sesión, en forma serial y sin
--     cargas concurrentes sobre catalog_record: el id "más recientemente
--     generado" se obtiene como MAX(id) del catálogo correspondiente.
--
-- Si falta un catálogo o un campo, la sección 1 falla con violación de
-- NOT NULL y detiene la carga antes de insertar datos (ejecutar con
-- detención ante error, p. ej. psql -v ON_ERROR_STOP=1).
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Tablas temporales de apoyo
-- ---------------------------------------------------------------------

-- 1.1 Catálogo destino por nivel del JSON
CREATE LOCAL TEMPORARY TABLE tmp_catalogo (
    nivel       INTEGER NOT NULL PRIMARY KEY,
    catalog_id  BIGINT  NOT NULL
) ON COMMIT PRESERVE ROWS;

INSERT INTO tmp_catalogo (nivel, catalog_id) VALUES (1, (SELECT id FROM catalog WHERE code = 'REGION'));
INSERT INTO tmp_catalogo (nivel, catalog_id) VALUES (2, (SELECT id FROM catalog WHERE code = 'DEPARTAMENTO'));
INSERT INTO tmp_catalogo (nivel, catalog_id) VALUES (3, (SELECT id FROM catalog WHERE code = 'DISTRITO'));

-- 1.2 Campo destino por nivel y alias
CREATE LOCAL TEMPORARY TABLE tmp_campo (
    nivel     INTEGER     NOT NULL,
    alias     VARCHAR(32) NOT NULL,
    field_id  BIGINT      NOT NULL,
    PRIMARY KEY (nivel, alias)
) ON COMMIT PRESERVE ROWS;

INSERT INTO tmp_campo (nivel, alias, field_id) VALUES (1, 'codigo', (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'REGION' AND f.name = 'codigo'));
INSERT INTO tmp_campo (nivel, alias, field_id) VALUES (1, 'nombre', (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'REGION' AND f.name = 'nombre'));
INSERT INTO tmp_campo (nivel, alias, field_id) VALUES (2, 'codigo', (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DEPARTAMENTO' AND f.name = 'codigo'));
INSERT INTO tmp_campo (nivel, alias, field_id) VALUES (2, 'nombre', (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DEPARTAMENTO' AND f.name = 'nombre'));
INSERT INTO tmp_campo (nivel, alias, field_id) VALUES (3, 'codigo', (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DISTRITO' AND f.name = 'codigo'));
INSERT INTO tmp_campo (nivel, alias, field_id) VALUES (3, 'nombre', (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DISTRITO' AND f.name = 'nombre'));
-- Ajuste aquí el nombre real del campo SI/NO del catálogo DISTRITO si es distinto:
INSERT INTO tmp_campo (nivel, alias, field_id) VALUES (3, 'marcador', (SELECT f.id FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'DISTRITO' AND f.name = 'marcador_nivel_departamental'));

-- 1.3 Último catalog_record generado por nivel (padre de los niveles siguientes)
CREATE LOCAL TEMPORARY TABLE tmp_ultimo (
    nivel      INTEGER NOT NULL PRIMARY KEY,
    record_id  BIGINT
) ON COMMIT PRESERVE ROWS;

INSERT INTO tmp_ultimo (nivel, record_id) VALUES (1, NULL);
INSERT INTO tmp_ultimo (nivel, record_id) VALUES (2, NULL);
INSERT INTO tmp_ultimo (nivel, record_id) VALUES (3, NULL);

-- ---------------------------------------------------------------------
-- 2. Carga de registros (orden jerárquico del JSON)
-- ---------------------------------------------------------------------

-- [1] REGION: Occidental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, CAST(NULL AS BIGINT), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 1;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 1)) WHERE nivel = 1;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 1 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 1 AND f.alias = 'nombre';

-- [2] DEPARTAMENTO: Occidental::Ahuachapán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Ahuachapán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Occidental::Ahuachapán::Ahuachapán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::Ahuachapán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Ahuachapán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::Apaneca
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::Apaneca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Apaneca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::Concepción de Ataco
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::Concepción de Ataco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Concepción de Ataco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::Tacuba
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::Tacuba' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Tacuba' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::Atiquizaya
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::Atiquizaya' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Atiquizaya' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::El Refugio
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::El Refugio' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Refugio' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::San Lorenzo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::San Lorenzo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Lorenzo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::Turín
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::Turín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Turín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::Guaymango
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::Guaymango' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Guaymango' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::Jujutla
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::Jujutla' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Jujutla' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::San Francisco Menéndez
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::San Francisco Menéndez' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Francisco Menéndez' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::San Pedro Puxtla
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::San Pedro Puxtla' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Pedro Puxtla' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Ahuachapán::Ahuachapán - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Ahuachapán::Ahuachapán - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Ahuachapán - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [2] DEPARTAMENTO: Occidental::Santa Ana
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Ana' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Occidental::Santa Ana::Santa Ana
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::Santa Ana' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Ana' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::Coatepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::Coatepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Coatepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::El Congo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::El Congo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Congo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::Masahuat
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::Masahuat' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Masahuat' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::Metapán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::Metapán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Metapán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::Santa Rosa Guachipilín
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::Santa Rosa Guachipilín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Rosa Guachipilín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::Texistepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::Texistepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Texistepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::Candelaria de la Frontera
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::Candelaria de la Frontera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Candelaria de la Frontera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::Chalchuapa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::Chalchuapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Chalchuapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::El Porvenir
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::El Porvenir' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Porvenir' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::San Antonio Pajonal
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::San Antonio Pajonal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Antonio Pajonal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::San Sebastián Salitrillo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::San Sebastián Salitrillo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Sebastián Salitrillo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Santa Ana::Santiago de la Frontera
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Santa Ana::Santiago de la Frontera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santiago de la Frontera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [2] DEPARTAMENTO: Occidental::Sonsonate
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Sonsonate' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Occidental::Sonsonate::Nahulingo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Nahulingo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nahulingo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::San Antonio del Monte
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::San Antonio del Monte' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Antonio del Monte' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Santo Domingo de Guzmán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Santo Domingo de Guzmán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santo Domingo de Guzmán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Sonsonate
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Sonsonate' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Sonsonate' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Sonzacate
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Sonzacate' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Sonzacate' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Armenia
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Armenia' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Armenia' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Caluco
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Caluco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Caluco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Cuisnahuat
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Cuisnahuat' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Cuisnahuat' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Santa Isabel Ishuatán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Santa Isabel Ishuatán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Isabel Ishuatán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Izalco
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Izalco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Izalco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::San Julián
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::San Julián' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Julián' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Juayúa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Juayúa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Juayúa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Nahuizalco
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Nahuizalco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nahuizalco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Salcoatitán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Salcoatitán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Salcoatitán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Santa Catarina Masahuat
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Santa Catarina Masahuat' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Catarina Masahuat' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Acajutla
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Acajutla' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Acajutla' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Occidental::Sonsonate::Sonsonate - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Occidental::Sonsonate::Sonsonate - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Sonsonate - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [1] REGION: Central
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, CAST(NULL AS BIGINT), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 1;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 1)) WHERE nivel = 1;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 1 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 1 AND f.alias = 'nombre';

-- [2] DEPARTAMENTO: Central::Chalatenango
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Chalatenango' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Central::Chalatenango::Agua Caliente
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Agua Caliente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Agua Caliente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Dulce Nombre de María
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Dulce Nombre de María' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Dulce Nombre de María' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::El Paraíso
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::El Paraíso' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Paraíso' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::La Reina
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::La Reina' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'La Reina' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Nueva Concepción
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Nueva Concepción' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nueva Concepción' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San Fernando
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San Fernando' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Fernando' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San Francisco Morazán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San Francisco Morazán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Francisco Morazán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San Rafael
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San Rafael' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Rafael' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Santa Rita
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Santa Rita' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Rita' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Tejutla
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Tejutla' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Tejutla' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Citalá
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Citalá' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Citalá' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San Ignacio
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San Ignacio' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Ignacio' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::La Palma
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::La Palma' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'La Palma' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Arcatao
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Arcatao' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Arcatao' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Azacualpa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Azacualpa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Azacualpa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Comalapa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Comalapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Comalapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Concepción Quezaltepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Concepción Quezaltepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Concepción Quezaltepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Chalatenango
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Chalatenango' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Chalatenango' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::El Carrizal
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::El Carrizal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Carrizal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::La Laguna
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::La Laguna' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'La Laguna' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Las Vueltas
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Las Vueltas' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Las Vueltas' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Nombre de Jesús
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Nombre de Jesús' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nombre de Jesús' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Nueva Trinidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Nueva Trinidad' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nueva Trinidad' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Ojos de Agua
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Ojos de Agua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Ojos de Agua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Potonico
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Potonico' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Potonico' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San Antonio de la Cruz
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San Antonio de la Cruz' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Antonio de la Cruz' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San Antonio Los Ranchos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San Antonio Los Ranchos' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Antonio Los Ranchos' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San Isidro Labrador
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San Isidro Labrador' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Isidro Labrador' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San Francisco Lempa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San Francisco Lempa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Francisco Lempa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San José Cancasque - Cancasque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San José Cancasque - Cancasque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San José Cancasque - Cancasque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San José Las Flores - Las Flores
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San José Las Flores - Las Flores' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San José Las Flores - Las Flores' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San Luis del Carmen
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San Luis del Carmen' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Luis del Carmen' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::San Miguel de Mercedes
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::San Miguel de Mercedes' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Miguel de Mercedes' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Chalatenango::Chalatenango - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Chalatenango::Chalatenango - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Chalatenango - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [2] DEPARTAMENTO: Central::La Libertad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'La Libertad' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Central::La Libertad::Ciudad Arce
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Ciudad Arce' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Ciudad Arce' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::San Juan Opico
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::San Juan Opico' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Juan Opico' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Chiltiupán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Chiltiupán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Chiltiupán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Jicalapa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Jicalapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Jicalapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::La Libertad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::La Libertad' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'La Libertad' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Tamanique
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Tamanique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Tamanique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Teotepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Teotepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Teotepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Antiguo Cuscatlán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Antiguo Cuscatlán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Antiguo Cuscatlán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Huizúcar
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Huizúcar' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Huizúcar' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Nuevo Cuscatlán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Nuevo Cuscatlán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nuevo Cuscatlán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::San José Villanueva
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::San José Villanueva' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San José Villanueva' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Zaragoza
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Zaragoza' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Zaragoza' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Quezaltepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Quezaltepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Quezaltepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::San Matías
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::San Matías' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Matías' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::San Pablo Tacachico
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::San Pablo Tacachico' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Pablo Tacachico' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Colón
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Colón' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Colón' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Jayaque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Jayaque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Jayaque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Sacacoyo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Sacacoyo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Sacacoyo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Talnique
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Talnique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Talnique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Tepecoyo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Tepecoyo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Tepecoyo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Comasagua
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Comasagua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Comasagua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::Santa Tecla (antes: Nueva San Salvador)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::Santa Tecla (antes: Nueva San Salvador)' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Tecla (antes: Nueva San Salvador)' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Libertad::La Libertad - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Libertad::La Libertad - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'La Libertad - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [2] DEPARTAMENTO: Central::San Salvador
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Salvador' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Central::San Salvador::Ayutuxtepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Ayutuxtepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Ayutuxtepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Cuscatancingo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Cuscatancingo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Cuscatancingo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Mejicanos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Mejicanos' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Mejicanos' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::San Salvador
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::San Salvador' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Salvador' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Delgado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Delgado' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Delgado' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Ilopango
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Ilopango' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Ilopango' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::San Martín
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::San Martín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Martín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Soyapango
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Soyapango' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Soyapango' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Tonacatepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Tonacatepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Tonacatepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Aguilares
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Aguilares' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Aguilares' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::El Paisnal
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::El Paisnal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Paisnal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Guazapa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Guazapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Guazapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Apopa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Apopa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Apopa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Nejapa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Nejapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nejapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Panchimalco
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Panchimalco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Panchimalco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Rosario de Mora
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Rosario de Mora' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Rosario de Mora' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::San Marcos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::San Marcos' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Marcos' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Santiago Texacuangos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Santiago Texacuangos' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santiago Texacuangos' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::Santo Tomás
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::Santo Tomás' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santo Tomás' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Salvador::San Salvador - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Salvador::San Salvador - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Salvador - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [2] DEPARTAMENTO: Central::Cuscatlán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Cuscatlán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Central::Cuscatlán::Oratorio de Concepción
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::Oratorio de Concepción' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oratorio de Concepción' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::San Bartolomé Perulapía
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::San Bartolomé Perulapía' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Bartolomé Perulapía' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::San José Guayabal
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::San José Guayabal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San José Guayabal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::San Pedro Perulapán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::San Pedro Perulapán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Pedro Perulapán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::Suchitoto
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::Suchitoto' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Suchitoto' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::Candelaria
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::Candelaria' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Candelaria' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::Cojutepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::Cojutepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Cojutepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::El Carmen
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::El Carmen' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Carmen' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::El Rosario
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::El Rosario' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Rosario' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::Monte San Juan
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::Monte San Juan' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Monte San Juan' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::San Cristóbal
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::San Cristóbal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Cristóbal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::San Rafael Cedros
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::San Rafael Cedros' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Rafael Cedros' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::San Ramón
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::San Ramón' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Ramón' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::Santa Cruz Analquito
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::Santa Cruz Analquito' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Cruz Analquito' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::Santa Cruz Michapa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::Santa Cruz Michapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Cruz Michapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::Tenancingo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::Tenancingo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Tenancingo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cuscatlán::Cuscatlán - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cuscatlán::Cuscatlán - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Cuscatlán - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [2] DEPARTAMENTO: Central::La Paz
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'La Paz' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Central::La Paz::El Rosario - Rosario de La Paz
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::El Rosario - Rosario de La Paz' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Rosario - Rosario de La Paz' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::Jerusalén
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::Jerusalén' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Jerusalén' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::Mercedes La Ceiba
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::Mercedes La Ceiba' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Mercedes La Ceiba' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::Paraíso de Osorio
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::Paraíso de Osorio' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Paraíso de Osorio' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Antonio Masahuat
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Antonio Masahuat' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Antonio Masahuat' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Emigdio
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Emigdio' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Emigdio' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Juan Tepezontes
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Juan Tepezontes' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Juan Tepezontes' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Miguel Tepezontes
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Miguel Tepezontes' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Miguel Tepezontes' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Pedro Nonualco
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Pedro Nonualco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Pedro Nonualco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::Santa María Ostuma
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::Santa María Ostuma' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa María Ostuma' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::Santiago Nonualco
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::Santiago Nonualco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santiago Nonualco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Luis La Herradura
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Luis La Herradura' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Luis La Herradura' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Juan Nonualco
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Juan Nonualco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Juan Nonualco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Rafael Obrajuelo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Rafael Obrajuelo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Rafael Obrajuelo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::Zacatecoluca
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::Zacatecoluca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Zacatecoluca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::Cuyultitán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::Cuyultitán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Cuyultitán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::Olocuilta
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::Olocuilta' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Olocuilta' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Francisco Chinameca
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Francisco Chinameca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Francisco Chinameca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Juan Talpa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Juan Talpa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Juan Talpa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Luis Talpa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Luis Talpa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Luis Talpa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::San Pedro Masahuat
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::San Pedro Masahuat' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Pedro Masahuat' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::Tapalhuaca
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::Tapalhuaca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Tapalhuaca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::La Paz::La Paz - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::La Paz::La Paz - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'La Paz - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [2] DEPARTAMENTO: Central::Cabañas
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cabañas' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Cabañas' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Central::Cabañas::Dolores - Villa Dolores
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cabañas::Dolores - Villa Dolores' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Dolores - Villa Dolores' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cabañas::Guacotecti
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cabañas::Guacotecti' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Guacotecti' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cabañas::San Isidro
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cabañas::San Isidro' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Isidro' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cabañas::Sensuntepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cabañas::Sensuntepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Sensuntepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cabañas::Victoria
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cabañas::Victoria' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Victoria' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cabañas::Cinquera
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cabañas::Cinquera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Cinquera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cabañas::Ilobasco
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cabañas::Ilobasco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Ilobasco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cabañas::Jutiapa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cabañas::Jutiapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Jutiapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cabañas::Tejutepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cabañas::Tejutepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Tejutepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::Cabañas::Cabañas - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::Cabañas::Cabañas - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Cabañas - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [2] DEPARTAMENTO: Central::San Vicente
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Vicente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Central::San Vicente::Apastepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::Apastepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Apastepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::San Esteban Catarina
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::San Esteban Catarina' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Esteban Catarina' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::San Ildefonso
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::San Ildefonso' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Ildefonso' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::San Lorenzo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::San Lorenzo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Lorenzo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::San Sebastián
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::San Sebastián' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Sebastián' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::Santa Clara
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::Santa Clara' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Clara' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::Santo Domingo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::Santo Domingo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santo Domingo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::Guadalupe
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::Guadalupe' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Guadalupe' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::San Cayetano Istepeque
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::San Cayetano Istepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Cayetano Istepeque' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::San Vicente
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::San Vicente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Vicente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::Tecoluca
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::Tecoluca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Tecoluca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::Tepetitán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::Tepetitán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Tepetitán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::Verapaz
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::Verapaz' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Verapaz' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Central::San Vicente::San Vicente - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Central::San Vicente::San Vicente - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Vicente - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [1] REGION: Oriental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, CAST(NULL AS BIGINT), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 1;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 1)) WHERE nivel = 1;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 1 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 1 AND f.alias = 'nombre';

-- [2] DEPARTAMENTO: Oriental::Usulután
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Usulután' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Oriental::Usulután::California
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::California' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'California' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Concepción Batres
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Concepción Batres' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Concepción Batres' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Ereguayquín
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Ereguayquín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Ereguayquín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Jucuarán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Jucuarán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Jucuarán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Ozatlán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Ozatlán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Ozatlán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Usulután
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Usulután' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Usulután' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::San Dionisio
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::San Dionisio' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Dionisio' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Santa Elena
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Santa Elena' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Elena' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Santa María
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Santa María' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa María' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Tecapán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Tecapán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Tecapán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Alegría
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Alegría' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Alegría' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Berlín
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Berlín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Berlín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::El Triunfo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::El Triunfo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Triunfo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Estanzuelas
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Estanzuelas' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Estanzuelas' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Jucuapa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Jucuapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Jucuapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Mercedes Umaña
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Mercedes Umaña' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Mercedes Umaña' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Nueva Granada
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Nueva Granada' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nueva Granada' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::San Buenaventura
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::San Buenaventura' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Buenaventura' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Santiago de María
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Santiago de María' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santiago de María' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Jiquilisco
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Jiquilisco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Jiquilisco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Puerto El Triunfo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Puerto El Triunfo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Puerto El Triunfo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::San Agustín
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::San Agustín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Agustín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::San Francisco Javier
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::San Francisco Javier' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Francisco Javier' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Usulután::Usulután - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Usulután::Usulután - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Usulután - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [2] DEPARTAMENTO: Oriental::San Miguel
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Miguel' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Oriental::San Miguel::Comacarán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Comacarán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Comacarán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Moncagua
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Moncagua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Moncagua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Chirilagua
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Chirilagua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Chirilagua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Quelepa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Quelepa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Quelepa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::San Miguel
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::San Miguel' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Miguel' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Uluazapa
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Uluazapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Uluazapa' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Carolina
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Carolina' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Carolina' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Ciudad Barrios
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Ciudad Barrios' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Ciudad Barrios' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Chapeltique
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Chapeltique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Chapeltique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Nuevo Edén de San Juan
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Nuevo Edén de San Juan' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nuevo Edén de San Juan' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::San Antonio del Mosco
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::San Antonio del Mosco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Antonio del Mosco' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::San Gerardo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::San Gerardo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Gerardo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::San Luis de La Reina
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::San Luis de La Reina' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Luis de La Reina' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Sesori
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Sesori' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Sesori' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Chinameca
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Chinameca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Chinameca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::El Tránsito
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::El Tránsito' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Tránsito' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Lolotique
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Lolotique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Lolotique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::Nueva Guadalupe
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::Nueva Guadalupe' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nueva Guadalupe' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::San Jorge
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::San Jorge' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Jorge' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::San Rafael Oriente
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::San Rafael Oriente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Rafael Oriente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::San Miguel::San Miguel - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::San Miguel::San Miguel - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Miguel - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [2] DEPARTAMENTO: Oriental::Morazán
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Morazán' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Oriental::Morazán::Arambala
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Arambala' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Arambala' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Cacaopera
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Cacaopera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Cacaopera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Corinto
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Corinto' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Corinto' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::El Rosario
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::El Rosario' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Rosario' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Joateca
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Joateca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Joateca' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Jocoaitique
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Jocoaitique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Jocoaitique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Meanguera
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Meanguera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Meanguera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Perquín
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Perquín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Perquín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::San Fernando
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::San Fernando' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Fernando' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::San Isidro
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::San Isidro' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Isidro' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Torola
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Torola' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Torola' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Chilanga
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Chilanga' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Chilanga' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Delicias de Concepción
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Delicias de Concepción' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Delicias de Concepción' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::El Divisadero
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::El Divisadero' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Divisadero' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Gualococti
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Gualococti' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Gualococti' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Guatajiagua
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Guatajiagua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Guatajiagua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Jocoro
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Jocoro' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Jocoro' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Lolotiquillo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Lolotiquillo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Lolotiquillo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Osicala
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Osicala' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Osicala' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::San Carlos
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::San Carlos' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Carlos' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::San Francisco Gotera
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::San Francisco Gotera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Francisco Gotera' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::San Simón
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::San Simón' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Simón' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Sensembra
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Sensembra' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Sensembra' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Sociedad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Sociedad' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Sociedad' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Yamabal
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Yamabal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Yamabal' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Yoloaiquín
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Yoloaiquín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Yoloaiquín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::Morazán::Morazán - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::Morazán::Morazán - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Morazán - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [2] DEPARTAMENTO: Oriental::La Unión
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 1), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 2;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 2)) WHERE nivel = 2;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'La Unión' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 2 AND f.alias = 'nombre';

-- [3] DISTRITO: Oriental::La Unión::Anamorós
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Anamorós' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Anamorós' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Bolívar
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Bolívar' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Bolívar' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Concepción de Oriente
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Concepción de Oriente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Concepción de Oriente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::El Sauce
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::El Sauce' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Sauce' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Lislique
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Lislique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Lislique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Nueva Esparta
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Nueva Esparta' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nueva Esparta' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Pasaquina
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Pasaquina' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Pasaquina' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Polorós
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Polorós' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Polorós' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::San José La Fuente
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::San José La Fuente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San José La Fuente' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Santa Rosa de Lima
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Santa Rosa de Lima' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Santa Rosa de Lima' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Conchagua
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Conchagua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Conchagua' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::El Carmen
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::El Carmen' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'El Carmen' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Intipucá
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Intipucá' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Intipucá' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::La Unión
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::La Unión' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'La Unión' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Meanguera del Golfo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Meanguera del Golfo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Meanguera del Golfo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::San Alejo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::San Alejo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'San Alejo' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Yayantique
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Yayantique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Yayantique' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::Yucuaiquín
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::Yucuaiquín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Yucuaiquín' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'NO' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [3] DISTRITO: Oriental::La Unión::La Unión - Nivel departamental
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, (SELECT record_id FROM tmp_ultimo WHERE nivel = 2), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 3;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 3)) WHERE nivel = 3;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Oriental::La Unión::La Unión - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'La Unión - Nivel departamental' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'SI' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 3 AND f.alias = 'marcador';

-- [1] REGION: Nivel nacional
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT k.catalog_id, CAST(NULL AS BIGINT), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM tmp_catalogo k WHERE k.nivel = 1;
UPDATE tmp_ultimo SET record_id = (SELECT MAX(r.id) FROM catalog_record r WHERE r.catalog_id = (SELECT k.catalog_id FROM tmp_catalogo k WHERE k.nivel = 1)) WHERE nivel = 1;
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nivel nacional' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 1 AND f.alias = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT u.record_id, f.field_id, 'Nivel nacional' FROM tmp_ultimo u JOIN tmp_campo f ON f.nivel = u.nivel WHERE u.nivel = 1 AND f.alias = 'nombre';

-- ---------------------------------------------------------------------
-- 3. Verificación (valores esperados indicados en cada consulta)
-- ---------------------------------------------------------------------

-- Registros por catálogo: REGION=4, DEPARTAMENTO=14, DISTRITO=275
SELECT c.code, COUNT(*) AS registros
FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id
WHERE c.code IN ('REGION', 'DEPARTAMENTO', 'DISTRITO')
GROUP BY c.code;

-- Valores por catálogo: REGION=8, DEPARTAMENTO=28, DISTRITO=825
SELECT c.code, COUNT(*) AS valores
FROM catalog_record_value v
JOIN catalog_record r ON r.id = v.record_id
JOIN catalog c        ON c.id = r.catalog_id
WHERE c.code IN ('REGION', 'DEPARTAMENTO', 'DISTRITO')
GROUP BY c.code;

-- Distritos marcados 'SI' como nivel departamental: esperado 13
SELECT COUNT(*) AS marcados_si
FROM catalog_record_value v JOIN tmp_campo f ON f.field_id = v.field_id
WHERE f.nivel = 3 AND f.alias = 'marcador' AND v.valor = 'SI';

-- Integridad jerárquica: esperado 0 filas
SELECT r.id, c.code
FROM catalog_record r
JOIN catalog c ON c.id = r.catalog_id
LEFT JOIN catalog_record p ON p.id = r.parent_record_id
LEFT JOIN catalog pc ON pc.id = p.catalog_id
WHERE (c.code = 'REGION'       AND r.parent_record_id IS NOT NULL)
   OR (c.code = 'DEPARTAMENTO' AND (pc.code IS NULL OR pc.code <> 'REGION'))
   OR (c.code = 'DISTRITO'     AND (pc.code IS NULL OR pc.code <> 'DEPARTAMENTO'));

-- ---------------------------------------------------------------------
-- 4. Limpieza
-- ---------------------------------------------------------------------
DROP TABLE tmp_ultimo;
DROP TABLE tmp_campo;
DROP TABLE tmp_catalogo;
