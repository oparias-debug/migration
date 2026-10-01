-- =====================================================================
-- Inicialización de tablas catalog y catalog_field
-- Origen: catalogos__inicializacion.json (76 catálogos)
-- Dialecto: SQL ANSI (SQL:2003)
-- =====================================================================

-- ---------------------------------------------------------------------
-- Tabla: catalog
-- ---------------------------------------------------------------------
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('GRD', 'Gestión de Riesgo de Desastres (GRD)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('GRC', 'Gestión de Riesgo Climático (GRC)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ACC', 'Adaptación al Cambio Climático (ACC)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('EJE_PLAN_GOBIERNO', 'Ejes del Plan de Gobierno (Plan Cuscatlán)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('PLAN_SECTORIAL', 'Planes Sectoriales', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('MACROSECTOR', 'Macrosectores', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('EJE_TEMATICO', 'Ejes Temáticos', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('TIPO_EJECUTORA', 'Tipos de unidad ejecutora', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('INSTITUCION', 'Instituciones', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
-- INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
--            VALUES  ('UNIDAD_EJECUTORA', 'Unidades Ejecutoras', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ESTADO_PROYECTO', 'Estados de Proyecto (RN04)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('INTERESADO_TIPO', 'Tipo de interesados', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('REGION', ' Regiones geográficas (Región > Departamento > Distrito)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('TIPO_UNIDAD_MEDIDA', 'Tipo de unidades de medida (Tipo > Categoría > Unidad)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('COMPONENTE_PROYECTO', 'Componentes del proyecto', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('PROPIETARIO_TERRENO', 'Propietarios', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('MEDIO_AMBIENTAL', 'Medio (componente ambiental afectado)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('IMPACTO_AMBIENTAL', 'Impacto ambiental', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('MAGNITUD_AMBIENTAL', 'Magnitudes impacto ambiental', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('DURACION_AMBIENTAL', 'Duraciones del impacto ambiental', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('REVERSIBILIDAD_AMBIENTAL', 'Reversibilidad del impacto ambiental', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('PROBABILIDAD_RIESGO', 'Probabilidad de riesgo', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('IMPACTO_RIESGO', 'Impacto de riesgo)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('CALIFICACION_RIESGO_MATRIZ', 'Calificación del riesgo (matriz Probabilidad x Impacto)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('INSUMO_TIPO', 'Insumo Tipo', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('FUENTE_FINANCIAMIENTO', 'Fuentes de Financiamiento', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('FUENTE_RECURSOS', 'Fuentes de Recursos', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('CONVENIO_FINANCIAMIENTO', 'Convenios', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('PARAMETRO_EVALUACION_SOCIAL', 'Parámetros para evaluación social de beneficios', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('TIPO_BENEFICIO', 'Tipo de Beneficio', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('TIPO_BIEN_RESCATE', 'Tipo de bienes (Valor de rescate)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('UNIDAD_EJECUTORA_EVALUACION_FINANCIERA', 'Unidades ejecutoras con acceso al botón Evaluación Financiera', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('PRODUCTO', 'Productos (Productos > Indicadores de Producto)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('RESULTADO', 'Resultados (Resultados > Indicadores de resultado)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ODS_ONU', 'A cuáles ODS contribuye (Objetivos de Desarrollo Sostenible)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('MEDIO_AMBIENTE_COMPONENTE', 'A cuáles componentes del Medio Ambiente contribuye', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('GRUPO_POBLACIONAL_VULNERABLE', 'Grupos poblacionales en situación de vulnerabilidad', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('MEJORA_CALIDAD_VIDA', 'Mejoras en calidad de vida', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('DIMENSION_ELEGIBILIDAD', 'Dimensión de Elegibilidad  (Dimensiones > Criterios > Valor)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('CRITERIO_PRIORIZACION', 'Criterios y Subcriterios de Priorización (Anexo A.1)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('RANGO_PRIORIZACION', 'Rangos de Interpretación del puntaje de priorización', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('TIPO_CAPITAL', 'Tipo de capital', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('TIPO_COSTO', 'Tipo de Costos', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('TAMANO_PROYECTO', 'Tamaño del proyecto según monto', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('COMPLEJIDAD_PROYECTO', 'Complejidad del proyecto', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('RUTA_PREINVERSION', 'Ruta de Preinversión (Tipo de capital x Tamaño x Complejidad -> Ruta)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ETAPA_PROYECTO', 'Etapas del proyecto', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('HABILITACION_CAMPOS', 'Contenido de Iniciativas de Proyecto (habilitación de campos por etapa e iniciativa)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ENTREGABLE_ETAPA', 'Etapa de Entregable', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ESTADO_ESTUDIO_PREINVERSION', 'Estados del Estudio', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('TIPO_FINANCIAMIENTO', 'Tipo de Financiamiento', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('CLASIFICACION_ETAPA_FINANCIAMIENTO', 'Clasificación según Etapa (de financiamiento del proyecto)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ESTADO_PRIPME', 'Estados del PRIPME', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('NIVEL_PRIORIZACION_PRIPME', 'Nivel de Priorización (PRIPME)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ESTADO_REVISION_TECNICA', 'Estado de Revisión (Técnico)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('FORMATO_REPORTE_PRIPME', 'Formato de reporte (Generar reporte PRIPME)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ESTADOS_PROGRAMACION', 'Estados de Programación', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('TIPO_TRANSACCION', 'Tipo de Transacción', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('TIPO_APROBACION', 'Tipo de Aprobación', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('AGRUPACION_OPERACIONAL', 'Tabla de Agrupaciones Operacionales de la DGCG', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('FORMATO_EXPORTACION_UBICACION', 'Formato de exportación de reportes (Distribución Financiera por Ubicación Geográfica)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ESTADO_PROPUESTA_PAIP', 'Estados de la Propuesta PAIP', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ESTADO_COMPARACION_PROYECTO_PRO11', 'Estados de Comparación de Proyecto (contrapropuesta institucional)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ESTADO_COMPARACION_PROYECTO_PRO09', 'Estados de Comparación de Proyecto (propuesta de escenarios)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('INSTANCIA_APROBACION', 'Instancias de Aprobación', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('DIMENSION_PRIORIZACION_ESCENARIOS', 'Dimensiones de Priorización (Generación de Escenarios)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ATRIBUTO_OBLIGATORIO_ESCENARIOS', 'Atributos Obligatorios (Generación de Escenarios)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ESTADO_ESCENARIO', 'Estados del Escenario', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('HISTORICO_AUTORIZACION_TECHO', 'Histórico de Autorizaciones (Consolidado de Techos)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ESTADO_SEMAFORO_AVANCE', 'Estados de Semáforo de Avance (Físico/Financiero)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('METODO_CONTRATACION', 'Método de Contratación', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('CATEGORIA_PROCESO_ADMINISTRATIVO', 'Categoría (Procesos Administrativos)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('PROCESO_ADMINISTRATIVO_HITO', 'Proceso Administrativo - Estado del Proceso (hitos)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('FUENTE_FINANCIAMIENTO_SEGUIMIENTO', 'Fuente de Financiamiento (Seguimiento Mensual de Proyectos - Estatus)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('ESTADO_CONDICION_PREVIA', 'Estado (Condición Previa del Convenio)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');
INSERT INTO catalog (code, name, parent_id, active, from_date, to_date)
            VALUES  ('TIPO_AJUSTE_CONVENIO', 'Tipo de Ajuste (Modificaciones al Convenio)', NULL, 'ACTIVE', DATE '2000-01-01', DATE '2999-12-31');

-- ---------------------------------------------------------------------
-- Tabla: catalog_field
-- catalog_id se obtiene del por su code (uk_catalog_code)
-- ---------------------------------------------------------------------
-- GRD
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'GRD';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'GRD';
-- GRC
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'GRC';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'GRC';
-- ACC
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ACC';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ACC';
-- EJE_PLAN_GOBIERNO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'EJE_PLAN_GOBIERNO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'EJE_PLAN_GOBIERNO';
-- PLAN_SECTORIAL
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'PLAN_SECTORIAL';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'PLAN_SECTORIAL';
       SELECT id, 'sector_asociado', 'FIELD', 3 FROM catalog WHERE code = 'PLAN_SECTORIAL';
-- MACROSECTOR
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'MACROSECTOR';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'MACROSECTOR';
-- EJE_TEMATICO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'EJE_TEMATICO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'EJE_TEMATICO';
-- TIPO_EJECUTORA
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'TIPO_EJECUTORA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'TIPO_EJECUTORA';
-- INSTITUCION
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'INSTITUCION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'INSTITUCION';
-- UNIDAD_EJECUTORA
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo_institucion', 'FIELD', 3 FROM catalog WHERE code = 'UNIDAD_EJECUTORA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo_tipo_ejecutora', 'FIELD', 4 FROM catalog WHERE code = 'UNIDAD_EJECUTORA';
-- ESTADO_PROYECTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESTADO_PROYECTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESTADO_PROYECTO';
-- INTERESADO_TIPO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'INTERESADO_TIPO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'INTERESADO_TIPO';
-- REGION   (UBICACION_GEOGRAFICA)
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'REGION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'REGION';
-- TIPO_UNIDAD_MEDIDA (UNIDAD_MEDIDA)
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'TIPO_UNIDAD_MEDIDA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'TIPO_UNIDAD_MEDIDA';
-- COMPONENTE_PROYECTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'COMPONENTE_PROYECTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'COMPONENTE_PROYECTO';
-- PROPIETARIO_TERRENO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'PROPIETARIO_TERRENO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'PROPIETARIO_TERRENO';
-- MEDIO_AMBIENTAL
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'MEDIO_AMBIENTAL';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'MEDIO_AMBIENTAL';
-- IMPACTO_AMBIENTAL
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'IMPACTO_AMBIENTAL';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'IMPACTO_AMBIENTAL';
-- MAGNITUD_AMBIENTAL
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'MAGNITUD_AMBIENTAL';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'MAGNITUD_AMBIENTAL';
-- DURACION_AMBIENTAL
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'DURACION_AMBIENTAL';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'DURACION_AMBIENTAL';
-- REVERSIBILIDAD_AMBIENTAL
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'REVERSIBILIDAD_AMBIENTAL';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'REVERSIBILIDAD_AMBIENTAL';
-- PROBABILIDAD_RIESGO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'PROBABILIDAD_RIESGO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'PROBABILIDAD_RIESGO';
-- IMPACTO_RIESGO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'IMPACTO_RIESGO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'IMPACTO_RIESGO';
-- CALIFICACION_RIESGO_MATRIZ
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'probabilidad', 'FIELD', 3 FROM catalog WHERE code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'impacto', 'FIELD', 4 FROM catalog WHERE code = 'CALIFICACION_RIESGO_MATRIZ';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'calificacion_resultante', 'FIELD', 5 FROM catalog WHERE code = 'CALIFICACION_RIESGO_MATRIZ';
-- INSUMO_TIPO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'INSUMO_TIPO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'INSUMO_TIPO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'factor_correccion', 'FIELD', 3 FROM catalog WHERE code = 'INSUMO_TIPO';
-- FUENTE_FINANCIAMIENTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'FUENTE_FINANCIAMIENTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'FUENTE_FINANCIAMIENTO';
-- FUENTE_RECURSOS
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'FUENTE_RECURSOS';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'FUENTE_RECURSOS';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo_pais', 'FIELD', 3 FROM catalog WHERE code = 'FUENTE_RECURSOS';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo_clase_recurso', 'FIELD', 4 FROM catalog WHERE code = 'FUENTE_RECURSOS';
-- CONVENIO_FINANCIAMIENTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'moneda', 'FIELD', 3 FROM catalog WHERE code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'monto', 'FIELD', 4 FROM catalog WHERE code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'numero_sigade', 'FIELD', 5 FROM catalog WHERE code = 'CONVENIO_FINANCIAMIENTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo_tipo_convenio', 'FIELD', 6 FROM catalog WHERE code = 'CONVENIO_FINANCIAMIENTO';
-- PARAMETRO_EVALUACION_SOCIAL
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'PARAMETRO_EVALUACION_SOCIAL';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'factor_correccion', 'FIELD', 3 FROM catalog WHERE code = 'PARAMETRO_EVALUACION_SOCIAL';
-- TIPO_BENEFICIO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'TIPO_BENEFICIO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'TIPO_BENEFICIO';
-- TIPO_BIEN_RESCATE
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'TIPO_BIEN_RESCATE';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'TIPO_BIEN_RESCATE';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'factor_correccion', 'FIELD', 3 FROM catalog WHERE code = 'TIPO_BIEN_RESCATE';
-- UNIDAD_EJECUTORA_EVALUACION_FINANCIERA
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'item_sigla', 'FIELD', 3 FROM catalog WHERE code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'clasificacion_institucional', 'FIELD', 4 FROM catalog WHERE code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'referencia_cruzada', 'FIELD', 5 FROM catalog WHERE code = 'UNIDAD_EJECUTORA_EVALUACION_FINANCIERA';
-- PRODUCTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'PRODUCTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'PRODUCTO';
-- RESULTADO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'RESULTADO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'RESULTADO';
-- ODS_ONU
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ODS_ONU';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ODS_ONU';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'numero_ods', 'FIELD', 3 FROM catalog WHERE code = 'ODS_ONU';
-- MEDIO_AMBIENTE_COMPONENTE
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'MEDIO_AMBIENTE_COMPONENTE';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'MEDIO_AMBIENTE_COMPONENTE';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'numero', 'FIELD', 3 FROM catalog WHERE code = 'MEDIO_AMBIENTE_COMPONENTE';
-- GRUPO_POBLACIONAL_VULNERABLE
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'GRUPO_POBLACIONAL_VULNERABLE';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'numero', 'FIELD', 3 FROM catalog WHERE code = 'GRUPO_POBLACIONAL_VULNERABLE';
-- MEJORA_CALIDAD_VIDA
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'MEJORA_CALIDAD_VIDA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'MEJORA_CALIDAD_VIDA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'numero', 'FIELD', 3 FROM catalog WHERE code = 'MEJORA_CALIDAD_VIDA';
-- DIMENSION_ELEGIBILIDAD
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'DIMENSION_ELEGIBILIDAD';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'ponderacion', 'FIELD', 3 FROM catalog WHERE code = 'DIMENSION_ELEGIBILIDAD';
-- CRITERIO_PRIORIZACION
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'CRITERIO_PRIORIZACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'CRITERIO_PRIORIZACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'ponderacion', 'FIELD', 3 FROM catalog WHERE code = 'CRITERIO_PRIORIZACION';
-- RANGO_PRIORIZACION
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'RANGO_PRIORIZACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'RANGO_PRIORIZACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'rango_puntaje', 'FIELD', 3 FROM catalog WHERE code = 'RANGO_PRIORIZACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'implicacion', 'FIELD', 4 FROM catalog WHERE code = 'RANGO_PRIORIZACION';
-- TIPO_CAPITAL
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'TIPO_CAPITAL';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'TIPO_CAPITAL';
-- TIPO_COSTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'TIPO_COSTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'TIPO_COSTO';
-- TAMANO_PROYECTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'TAMANO_PROYECTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'TAMANO_PROYECTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'rango_monto', 'FIELD', 3 FROM catalog WHERE code = 'TAMANO_PROYECTO';
-- COMPLEJIDAD_PROYECTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'COMPLEJIDAD_PROYECTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'COMPLEJIDAD_PROYECTO';
-- RUTA_PREINVERSION
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'RUTA_PREINVERSION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'RUTA_PREINVERSION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'segun_tipo_capital', 'FIELD', 3 FROM catalog WHERE code = 'RUTA_PREINVERSION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'segun_tamano', 'FIELD', 4 FROM catalog WHERE code = 'RUTA_PREINVERSION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'segun_complejidad', 'FIELD', 5 FROM catalog WHERE code = 'RUTA_PREINVERSION';
-- ETAPA_PROYECTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ETAPA_PROYECTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ETAPA_PROYECTO';
-- HABILITACION_CAMPOS
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'HABILITACION_CAMPOS';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'HABILITACION_CAMPOS';
-- ENTREGABLE_ETAPA
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ENTREGABLE_ETAPA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ENTREGABLE_ETAPA';
-- ESTADO_ESTUDIO_PREINVERSION
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESTADO_ESTUDIO_PREINVERSION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESTADO_ESTUDIO_PREINVERSION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'criterio', 'FIELD', 3 FROM catalog WHERE code = 'ESTADO_ESTUDIO_PREINVERSION';
-- TIPO_FINANCIAMIENTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'TIPO_FINANCIAMIENTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'TIPO_FINANCIAMIENTO';
-- CLASIFICACION_ETAPA_FINANCIAMIENTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'CLASIFICACION_ETAPA_FINANCIAMIENTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'CLASIFICACION_ETAPA_FINANCIAMIENTO';
-- ESTADO_PRIPME
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESTADO_PRIPME';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESTADO_PRIPME';
-- NIVEL_PRIORIZACION_PRIPME
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'NIVEL_PRIORIZACION_PRIPME';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'NIVEL_PRIORIZACION_PRIPME';
-- ESTADO_REVISION_TECNICA
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESTADO_REVISION_TECNICA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESTADO_REVISION_TECNICA';
-- FORMATO_REPORTE_PRIPME
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'FORMATO_REPORTE_PRIPME';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'FORMATO_REPORTE_PRIPME';
-- ESTADOS_PROGRAMACION
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESTADOS_PROGRAMACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESTADOS_PROGRAMACION';
-- TIPO_TRANSACCION
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'TIPO_TRANSACCION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'TIPO_TRANSACCION';
-- TIPO_APROBACION
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'TIPO_APROBACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'TIPO_APROBACION';
-- AGRUPACION_OPERACIONAL
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'AGRUPACION_OPERACIONAL';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'AGRUPACION_OPERACIONAL';
-- FORMATO_EXPORTACION_UBICACION
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'FORMATO_EXPORTACION_UBICACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'FORMATO_EXPORTACION_UBICACION';
-- ESTADO_PROPUESTA_PAIP
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESTADO_PROPUESTA_PAIP';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESTADO_PROPUESTA_PAIP';
-- ESTADO_COMPARACION_PROYECTO_PRO11
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESTADO_COMPARACION_PROYECTO_PRO11';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESTADO_COMPARACION_PROYECTO_PRO11';
-- ESTADO_COMPARACION_PROYECTO_PRO09
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESTADO_COMPARACION_PROYECTO_PRO09';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESTADO_COMPARACION_PROYECTO_PRO09';
-- INSTANCIA_APROBACION
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'INSTANCIA_APROBACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'INSTANCIA_APROBACION';
-- DIMENSION_PRIORIZACION_ESCENARIOS
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'DIMENSION_PRIORIZACION_ESCENARIOS';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'DIMENSION_PRIORIZACION_ESCENARIOS';
-- ATRIBUTO_OBLIGATORIO_ESCENARIOS
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ATRIBUTO_OBLIGATORIO_ESCENARIOS';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ATRIBUTO_OBLIGATORIO_ESCENARIOS';
-- ESTADO_ESCENARIO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESTADO_ESCENARIO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESTADO_ESCENARIO';
-- HISTORICO_AUTORIZACION_TECHO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'HISTORICO_AUTORIZACION_TECHO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'HISTORICO_AUTORIZACION_TECHO';
-- ESTADO_SEMAFORO_AVANCE
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESTADO_SEMAFORO_AVANCE';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESTADO_SEMAFORO_AVANCE';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'color', 'FIELD', 3 FROM catalog WHERE code = 'ESTADO_SEMAFORO_AVANCE';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'criterio', 'FIELD', 4 FROM catalog WHERE code = 'ESTADO_SEMAFORO_AVANCE';
-- METODO_CONTRATACION
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'METODO_CONTRATACION';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'METODO_CONTRATACION';
-- CATEGORIA_PROCESO_ADMINISTRATIVO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'CATEGORIA_PROCESO_ADMINISTRATIVO';
-- PROCESO_ADMINISTRATIVO_HITO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'PROCESO_ADMINISTRATIVO_HITO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'PROCESO_ADMINISTRATIVO_HITO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'orden', 'FIELD', 3 FROM catalog WHERE code = 'PROCESO_ADMINISTRATIVO_HITO';
-- FUENTE_FINANCIAMIENTO_SEGUIMIENTO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'FUENTE_FINANCIAMIENTO_SEGUIMIENTO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'FUENTE_FINANCIAMIENTO_SEGUIMIENTO';
-- ESTADO_CONDICION_PREVIA
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'ESTADO_CONDICION_PREVIA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'ESTADO_CONDICION_PREVIA';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'descripcion', 'FIELD', 3 FROM catalog WHERE code = 'ESTADO_CONDICION_PREVIA';
-- TIPO_AJUSTE_CONVENIO
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'codigo', 'KEY', 1 FROM catalog WHERE code = 'TIPO_AJUSTE_CONVENIO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'nombre', 'FIELD', 2 FROM catalog WHERE code = 'TIPO_AJUSTE_CONVENIO';
INSERT INTO catalog_field (catalog_id, name, qualifier, posicion)
       SELECT id, 'descripcion', 'FIELD', 3 FROM catalog WHERE code = 'TIPO_AJUSTE_CONVENIO';

COMMIT;
