-- R003__rollback_catalogo_padre_por_id.sql
-- Autor: equipo SIIP
-- Fecha: 2026-09-30
-- Descripción: revierte V003__catalogo_padre_por_id.sql (CU-ADM-01): vuelve a referenciar el
--   catálogo padre por código, conservando los enlaces.

ALTER TABLE CATALOGO ADD (CATALOGO_PADRE_CODIGO VARCHAR2(100));

UPDATE CATALOGO hijo
   SET hijo.CATALOGO_PADRE_CODIGO = (SELECT padre.CODIGO FROM CATALOGO padre
                                      WHERE padre.ID_CATALOGO = hijo.ID_CATALOGO_PADRE)
 WHERE hijo.ID_CATALOGO_PADRE IS NOT NULL;

COMMIT;

COMMENT ON COLUMN CATALOGO.CATALOGO_PADRE_CODIGO IS 'Código del catálogo padre (referencia por código de negocio, sin FK)';

DROP INDEX IDX_CATALOGO_PADRE;
ALTER TABLE CATALOGO DROP CONSTRAINT FK_CATALOGO_PADRE;
ALTER TABLE CATALOGO DROP COLUMN ID_CATALOGO_PADRE;
CREATE INDEX IDX_CATALOGO_PADRE ON CATALOGO (CATALOGO_PADRE_CODIGO);
