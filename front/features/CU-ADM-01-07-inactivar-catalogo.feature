# language: es
@CU-ADM-01 @HU-ADM-01-07 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Inactivar un catálogo

  Como Administrador de Catálogos
  Quiero inactivar un catálogo
  Para dejarlo fuera de uso sin perder su historia

  # Reglas: RN-06, RN-12, RN-13, RN-16 · Subflujo: SF-05
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-07-inactivar-catalogo.yaml

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"
    Y que existe en el catalogMaster el catálogo "PAIS" con estado "ACTIVE" y los registros activos:
      | codPais | nombre   |
      | COL     | Colombia |
      | PER     | Perú     |

  @RN-06 @RN-12
  Escenario: Inactivación por estado
    Cuando fijo el estado del catálogo "PAIS" en "INACTIVE"
    Entonces el catálogo "PAIS" queda con estado "INACTIVE"
    Y la TO DATE del catálogo "PAIS" queda fijada en la fecha actual
    Y todos los registros del catálogo "PAIS" quedan con estado "INACTIVE"

  @RN-06 @RN-12 @RN-16
  Esquema del escenario: Inactivación por fecha
    Cuando fijo la TO DATE del catálogo "PAIS" en <fecha>
    Entonces el catálogo "PAIS" queda con estado "INACTIVE"
    Y todos los registros del catálogo "PAIS" quedan con estado "INACTIVE"

    Ejemplos:
      | fecha            |
      | la fecha actual  |
      | una fecha pasada |

  @RN-12
  Escenario: Fijar una TO DATE futura no inactiva el catálogo
    Cuando fijo la TO DATE del catálogo "PAIS" en una fecha futura
    Entonces el catálogo "PAIS" queda con estado "ACTIVE"
    Y los registros del catálogo "PAIS" conservan su estado

  @RN-06
  Escenario: Cascada recursiva a los catálogos hijos
    Dado que "PAIS" es padre de "DEPTO" y "DEPTO" es padre de "MUNICIPIO"
    Y que los catálogos "DEPTO" y "MUNICIPIO" tienen registros activos
    Cuando inactivo el catálogo "PAIS"
    Entonces los catálogos "PAIS", "DEPTO" y "MUNICIPIO" quedan con estado "INACTIVE"
    Y todos los registros de "PAIS", "DEPTO" y "MUNICIPIO" quedan con estado "INACTIVE"

  @RN-13 @E-24
  Escenario: Eliminación no permitida
    Cuando intento eliminar el catálogo "PAIS"
    Entonces el sistema rechaza la operación con el error "E-24" y el mensaje "Operación no permitida: solo se admite la inactivación."
    Y el catálogo "PAIS" sigue existiendo en el catalogMaster

  @S-08 @E-25
  Escenario: Rechazar la inactivación a un usuario sin rol de Administrador de Catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Cuando intento inactivar el catálogo "PAIS"
    Entonces el sistema rechaza la operación con el error "E-25" y el mensaje "No tiene permisos para modificar catálogos o registros."
    Y el catálogo "PAIS" queda con estado "ACTIVE"
