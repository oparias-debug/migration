# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Registrar una excepción sobre una fecha

  Como ADMINISTRADOR o ADMINISTRADOR_CALENDARIO
  Quiero registrar una excepción puntual sobre una fecha, indicando su tipo y una descripción

  Antecedentes:
    Dado que existe un calendario ACTIVO con código "CAL-2026", fecha de inicio "2026-01-01" y fecha de fin "2026-12-31"

  Esquema del escenario: Registrar una excepción dentro del rango del calendario
    Dado que el actor tiene el rol "ADMINISTRADOR"
    Cuando el actor registra en el calendario "CAL-2026" una excepción con fecha "2026-05-01", tipo "<tipo>" y descripción "<descripcion>"
    Entonces la excepción se registra correctamente
    Y la excepción hereda el estado "ACTIVO" del calendario

    Ejemplos:
      | tipo           | descripcion            |
      | DIA_LABORAL    | Jornada especial       |
      | DIA_NO_LABORAL | Día festivo declarado  |

  Escenario: Rechazar la excepción si la fecha está fuera del rango del calendario
    Cuando el actor intenta registrar en el calendario "CAL-2026" una excepción con fecha "2027-01-01"
    Entonces el sistema rechaza la operación indicando que la fecha no está enmarcada dentro del rango del calendario

  Escenario: Rechazar la operación cuando el actor no tiene el rol adecuado
    Dado que el actor no tiene el rol "ADMINISTRADOR" ni "ADMINISTRADOR_CALENDARIO"
    Cuando el actor intenta registrar una excepción en el calendario "CAL-2026"
    Entonces el sistema rechaza la operación por falta de permisos
