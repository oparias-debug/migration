# language: es
@CU-PRE-31 @rol:COORDINADOR_PRO @rol:TECNICO_PRO @rol:JEFE_DGI @rol:SUBJEFE_DGI @rol:TECNICO_SYMP
Característica: Consultar la Programación de Metas Físicas del PAP

  Como Coordinador PRO, Técnico PRO, Jefe DGI, Subjefe DGI o Técnico SYMP
  Quiero consultar la información de la Programación de Metas Físicas del PAP, sin poder editarla

  Escenario: Consulta de solo lectura para el resto de actores
    Cuando el actor accede a la pantalla "Programación por Meta Física Cuatrimestral del PAP"
    Entonces no ve ningún botón de acción en la tabla
    Y solo tiene disponible el botón "Generar reporte" (RN-A.c)

  Escenario: Visibilidad del campo Comentarios al reporte DGICP para actores internos de la DGICP
    Dado que el actor es uno de los actores internos de la DGICP (Técnico PRE, Técnico PRO, Técnico SYMP, Coordinador PRE, Coordinador PRO, Coordinador SYMP, Subjefe DGI o Jefe DGI)
    Entonces puede visualizar el campo "Comentarios al reporte DGICP" (RN-C)