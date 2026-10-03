# language: es
@CU-PRE-32 @rol:TECNICO_URP
Característica: Navegar al Avance Cuatrimestral por Metas Físicas

  Como Técnico URP
  Quiero navegar al Avance Cuatrimestral por Metas Físicas del PAP

  Escenario: Navegar a CU-PRE-33 (camino feliz, SF-2)
    Dado que el Técnico URP se encuentra en la pantalla "Avance Financiero Cuatrimestral del PAP" avance-financiero
    Cuando hace clic en el botón "Siguiente" avance-financiero
    Entonces el sistema muestra el Anexo A.1 de CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP"