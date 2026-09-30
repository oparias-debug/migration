# language: es
@CU-ADM-01 @HU-ADM-01-10 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Buscar un registro específico por su clave

  Como Administrador del Sistema
  Quiero buscar un registro de un catálogo por el valor de su campo KEY, indicando opcionalmente una lista de nombres de campos
  Para obtener los valores requeridos de ese registro

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" con los campos, en este orden:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
      | sigla       | FIELD       |
    Y que el catálogo "CAT-A" contiene el registro:
      | codigo | descripcion   | sigla |
      | 01     | Descripción 1 | D1    |

  Escenario: Buscar por KEY indicando una lista de campos
    Cuando busco en el catálogo "CAT-A" el registro con KEY "01" solicitando los campos "descripcion, sigla"
    Entonces el sistema retorna los valores:
      | campo       | valor         |
      | descripcion | Descripción 1 |
      | sigla       | D1            |

  Escenario: Buscar por KEY sin lista de campos retorna el primer campo no KEY
    Cuando busco en el catálogo "CAT-A" el registro con KEY "01" sin indicar lista de campos
    Entonces el sistema retorna el valor "Descripción 1" del campo "descripcion"

  Escenario: Reportar error al buscar un valor de KEY sin registro asociado
    Cuando busco en el catálogo "CAT-A" el registro con KEY "99" sin indicar lista de campos
    Entonces el sistema reporta un error

  Escenario: Reportar error al solicitar un campo que no existe en el catálogo
    Cuando busco en el catálogo "CAT-A" el registro con KEY "01" solicitando los campos "campo_inexistente"
    Entonces el sistema reporta un error

  Esquema del escenario: Retornar INACTIVE al buscar por KEY cuando el catálogo o el registro está inactivo
    Dado que <elemento> está en estado "INACTIVE"
    Cuando busco en el catálogo "CAT-A" el registro con KEY "01" sin indicar lista de campos
    Entonces el sistema retorna "INACTIVE"

    Ejemplos:
      | elemento                                    |
      | el catálogo "CAT-A"                         |
      | el registro con KEY "01" del catálogo "CAT-A" |

  # ℹ️ Sin escenario de permisos (ambigüedad, ver historias-CU-ADM-01.md): la historia menciona
  # "(o un sistema consumidor)" como actor, que no figura en el catálogo de actores; no queda claro
  # si la búsqueda está restringida a quienes tienen permisos de administración de catálogos.
