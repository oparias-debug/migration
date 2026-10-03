# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Listar los calendarios registrados en el sistema

  Como cualquier usuario del sistema
  Quiero consultar y recuperar la lista de todos los calendarios registrados en el sistema

  Antecedentes:
    Dado que existen los calendarios "CAL-2026" (ACTIVO) y "CAL-2025" (INACTIVO)

  Escenario: Listar los calendarios registrados
    Cuando cualquier usuario consulta la lista de calendarios registrados
    Entonces el sistema devuelve una lista que incluye el código, nombre y estado de "CAL-2026" y de "CAL-2025"

  # ⚠️ Escenario pendiente: el CU no especifica el comportamiento cuando no hay
  # calendarios registrados en el sistema (lista vacía, error u otra respuesta).
  # No implementar hasta que se resuelva con el Analista Funcional.
