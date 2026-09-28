# language: es
@CU-PRE-33 @rol:TECNICO_PRE @rol:COORDINADOR_PRE
Característica: Registrar observaciones sobre el avance de metas físicas

  Como Técnico PRE o Coordinador PRE
  Quiero registrar observaciones sobre el avance de metas físicas del PAP

  Escenario: Registrar y enviar observaciones (camino feliz, SF-2 pasos 1-3)
    Cuando el actor registra información en el campo "Observaciones DGICP" avance-metas
    Y hace clic en el botón "Enviar observaciones" avance-metas
    Entonces el sistema guarda la información con la fecha de registro
    Y habilita el campo "Respuesta Institución" para el Técnico URP y los campos de los Anexos A.1 y A.4
    Y notifica al Técnico URP por correo electrónico que se han realizado observaciones avance-metas