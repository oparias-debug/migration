-- =====================================================================
-- Inicialización de catalog_record y catalog_record_value
-- Jerarquía RESULTADO > INDICADOR_RESULTADO
-- Origen : catalogos__inicializacion.json, grupo code = 'RESULTADO_INDICADOR'
--          3 items: 1 resultados (nivel 1), 2 indicadores (nivel 2)
-- Dialecto: SQL ANSI (SQL:2003)
-- ---------------------------------------------------------------------
-- Prerrequisitos
--   * catalog contiene los code 'RESULTADO', 'INDICADOR_RESULTADO'
--     (INDICADOR_RESULTADO.parent_id -> RESULTADO; Regla 23).
--   * catalog_field define name = 'codigo' y name = 'nombre' en cada uno,
--     y además name = 'unidad_medida' y name = 'descripcion' en INDICADOR_RESULTADO.
--
-- Mecánica ("id más recientemente generado")
--   SQL ANSI no tiene una función portable para leer el último id generado.
--   El script la sustituye con subconsultas escalares:
--     * registro recién creado de un catálogo X:
--         (SELECT MAX(r.id) FROM catalog_record r
--           WHERE r.catalog_id = <id de X> AND r.id > <MAX previo a la carga>)
--   Como cada item se inserta con su propia sentencia y en el orden del JSON:
--     * el padre de un INDICADOR_RESULTADO es el último RESULTADO insertado;
--     * los valores de cada item se asignan al registro recién creado:
--       'codigo' y 'nombre' en ambos niveles; en los indicadores, además
--       'unidad_medida' (atributos_extra.unidad_medida) y 'descripcion'
--       (atributos_extra.descripcion), con el valor literal del JSON.
--   Condición: una sola sesión, sin inserciones concurrentes en
--   catalog_record. Se recomienda ejecutarlo dentro de una única transacción.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Paso 1. Tablas temporales
--   tmp_ri_control : MAX(id) de catalog_record antes de la carga.
--   tmp_ri_item    : copia de los items del JSON, usada solo para
--                   verificar la jerarquía al final (paso 5).
-- ---------------------------------------------------------------------
CREATE LOCAL TEMPORARY TABLE tmp_ri_control (
    max_record_id_previo BIGINT NOT NULL
) ON COMMIT PRESERVE ROWS;

CREATE LOCAL TEMPORARY TABLE tmp_ri_item (
    orden          INTEGER       NOT NULL PRIMARY KEY,
    nivel          INTEGER       NOT NULL,
    codigo         VARCHAR(4000) NOT NULL,
    nombre         VARCHAR(4000) NOT NULL,
    padre_codigo   VARCHAR(4000)
) ON COMMIT PRESERVE ROWS;

INSERT INTO tmp_ri_item (orden, nivel, codigo, nombre, padre_codigo) VALUES
    (1, 1, 'R2200000', 'Mejoras en diversos temas', NULL),
    (2, 2, 'R2201101', 'Tiempo de viaje disminuído en horas', 'R2200000'),
    (3, 2, 'R2201102', 'Nombre no especificado', 'R2200000');

-- ---------------------------------------------------------------------
-- Paso 2. Verificación previa — DEBE DEVOLVER 0 FILAS antes de continuar
-- ---------------------------------------------------------------------
SELECT x.code, x.problema
FROM (
    SELECT e.code,
           CASE WHEN c.id  IS NULL THEN 'catálogo inexistente'
                WHEN fc.id IS NULL THEN 'falta campo codigo'
                WHEN fn.id IS NULL THEN 'falta campo nombre'
                WHEN e.code = 'INDICADOR_RESULTADO' AND fu.id IS NULL THEN 'falta campo unidad_medida'
                WHEN e.code = 'INDICADOR_RESULTADO' AND fp.id IS NULL THEN 'falta campo descripcion'
                WHEN e.code = 'INDICADOR_RESULTADO'
                 AND (c.parent_id IS NULL OR c.parent_id <> (SELECT id FROM catalog WHERE code = 'RESULTADO'))
                     THEN 'parent_id no apunta a RESULTADO (Regla 23)'
           END AS problema
    FROM (VALUES ('RESULTADO'), ('INDICADOR_RESULTADO')) AS e (code)
    LEFT JOIN catalog c        ON c.code = e.code
    LEFT JOIN catalog_field fc ON fc.catalog_id = c.id AND fc.name = 'codigo'
    LEFT JOIN catalog_field fn ON fn.catalog_id = c.id AND fn.name = 'nombre'
    LEFT JOIN catalog_field fu ON fu.catalog_id = c.id AND fu.name = 'unidad_medida'
    LEFT JOIN catalog_field fp ON fp.catalog_id = c.id AND fp.name = 'descripcion'
) x
WHERE x.problema IS NOT NULL;

-- ---------------------------------------------------------------------
-- Paso 3. Punto de partida de la identidad
-- ---------------------------------------------------------------------
INSERT INTO tmp_ri_control (max_record_id_previo)
SELECT COALESCE(MAX(id), 0) FROM catalog_record;

-- ---------------------------------------------------------------------
-- Paso 4. Carga jerárquica, item por item, en el orden del JSON
-- ---------------------------------------------------------------------

-- =============================== RESULTADO: Mejoras en diversos temas
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'RESULTADO';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), f.id, 'R2200000' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'RESULTADO' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), f.id, 'Mejoras en diversos temas' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'RESULTADO' AND f.name = 'nombre';
-- INDICADOR_RESULTADO: Tiempo de viaje disminuído en horas
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INDICADOR_RESULTADO';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), f.id, 'R2201101' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), f.id, 'Tiempo de viaje disminuído en horas' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND f.name = 'nombre';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), f.id, 'horas' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND f.name = 'unidad_medida';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), f.id, 'Reducción del tiempo de traslado de un vehículo a través de la carretera (con proyecto), comparado con el tiempo promedio anterior (sin proyecto)' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND f.name = 'descripcion';
-- INDICADOR_RESULTADO: Nombre no especificado
INSERT INTO catalog_record (catalog_id, parent_record_id, active, from_date, to_date) SELECT c.id, (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31' FROM catalog c WHERE c.code = 'INDICADOR_RESULTADO';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), f.id, 'R2201102' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND f.name = 'codigo';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), f.id, 'Nombre no especificado' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND f.name = 'nombre';
-- ATENCIÓN: item marcado en el JSON como dato_incompleto_en_documento_fuente; se cargan los valores de relleno tal cual.
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), f.id, 'No especificada.' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND f.name = 'unidad_medida';
INSERT INTO catalog_record_value (record_id, field_id, valor) SELECT (SELECT MAX(r.id) FROM catalog_record r JOIN catalog c ON c.id = r.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)), f.id, 'No especificada.' FROM catalog_field f JOIN catalog c ON c.id = f.catalog_id WHERE c.code = 'INDICADOR_RESULTADO' AND f.name = 'descripcion';

-- ---------------------------------------------------------------------
-- Paso 5. Verificación posterior
-- ---------------------------------------------------------------------
-- 5.1 Conteos. Esperado: RESULTADO 1/2, INDICADOR_RESULTADO 2/8
SELECT c.code,
       COUNT(DISTINCT r.id) AS registros,
       COUNT(v.id)          AS valores
FROM catalog c
JOIN catalog_record r            ON r.catalog_id = c.id
LEFT JOIN catalog_record_value v ON v.record_id = r.id
WHERE c.code IN ('RESULTADO', 'INDICADOR_RESULTADO')
  AND r.id > (SELECT max_record_id_previo FROM tmp_ri_control)
GROUP BY c.code;

-- 5.2 Jerarquía: el 'codigo' del registro padre debe coincidir con el
--     item_padre_codigo del JSON. DEBE DEVOLVER 0 FILAS.
SELECT t.orden, t.codigo, t.padre_codigo AS padre_esperado, vp.valor AS padre_cargado
FROM tmp_ri_item t
JOIN catalog_record_value vh ON vh.valor = t.codigo
JOIN catalog_field fh        ON fh.id = vh.field_id AND fh.name = 'codigo'
JOIN catalog ch              ON ch.id = fh.catalog_id
                            AND ch.code IN ('RESULTADO', 'INDICADOR_RESULTADO')
JOIN catalog_record rh       ON rh.id = vh.record_id
                            AND rh.id > (SELECT max_record_id_previo FROM tmp_ri_control)
LEFT JOIN catalog_record_value vp ON vp.record_id = rh.parent_record_id
LEFT JOIN catalog_field fp        ON fp.id = vp.field_id
WHERE (fp.name = 'codigo' OR fp.id IS NULL)
  AND ( (t.padre_codigo IS NULL AND rh.parent_record_id IS NOT NULL)
     OR (t.padre_codigo IS NOT NULL AND (vp.valor IS NULL OR vp.valor <> t.padre_codigo)) );

-- ---------------------------------------------------------------------
-- Paso 6. Limpieza
-- ---------------------------------------------------------------------
DROP TABLE tmp_ri_item;
DROP TABLE tmp_ri_control;
