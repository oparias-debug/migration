# language: es
@CU-PRE-32 @rol:COORDINADOR_PRE @rol:COORDINADOR_PRO @rol:TECNICO_PRO @rol:JEFE_DGI @rol:SUBJEFE_DGI
Característica: Consultar el avance financiero cuatrimestral del PAP

  Como Coordinador PRE, Coordinador PRO, Técnico PRO, Jefe DGI o Subjefe DGI
  Quiero consultar el avance financiero cuatrimestral del PAP, sin poder editarlo

  Escenario: Consulta de solo lectura (camino feliz)
    Cuando el actor accede a la pantalla "Avance Financiero Cuatrimestral del PAP"
    Entonces no ve ningún botón de acción en la tabla avance-financiero
    Y solo tiene disponibles los botones "Generar reporte" y "Seguimiento de Metas" (RN-A.c)