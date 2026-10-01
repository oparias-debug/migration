-- =====================================================================
-- Script de creación de tablas — Administración de Catálogos (CU-ADM-01)
-- Dialecto: SQL ANSI (SQL:2003), portable entre PostgreSQL, DB2, H2, etc.
-- Corresponde al modelo de dominio en modelo-dominio-catalogos.md (v2.0)
-- =====================================================================

-- ---------------------------------------------------------------------
-- Tabla: catalog
-- Catálogo maestro (catalogMaster). Reglas 6,7,9,10,13,14,15,17,18,20,21,22,23,24
-- ---------------------------------------------------------------------
CREATE TABLE catalog (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code            VARCHAR(64)  NOT NULL,
    name            VARCHAR(255) NOT NULL,
    parent_id       BIGINT,
    active          VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    from_date       DATE,
    to_date         DATE,

    CONSTRAINT uk_catalog_code UNIQUE (code),
    CONSTRAINT ck_catalog_active CHECK (active IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT fk_catalog_parent
        FOREIGN KEY (parent_id) REFERENCES catalog (id)
);

-- ---------------------------------------------------------------------
-- Tabla: catalog_field
-- Definición de campos de un catálogo. Reglas 2,3,16,18,19
-- ---------------------------------------------------------------------
CREATE TABLE catalog_field (
    id              BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    catalog_id      BIGINT       NOT NULL,
    name            VARCHAR(128) NOT NULL,
    qualifier       VARCHAR(16)  NOT NULL,
    posicion        INTEGER      NOT NULL,

    CONSTRAINT uk_catalog_field_name UNIQUE (catalog_id, name),
    CONSTRAINT ck_catalog_field_qualifier CHECK (qualifier IN ('KEY', 'FIELD')),
    CONSTRAINT fk_catalog_field_catalog
        FOREIGN KEY (catalog_id) REFERENCES catalog (id)
);

-- ---------------------------------------------------------------------
-- Tabla: catalog_record
-- Registro de datos maestros de un catálogo.
-- Reglas 1,8,9,10,11,12,13,14,16,23,24
-- ---------------------------------------------------------------------
CREATE TABLE catalog_record (
    id                BIGINT      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    catalog_id        BIGINT      NOT NULL,
    parent_record_id  BIGINT,
    active            VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    from_date         DATE,
    to_date           DATE,

    CONSTRAINT ck_catalog_record_active CHECK (active IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT fk_catalog_record_catalog
        FOREIGN KEY (catalog_id) REFERENCES catalog (id),
    CONSTRAINT fk_catalog_record_parent_record
        FOREIGN KEY (parent_record_id) REFERENCES catalog_record (id)
);

-- ---------------------------------------------------------------------
-- Tabla: catalog_record_value
-- Valor de un campo dentro de un registro. Reglas 1,4,5,16
-- ---------------------------------------------------------------------
CREATE TABLE catalog_record_value (
    id          BIGINT  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    record_id   BIGINT  NOT NULL,
    field_id    BIGINT  NOT NULL,
    valor       VARCHAR(4000) NOT NULL,

    CONSTRAINT uk_record_field UNIQUE (record_id, field_id),
    CONSTRAINT fk_record_value_record
        FOREIGN KEY (record_id) REFERENCES catalog_record (id),
    CONSTRAINT fk_record_value_field
        FOREIGN KEY (field_id) REFERENCES catalog_field (id)
);

-- ---------------------------------------------------------------------
-- Índices de apoyo a las consultas más frecuentes del caso de uso
-- ---------------------------------------------------------------------

-- Regla 15/24: catálogos hijos de un catálogo padre
CREATE INDEX ix_catalog_parent_id ON catalog (parent_id);

-- Regla 20/21: listado y búsqueda de catálogos por nombre (Regla 6)
CREATE INDEX ix_catalog_name ON catalog (name);

-- Regla 4/5: registros de un catálogo
CREATE INDEX ix_catalog_record_catalog_id ON catalog_record (catalog_id);

-- Regla 23/24: registros hijos de un registro padre
CREATE INDEX ix_catalog_record_parent_record_id ON catalog_record (parent_record_id);

-- Recuperación de valores por registro y por campo
CREATE INDEX ix_record_value_record_id ON catalog_record_value (record_id);
CREATE INDEX ix_record_value_field_id ON catalog_record_value (field_id);

-- ---------------------------------------------------------------------
-- Notas de integridad no expresables en SQL ANSI puro
-- ---------------------------------------------------------------------
-- 1) Regla 2/18: "al menos un campo, al menos uno KEY" y Regla 3
--    ("nombres de campo únicos" ya cubierta por uk_catalog_field_name)
--    son invariantes de agregado que se validan en la capa de dominio
--    (ver Catalog.assertHasAtLeastOneField() - assertHasAtLeastOneKey()
--    en modelo-dominio-catalogos.md), no con un CHECK de tabla.
--
-- 2) Regla 23 (integridad cruzada): si catalog.parent_id IS NOT NULL,
--    todo catalog_record de ese catalog_id debe tener parent_record_id
--    IS NOT NULL, y el catalog_record referenciado por parent_record_id
--    debe pertenecer a catalog.parent_id. Esta regla involucra a las
--    tablas catalog y catalog_record simultáneamente y no puede
--    expresarse como FK o CHECK estándar; se aplica en la capa de
--    servicio (ver CatalogRecord.create()) o, si se requiere reforzarla
--    a nivel de base de datos, mediante un TRIGGER (sintaxis específica
--    del motor, fuera del alcance de este script ANSI).
--
-- 3) Regla 19: los campos de un catálogo solo pueden modificarse si el
--    catálogo no tiene registros. Es una regla de proceso (momento de la
--    operación), no un invariante de estado persistente; se valida en la
--    capa de aplicación antes de ejecutar UPDATE-DELETE sobre catalog_field.
