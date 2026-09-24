# language: es
@CU-PRE-30 @rol:TECNICO_PRE @rol:COORDINADOR_PRE @rol:COORDINADOR_PRO @rol:TECNICO_PRO @rol:JEFE_DGI @rol:SUBJEFE_DGI
Característica: Consultar la Programación Financiera Cuatrimestral del PAP

  Como Técnico PRE, Coordinador PRE, Coordinador PRO, Técnico PRO, Jefe DGI o Subjefe DGI
  Quiero consultar la información de la Programación Financiera Cuatrimestral del PAP, sin poder editarla

  Escenario: El Técnico PRE puede consultar todas las Unidades Ejecutoras
    Cuando el Técnico PRE accede a la pantalla "Programación Financiera Cuatrimestral del PAP"
    Entonces el selector "Unidad Ejecutora" le permite elegir cualquier Unidad Ejecutora del sistema (RN-A.a)

  Escenario: Consulta de solo lectura para el resto de actores
    Dado que el actor es Coordinador PRE, Coordinador PRO, Técnico PRO, Jefe DGI o Subjefe DGI
    Cuando accede a la pantalla "Programación Financiera Cuatrimestral del PAP"
    Entonces no ve ningún ícono de acción en la columna de acciones de la tabla
    Y solo tiene disponibles los botones "Generar reporte" y "Programación de Metas" (RN-A.c)