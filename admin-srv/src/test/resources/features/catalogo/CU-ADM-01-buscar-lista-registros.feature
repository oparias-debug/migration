# language: es
@CU-ADM-01 @HU-ADM-01-11 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Buscar una lista de registros de un catálogo

  Como Administrador del Sistema
  Quiero buscar la lista de registros de un catálogo, indicando opcionalmente una lista de nombres de campos
  Para obtener los valores requeridos de todos los registros

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" con los campos, en este orden:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
      | sigla       | FIELD       |
    Y que el catálogo "CAT-A" contiene los registros:
      | codigo | descripcion   | sigla |
      | 01     | Descripción 1 | D1    |
      | 02     | Descripción 2 | D2    |

  Escenario: Listar registros sin indicar KEY y con una lista de campos
    Cuando busco la lista de registros del catálogo "CAT-A" solicitando los campos "descripcion, sigla"
    Entonces el sistema retorna para cada registro los valores:
      | descripcion   | sigla |
      | Descripción 1 | D1    |
      | Descripción 2 | D2    |

  Escenario: Listar registros sin lista de campos retorna el primer campo no KEY de todos los registros
    Cuando busco la lista de registros del catálogo "CAT-A" sin indicar lista de campos
    Entonces el sistema retorna para cada registro los valores:
      | descripcion   |
      | Descripción 1 |
      | Descripción 2 |

  Escenario: Retornar INACTIVE para todos los registros de un catálogo inactivo
    Dado que el catálogo "CAT-A" está en estado "INACTIVE"
    Cuando busco la lista de registros del catálogo "CAT-A" sin indicar lista de campos
    Entonces el sistema retorna "INACTIVE" para todos los registros del catálogo "CAT-A"

  Escenario: Listar los registros de un catálogo hijo indica el registro padre de cada uno
    Dado que existe el catálogo "CAT-HIJO" con catálogo padre "CAT-PADRE"
    Y que el catálogo "CAT-PADRE" contiene un registro con KEY "P01"
    Y que el catálogo "CAT-HIJO" contiene los registros enlazados al registro padre "P01":
      | codigo |
      | H01    |
      | H02    |
    Cuando busco la lista de registros del catálogo "CAT-HIJO" sin indicar lista de campos
    Entonces el sistema retorna para cada registro su registro padre:
      | keyValue | parentRecord |
      | H01      | P01          |
      | H02      | P01          |

  Escenario: Listar los registros de un catálogo sin padre no indica registro padre
    Cuando busco la lista de registros del catálogo "CAT-A" sin indicar lista de campos
    Entonces el sistema retorna para cada registro su registro padre:
      | keyValue | parentRecord |
      | 01       |              |
      | 02       |              |

  # ℹ️ Sin escenario (ambigüedad, ver historias-CU-ADM-01.md): el CU no indica qué ocurre al solicitar
  # en el listado un campo inexistente (la Regla 4 solo lo define para la búsqueda por KEY), ni qué se
  # retorna para un catálogo sin registros. Tampoco se genera escenario de permisos: la historia menciona
  # "(o un sistema consumidor)", actor que no figura en el catálogo de actores.
