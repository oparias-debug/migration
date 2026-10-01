-- traslado-catalogos.sql
-- Autor: equipo SIIP
-- Fecha: 2026-09-30
-- Descripción: traslada la carga inicial de catálogos al esquema de admin-srv (CU-ADM-01).
--   Los scripts de "sql/inicializacion catalogos" se ejecutan tal cual sobre el esquema de trabajo
--   carga_catalogos (tablas catalog, catalog_field, catalog_record, catalog_record_value); este
--   script los pasa a catalogo, campo_definicion, registro_catalogo y registro_valor con los ids de
--   sus secuencias. Lo ejecuta postgresql/init-catalogos.sh, que después borra carga_catalogos.
--
--   Reglas del traslado (cualquier caso fuera de ellas detiene la carga con un error):
--   1. Copias de un mismo registro. Un registro sin registro padre en un catálogo que sí tiene
--      padre (Regla 23) se descarta si su clave ya existe en el mismo catálogo con registro padre
--      o en un catálogo ancestro (fue cargado de más por "catálogos planos"). En un catálogo, dos
--      registros con la misma clave y el mismo registro padre son copias: se conserva el de más
--      valores (a igualdad, el cargado después) y los hijos del descartado pasan al conservado.
--   2. Claves únicas solo dentro de su padre. Si después de lo anterior un catálogo repite
--      claves, la de cada registro pasa a ser "<clave del registro padre>-<clave>", también en el
--      valor de su campo KEY, porque admin-srv exige la clave única por catálogo (Regla 4).
--   3. Todo campo se define como STRING: la carga no declara tipos.

\set ON_ERROR_STOP on

BEGIN;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM catalogo) THEN
        RAISE EXCEPTION 'catalogo ya tiene datos: el traslado solo corre sobre una base vacía';
    END IF;
END $$;

-- ---------- Registros de trabajo, con su clave (valor del primer campo KEY) ----------

CREATE TEMP TABLE t_campo_key ON COMMIT DROP AS
SELECT DISTINCT ON (f.catalog_id) f.catalog_id, f.id AS field_id
  FROM carga_catalogos.catalog_field f
 WHERE f.qualifier = 'KEY'
 ORDER BY f.catalog_id, f.posicion, f.id;

CREATE TEMP TABLE t_registro ON COMMIT DROP AS
SELECT r.id, r.catalog_id, c.parent_id AS catalogo_padre_id, r.parent_record_id, r.active, r.from_date, r.to_date,
       v.valor AS clave,
       (SELECT count(*) FROM carga_catalogos.catalog_record_value x WHERE x.record_id = r.id) AS n_valores
  FROM carga_catalogos.catalog_record r
  JOIN carga_catalogos.catalog c ON c.id = r.catalog_id
  LEFT JOIN t_campo_key k ON k.catalog_id = r.catalog_id
  LEFT JOIN carga_catalogos.catalog_record_value v ON v.record_id = r.id AND v.field_id = k.field_id;

DO $$
DECLARE
    detalle TEXT;
BEGIN
    SELECT string_agg(DISTINCT c.code, ', ') INTO detalle
      FROM t_registro t JOIN carga_catalogos.catalog c ON c.id = t.catalog_id WHERE t.clave IS NULL;
    IF detalle IS NOT NULL THEN
        RAISE EXCEPTION 'Registros sin campo KEY o sin valor KEY en: %', detalle;
    END IF;
END $$;

-- ---------- Regla 1: copias ----------

-- Descartes y, cuando existe, el registro que los reemplaza como padre de sus hijos.
CREATE TEMP TABLE t_descarte (id BIGINT PRIMARY KEY, reemplazo BIGINT) ON COMMIT DROP;

-- Sin registro padre en un catálogo con padre, pero con su clave en el mismo catálogo con padre.
INSERT INTO t_descarte (id, reemplazo)
SELECT a.id, (SELECT min(b.id) FROM t_registro b
               WHERE b.catalog_id = a.catalog_id AND b.clave = a.clave AND b.parent_record_id IS NOT NULL)
  FROM t_registro a
 WHERE a.catalogo_padre_id IS NOT NULL AND a.parent_record_id IS NULL
   AND EXISTS (SELECT 1 FROM t_registro b
                WHERE b.catalog_id = a.catalog_id AND b.clave = a.clave AND b.parent_record_id IS NOT NULL);

-- Sin registro padre en un catálogo con padre, con su clave en un catálogo ancestro (catálogo equivocado).
INSERT INTO t_descarte (id, reemplazo)
WITH RECURSIVE ancestro (catalog_id, ancestro_id) AS (
    SELECT c.id, c.parent_id FROM carga_catalogos.catalog c WHERE c.parent_id IS NOT NULL
    UNION
    SELECT a.catalog_id, c.parent_id FROM ancestro a
      JOIN carga_catalogos.catalog c ON c.id = a.ancestro_id WHERE c.parent_id IS NOT NULL
)
SELECT a.id, NULL
  FROM t_registro a
 WHERE a.catalogo_padre_id IS NOT NULL AND a.parent_record_id IS NULL
   AND a.id NOT IN (SELECT id FROM t_descarte)
   AND EXISTS (SELECT 1 FROM t_registro p JOIN ancestro an ON an.ancestro_id = p.catalog_id
                WHERE an.catalog_id = a.catalog_id AND p.clave = a.clave);

DELETE FROM t_registro WHERE id IN (SELECT id FROM t_descarte);

-- Misma clave y mismo registro padre: se conserva el de más valores y, a igualdad, el último cargado.
INSERT INTO t_descarte (id, reemplazo)
SELECT a.id, (SELECT b.id FROM t_registro b
               WHERE b.catalog_id = a.catalog_id AND b.clave = a.clave
                 AND b.parent_record_id IS NOT DISTINCT FROM a.parent_record_id
               ORDER BY b.n_valores DESC, b.id DESC LIMIT 1)
  FROM t_registro a
 WHERE EXISTS (SELECT 1 FROM t_registro b
                WHERE b.catalog_id = a.catalog_id AND b.clave = a.clave AND b.id <> a.id
                  AND b.parent_record_id IS NOT DISTINCT FROM a.parent_record_id
                  AND (b.n_valores > a.n_valores OR (b.n_valores = a.n_valores AND b.id > a.id)));

DELETE FROM t_registro WHERE id IN (SELECT id FROM t_descarte);

-- Los hijos de un descartado pasan a su reemplazo; un descarte sin reemplazo no puede tener hijos.
UPDATE t_registro t SET parent_record_id = d.reemplazo
  FROM t_descarte d WHERE t.parent_record_id = d.id AND d.reemplazo IS NOT NULL;

DO $$
DECLARE
    detalle TEXT;
BEGIN
    SELECT string_agg(DISTINCT c.code, ', ') INTO detalle
      FROM t_registro t JOIN carga_catalogos.catalog c ON c.id = t.catalog_id
     WHERE t.parent_record_id IN (SELECT id FROM t_descarte);
    IF detalle IS NOT NULL THEN
        RAISE EXCEPTION 'Registros enlazados a un registro descartado en: %', detalle;
    END IF;
    SELECT string_agg(DISTINCT c.code, ', ') INTO detalle
      FROM t_registro t JOIN carga_catalogos.catalog c ON c.id = t.catalog_id
     WHERE t.catalogo_padre_id IS NOT NULL AND t.parent_record_id IS NULL;
    IF detalle IS NOT NULL THEN
        RAISE EXCEPTION 'Registros sin registro padre en catálogos con padre (Regla 23): %', detalle;
    END IF;
END $$;

-- ---------- Regla 2: claves únicas solo dentro de su padre ----------

CREATE TEMP TABLE t_catalogo_compuesto ON COMMIT DROP AS
SELECT DISTINCT catalog_id FROM t_registro GROUP BY catalog_id, clave HAVING count(*) > 1;

UPDATE t_registro t SET clave = p.clave || '-' || t.clave
  FROM t_registro p
 WHERE p.id = t.parent_record_id AND t.catalog_id IN (SELECT catalog_id FROM t_catalogo_compuesto);

DO $$
DECLARE
    detalle TEXT;
BEGIN
    SELECT string_agg(DISTINCT c.code || ' (' || t.clave || ')', ', ') INTO detalle
      FROM (SELECT catalog_id, clave FROM t_registro GROUP BY catalog_id, clave HAVING count(*) > 1) t
      JOIN carga_catalogos.catalog c ON c.id = t.catalog_id;
    IF detalle IS NOT NULL THEN
        RAISE EXCEPTION 'Claves repetidas que no se resuelven con la clave del padre: %', detalle;
    END IF;
END $$;

-- ---------- Catálogos y campos ----------

CREATE TEMP TABLE t_mapa_catalogo ON COMMIT DROP AS
SELECT c.id AS origen, nextval('catalogo_seq') AS destino FROM carga_catalogos.catalog c ORDER BY c.id;

INSERT INTO catalogo (id_catalogo, codigo, nombre, id_catalogo_padre, estado, fecha_desde, fecha_hasta)
SELECT m.destino, c.code, c.name, NULL, c.active, c.from_date, c.to_date
  FROM carga_catalogos.catalog c JOIN t_mapa_catalogo m ON m.origen = c.id;

UPDATE catalogo d SET id_catalogo_padre = mp.destino
  FROM carga_catalogos.catalog c
  JOIN t_mapa_catalogo m ON m.origen = c.id
  JOIN t_mapa_catalogo mp ON mp.origen = c.parent_id
 WHERE d.id_catalogo = m.destino;

INSERT INTO campo_definicion (id_campo_definicion, id_catalogo, nombre, tipo, es_key, posicion)
SELECT nextval('campo_definicion_seq'), m.destino, f.name, 'STRING', f.qualifier = 'KEY', f.posicion
  FROM carga_catalogos.catalog_field f JOIN t_mapa_catalogo m ON m.origen = f.catalog_id
 ORDER BY f.catalog_id, f.posicion, f.id;

-- ---------- Registros y sus valores ----------

CREATE TEMP TABLE t_mapa_registro ON COMMIT DROP AS
SELECT t.id AS origen, nextval('registro_catalogo_seq') AS destino FROM t_registro t ORDER BY t.id;

INSERT INTO registro_catalogo (id_registro, id_catalogo, clave, id_registro_padre, estado, fecha_desde, fecha_hasta)
SELECT mr.destino, mc.destino, t.clave, NULL, t.active, t.from_date, t.to_date
  FROM t_registro t
  JOIN t_mapa_registro mr ON mr.origen = t.id
  JOIN t_mapa_catalogo mc ON mc.origen = t.catalog_id;

UPDATE registro_catalogo d SET id_registro_padre = mp.destino
  FROM t_registro t
  JOIN t_mapa_registro m ON m.origen = t.id
  JOIN t_mapa_registro mp ON mp.origen = t.parent_record_id
 WHERE d.id_registro = m.destino;

-- El valor del campo KEY es la clave (compuesta, si la Regla 2 la cambió).
INSERT INTO registro_valor (id_registro, nombre_campo, valor)
SELECT mr.destino, f.name, CASE WHEN f.id = k.field_id THEN t.clave ELSE v.valor END
  FROM t_registro t
  JOIN t_mapa_registro mr ON mr.origen = t.id
  JOIN carga_catalogos.catalog_record_value v ON v.record_id = t.id
  JOIN carga_catalogos.catalog_field f ON f.id = v.field_id
  LEFT JOIN t_campo_key k ON k.catalog_id = t.catalog_id;

-- ---------- Resumen ----------

SELECT (SELECT count(*) FROM carga_catalogos.catalog_record) AS registros_cargados,
       (SELECT count(*) FROM t_descarte) AS copias_descartadas,
       (SELECT count(*) FROM registro_catalogo) AS registros_trasladados,
       (SELECT string_agg(c.code, ', ' ORDER BY c.code) FROM t_catalogo_compuesto x
          JOIN carga_catalogos.catalog c ON c.id = x.catalog_id) AS catalogos_con_clave_compuesta;

COMMIT;
