# language: es
@CU-PRE-02 @rol:TECNICO_PRE
Característica: Acceder al caso asignado desde la Bandeja Preinversión

  Como Técnico PRE
  Quiero acceder al caso que me ha sido asignado en la Bandeja Preinversión

  Antecedentes:
    Dado que el Técnico PRE ingresa a la pantalla "Bandeja Preinversión"
    Y tiene un caso asignado por el Coordinador PRE

  Escenario: Acceder a un caso asignado de solicitud de CUP (FA-01)
    Cuando el Técnico PRE hace clic en el caso asignado correspondiente a una solicitud de CUP
    Entonces el sistema muestra la pantalla "Nuevo Registro" en el contexto de CU-PRE-01.5 "Revisión y Emisión de CUP"
    Y la sección "Revisión PRE" queda habilitada para el Técnico PRE

  Escenario: Acceder a un caso asignado de solicitud de Opinión Técnica (FA-02)
    Cuando el Técnico PRE hace clic en el caso asignado correspondiente a una solicitud de Opinión Técnica
    Entonces el sistema muestra la pantalla "Opinión Técnica" del caso de uso CU-PRE-26