# language: es
@CU-PRE-33 @rol:TECNICO_PRE @rol:COORDINADOR_PRE
Característica: Finalizar la revisión del avance cuatrimestral del PAP

  Como Técnico PRE o Coordinador PRE
  Quiero finalizar la revisión del avance cuatrimestral del PAP (financiero y de metas físicas)

  Escenario: Finalizar la revisión (camino feliz, SF-3)
    Cuando el actor registra los campos "Comentarios al reporte financiero DGICP" y "Comentarios al reporte de metas físicas DGICP" avance-metas
    Y hace clic en el botón "Revisión Finalizada" avance-metas
    Entonces el sistema guarda la información avance-metas
    Y actualiza el estado a "Revisado" en el Monitoreo PAP de CU-EJE-10 "Monitoreo del Avance Cuatrimestral del PAP" (RN-E)

  Escenario: El botón Revisión Finalizada es visible y se habilita solo para Técnico PRE y Coordinador PRE
    Entonces el botón "Revisión Finalizada" es visible y está habilitado únicamente para Técnico PRE y Coordinador PRE (RN-D.b)