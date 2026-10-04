# language: es
@CU-ADM-01 @HU-ADM-01-08 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Reactivar un catálogo

  Como Administrador de Catálogos
  Quiero reactivar un catálogo inactivo
  Para volver a utilizarlo

  # Reglas: RN-14, RN-19 · Supuestos: S-02, S-03 · Subflujo: SF-06
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-08-reactivar-catalogo.yaml

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"

  @RN-14 @S-02
  Esquema del escenario: Reactivación exitosa
    Dado que existe en el catalogMaster el catálogo plano "PAIS" con estado "INACTIVE"
    Cuando fijo el estado del catálogo "PAIS" en "ACTIVE" con <to_date>
    Entonces el catálogo "PAIS" queda con estado "ACTIVE"
    Y se permite crear registros en el catálogo "PAIS"

    Ejemplos:
      | to_date            |
      | la TO DATE vacía   |
      | una TO DATE futura |

  @RN-14 @S-03
  Escenario: La reactivación no reactiva registros ni catálogos hijos
    Dado que "PAIS" es padre de "DEPTO"
    Y que el catálogo "PAIS" fue inactivado junto con sus registros y con el catálogo "DEPTO"
    Cuando reactivo el catálogo "PAIS" con la TO DATE vacía
    Entonces el catálogo "PAIS" queda con estado "ACTIVE"
    Y los registros del catálogo "PAIS" siguen con estado "INACTIVE"
    Y el catálogo "DEPTO" sigue con estado "INACTIVE"

  @RN-14 @E-15
  Escenario: Padre inactivo
    Dado que "PAIS" está INACTIVE y es padre de "DEPTO"
    Y que el catálogo "DEPTO" está en estado "INACTIVE"
    Cuando intento reactivar "DEPTO"
    Entonces el sistema rechaza la operación con el error "E-15" y el mensaje "El catálogo padre PAIS está inactivo."

  @S-02 @E-16
  Escenario: TO DATE vencida
    Dado que existe en el catalogMaster el catálogo plano "PAIS" con estado "INACTIVE" y TO DATE en una fecha pasada
    Cuando intento reactivar el catálogo "PAIS" manteniendo una TO DATE pasada
    Entonces el sistema rechaza la operación con el error "E-16" y el mensaje "Debe actualizar la fecha de vigencia final."
    Y el catálogo "PAIS" sigue con estado "INACTIVE"

  @S-08 @E-25
  Escenario: Rechazar la reactivación a un usuario sin rol de Administrador de Catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Y que existe en el catalogMaster el catálogo plano "PAIS" con estado "INACTIVE"
    Cuando intento reactivar el catálogo "PAIS"
    Entonces el sistema rechaza la operación con el error "E-25" y el mensaje "No tiene permisos para modificar catálogos o registros."
