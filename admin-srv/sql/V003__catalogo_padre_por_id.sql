-- V003__catalogo_padre_por_id.sql
-- Autor: equipo SIIP
-- Fecha: 2026-09-30
-- Descripción: CU-ADM-01. El catálogo padre deja de referenciarse por código
--   (CATALOGO_PADRE_CODIGO, sin FK) y pasa a enlazarse por id (ID_CATALOGO_PADRE, con FK), como
--   la entidad CatalogoEntity. Traslada los enlaces existentes antes de borrar la columna anterior.
--   Ejecutar conectado como el esquema dueño de las tablas (sin prefijo de esquema).

-- Un código de padre que no corresponde a ningún catálogo no puede enlazarse: la migración se
-- detiene antes de cambiar nada (el DDL de Oracle confirma solo), para corregir el dato y reintentar.
DECLARE
    huerfanos NUMBER;
BEGIN
    SELECT COUNT(*) INTO huerfanos FROM CATALOGO hijo
     WHERE hijo.CATALOGO_PADRE_CODIGO IS NOT NULL
       AND NOT EXISTS (SELECT 1 FROM CATALOGO padre WHERE padre.CODIGO = hijo.CATALOGO_PADRE_CODIGO);
    IF huerfanos > 0 THEN
        RAISE_APPLICATION_ERROR(-20001, huerfanos || ' catálogo(s) con CATALOGO_PADRE_CODIGO inexistente');
    END IF;
END;
/

ALTER TABLE CATALOGO ADD (ID_CATALOGO_PADRE NUMBER(19));

UPDATE CATALOGO hijo
   SET hijo.ID_CATALOGO_PADRE = (SELECT padre.ID_CATALOGO FROM CATALOGO padre
                                  WHERE padre.CODIGO = hijo.CATALOGO_PADRE_CODIGO)
 WHERE hijo.CATALOGO_PADRE_CODIGO IS NOT NULL;

COMMIT;

ALTER TABLE CATALOGO ADD CONSTRAINT FK_CATALOGO_PADRE
    FOREIGN KEY (ID_CATALOGO_PADRE) REFERENCES CATALOGO (ID_CATALOGO);

COMMENT ON COLUMN CATALOGO.ID_CATALOGO_PADRE IS 'Catálogo padre (Regla 15); nulo si no tiene';

DROP INDEX IDX_CATALOGO_PADRE;
ALTER TABLE CATALOGO DROP COLUMN CATALOGO_PADRE_CODIGO;
CREATE INDEX IDX_CATALOGO_PADRE ON CATALOGO (ID_CATALOGO_PADRE);
