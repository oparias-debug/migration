# CU-ADM-01 — Administración de Catálogos

## 1. Nombre del Caso de Uso
**CU-ADM-01 — Administración de Catálogos**

## 2. Actores
- **Administrador de Catálogos** (actor primario): usuario responsable de definir, mantener e inactivar catálogos y sus registros.
- **Sistema de Catálogos** (actor de soporte): componente que valida y persiste la información.

## 3. Descripción / Propósito
Permite crear, consultar, actualizar e inactivar catálogos (estructuras maestras de datos) y sus registros, garantizando la integridad de las claves, los campos y la vigencia (activo/inactivo) de cada catálogo y de cada registro que contiene.

## 4. Precondiciones
- El actor debe estar autenticado y autorizado para administrar catálogos.
- Para crear un catálogo hijo, el catálogo padre (PARENT) debe existir previamente.

## 5. Postcondiciones
- El catálogo y/o sus registros quedan creados, actualizados o inactivados según la operación ejecutada.
- Un catálogo o registro inactivado nunca queda eliminado; solo cambia su estado de vigencia.

## 6. Flujo Principal — Crear Catálogo
1. El Administrador solicita crear un nuevo catálogo.
2. El sistema solicita: código, nombre, catálogo padre (opcional), estado (ACTIVE/INACTIVE) y vigencia (rango de fechas, opcional).
3. El Administrador define la lista de campos (FIELD/KEY) del catálogo, indicando para cada uno: nombre, tipo (NUMBER, STRING, DATE o ENUM) y su respectiva restricción de tipo.
4. El sistema valida que:
   - Exista al menos un campo definido (regla 17).
   - Exista al menos un campo marcado como KEY (regla 2).
   - No haya nombres de campo repetidos (regla 3).
   - Si no se indicaron fechas de vigencia, se asigna estado ACTIVE (regla 12).
   - Si la fecha "hasta" (TO DATE) es anterior a la fecha actual, el catálogo queda INACTIVE (regla 13).
5. El sistema crea el catálogo y lo agrega al catalogMaster.

## 7. Flujos Alternativos

### 7.1 Crear Registro de Catálogo
1. El Administrador selecciona un catálogo existente y solicita crear un registro.
2. Provee un valor para cada campo definido en el catálogo (regla 7).
3. El sistema valida que el valor de la clave (KEY) no exista ya en el catálogo.
4. El sistema aplica las reglas de vigencia del registro (reglas 12 y 13, análogas a las del catálogo).
5. El sistema almacena el nuevo registro.

### 7.2 Buscar un Registro por Clave
1. El Administrador (o un proceso consumidor) provee el código del catálogo, el valor de la clave (KEY) y, opcionalmente, una lista de nombres de campos a retornar.
2. El sistema localiza el registro con esa clave.
   - Si no existe, se reporta un error (regla 3, punto de búsqueda).
3. Si no se especificó lista de campos, el sistema retorna el valor del primer campo no-KEY del registro (regla 3).
4. Si se especificó una lista de campos, el sistema valida que cada nombre exista en el catálogo.
   - Si algún campo no existe, se reporta un error.
5. El sistema retorna el conjunto de valores solicitados.
6. Si el catálogo está marcado INACTIVE, el sistema retorna INACTIVO para todos los registros consultados (regla 11).

### 7.3 Buscar Lista de Registros
1. El Administrador solicita la lista de registros de un catálogo sin especificar clave, indicando opcionalmente una lista de nombres de campos.
2. Si no se especifica lista de campos, el sistema retorna el valor del primer campo no-KEY de todos los registros (regla 4).
3. El sistema retorna la lista de valores de los campos solicitados para todos los registros encontrados.

### 7.4 Buscar Catálogo por Nombre (catalogMaster)
1. Se provee un nombre de catálogo.
2. El sistema indica si existe un catálogo definido con ese nombre.

### 7.5 Buscar Catálogos Hijos
1. Se provee el código de un catálogo padre.
2. El sistema retorna la lista de {código, nombre} de todos los catálogos que lo referencian como PARENT (regla 14).

### 7.6 Actualizar Catálogo
1. El Administrador selecciona un catálogo y modifica nombre, padre, estado activo/inactivo y/o vigencia.
2. El sistema valida que el código no se esté modificando (regla 16, el código es inmutable).
3. Si se modifican los campos (fields) del catálogo, el sistema valida primero que el catálogo no contenga registros (regla 18); si ya contiene registros, se rechaza la modificación de los campos.
4. El sistema aplica los cambios permitidos.

### 7.7 Actualizar Registro
1. El Administrador selecciona un registro y modifica el valor de uno o más campos no-KEY.
2. El sistema valida que no se intente modificar el campo KEY (regla 15).
   - Si se intenta, se reporta un error.
3. El sistema aplica los cambios.

### 7.8 Inactivar Catálogo o Registro
1. El Administrador solicita inactivar un catálogo o un registro, mediante una de dos formas:
   a) Cambiando el flag ACTIVE a INACTIVE → el sistema asigna automáticamente la TO DATE con la fecha actual (regla 8a).
   b) Estableciendo la TO DATE en la fecha actual o una fecha pasada (regla 8b).
2. El sistema marca el catálogo/registro como INACTIVE.
3. El catálogo o registro **no se elimina**; permanece almacenado con estado INACTIVE (reglas 9 y 10).

## 8. Reglas de Negocio Asociadas
- Un catálogo debe tener al menos un campo (regla 17) y al menos un campo KEY (regla 2).
- Los nombres de los campos de un catálogo deben ser únicos (regla 3).
- El código de un catálogo es inmutable; nombre, padre, estado y vigencia sí pueden actualizarse (regla 16).
- El campo KEY de un registro es inmutable; los demás campos sí pueden actualizarse (regla 15).
- Los campos (fields) de un catálogo solo pueden modificarse si el catálogo aún no tiene registros (regla 18).
- Sin fechas de vigencia especificadas, el estado por defecto es ACTIVE (regla 12).
- Una fecha "hasta" (TO DATE) en el pasado implica estado INACTIVE (regla 13).
- Un catálogo INACTIVE hace que toda búsqueda de sus registros retorne INACTIVO (regla 11).
- Ni catálogos ni registros pueden eliminarse, solo inactivarse (reglas 9 y 10).

## 9. Excepciones / Manejo de Errores
- Búsqueda por KEY inexistente → error.
- Búsqueda por nombre de campo inexistente → error.
- Intento de modificar el código de un catálogo → rechazado.
- Intento de modificar el campo KEY de un registro → rechazado.
- Intento de modificar los fields de un catálogo que ya tiene registros → rechazado.
- Intento de crear catálogo sin campo KEY o sin campos → rechazado.
