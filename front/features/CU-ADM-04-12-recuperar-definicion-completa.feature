# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Recuperar la definición completa de un calendario

  Como cualquier usuario del sistema
  Quiero recuperar la definición completa de un calendario presentando su código

  Antecedentes:
    Dado que existe un calendario ACTIVO con código "CAL-2026" con períodos LABORAL, NO_LABORAL y una excepción registrada

  Escenario: Recuperar la definición completa de un calendario existente
    Cuando cualquier usuario recupera la definición del calendario "CAL-2026"
    Entonces el sistema devuelve el código, nombre, descripción, fecha_desde, fecha_hasta, estado y todos los CalendarItems (períodos LABORAL, NO_LABORAL y excepciones) del calendario

  Escenario: Retornar error si el código de calendario no existe
    Cuando cualquier usuario recupera la definición del calendario "CAL-INEXISTENTE"
    Entonces el sistema retorna error indicando que el calendario no existe
