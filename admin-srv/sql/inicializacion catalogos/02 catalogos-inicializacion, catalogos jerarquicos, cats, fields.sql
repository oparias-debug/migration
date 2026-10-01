--------------------------------------------------------
-- Inicialización de catálogos jerárquicos
--------------------------------------------------------

--  MACROSECTOR > SECTOR
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('SECTOR',  'Sector', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'MACROSECTOR') WHERE code = 'SECTOR';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'SECTOR';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'SECTOR';

--  REGION > DEPARTAMENTO
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('DEPARTAMENTO',  'Departamento', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'REGION') WHERE code = 'DEPARTAMENTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'DEPARTAMENTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'DEPARTAMENTO';

-- DEPARTAMENTO > DISTRITO
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('DISTRITO',  'Distrito', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'DEPARTAMENTO') WHERE code = 'DISTRITO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'DISTRITO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'DISTRITO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'marcador_nivel_departamental', 'FIELD', 3 FROM catalog WHERE code = 'DISTRITO';

-- TIPO_UNIDAD_MEDIDA > CATEGORIA_UNIDAD_MEDIDA
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('CATEGORIA_UNIDAD_MEDIDA',  'Categoría de unidad de medida', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'TIPO_UNIDAD_MEDIDA') WHERE code = 'CATEGORIA_UNIDAD_MEDIDA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'CATEGORIA_UNIDAD_MEDIDA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'CATEGORIA_UNIDAD_MEDIDA';

-- CATEGORIA_UNIDAD_MEDIDA > UNIDAD_DE_MEDIDA
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('UNIDAD_MEDIDA',  'Unidad de medida', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'CATEGORIA_UNIDAD_MEDIDA') WHERE code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'UNIDAD_MEDIDA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'descripcion', 'FIELD', 3 FROM catalog WHERE code = 'UNIDAD_MEDIDA';

-- DIMENSION_ELEGIBILIDAD > CRITERIO_ELEGIBILIDAD
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('CRITERIO_ELEGIBILIDAD',  'Criterio elegibilidad', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'DIMENSION_ELEGIBILIDAD') WHERE code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'pregunta', 'FIELD', 3 FROM catalog WHERE code = 'CRITERIO_ELEGIBILIDAD';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'ponderacion', 'FIELD', 4 FROM catalog WHERE code = 'CRITERIO_ELEGIBILIDAD';

-- PRODUCTO >  INDICADOR_PRODUCTO
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('INDICADOR_PRODUCTO',  'Indicadores de producto', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'PRODUCTO') WHERE code = 'INDICADOR_PRODUCTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'INDICADOR_PRODUCTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'INDICADOR_PRODUCTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'unidad_medida', 'FIELD', 3 FROM catalog WHERE code = 'INDICADOR_PRODUCTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'principal', 'FIELD', 4 FROM catalog WHERE code = 'INDICADOR_PRODUCTO';

-- RESULTADO >  INDICADOR_RESULTADO
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('INDICADOR_RESULTADO',  'Indicadores de resultado', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'RESULTADO') WHERE code = 'INDICADOR_RESULTADO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'INDICADOR_RESULTADO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'INDICADOR_RESULTADO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'unidad_medida', 'FIELD', 3 FROM catalog WHERE code = 'INDICADOR_RESULTADO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'descripcion', 'FIELD', 4 FROM catalog WHERE code = 'INDICADOR_RESULTADO';

-- CRITERIO_PRIORIZACION > SUBCRITERIO_PRIORIZACION
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('SUBCRITERIO_PRIORIZACION',  'Subcriterio de priorizacion', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'CRITERIO_PRIORIZACION') WHERE code = 'SUBCRITERIO_PRIORIZACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'SUBCRITERIO_PRIORIZACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'SUBCRITERIO_PRIORIZACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'ponderacion', 'FIELD', 3 FROM catalog WHERE code = 'SUBCRITERIO_PRIORIZACION';

-- SUBCRITERIO_PRIORIZACION > ESCALA_PRIORIZACION
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('ESCALA_PRIORIZACION',  'Escala calificacion subcriterio de priorizacion', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'SUBCRITERIO_PRIORIZACION') WHERE code = 'ESCALA_PRIORIZACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESCALA_PRIORIZACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESCALA_PRIORIZACION';

-- HABILITACION_CAMPOS > SECCIONES_HABILITADAS
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('SECCIONES_HABILITADAS',  'Secciones habilitadas', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'HABILITACION_CAMPOS') WHERE code = 'SECCIONES_HABILITADAS';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'SECCIONES_HABILITADAS';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'SECCIONES_HABILITADAS';


-- SECCIONES_HABILITADAS > ETAPAS_HABILITADAS
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('ETAPAS_HABILITADAS',  'Etapas habilitadas', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'SECCIONES_HABILITADAS') WHERE code = 'ETAPAS_HABILITADAS';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'etapa', 'KEY', 1 FROM catalog WHERE code = 'ETAPAS_HABILITADAS';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'habilitacion', 'FIELD', 2 FROM catalog WHERE code = 'ETAPAS_HABILITADAS';

-- INSTITUCION > INSTITUCION_ADSCRITA
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('INSTITUCION_ADSCRITA',  'Instituciones Adscritas', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'INSTITUCION') WHERE code = 'INSTITUCION_ADSCRITA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'INSTITUCION_ADSCRITA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'INSTITUCION_ADSCRITA';

-- TIPO_EJECUTORA > UNIDAD_EJECUTORA
INSERT INTO catalog (code, name, active, from_date, to_date)
SELECT v.code, v.name, v.active, v.from_date, v.to_date
FROM ( VALUES ('UNIDAD_EJECUTORA',  'Unidades Ejecutoras', 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31'))
AS v (code, name, active, from_date, to_date)
WHERE NOT EXISTS (
    SELECT 1 FROM catalog c WHERE c.code = v.code
);
UPDATE catalog SET parent_id = (SELECT id FROM Catalog WHERE code = 'TIPO_EJECUTORA') WHERE code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'sigla', 'FIELD', 3 FROM catalog WHERE code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo_institucion', 'FIELD', 4 FROM catalog WHERE code = 'UNIDAD_EJECUTORA';
