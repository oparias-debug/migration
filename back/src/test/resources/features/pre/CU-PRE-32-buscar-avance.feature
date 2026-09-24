# language: es
@CU-PRE-32 @rol:TECNICO_URP
Característica: Buscar el avance financiero cuatrimestral

  Como Técnico URP
  Quiero buscar el avance financiero cuatrimestral filtrando por Unidad Ejecutora, año y período

  Escenario: Buscar el avance financiero cuatrimestral (camino feliz)
    Cuando el Técnico URP selecciona la "Unidad Ejecutora", el "Año" y el "Período"
    Y hace clic en el botón "Buscar" avance-financiero
    Entonces el sistema muestra la tabla "Ejecución Cuatrimestral del PAP" filtrada según lo seleccionado

  Escenario: Valores por defecto de los filtros
    Entonces el campo "Unidad Ejecutora" muestra por defecto la del Técnico URP según credenciales
    Y el campo "Año" muestra por defecto el año vigente
    Y el campo "Período" muestra por defecto el cuatrimestre vigente (RN-A.a)

  Escenario: Solo se muestran los estudios activos del ejercicio consultado
    Entonces la tabla muestra únicamente los estudios activos para el ejercicio fiscal vigente al cuatrimestre del período de seguimiento, según CU-PRE-30 (RN-B.a)

  Escenario: Cambiar el Año y el Período filtra la tabla a nivel de datos
    Dado que otro estudio de la Unidad Ejecutora tiene Programación Financiera únicamente en el año siguiente
    Cuando el Técnico URP cambia el "Año" y el "Período" y hace clic en "Buscar" avance-financiero
    Entonces la tabla y su paginación solo contienen los estudios programados en el año seleccionado (RN-A.a, RN-B.a)
    Y las columnas del avance se calculan para el "Período" seleccionado (RN-E)