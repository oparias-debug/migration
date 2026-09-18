# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Registrar una excepción sobre una fecha

  Como ADMINISTRADOR o ADMINISTRADOR_CALENDARIO
  Quiero registrar una excepción puntual sobre una fecha, indicando su tipo y una descripción
  Para reclasificar el tipo de un día concreto independientemente de los períodos definidos

  Esquema del escenario: Registrar una excepción sobre una fecha con cada tipo posible
    Dado un calendario existente
    Cuando el actor registra una excepción sobre una fecha con tipo "<tipo>" y una descripción
    Entonces la excepción queda registrada en el calendario con el tipo "<tipo>"
    Y la excepción hereda el estado del calendario

    Ejemplos:
      | tipo             |
      | DIA_LABORAL      |
      | DIA_NO_LABORAL   |

  Escenario: La excepción prevalece sobre la clasificación derivada de los períodos
    Dado una fecha que cae dentro de un período LABORAL o NO_LABORAL definido en el calendario
    Y existe una excepción registrada sobre esa misma fecha con un tipo distinto al que resultaría del período
    Cuando se consulta el tipo de esa fecha
    Entonces el resultado corresponde al tipo indicado en la excepción, no al del período

  Escenario: Rechazar el registro de una excepción a un actor sin el rol adecuado
    Dado que el actor autenticado no tiene el rol ADMINISTRADOR ni ADMINISTRADOR_CALENDARIO
    Cuando el actor intenta registrar una excepción sobre una fecha
    Entonces el sistema rechaza la operación por falta de permisos

  # ⚠️ Escenario pendiente: el CU no especifica si la fecha de la excepción debe validarse contra
  # el rango del calendario, ni si la descripción es un campo obligatorio con rechazo explícito
  # si falta. No implementar validaciones para estos dos puntos hasta que se resuelva con el
  # Analista Funcional / Gestor de Requisitos.
