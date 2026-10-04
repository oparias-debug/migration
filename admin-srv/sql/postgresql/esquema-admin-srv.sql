-- esquema-admin-srv.sql
-- Autor: equipo SIIP
-- Fecha: 2026-09-30
-- Descripción: esquema de admin-srv para la base PostgreSQL local del docker-compose del monorepo.
--   Equivale, en dialecto PostgreSQL, a los scripts Oracle V001 (CU-ADM-01, catálogos), V002
--   (CU-ADM-04, calendarios), V003 (padre del catálogo por id) y V004 (restricciones de tipo de
--   los campos). Lo ejecuta
--   postgresql/init-catalogos.sh al crear la base; admin-srv corre en local con ddl-auto: validate,
--   así que cualquier cambio en las entidades debe reflejarse aquí y en los scripts Oracle.

-- ---------- CU-ADM-01: catálogos ----------

CREATE SEQUENCE catalogo_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE campo_definicion_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE registro_catalogo_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE catalogo (
    id_catalogo       BIGINT       NOT NULL,
    codigo            VARCHAR(100) NOT NULL,
    nombre            VARCHAR(300) NOT NULL,
    id_catalogo_padre BIGINT,
    estado            VARCHAR(20)  NOT NULL,
    fecha_desde       DATE,
    fecha_hasta       DATE,
    CONSTRAINT pk_catalogo PRIMARY KEY (id_catalogo),
    CONSTRAINT uk_catalogo_codigo UNIQUE (codigo),
    CONSTRAINT fk_catalogo_padre FOREIGN KEY (id_catalogo_padre) REFERENCES catalogo (id_catalogo),
    CONSTRAINT ck_catalogo_estado CHECK (estado IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_catalogo_padre ON catalogo (id_catalogo_padre);

CREATE TABLE campo_definicion (
    id_campo_definicion BIGINT       NOT NULL,
    id_catalogo         BIGINT       NOT NULL,
    nombre              VARCHAR(150) NOT NULL,
    tipo                VARCHAR(20)  NOT NULL,
    es_key              BOOLEAN      NOT NULL,
    posicion            INTEGER,
    valor_minimo        NUMERIC(38, 10),
    valor_maximo        NUMERIC(38, 10),
    longitud_maxima     INTEGER,
    fecha_minima        DATE,
    fecha_maxima        DATE,
    CONSTRAINT pk_campo_definicion PRIMARY KEY (id_campo_definicion),
    CONSTRAINT fk_campo_definicion_catalogo FOREIGN KEY (id_catalogo) REFERENCES catalogo (id_catalogo),
    CONSTRAINT ck_campo_definicion_tipo CHECK (tipo IN ('NUMBER', 'STRING', 'DATE', 'ENUM'))
);

CREATE INDEX idx_campo_definicion_catalogo ON campo_definicion (id_catalogo);

CREATE TABLE campo_definicion_valor_enum (
    id_campo_definicion BIGINT       NOT NULL,
    orden               INTEGER      NOT NULL,
    valor               VARCHAR(255),
    CONSTRAINT pk_campo_definicion_valor_enum PRIMARY KEY (id_campo_definicion, orden),
    CONSTRAINT fk_campo_valor_enum_campo FOREIGN KEY (id_campo_definicion)
        REFERENCES campo_definicion (id_campo_definicion)
);

CREATE TABLE registro_catalogo (
    id_registro       BIGINT       NOT NULL,
    id_catalogo       BIGINT       NOT NULL,
    clave             VARCHAR(255) NOT NULL,
    id_registro_padre BIGINT,
    estado            VARCHAR(20)  NOT NULL,
    fecha_desde       DATE,
    fecha_hasta       DATE,
    CONSTRAINT pk_registro_catalogo PRIMARY KEY (id_registro),
    CONSTRAINT uk_registro_catalogo_clave UNIQUE (id_catalogo, clave),
    CONSTRAINT fk_registro_catalogo_catalogo FOREIGN KEY (id_catalogo) REFERENCES catalogo (id_catalogo),
    CONSTRAINT fk_registro_catalogo_padre FOREIGN KEY (id_registro_padre) REFERENCES registro_catalogo (id_registro),
    CONSTRAINT ck_registro_catalogo_estado CHECK (estado IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_registro_catalogo_padre ON registro_catalogo (id_registro_padre);

CREATE TABLE registro_valor (
    id_registro  BIGINT        NOT NULL,
    nombre_campo VARCHAR(255)  NOT NULL,
    valor        VARCHAR(4000),
    CONSTRAINT pk_registro_valor PRIMARY KEY (id_registro, nombre_campo),
    CONSTRAINT fk_registro_valor_registro FOREIGN KEY (id_registro) REFERENCES registro_catalogo (id_registro)
);

-- ---------- CU-ADM-04: calendarios ----------

CREATE SEQUENCE calendario_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE periodo_calendario_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE excepcion_calendario_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE calendario (
    id_calendario BIGINT        NOT NULL,
    codigo        VARCHAR(100)  NOT NULL,
    nombre        VARCHAR(300)  NOT NULL,
    descripcion   VARCHAR(1000),
    fecha_inicio  DATE          NOT NULL,
    fecha_fin     DATE          NOT NULL,
    estado        VARCHAR(20)   NOT NULL,
    administrador VARCHAR(100)  NOT NULL,
    CONSTRAINT pk_calendario PRIMARY KEY (id_calendario),
    CONSTRAINT uk_calendario_codigo UNIQUE (codigo),
    CONSTRAINT ck_calendario_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT ck_calendario_rango CHECK (fecha_inicio <= fecha_fin)
);

CREATE TABLE periodo_calendario (
    id_periodo       BIGINT       NOT NULL,
    id_calendario    BIGINT       NOT NULL,
    codigo           VARCHAR(100) NOT NULL,
    nombre           VARCHAR(300) NOT NULL,
    tipo             VARCHAR(20)  NOT NULL,
    tipo_recurrencia VARCHAR(20)  NOT NULL,
    fecha_inicio     DATE,
    fecha_fin        DATE,
    CONSTRAINT pk_periodo_calendario PRIMARY KEY (id_periodo),
    CONSTRAINT uk_periodo_calendario_codigo UNIQUE (id_calendario, codigo),
    CONSTRAINT fk_periodo_calendario_cal FOREIGN KEY (id_calendario) REFERENCES calendario (id_calendario),
    CONSTRAINT ck_periodo_calendario_tipo CHECK (tipo IN ('LABORAL', 'NO_LABORAL')),
    CONSTRAINT ck_periodo_calendario_recurr CHECK (tipo_recurrencia IN ('UNA_VEZ', 'SEMANAL', 'MENSUAL')),
    CONSTRAINT ck_periodo_calendario_fechas CHECK (
        (tipo_recurrencia = 'MENSUAL' AND fecha_inicio IS NULL AND fecha_fin IS NULL)
        OR (tipo_recurrencia <> 'MENSUAL' AND fecha_inicio IS NOT NULL AND fecha_fin IS NOT NULL
            AND fecha_inicio <= fecha_fin))
);

CREATE INDEX idx_periodo_calendario_cal ON periodo_calendario (id_calendario);

CREATE TABLE periodo_calendario_dia_semana (
    id_periodo BIGINT      NOT NULL,
    dia_semana VARCHAR(15) NOT NULL,
    CONSTRAINT pk_periodo_cal_dia_semana PRIMARY KEY (id_periodo, dia_semana),
    CONSTRAINT fk_periodo_cal_dia_semana FOREIGN KEY (id_periodo) REFERENCES periodo_calendario (id_periodo),
    CONSTRAINT ck_periodo_cal_dia_semana CHECK (dia_semana IN
        ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'))
);

CREATE TABLE periodo_calendario_dia_mes (
    id_periodo BIGINT  NOT NULL,
    dia_mes    INTEGER NOT NULL,
    CONSTRAINT pk_periodo_cal_dia_mes PRIMARY KEY (id_periodo, dia_mes),
    CONSTRAINT fk_periodo_cal_dia_mes FOREIGN KEY (id_periodo) REFERENCES periodo_calendario (id_periodo),
    CONSTRAINT ck_periodo_cal_dia_mes CHECK (dia_mes BETWEEN 1 AND 31)
);

CREATE TABLE periodo_calendario_mes (
    id_periodo BIGINT      NOT NULL,
    mes        VARCHAR(10) NOT NULL,
    CONSTRAINT pk_periodo_cal_mes PRIMARY KEY (id_periodo, mes),
    CONSTRAINT fk_periodo_cal_mes FOREIGN KEY (id_periodo) REFERENCES periodo_calendario (id_periodo),
    CONSTRAINT ck_periodo_cal_mes CHECK (mes IN ('JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE',
        'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER'))
);

CREATE TABLE excepcion_calendario (
    id_excepcion  BIGINT       NOT NULL,
    id_calendario BIGINT       NOT NULL,
    fecha         DATE         NOT NULL,
    tipo          VARCHAR(20)  NOT NULL,
    descripcion   VARCHAR(500),
    CONSTRAINT pk_excepcion_calendario PRIMARY KEY (id_excepcion),
    CONSTRAINT uk_excepcion_calendario_fecha UNIQUE (id_calendario, fecha),
    CONSTRAINT fk_excepcion_calendario_cal FOREIGN KEY (id_calendario) REFERENCES calendario (id_calendario),
    CONSTRAINT ck_excepcion_calendario_tipo CHECK (tipo IN ('DIA_LABORAL', 'DIA_NO_LABORAL'))
);
