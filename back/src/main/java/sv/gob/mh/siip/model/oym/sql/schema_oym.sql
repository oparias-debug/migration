--------------------------------------------------------------------------------
-- SIIP - Sistema de Información de Inversión Pública de El Salvador
-- Esquema Oracle - Módulo Operación y Mantenimiento (M-13)
-- Depende de: schema_preinversion.sql (PROYECTO, USUARIO)
--             schema_ejecucion.sql (CIERRE_PROYECTO, para calcular Fecha de Finalizacion)
--------------------------------------------------------------------------------

CREATE SEQUENCE PROY_FIN_EVAL_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE PROYECTO_FINALIZADO_EVALUACION (
    ID_PROY_FIN_EVAL     NUMBER(19)     NOT NULL,
    ID_PROYECTO         NUMBER(19)      NOT NULL,
    SELECCIONADO_EVALUACION NUMBER(1)   DEFAULT 0 NOT NULL,
    FECHA_SELECCION      TIMESTAMP,
    ID_USUARIO_EXCEPCION NUMBER(19),
    FECHA_EXCEPCION      TIMESTAMP,
    MOTIVO_EXCEPCION     VARCHAR2(2000),
    CONSTRAINT PK_PROY_FIN_EVAL PRIMARY KEY (ID_PROY_FIN_EVAL),
    CONSTRAINT UK_PROY_FIN_EVAL UNIQUE (ID_PROYECTO),
    CONSTRAINT FK_PROY_FIN_EVAL_PROYECTO FOREIGN KEY (ID_PROYECTO) REFERENCES PROYECTO (ID_PROYECTO),
    CONSTRAINT FK_PROY_FIN_EVAL_USUARIO FOREIGN KEY (ID_USUARIO_EXCEPCION) REFERENCES USUARIO (ID_USUARIO)
);
COMMENT ON TABLE PROYECTO_FINALIZADO_EVALUACION IS
    'Seleccion de un proyecto finalizado para evaluacion ex post y excepcion de bloqueo (RN08, autorizacion DGICP). CU-OYM-01';

CREATE SEQUENCE DOC_EVAL_EXPOST_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE DOCUMENTO_EVALUACION_EXPOST (
    ID_DOCUMENTO_EVAL_EXPOST NUMBER(19) NOT NULL,
    ID_PROYECTO         NUMBER(19)      NOT NULL,
    NOMBRE_ARCHIVO       VARCHAR2(300)  NOT NULL,
    RUTA_ARCHIVO         VARCHAR2(500)  NOT NULL,
    DESCRIPCION          VARCHAR2(1000),
    USUARIO_CARGA        VARCHAR2(100),
    FECHA_CARGA          TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_DOC_EVAL_EXPOST PRIMARY KEY (ID_DOCUMENTO_EVAL_EXPOST),
    CONSTRAINT FK_DOC_EVAL_EXPOST_PROYECTO FOREIGN KEY (ID_PROYECTO) REFERENCES PROYECTO (ID_PROYECTO)
);
COMMENT ON TABLE DOCUMENTO_EVALUACION_EXPOST IS
    'Documento de evaluacion ex post adjunto a un proyecto finalizado (RN02, RN09). CU-OYM-01';

--------------------------------------------------------------------------------
-- FIN DEL SCRIPT - Módulo Operación y Mantenimiento
--------------------------------------------------------------------------------
