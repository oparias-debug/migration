-- V004__restricciones_tipo_campo.sql
-- Autor: equipo SIIP
-- Fecha: 2026-10-04
-- Descripción: CU-ADM-01 v1.2 / modelo de dominio v3.0. Cada campo de un catálogo guarda la
--   restricción de su tipo (RN-04): mínimo y máximo de NUMERIC, longitud de STRING y rango de
--   FECHA (los valores de ENUM ya tenían tabla propia). Además:
--   - La clave del registro admite 255 caracteres, el máximo de un campo KEY STRING.
--   - Los valores de registro y de ENUM admiten el máximo que declara el contrato (4000 y 255).
--   Las columnas nuevas son nulas: los campos ya existentes quedan sin restricción y la
--   aplicación solo valida el formato de sus valores. Corresponde a CampoDefinicionEntity y
--   RegistroEntity. Ejecutar conectado como el esquema dueño de las tablas (sin prefijo de esquema).

ALTER TABLE CAMPO_DEFINICION ADD (
    VALOR_MINIMO    NUMBER(38, 10),
    VALOR_MAXIMO    NUMBER(38, 10),
    LONGITUD_MAXIMA NUMBER(10),
    FECHA_MINIMA    DATE,
    FECHA_MAXIMA    DATE
);

COMMENT ON COLUMN CAMPO_DEFINICION.VALOR_MINIMO IS 'NUMERIC {mínimo : ...}; nulo en otros tipos';
COMMENT ON COLUMN CAMPO_DEFINICION.VALOR_MAXIMO IS 'NUMERIC {... : máximo}; nulo en otros tipos';
COMMENT ON COLUMN CAMPO_DEFINICION.LONGITUD_MAXIMA IS 'STRING {longitud}; máximo 255 en el campo KEY';
COMMENT ON COLUMN CAMPO_DEFINICION.FECHA_MINIMA IS 'FECHA {desde : ...}; nulo en otros tipos';
COMMENT ON COLUMN CAMPO_DEFINICION.FECHA_MAXIMA IS 'FECHA {... : hasta}; nulo en otros tipos';
COMMENT ON COLUMN CAMPO_DEFINICION.POSICION IS 'Orden del campo desde 1, único en el catálogo (S-05); nulo en catálogos anteriores al contrato';

ALTER TABLE CAMPO_DEFINICION_VALOR_ENUM MODIFY (VALOR VARCHAR2(255));
COMMENT ON TABLE CAMPO_DEFINICION_VALOR_ENUM IS 'CU-ADM-01: valores permitidos de un campo ENUM, en orden';

ALTER TABLE REGISTRO_CATALOGO MODIFY (CLAVE VARCHAR2(255));
ALTER TABLE REGISTRO_VALOR MODIFY (VALOR VARCHAR2(4000));
