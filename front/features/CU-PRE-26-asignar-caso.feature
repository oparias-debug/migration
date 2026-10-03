# language: es
@CU-PRE-26 @rol:COORDINADOR_PRE @rol:TECNICO_PRE @rol:SISTEMA
Característica: Asignar la solicitud de Opinión Técnica a un Técnico PRE

  Como Coordinador PRE
  Quiero asignar la solicitud de Opinión Técnica a un Técnico PRE

  Escenario: Notificar al Técnico PRE la asignación del caso
    Dado que existe una solicitud de Opinión Técnica para un proyecto
    Y el usuario autenticado tiene el rol "Coordinador PRE"
    Cuando asigna el caso a un Técnico PRE
    Entonces el Sistema envía al Técnico PRE asignado el correo electrónico del Anexo A2 b

  # Sin escenario: pantalla y validaciones de la asignación — no descritas en el CU.
