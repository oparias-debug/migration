# language: es
@CU-ADM-01 @HU-ADM-01-06 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Establecer jerarquía padre-hijo

  Como Administrador de Catálogos
  Quiero asignar un catálogo padre a otro catálogo
  Para representar estructuras jerárquicas (p. ej. País → Departamento)

  # Reglas: RN-05, RN-17 · Supuesto: S-04 · Subflujos: SF-01, SF-04, SF-15
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-06-establecer-jerarquia-catalogo.yaml

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"

  @RN-05
  Escenario: Asignación de padre válida
    Dado que existen los catálogos "PAIS" (sin hijo, ACTIVE) y "DEPTO"
    Cuando asigno "PAIS" como padre de "DEPTO"
    Entonces "DEPTO" referencia a "PAIS" como padre
    Y "PAIS" registra a "DEPTO" como su hijo

  @RN-05 @E-07
  Escenario: El padre ya tiene hijo
    Dado que "PAIS" ya es padre de "DEPTO"
    Y que existe en el catalogMaster el catálogo "REGION"
    Cuando intento asignar "PAIS" como padre de "REGION"
    Entonces el sistema rechaza la operación con el error "E-07" y el mensaje "El catálogo PAIS ya tiene un catálogo hijo (DEPTO)."

  @RN-05 @E-08
  Esquema del escenario: Rechazar una relación que genera un ciclo
    Dado que "PAIS" es padre de "DEPTO"
    Cuando intento asignar "<padre>" como padre de "<hijo>"
    Entonces el sistema rechaza la operación con el error "E-08" y el mensaje "La relación padre-hijo genera un ciclo."

    Ejemplos:
      | padre | hijo |
      | DEPTO | PAIS |
      | PAIS  | PAIS |

  @RN-05 @E-06
  Escenario: Padre inexistente
    Dado que existe en el catalogMaster el catálogo "DEPTO"
    Cuando asigno como padre de "DEPTO" el código "NOEXISTE"
    Entonces el sistema rechaza la operación con el error "E-06" y el mensaje "El catálogo padre NOEXISTE no existe."

  @RN-05 @E-15
  Escenario: Rechazar un catálogo padre inactivo
    Dado que existe en el catalogMaster el catálogo "PAIS" con estado "INACTIVE"
    Y que existe en el catalogMaster el catálogo "DEPTO"
    Cuando intento asignar "PAIS" como padre de "DEPTO"
    Entonces el sistema rechaza la operación con el error "E-15" y el mensaje "El catálogo padre PAIS está inactivo."

  @RN-05
  Escenario: Quitar el padre de un catálogo sin registros
    Dado que "PAIS" es padre de "DEPTO"
    Y que el catálogo "DEPTO" no tiene registros
    Cuando quito el padre del catálogo "DEPTO"
    Entonces "DEPTO" queda como catálogo plano
    Y "PAIS" no tiene catálogo hijo

  @RN-05 @S-04
  Escenario: Exigir confirmación para cambiar el padre de un catálogo con registros
    Dado que "PAIS" es padre de "DEPTO"
    Y que el catálogo "DEPTO" contiene registros enlazados a registros de "PAIS"
    Cuando intento quitar el padre del catálogo "DEPTO" sin confirmar el cambio
    Entonces el sistema rechaza la operación con el error "S-04"
    Y "DEPTO" sigue referenciando a "PAIS" como padre

  @RN-05 @S-04
  Escenario: Quitar el padre de un catálogo con registros, con confirmación
    Dado que "PAIS" es padre de "DEPTO"
    Y que el catálogo "DEPTO" contiene registros enlazados a registros de "PAIS"
    Cuando quito el padre del catálogo "DEPTO" confirmando el cambio
    Entonces "DEPTO" queda como catálogo plano
    Y los registros de "DEPTO" quedan sin id de registro padre

  # Escenarios de padre NUEVO para un catálogo con registros (S-04): el CU exige "revisión de los ids de
  # registro padre" pero no define cómo se proporcionan. El modelo de dominio v4.0
  # (CatalogAdminService.updateDescriptors) recibe la correspondencia "KEY del registro -> KEY del nuevo
  # registro padre" y rechaza con E-18 si algún registro queda sin registro padre válido. Ver pendiente P-03.
  @RN-05 @S-04 @modelo-dominio
  Escenario: Asignar un padre nuevo a un catálogo con registros, con confirmación y registros padre
    Dado que existen los catálogos "PAIS" (sin hijo, ACTIVE) y "DEPTO"
    Y que el catálogo "PAIS" contiene el registro "COL" con estado "ACTIVE"
    Y que el catálogo plano "DEPTO" contiene los registros "ANT" y "CUN"
    Cuando asigno "PAIS" como padre de "DEPTO" confirmando el cambio y con los registros padre:
      | registro | registro padre |
      | ANT      | COL            |
      | CUN      | COL            |
    Entonces "DEPTO" referencia a "PAIS" como padre
    Y los registros "ANT" y "CUN" quedan vinculados al registro padre "COL"

  @RN-05 @S-04 @E-18 @modelo-dominio
  Esquema del escenario: Rechazar un padre nuevo si algún registro queda sin registro padre válido
    Dado que existen los catálogos "PAIS" (sin hijo, ACTIVE) y "DEPTO"
    Y que el catálogo "PAIS" contiene el registro "COL" con estado "ACTIVE"
    Y que el catálogo plano "DEPTO" contiene los registros "ANT" y "CUN"
    Cuando intento asignar "PAIS" como padre de "DEPTO" confirmando el cambio y con los registros padre:
      | registro | registro padre   |
      | ANT      | COL              |
      | CUN      | <registro_padre> |
    Entonces el sistema rechaza la operación con el error "E-18" y el mensaje "<mensaje>"
    Y "DEPTO" sigue siendo un catálogo plano

    Ejemplos:
      | registro_padre | mensaje                                                     |
      |                | Registro padre (no informado) inválido en el catálogo PAIS. |
      | ZZZ            | Registro padre ZZZ inválido en el catálogo PAIS.            |

  @S-08 @E-25
  Escenario: Rechazar la asignación de padre a un usuario sin rol de Administrador de Catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Cuando intento asignar "PAIS" como padre de "DEPTO"
    Entonces el sistema rechaza la operación con el error "E-25" y el mensaje "No tiene permisos para modificar catálogos o registros."
