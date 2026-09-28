# language: es
@CU-PRE-33 @rol:COORDINADOR_PRO @rol:TECNICO_PRO @rol:JEFE_DGI @rol:SUBJEFE_DGI
Característica: Consultar el avance cuatrimestral de metas físicas del PAP

  Como Coordinador PRO, Técnico PRO, Jefe DGI o Subjefe DGI
  Quiero consultar el avance cuatrimestral de metas físicas del PAP, sin poder editarlo

  Escenario: Consulta de solo lectura para el resto de actores
    Cuando el actor accede a la pantalla "Avance de la ejecución Cuatrimestral de Metas del PAP"
    Entonces no ve ningún botón de acción en la tabla avance-metas
    Y solo tiene disponible el botón "Generar reporte" (RN-A.b)

  Escenario: La subsección de revisión DGICP solo es visible para actores internos
    Entonces la subsección "Revisión del avance cuatrimestral del PAP" solo es visible para los actores internos de la DGICP (RN-D.a)