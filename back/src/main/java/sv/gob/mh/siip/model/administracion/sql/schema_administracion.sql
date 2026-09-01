--------------------------------------------------------------------------------
-- SIIP - Sistema de Información de Inversión Pública de El Salvador
-- Esquema Oracle - Módulo Administración e Interfaces (M-00, M-15)
--
-- *** ADVERTENCIA ***
-- Este esquema es 100% ESPECULATIVO. No existe ningún caso de uso documentado
-- (CU-ADM-xx, CU-ITF-xx) en el corpus recibido para este módulo. Se construyó
-- inferiendo necesidades a partir de referencias sueltas en otros 5 módulos ya
-- procesados (CU-PRE, CU-PRO, CU-EJE, CU-OYM, CU-MPD). NO IMPLEMENTAR sin antes
-- validar con el equipo de negocio y, de ser posible, obtener las fichas reales
-- de CU-ADM-01, CU-ADM-02, CU-ADM-04, CU-ADM-13 y CU-ITF-01.
--
-- Depende de: schema_preinversion.sql (USUARIO)
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- 1. SEGURIDAD Y PERMISOS [SUPUESTO — infiere CU-ADM-01 "Starter de programacion y seguridad"]
--------------------------------------------------------------------------------

CREATE SEQUENCE ROL_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE ROL (
    ID_ROL              NUMBER(19)      NOT NULL,
    CODIGO              VARCHAR2(40)    NOT NULL,
    NOMBRE              VARCHAR2(150)   NOT NULL,
    DESCRIPCION         VARCHAR2(1000),
    ACTIVO              NUMBER(1)       DEFAULT 1 NOT NULL,
    CONSTRAINT PK_ROL PRIMARY KEY (ID_ROL),
    CONSTRAINT UK_ROL_CODIGO UNIQUE (CODIGO)
);
COMMENT ON TABLE ROL IS
    '[SUPUESTO] Catalogo configurable de roles. Complementa (no reemplaza) el enum ROL de USUARIO; '
    'ver nota de refactor en TRAZABILIDAD-ADMINISTRACION.md.';

CREATE SEQUENCE MODULO_SISTEMA_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE MODULO_SISTEMA (
    ID_MODULO_SISTEMA   NUMBER(19)      NOT NULL,
    CODIGO              VARCHAR2(20)    NOT NULL,
    NOMBRE              VARCHAR2(150)   NOT NULL,
    CONSTRAINT PK_MODULO_SISTEMA PRIMARY KEY (ID_MODULO_SISTEMA),
    CONSTRAINT UK_MODULO_SISTEMA_COD UNIQUE (CODIGO)
);
COMMENT ON TABLE MODULO_SISTEMA IS '[SUPUESTO] Catalogo de modulos funcionales del SIIP (Preinversion, Programacion, etc.)';

CREATE SEQUENCE PERMISO_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE PERMISO (
    ID_PERMISO          NUMBER(19)      NOT NULL,
    ID_MODULO_SISTEMA   NUMBER(19)      NOT NULL,
    CODIGO              VARCHAR2(40)    NOT NULL,
    NOMBRE              VARCHAR2(150)   NOT NULL,
    ACCION              VARCHAR2(20)    NOT NULL,
    CONSTRAINT PK_PERMISO PRIMARY KEY (ID_PERMISO),
    CONSTRAINT UK_PERMISO_CODIGO UNIQUE (CODIGO),
    CONSTRAINT FK_PERMISO_MODULO FOREIGN KEY (ID_MODULO_SISTEMA) REFERENCES MODULO_SISTEMA (ID_MODULO_SISTEMA),
    CONSTRAINT CK_PERMISO_ACCION CHECK (ACCION IN ('CREAR','LEER','ACTUALIZAR','ELIMINAR','APROBAR','EXPORTAR'))
);
COMMENT ON TABLE PERMISO IS '[SUPUESTO] Accion permitida sobre un modulo del sistema.';

CREATE SEQUENCE ROL_PERMISO_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE ROL_PERMISO (
    ID_ROL_PERMISO      NUMBER(19)      NOT NULL,
    ID_ROL              NUMBER(19)      NOT NULL,
    ID_PERMISO          NUMBER(19)      NOT NULL,
    CONSTRAINT PK_ROL_PERMISO PRIMARY KEY (ID_ROL_PERMISO),
    CONSTRAINT UK_ROL_PERMISO UNIQUE (ID_ROL, ID_PERMISO),
    CONSTRAINT FK_ROL_PERMISO_ROL FOREIGN KEY (ID_ROL) REFERENCES ROL (ID_ROL),
    CONSTRAINT FK_ROL_PERMISO_PERMISO FOREIGN KEY (ID_PERMISO) REFERENCES PERMISO (ID_PERMISO)
);
COMMENT ON TABLE ROL_PERMISO IS '[SUPUESTO] Matriz rol-permiso.';

CREATE SEQUENCE SESION_USUARIO_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE SESION_USUARIO (
    ID_SESION           NUMBER(19)      NOT NULL,
    ID_USUARIO          NUMBER(19)      NOT NULL,
    FECHA_INICIO        TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    FECHA_FIN           TIMESTAMP,
    IP_ORIGEN           VARCHAR2(45),
    TOKEN_HASH          VARCHAR2(255),
    CONSTRAINT PK_SESION_USUARIO PRIMARY KEY (ID_SESION),
    CONSTRAINT FK_SESION_USUARIO FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO (ID_USUARIO)
);
COMMENT ON TABLE SESION_USUARIO IS '[SUPUESTO] Trazabilidad de inicio/cierre de sesion.';

--------------------------------------------------------------------------------
-- 2. PARÁMETROS DEL SISTEMA [SUPUESTO — infiere CU-ADM-02 "Catalogos de tablas basicas"]
--------------------------------------------------------------------------------

CREATE SEQUENCE PARAMETRO_SISTEMA_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE PARAMETRO_SISTEMA (
    ID_PARAMETRO_SISTEMA NUMBER(19)     NOT NULL,
    CLAVE               VARCHAR2(100)   NOT NULL,
    VALOR               VARCHAR2(500)   NOT NULL,
    DESCRIPCION         VARCHAR2(1000),
    FECHA_MODIFICACION  TIMESTAMP       DEFAULT SYSTIMESTAMP,
    USUARIO_MODIFICACION VARCHAR2(100),
    CONSTRAINT PK_PARAMETRO_SISTEMA PRIMARY KEY (ID_PARAMETRO_SISTEMA),
    CONSTRAINT UK_PARAMETRO_SISTEMA_CLAVE UNIQUE (CLAVE)
);
COMMENT ON TABLE PARAMETRO_SISTEMA IS
    '[SUPUESTO] Parametros configurables, ej. dias de inactividad para archivo automatico (CU-PRE-01 RN4: 3 meses + 5 dias habiles).';

--------------------------------------------------------------------------------
-- 3. CALENDARIO DE EVENTOS [SUPUESTO — infiere CU-ADM-04, no documentado]
--------------------------------------------------------------------------------

CREATE SEQUENCE CALENDARIO_EVENTO_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE CALENDARIO_EVENTO (
    ID_CALENDARIO_EVENTO NUMBER(19)     NOT NULL,
    TIPO_EVENTO         VARCHAR2(40)    NOT NULL,
    ANIO                NUMBER(4)       NOT NULL,
    MES                 NUMBER(2),
    CUATRIMESTRE        NUMBER(1),
    FECHA_APERTURA      TIMESTAMP,
    FECHA_CIERRE        TIMESTAMP,
    ESTADO              VARCHAR2(20)    DEFAULT 'ABIERTO' NOT NULL,
    DESCRIPCION         VARCHAR2(500),
    CONSTRAINT PK_CALENDARIO_EVENTO PRIMARY KEY (ID_CALENDARIO_EVENTO),
    CONSTRAINT CK_CALENDARIO_EVENTO_TIPO CHECK (TIPO_EVENTO IN
        ('PROGRAMACION_PRIPME','PROGRAMACION_PAIP','EJECUCION_PAIP','PROGRAMACION_PAP','EJECUCION_PAP')),
    CONSTRAINT CK_CALENDARIO_EVENTO_ESTADO CHECK (ESTADO IN ('ABIERTO','CERRADO')),
    CONSTRAINT CK_CALENDARIO_EVENTO_MES CHECK (MES IS NULL OR MES BETWEEN 1 AND 12),
    CONSTRAINT CK_CALENDARIO_EVENTO_CUATRI CHECK (CUATRIMESTRE IS NULL OR CUATRIMESTRE IN (1,2,3))
);
COMMENT ON TABLE CALENDARIO_EVENTO IS
    '[SUPUESTO] Fuente central de aperturas/cierres por tipo de proceso. NO reemplaza las tablas '
    'PERIODO_PROGRAMACION_* ya creadas en otros modulos (ver nota de refactor).';

--------------------------------------------------------------------------------
-- 4. AUDITORÍA TRANSVERSAL [SUPUESTO — infiere multiples menciones sueltas de "historial/registro"]
--------------------------------------------------------------------------------

CREATE SEQUENCE LOG_AUDITORIA_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE LOG_AUDITORIA (
    ID_LOG_AUDITORIA    NUMBER(19)      NOT NULL,
    ENTIDAD             VARCHAR2(100)   NOT NULL,
    ID_ENTIDAD          NUMBER(19)      NOT NULL,
    ACCION              VARCHAR2(20)    NOT NULL,
    ID_USUARIO          NUMBER(19),
    FECHA               TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    DETALLE             CLOB,
    CONSTRAINT PK_LOG_AUDITORIA PRIMARY KEY (ID_LOG_AUDITORIA),
    CONSTRAINT FK_LOG_AUDITORIA_USUARIO FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO (ID_USUARIO),
    CONSTRAINT CK_LOG_AUDITORIA_ACCION CHECK (ACCION IN ('CREAR','ACTUALIZAR','ELIMINAR'))
);
COMMENT ON TABLE LOG_AUDITORIA IS '[SUPUESTO] Bitacora generica CRUD, complementaria a auditorias puntuales ya existentes (ej. HISTORIAL_CIERRE).';

--------------------------------------------------------------------------------
-- 5. INTERFACES EXTERNAS [SUPUESTO — infiere "CU-ITF-01 Interfaz con SIAF", no documentado]
--------------------------------------------------------------------------------

CREATE SEQUENCE SISTEMA_EXTERNO_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE SISTEMA_EXTERNO (
    ID_SISTEMA_EXTERNO  NUMBER(19)      NOT NULL,
    CODIGO              VARCHAR2(20)    NOT NULL,
    NOMBRE              VARCHAR2(150)   NOT NULL,
    ACTIVO              NUMBER(1)       DEFAULT 1 NOT NULL,
    CONSTRAINT PK_SISTEMA_EXTERNO PRIMARY KEY (ID_SISTEMA_EXTERNO),
    CONSTRAINT UK_SISTEMA_EXTERNO_COD UNIQUE (CODIGO)
);
COMMENT ON TABLE SISTEMA_EXTERNO IS '[SUPUESTO] Catalogo de sistemas externos integrados (SIAF, etc.). CU-ITF-01';

CREATE SEQUENCE LOG_INTEGRACION_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE LOG_INTEGRACION_EXTERNA (
    ID_LOG_INTEGRACION  NUMBER(19)      NOT NULL,
    ID_SISTEMA_EXTERNO  NUMBER(19)      NOT NULL,
    TIPO_OPERACION      VARCHAR2(100)   NOT NULL,
    ENTIDAD_RELACIONADA VARCHAR2(100),
    ID_ENTIDAD_RELACIONADA NUMBER(19),
    FECHA_ENVIO         TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    FECHA_RESPUESTA     TIMESTAMP,
    ESTADO              VARCHAR2(20)    DEFAULT 'ENVIADO' NOT NULL,
    MENSAJE_ERROR       VARCHAR2(2000),
    PAYLOAD_REFERENCIA  VARCHAR2(500),
    CONSTRAINT PK_LOG_INTEGRACION PRIMARY KEY (ID_LOG_INTEGRACION),
    CONSTRAINT FK_LOG_INTEG_SISTEMA FOREIGN KEY (ID_SISTEMA_EXTERNO) REFERENCES SISTEMA_EXTERNO (ID_SISTEMA_EXTERNO),
    CONSTRAINT CK_LOG_INTEG_ESTADO CHECK (ESTADO IN ('ENVIADO','EXITOSO','ERROR'))
);
COMMENT ON TABLE LOG_INTEGRACION_EXTERNA IS '[SUPUESTO] Bitacora de envios/respuestas hacia sistemas externos. CU-ITF-01';

--------------------------------------------------------------------------------
-- FIN DEL SCRIPT - Módulo Administración e Interfaces (ESPECULATIVO)
--------------------------------------------------------------------------------
