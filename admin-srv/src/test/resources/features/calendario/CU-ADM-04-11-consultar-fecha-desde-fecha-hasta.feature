# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Consultar fecha_desde y fecha_hasta de un calendario

  Como cualquier usuario del sistema
  Quiero consultar cuáles son la fecha_desde y la fecha_hasta de un calendario presentando su código

  Antecedentes:
    Dado que existe un calendario ACTIVO con código "CAL-2026", fecha de inicio "2026-01-01" y fecha de fin "2026-12-31"

  Escenario: Consultar el rango de fechas de un calendario existente
    Cuando cualquier usuario consulta la fecha_desde y fecha_hasta del calendario "CAL-2026"
    Entonces el sistema devuelve fecha_desde "2026-01-01" y fecha_hasta "2026-12-31"

  Escenario: Retornar error si el código de calendario no existe
    Cuando cualquier usuario consulta la fecha_desde y fecha_hasta del calendario "CAL-INEXISTENTE"
    Entonces el sistema retorna error indicando que el calendario no existe
