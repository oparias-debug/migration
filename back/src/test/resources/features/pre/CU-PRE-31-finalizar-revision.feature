# language: es
@CU-PRE-31 @rol:TECNICO_PRE @rol:COORDINADOR_PRE
Característica: Finalizar la revisión de la programación PAP

  Como Técnico PRE o Coordinador PRE
  Quiero finalizar la revisión de la programación PAP (financiera y de metas físicas)

  Escenario: Finalizar la revisión (camino feliz, SF-6)
    Cuando el actor registra los campos "Comentarios al reporte financiero DGICP" y "Comentarios al reporte de metas físicas DGICP"
    Y hace clic en el botón "Revisión finalizada"
    Entonces el sistema guarda la información revisión finalizada
    Y actualiza el estado a "PAP Revisado" en el Monitoreo PAP de CU-PRO-25 "Monitoreo Programación PAP" (RN-D)

  Escenario: El botón Revisión Finalizada es visible y se habilita solo para Técnico PRE y Coordinador PRE
    Entonces el botón "REVISIÓN FINALIZADA" es visible y está habilitado únicamente para Técnico PRE y Coordinador PRE
    Y solo durante el período de ingreso de información o cuando se presenten modificaciones al PAP (RN-E)