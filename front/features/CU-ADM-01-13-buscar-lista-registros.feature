# language: es
@CU-ADM-01 @HU-ADM-01-13 @rol:USUARIO @rol:SISTEMA_CONSUMIDOR
Característica: Listar registros de un catálogo

  Como cualquier usuario o Sistema Consumidor
  Quiero obtener todos los registros de un catálogo con los campos que indique
  Para poblar listas y reportes

  # Reglas: RN-04, RN-06, RN-08, RN-25 · Supuestos: S-08, S-09 · Subflujo: SF-11 · Algoritmo: 4.1
  # El CU no define el orden del Result Set: la verificación es independiente del orden de los elementos.
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-13-buscar-lista-registros.yaml
  # Contrato de resultado: SF-11 (esquema CatalogRecordsResult).

  Antecedentes:
    Dado que he iniciado sesión con un usuario autenticado
    Y que existe en el catalogMaster el catálogo "PAIS" con los campos:
      | nombre     | calificador | tipo                     | posicion |
      | codPais    | KEY         | STRING{3}                | 1        |
      | nombre     | FIELD       | STRING{60}               | 2        |
      | continente | FIELD       | ENUM{"AMERICA","EUROPA"} | 3        |
    Y que el catálogo "PAIS" contiene los registros:
      | codPais | nombre   | continente | estado   |
      | COL     | Colombia | AMERICA    | ACTIVE   |
      | PER     | Perú     | AMERICA    | ACTIVE   |
      | ESP     | España   | EUROPA     | INACTIVE |

  @RN-08
  Escenario: Listado con campos por defecto
    Cuando solicito el listado de registros de "PAIS" sin indicar campos
    Entonces el sistema retorna:
      """json
      {
        "codigoCatalogo": "PAIS",
        "fieldSet": ["codPais", "nombre"],
        "resultSet": [
          { "valores": ["COL", "Colombia"], "estado": "ACTIVE" },
          { "valores": ["PER", "Perú"],     "estado": "ACTIVE" },
          { "valores": ["ESP", "España"],   "estado": "INACTIVE" }
        ]
      }
      """

  @RN-08
  Escenario: Listado con un conjunto de campos sin KEY
    Cuando solicito el listado de registros de "PAIS" con los campos "continente"
    Entonces el Field Set es "codPais, continente"
    Y el Result Set contiene un elemento por registro, con sus valores en el orden del Field Set

  @RN-08 @E-21
  Escenario: Campo inexistente
    Cuando solicito el listado de registros de "PAIS" con los campos "moneda"
    Entonces el sistema reporta el error "E-21" con el mensaje "El campo moneda no está definido en el catálogo PAIS."

  @RN-08
  Escenario: Catálogo sin registros
    Dado que existe en el catalogMaster el catálogo "MONEDA" sin registros
    Cuando solicito el listado de registros de "MONEDA" sin indicar campos
    Entonces el Result Set es vacío

  @RN-04 @RN-08 @S-09
  Escenario: Un campo definido sin tipo forma parte del Field Set por defecto
    Dado que existe en el catalogMaster el catálogo "MONEDA" sin registros, con los campos:
      | nombre      | calificador | tipo      | posicion |
      | codMoneda   | KEY         | STRING{3} | 1        |
      | descripcion | FIELD       |           | 2        |
    Cuando solicito el listado de registros de "MONEDA" sin indicar campos
    Entonces el sistema retorna:
      """json
      {
        "codigoCatalogo": "MONEDA",
        "fieldSet": ["codMoneda", "descripcion"],
        "resultSet": []
      }
      """

  @RN-06 @RN-08
  Escenario: Los registros de un catálogo inactivo se listan como INACTIVE
    Dado que el catálogo "PAIS" fue inactivado
    Cuando solicito el listado de registros de "PAIS" sin indicar campos
    Entonces todos los elementos del Result Set tienen estado "INACTIVE"

  @RN-08 @E-10
  Escenario: Catálogo inexistente
    Cuando solicito el listado de registros de "NOEXISTE" sin indicar campos
    Entonces el sistema reporta el error "E-10" con el mensaje "El catálogo NOEXISTE no existe."

  @RN-25 @S-08
  Esquema del escenario: Listado por cualquier usuario
    Dado que <consultante> realiza la consulta
    Cuando solicita el listado de registros de "PAIS"
    Entonces obtiene el mismo resultado que un administrador

    Ejemplos:
      | consultante                                      |
      | un usuario sin rol de Administrador de Catálogos |
      | un Sistema Consumidor                            |
