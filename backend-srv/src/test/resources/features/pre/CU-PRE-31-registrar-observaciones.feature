# language: es
@CU-PRE-31 @rol:TECNICO_PRE @rol:COORDINADOR_PRE
Característica: Registrar observaciones a la programación del PAP

  Como Técnico PRE o Coordinador PRE
  Quiero registrar observaciones a la programación financiera y de metas físicas del PAP

  Escenario: Registrar y enviar observaciones (camino feliz, SF-3)
    Cuando el actor registra información en el campo "Observaciones DGICP"
    Y hace clic en el botón "Enviar observaciones"
    Entonces el sistema guarda la información con la fecha de registro observaciones
    Y habilita los campos del Anexo A.1 y A.4, así como el campo "Respuesta Institución" para el Técnico URP
    Y notifica al Técnico URP por correo electrónico que se han realizado observaciones