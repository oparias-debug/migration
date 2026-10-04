# language: es
@CU-ADM-01 @HU-ADM-01-16 @rol:SISTEMA
Característica: Evaluar vigencia automáticamente

  Como Sistema (Planificador)
  Quiero evaluar diariamente las fechas de vigencia de catálogos y registros
  Para que su estado refleje siempre su vigencia

  # Reglas: RN-06, RN-14, RN-15, RN-16 · Subflujo: SF-14
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Sin contrato: proceso planificado (CatalogValidityJob), sin operación de servicio.

  @RN-15
  Esquema del escenario: Sin fechas de vigencia
    Dado <elemento> sin fechas de vigencia
    Cuando se ejecuta la evaluación diaria
    Entonces su estado es "ACTIVE"

    Ejemplos:
      | elemento           |
      | un catálogo activo |
      | un registro activo |

  @RN-16 @RN-14
  Escenario: TO DATE vencida en un registro
    Dado que "PAIS" es padre de "DEPTO"
    Y que el registro "COL" de "PAIS" tiene TO DATE igual a ayer y estado "ACTIVE"
    Y que los registros "ANT" y "CUN" de "DEPTO" tienen como registro padre "COL"
    Cuando se ejecuta la evaluación diaria
    Entonces el registro "COL" pasa a "INACTIVE"
    Y sus registros hijos "ANT" y "CUN" pasan a "INACTIVE"

  @RN-16 @RN-06
  Escenario: Catálogo vencido con hijo
    Dado el catálogo "PAIS" con TO DATE vencida, padre de "DEPTO"
    Y que "PAIS" y "DEPTO" tienen registros activos
    Cuando se ejecuta la evaluación diaria
    Entonces "PAIS", "DEPTO" y todos sus registros pasan a "INACTIVE"

  @RN-16
  Escenario: Una TO DATE futura no cambia el estado
    Dado un registro activo con TO DATE igual a mañana
    Cuando se ejecuta la evaluación diaria
    Entonces su estado es "ACTIVE"

  @RN-16 @S-03
  Escenario: La evaluación no reactiva elementos inactivos
    Dado un catálogo inactivo cuya TO DATE fue cambiada a una fecha futura
    Cuando se ejecuta la evaluación diaria
    Entonces su estado sigue siendo "INACTIVE"

  # ℹ️ Sin escenario (ver historias-CU-ADM-01.md, pendiente P-01): SF-14 inactiva con TO DATE "anterior a la
  # fecha actual", mientras que RN-12b y el modelo de dominio v4.0 (Validity.isExpired) consideran vencida
  # también la TO DATE igual a hoy. Se requiere decidir el criterio antes de escribir el escenario
  # "TO DATE igual a hoy".
