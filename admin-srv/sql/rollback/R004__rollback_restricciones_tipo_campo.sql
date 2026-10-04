-- R004__rollback_restricciones_tipo_campo.sql
-- Autor: equipo SIIP
-- Fecha: 2026-10-04
-- Descripción: revierte V004__restricciones_tipo_campo.sql (CU-ADM-01): elimina las columnas de
--   restricción de tipo y vuelve a los largos anteriores. Las restricciones guardadas se pierden.
--   Reducir el largo falla (ORA-01441) si algún valor ya excede el anterior: corregir el dato y
--   reintentar.

ALTER TABLE CAMPO_DEFINICION DROP (VALOR_MINIMO, VALOR_MAXIMO, LONGITUD_MAXIMA, FECHA_MINIMA, FECHA_MAXIMA);

COMMENT ON COLUMN CAMPO_DEFINICION.POSICION IS 'Orden del campo desde 1 (Reglas 4 y 5); nulo en catálogos anteriores al contrato';
COMMENT ON TABLE CAMPO_DEFINICION_VALOR_ENUM IS 'CU-ADM-01: valores permitidos de un campo ENUM (aún sin uso en el CU)';

ALTER TABLE CAMPO_DEFINICION_VALOR_ENUM MODIFY (VALOR VARCHAR2(200));
ALTER TABLE REGISTRO_CATALOGO MODIFY (CLAVE VARCHAR2(200));
ALTER TABLE REGISTRO_VALOR MODIFY (VALOR VARCHAR2(1000));
