# language: es
@CU-ADM-01 @HU-ADM-01-11 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Inactivar y reactivar un registro

  Como Administrador de Catálogos
  Quiero inactivar un registro
  Para retirarlo de uso sin eliminarlo

  # Reglas: RN-12, RN-14, RN-16 · Supuestos: S-02, S-03 · Subflujo: SF-09
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-11-inactivar-registro.yaml

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"
    Y que "PAIS" es padre de "DEPTO" y "DEPTO" es padre de "MUNICIPIO"
    Y que existe el registro "COL" con estado "ACTIVE" en "PAIS"
    Y que existen en "DEPTO" los registros "ANT" y "CUN" con registro padre "COL"
    Y que existe en "MUNICIPIO" el registro "MED" con registro padre "ANT"

  @RN-12
  Escenario: Inactivación por estado
    Cuando fijo el estado del registro "COL" en "INACTIVE"
    Entonces el registro "COL" queda con estado "INACTIVE"
    Y la TO DATE del registro "COL" queda fijada en la fecha actual

  @RN-12 @RN-16
  Esquema del escenario: Inactivación por fecha
    Cuando fijo la TO DATE del registro "COL" en <fecha>
    Entonces el registro "COL" queda con estado "INACTIVE"

    Ejemplos:
      | fecha            |
      | la fecha actual  |
      | una fecha pasada |

  @RN-14
  Escenario: Cascada recursiva a registros hijos
    Cuando inactivo el registro "COL"
    Entonces los registros "ANT" y "CUN" quedan con estado "INACTIVE"
    Y el registro "MED" queda con estado "INACTIVE"

  @RN-14 @S-03
  Escenario: Reactivación de un registro sin reactivar sus hijos
    Dado que el registro "COL" fue inactivado junto con sus registros hijos
    Cuando reactivo el registro "COL" con la TO DATE vacía
    Entonces el registro "COL" queda con estado "ACTIVE"
    Y los registros "ANT" y "CUN" siguen con estado "INACTIVE"

  @RN-14 @E-20
  Esquema del escenario: Reactivación con catálogo o registro padre inactivo
    Dado que el registro "ANT" está en estado "INACTIVE"
    Y que <elemento> está en estado "INACTIVE"
    Cuando intento reactivar el registro hijo "ANT"
    Entonces el sistema rechaza la operación con el error "E-20" y el mensaje "No se puede reactivar: el catálogo o registro padre está inactivo."

    Ejemplos:
      | elemento            |
      | el registro "COL"   |
      | el catálogo "DEPTO" |

  @S-02 @E-16
  Escenario: Rechazar la reactivación manteniendo una TO DATE vencida
    Dado que el registro "COL" está en estado "INACTIVE" con TO DATE en una fecha pasada
    Cuando intento reactivar el registro "COL" manteniendo una TO DATE pasada
    Entonces el sistema rechaza la operación con el error "E-16" y el mensaje "Debe actualizar la fecha de vigencia final."

  @RN-14 @E-24
  Escenario: Eliminación no permitida
    Cuando intento eliminar el registro "COL"
    Entonces el sistema rechaza la operación con el error "E-24" y el mensaje "Operación no permitida: solo se admite la inactivación."
    Y el registro "COL" sigue existiendo en "PAIS"

  @S-08 @E-25
  Escenario: Rechazar la inactivación a un usuario sin rol de Administrador de Catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Cuando intento inactivar el registro "COL"
    Entonces el sistema rechaza la operación con el error "E-25" y el mensaje "No tiene permisos para modificar catálogos o registros."
    Y el registro "COL" queda con estado "ACTIVE"
