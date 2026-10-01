-- =====================================================================
-- Inicialización de catalog_record y catalog_record_value
-- Jerarquía TIPO_UNIDAD_MEDIDA > CATEGORIA_UNIDAD_MEDIDA > UNIDAD_MEDIDA
-- Origen : catalogos__inicializacion.json, grupo code = 'UNIDAD_MEDIDA'
--          51 items: 3 tipos (nivel 1), 9 categorías (nivel 2), 39 unidades (nivel 3)
-- Dialecto: SQL ANSI (SQL:2003)
-- ---------------------------------------------------------------------
-- Prerrequisitos
--   * catalog contiene los code 'TIPO_UNIDAD_MEDIDA', 'CATEGORIA_UNIDAD_MEDIDA', 'UNIDAD_MEDIDA', encadenados por parent_id
--     (CATEGORIA_UNIDAD_MEDIDA -> TIPO_UNIDAD_MEDIDA,
--      UNIDAD_MEDIDA -> CATEGORIA_UNIDAD_MEDIDA; Regla 23).
--   * catalog_field define name = 'codigo' y name = 'nombre' en cada uno,
--     y además name = 'descripcion' en UNIDAD_MEDIDA.
--
-- Mecánica ("id más recientemente generado")
--   SQL ANSI no tiene una función portable para leer el último id generado.
--   El script la sustituye con subconsultas escalares:
--     * registro recién creado de un catálogo X:
--         (SELECT MAX(r.id) FROM catalog_record r
--           WHERE r.catalog_id = <id de X> AND r.id > <MAX previo a la carga>)
--   Como cada item se inserta con su propia sentencia y en el orden del JSON:
--     * el padre de una CATEGORIA es el último TIPO insertado;
--     * el padre de una UNIDAD es la última CATEGORIA insertada;
--     * los valores de cada item se asignan al registro recién creado:
--       'codigo' y 'nombre' en los tres niveles, y además 'descripcion'
--       (de atributos_extra.descripcion del JSON) en las unidades de nivel 3.
--   Condición: una sola sesión, sin inserciones concurrentes en
--   catalog_record. Se recomienda ejecutarlo dentro de una única transacción.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Paso 1. Tablas temporales
--   tmp_um_control : MAX(id) de catalog_record antes de la carga.
--   tmp_um_item    : copia de los items del JSON, usada solo para
--                   verificar la jerarquía al final (paso 5).
-- ---------------------------------------------------------------------
CREATE LOCAL TEMPORARY TABLE tmp_um_control (
    max_record_id_previo BIGINT NOT NULL
) ON COMMIT PRESERVE ROWS;

CREATE LOCAL TEMPORARY TABLE tmp_um_item (
    orden          INTEGER       NOT NULL PRIMARY KEY,
    nivel          INTEGER       NOT NULL,
    codigo         VARCHAR(4000) NOT NULL,
    nombre         VARCHAR(4000) NOT NULL,
    padre_codigo   VARCHAR(4000)
) ON COMMIT PRESERVE ROWS;

INSERT INTO tmp_um_item (orden, nivel, codigo, nombre, padre_codigo) VALUES
    (1, 1, 'Bien', 'Bien', NULL),
    (2, 2, 'Bien::Conteo', 'Conteo', 'Bien'),
    (3, 3, 'Bien::Conteo::Unidad (u)', 'Unidad (u)', 'Bien::Conteo'),
    (4, 3, 'Bien::Conteo::Pieza', 'Pieza', 'Bien::Conteo'),
    (5, 3, 'Bien::Conteo::Artículo', 'Artículo', 'Bien::Conteo'),
    (6, 3, 'Bien::Conteo::Ítem', 'Ítem', 'Bien::Conteo'),
    (7, 3, 'Bien::Conteo::Lote', 'Lote', 'Bien::Conteo'),
    (8, 3, 'Bien::Conteo::Paquete', 'Paquete', 'Bien::Conteo'),
    (9, 3, 'Bien::Conteo::Caja', 'Caja', 'Bien::Conteo'),
    (10, 3, 'Bien::Conteo::Docena', 'Docena', 'Bien::Conteo'),
    (11, 3, 'Bien::Conteo::Juego - Set', 'Juego - Set', 'Bien::Conteo'),
    (12, 3, 'Bien::Conteo::Kit', 'Kit', 'Bien::Conteo'),
    (13, 2, 'Bien::Peso', 'Peso', 'Bien'),
    (14, 3, 'Bien::Peso::Gramo (g)', 'Gramo (g)', 'Bien::Peso'),
    (15, 3, 'Bien::Peso::Kilogramo (kg)', 'Kilogramo (kg)', 'Bien::Peso'),
    (16, 3, 'Bien::Peso::Quintal', 'Quintal', 'Bien::Peso'),
    (17, 3, 'Bien::Peso::Tonelada (t)', 'Tonelada (t)', 'Bien::Peso'),
    (18, 3, 'Bien::Peso::Libra (lb)', 'Libra (lb)', 'Bien::Peso'),
    (19, 2, 'Bien::Longitud', 'Longitud', 'Bien'),
    (20, 3, 'Bien::Longitud::Milímetro (mm)', 'Milímetro (mm)', 'Bien::Longitud'),
    (21, 3, 'Bien::Longitud::Centímetro (cm)', 'Centímetro (cm)', 'Bien::Longitud'),
    (22, 3, 'Bien::Longitud::Metro (m)', 'Metro (m)', 'Bien::Longitud'),
    (23, 3, 'Bien::Longitud::Kilómetro (km)', 'Kilómetro (km)', 'Bien::Longitud'),
    (24, 2, 'Bien::Superficie', 'Superficie', 'Bien'),
    (25, 3, 'Bien::Superficie::Metro cuadrado (m²)', 'Metro cuadrado (m²)', 'Bien::Superficie'),
    (26, 3, 'Bien::Superficie::Hectárea (ha)', 'Hectárea (ha)', 'Bien::Superficie'),
    (27, 3, 'Bien::Superficie::Manzana', 'Manzana', 'Bien::Superficie'),
    (28, 2, 'Bien::Volumen', 'Volumen', 'Bien'),
    (29, 3, 'Bien::Volumen::Mililitro (ml)', 'Mililitro (ml)', 'Bien::Volumen'),
    (30, 3, 'Bien::Volumen::Litro (l)', 'Litro (l)', 'Bien::Volumen'),
    (31, 3, 'Bien::Volumen::Metro cúbico (m³)', 'Metro cúbico (m³)', 'Bien::Volumen'),
    (32, 3, 'Bien::Volumen::Galón', 'Galón', 'Bien::Volumen'),
    (33, 2, 'Bien::Capacidad', 'Capacidad', 'Bien'),
    (34, 3, 'Bien::Capacidad::Watt (W)', 'Watt (W)', 'Bien::Capacidad'),
    (35, 3, 'Bien::Capacidad::Kilowatt (kW)', 'Kilowatt (kW)', 'Bien::Capacidad'),
    (36, 3, 'Bien::Capacidad::Kilovoltio-amperio (kVA)', 'Kilovoltio-amperio (kVA)', 'Bien::Capacidad'),
    (37, 1, 'Servicio', 'Servicio', NULL),
    (38, 2, 'Servicio::Tiempo', 'Tiempo', 'Servicio'),
    (39, 3, 'Servicio::Tiempo::Hora', 'Hora', 'Servicio::Tiempo'),
    (40, 3, 'Servicio::Tiempo::Día', 'Día', 'Servicio::Tiempo'),
    (41, 3, 'Servicio::Tiempo::Mes', 'Mes', 'Servicio::Tiempo'),
    (42, 3, 'Servicio::Tiempo::Año', 'Año', 'Servicio::Tiempo'),
    (43, 2, 'Servicio::Proporción', 'Proporción', 'Servicio'),
    (44, 3, 'Servicio::Proporción::Porcentaje', 'Porcentaje', 'Servicio::Proporción'),
    (45, 1, 'Mixta', 'Mixta', NULL),
    (46, 2, 'Mixta::Compuesta', 'Compuesta', 'Mixta'),
    (47, 3, 'Mixta::Compuesta::Usuario - mes', 'Usuario - mes', 'Mixta::Compuesta'),
    (48, 3, 'Mixta::Compuesta::Atención - día', 'Atención - día', 'Mixta::Compuesta'),
    (49, 3, 'Mixta::Compuesta::Servicio - año', 'Servicio - año', 'Mixta::Compuesta'),
    (50, 3, 'Mixta::Compuesta::Tonelada - mes', 'Tonelada - mes', 'Mixta::Compuesta'),
    (51, 3, 'Mixta::Compuesta::m² - año', 'm² - año', 'Mixta::Compuesta');

-- ---------------------------------------------------------------------
-- Paso 2. Verificación previa — DEBE DEVOLVER 0 FILAS antes de continuar
-- ---------------------------------------------------------------------
SELECT x.code, x.problema
FROM (
    SELECT e.code,
           CASE WHEN c.id  IS NULL THEN 'catálogo inexistente'
                WHEN fc.id IS NULL THEN 'falta campo codigo'
                WHEN fn.id IS NULL THEN 'falta campo nombre'
                WHEN e.code = 'UNIDAD_MEDIDA' AND fd.id IS NULL THEN 'falta campo descripcion'
                WHEN e.code = 'CATEGORIA_UNIDAD_MEDIDA'
                 AND (c.parent_id IS NULL OR c.parent_id <> (SELECT id FROM catalog WHERE code = 'TIPO_UNIDAD_MEDIDA'))
                     THEN 'parent_id no apunta a TIPO_UNIDAD_MEDIDA (Regla 23)'
                WHEN e.code = 'UNIDAD_MEDIDA'
                 AND (c.parent_id IS NULL OR c.parent_id <> (SELECT id FROM catalog WHERE code = 'CATEGORIA_UNIDAD_MEDIDA'))
                     THEN 'parent_id no apunta a CATEGORIA_UNIDAD_MEDIDA (Regla 23)'
           END AS problema
    FROM (VALUES ('TIPO_UNIDAD_MEDIDA'), ('CATEGORIA_UNIDAD_MEDIDA'), ('UNIDAD_MEDIDA')) AS e (code)
    LEFT JOIN catalog c        ON c.code = e.code
    LEFT JOIN catalog_field fc ON fc.catalog_id = c.id AND fc.name = 'codigo'
    LEFT JOIN catalog_field fn ON fn.catalog_id = c.id AND fn.name = 'nombre'
    LEFT JOIN catalog_field fd ON fd.catalog_id = c.id AND fd.name = 'descripcion'
) x
WHERE x.problema IS NOT NULL;

-- ---------------------------------------------------------------------
-- Paso 3. Punto de partida de la identidad
-- ---------------------------------------------------------------------
INSERT INTO tmp_um_control (max_record_id_previo)
SELECT COALESCE(MAX(id), 0) FROM catalog_record;

-- ---------------------------------------------------------------------
-- Paso 4. Carga jerárquica, item por item, en el orden del JSON
-- ---------------------------------------------------------------------

-- =============================== TIPO_UNIDAD_MEDIDA: Bien
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND f.name = 'nombre';

-- ---------- CATEGORIA_UNIDAD_MEDIDA: Conteo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Conteo' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Conteo' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'nombre';
-- UNIDAD_MEDIDA: Unidad (u)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Conteo::Unidad (u)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Unidad (u)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Medida básica para bienes individuales' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Pieza
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Conteo::Pieza' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Pieza' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien físico individual' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Artículo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Conteo::Artículo' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Artículo' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien comercializable' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Ítem
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Conteo::Ítem' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Ítem' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Elemento individual de un conjunto' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Lote
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Conteo::Lote' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Lote' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Conjunto de unidades' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Paquete
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Conteo::Paquete' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Paquete' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Agrupación comercial' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Caja
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Conteo::Caja' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Caja' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Contenedor con varias unidades' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Docena
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Conteo::Docena' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Docena' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Conjunto de 12 unidades' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Juego - Set
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Conteo::Juego - Set' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Juego - Set' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Conjunto de piezas relacionadas' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Kit
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Conteo::Kit' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Kit' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Conjunto de insumos o herramientas' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';

-- ---------- CATEGORIA_UNIDAD_MEDIDA: Peso
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Peso' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Peso' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'nombre';
-- UNIDAD_MEDIDA: Gramo (g)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Peso::Gramo (g)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Gramo (g)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Pequeñas cantidades' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Kilogramo (kg)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Peso::Kilogramo (kg)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Kilogramo (kg)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Medida estándar de peso' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Quintal
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Peso::Quintal' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Quintal' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Productos agrícolas' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Tonelada (t)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Peso::Tonelada (t)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Tonelada (t)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Grandes volúmenes' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Libra (lb)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Peso::Libra (lb)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Libra (lb)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Uso comercial' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';

-- ---------- CATEGORIA_UNIDAD_MEDIDA: Longitud
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Longitud' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Longitud' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'nombre';
-- UNIDAD_MEDIDA: Milímetro (mm)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Longitud::Milímetro (mm)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Milímetro (mm)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Componentes pequeños' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Centímetro (cm)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Longitud::Centímetro (cm)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Centímetro (cm)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Medidas menores' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Metro (m)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Longitud::Metro (m)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Metro (m)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Medida estándar' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Kilómetro (km)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Longitud::Kilómetro (km)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Kilómetro (km)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Grandes extensiones' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';

-- ---------- CATEGORIA_UNIDAD_MEDIDA: Superficie
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Superficie' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Superficie' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'nombre';
-- UNIDAD_MEDIDA: Metro cuadrado (m²)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Superficie::Metro cuadrado (m²)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Metro cuadrado (m²)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Construcción y áreas' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Hectárea (ha)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Superficie::Hectárea (ha)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Hectárea (ha)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Terrenos agrícolas' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Manzana
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Superficie::Manzana' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Manzana' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Uso catastral' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';

-- ---------- CATEGORIA_UNIDAD_MEDIDA: Volumen
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Volumen' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Volumen' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'nombre';
-- UNIDAD_MEDIDA: Mililitro (ml)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Volumen::Mililitro (ml)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Mililitro (ml)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Líquidos pequeños' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Litro (l)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Volumen::Litro (l)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Litro (l)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Líquidos comerciales' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Metro cúbico (m³)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Volumen::Metro cúbico (m³)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Metro cúbico (m³)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Obras y materiales' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Galón
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Volumen::Galón' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Galón' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Combustibles y líquidos' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';

-- ---------- CATEGORIA_UNIDAD_MEDIDA: Capacidad
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Capacidad' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Capacidad' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'nombre';
-- UNIDAD_MEDIDA: Watt (W)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Capacidad::Watt (W)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Watt (W)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Potencia eléctrica' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Kilowatt (kW)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Capacidad::Kilowatt (kW)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Kilowatt (kW)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Equipos eléctricos' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Kilovoltio-amperio (kVA)
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Bien::Capacidad::Kilovoltio-amperio (kVA)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Kilovoltio-amperio (kVA)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Capacidad instalada' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';

-- =============================== TIPO_UNIDAD_MEDIDA: Servicio
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicio' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicio' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND f.name = 'nombre';

-- ---------- CATEGORIA_UNIDAD_MEDIDA: Tiempo
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicio::Tiempo' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Tiempo' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'nombre';
-- UNIDAD_MEDIDA: Hora
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicio::Tiempo::Hora' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Hora' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Prestación por tiempo' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Día
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicio::Tiempo::Día' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Día' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicios diarios' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Mes
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicio::Tiempo::Mes' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Mes' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicios periódicos' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Año
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicio::Tiempo::Año' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Año' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Proyección anual' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';

-- ---------- CATEGORIA_UNIDAD_MEDIDA: Proporción
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicio::Proporción' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Proporción' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'nombre';
-- UNIDAD_MEDIDA: Porcentaje
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicio::Proporción::Porcentaje' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Porcentaje' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Proporción, fracción o razón de una cantidad total dividida en 100 partes iguales' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';

-- =============================== TIPO_UNIDAD_MEDIDA: Mixta
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'TIPO_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Mixta' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Mixta' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND f.name = 'nombre';

-- ---------- CATEGORIA_UNIDAD_MEDIDA: Compuesta
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'TIPO_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Mixta::Compuesta' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Compuesta' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND f.name = 'nombre';
-- UNIDAD_MEDIDA: Usuario - mes
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Mixta::Compuesta::Usuario - mes' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Usuario - mes' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Demanda periódica' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Atención - día
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Mixta::Compuesta::Atención - día' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Atención - día' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Capacidad operativa' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Servicio - año
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Mixta::Compuesta::Servicio - año' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Servicio - año' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Proyección anual' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: Tonelada - mes
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Mixta::Compuesta::Tonelada - mes' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Tonelada - mes' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Flujo de bienes' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';
-- UNIDAD_MEDIDA: m² - año
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'CATEGORIA_UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Mixta::Compuesta::m² - año' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'm² - año' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)), f.id, 'Uso de infraestructura' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'UNIDAD_MEDIDA' AND f.name = 'descripcion';

-- ---------------------------------------------------------------------
-- Paso 5. Verificación posterior
-- ---------------------------------------------------------------------
-- 5.1 Conteos. Esperado: TIPO_UNIDAD_MEDIDA 3-6, CATEGORIA_UNIDAD_MEDIDA 9-18, UNIDAD_MEDIDA 39-117
SELECT c.code,
       COUNT(DISTINCT r.id) AS registros,
       COUNT(v.id)          AS valores
FROM catalog c
JOIN catalog_record r            ON r.catalog_id = c.id
LEFT JOIN catalog_record_value v ON v.record_id = r.id
WHERE c.code IN ('TIPO_UNIDAD_MEDIDA', 'CATEGORIA_UNIDAD_MEDIDA', 'UNIDAD_MEDIDA')
  AND r.id > (SELECT max_record_id_previo FROM tmp_um_control)
GROUP BY c.code;

-- 5.2 Jerarquía: el 'codigo' del registro padre debe coincidir con el
--     item_padre_codigo del JSON. DEBE DEVOLVER 0 FILAS.
SELECT t.orden, t.codigo, t.padre_codigo AS padre_esperado, vp.valor AS padre_cargado
FROM tmp_um_item t
JOIN catalog_record_value vh ON vh.valor = t.codigo
JOIN catalog_field fh        ON fh.id = vh.field_id AND fh.name = 'codigo'
JOIN catalog ch              ON ch.id = fh.catalog_id
                            AND ch.code IN ('TIPO_UNIDAD_MEDIDA', 'CATEGORIA_UNIDAD_MEDIDA', 'UNIDAD_MEDIDA')
JOIN catalog_record rh       ON rh.id = vh.record_id
                            AND rh.id > (SELECT max_record_id_previo FROM tmp_um_control)
LEFT JOIN catalog_record_value vp ON vp.record_id = rh.parent_record_id
LEFT JOIN catalog_field fp        ON fp.id = vp.field_id
WHERE (fp.name = 'codigo' OR fp.id IS NULL)
  AND ( (t.padre_codigo IS NULL AND rh.parent_record_id IS NOT NULL)
     OR (t.padre_codigo IS NOT NULL AND (vp.valor IS NULL OR vp.valor <> t.padre_codigo)) );

-- ---------------------------------------------------------------------
-- Paso 6. Limpieza
-- ---------------------------------------------------------------------
DROP TABLE tmp_um_item;
DROP TABLE tmp_um_control;
