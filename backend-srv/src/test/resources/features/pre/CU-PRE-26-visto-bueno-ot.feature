# language: es
@CU-PRE-26 @rol:COORDINADOR_PRE @rol:TECNICO_PRE @rol:SISTEMA
Característica: Dar visto bueno a la Opinión Técnica

  Como Coordinador PRE
  Quiero dar el visto bueno a la Opinión Técnica preparada por el Técnico PRE

  Escenario: Dar el visto bueno de la OT
    Dado que el usuario autenticado tiene el rol "Coordinador PRE"
    Y el Técnico PRE registró y guardó las "Conclusiones" del proyecto
    Cuando revisa la información y da el "Visto bueno OT"
    Entonces el Sistema notifica al Técnico PRE el visto bueno de la OT
    Y habilita el botón "OT favorable"

  Escenario: Sin visto bueno no se habilita el botón "OT favorable"
    Dado que el Coordinador PRE no ha dado el "Visto bueno OT" del proyecto
    Cuando el Técnico PRE ingresa a la pantalla "Opinión Técnica" del proyecto
    Entonces el botón "OT favorable" no está habilitado

  # Sin escenario: naturaleza del "Visto bueno OT" (botón vs. casilla) y texto de la notificación al Técnico PRE. Ver historias-CU-PRE-26.md.
