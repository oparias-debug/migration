# language: es
@CU-ADM-01 @HU-ADM-01-15 @rol:USUARIO @rol:SISTEMA_CONSUMIDOR
Característica: Buscar registros hijos de un registro padre

  Como cualquier usuario o Sistema Consumidor
  Quiero obtener los registros hijos de un registro indicando el código de su catálogo y su valor KEY
  Para construir listas dependientes (p. ej. departamentos de un país)

  # Reglas: RN-24, RN-25 · Supuesto: S-08 · Subflujo: SF-13 · Algoritmo: 4.1
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-15-consultar-registros-hijos.yaml
  # El CU no define el orden del Result Set: la verificación es independiente del orden de los elementos.
  # Contrato de resultado: SF-13 (esquema ChildRecordsResult).

  Antecedentes:
    Dado que he iniciado sesión con un usuario autenticado
    Y que "PAIS" es padre de "DEPTO"
    Y que el catálogo "DEPTO" tiene los campos:
      | nombre   | calificador | tipo       | posicion |
      | codDepto | KEY         | STRING{3}  | 1        |
      | nombre   | FIELD       | STRING{60} | 2        |
      | capital  | FIELD       | STRING{60} | 3        |
    Y que el catálogo "PAIS" contiene los registros "COL" y "PER"
    Y que el catálogo "DEPTO" contiene los registros:
      | codDepto | nombre       | capital  | registro padre | estado   |
      | ANT      | Antioquia    | Medellín | COL            | ACTIVE   |
      | CUN      | Cundinamarca | Bogotá   | COL            | INACTIVE |

  @RN-24
  Escenario: Registro con hijos
    Cuando busco en el catálogo "PAIS" los hijos de "COL" sin indicar campos
    Entonces el sistema retorna:
      """json
      {
        "argumento": "COL",
        "codigoCatalogoPadre": "PAIS",
        "codigoCatalogoHijo": "DEPTO",
        "fieldSet": ["codDepto", "nombre"],
        "resultSet": [
          { "valores": ["ANT", "Antioquia"],    "estado": "ACTIVE" },
          { "valores": ["CUN", "Cundinamarca"], "estado": "INACTIVE" }
        ]
      }
      """

  @RN-24
  Escenario: Conjunto de campos sobre el catálogo hijo
    Cuando busco en el catálogo "PAIS" los hijos de "COL" con los campos "capital"
    Entonces el Field Set es "codDepto, capital"

  @RN-24
  Escenario: Registro padre sin hijos
    Cuando busco en el catálogo "PAIS" los hijos de "PER" sin indicar campos
    Entonces el Result Set es vacío
    Y el código del catálogo hijo del resultado es "DEPTO"

  @RN-24
  Escenario: Catálogo sin hijo
    Dado que "MONEDA" no tiene catálogo hijo
    Y que el catálogo "MONEDA" contiene un registro con KEY "USD"
    Cuando busco en el catálogo "MONEDA" los hijos del registro "USD"
    Entonces el sistema retorna:
      """json
      {
        "argumento": "USD",
        "codigoCatalogoPadre": "MONEDA",
        "codigoCatalogoHijo": null,
        "fieldSet": [],
        "resultSet": []
      }
      """

  @RN-24 @E-22
  Escenario: Registro padre inexistente
    Cuando busco en el catálogo "PAIS" los hijos de "ZZZ"
    Entonces el sistema reporta el error "E-22" con el mensaje "No existe un registro con la llave ZZZ."

  @RN-24 @E-10
  Escenario: Catálogo padre inexistente
    Cuando busco en el catálogo "NOEXISTE" los hijos de "COL"
    Entonces el sistema reporta el error "E-10" con el mensaje "El catálogo NOEXISTE no existe."

  @RN-24 @E-21
  Escenario: Campo no definido en el catálogo hijo
    Cuando busco en el catálogo "PAIS" los hijos de "COL" con los campos "poblacion"
    Entonces el sistema reporta el error "E-21" con el mensaje "El campo poblacion no está definido en el catálogo DEPTO."

  @RN-25 @S-08
  Esquema del escenario: Consulta por cualquier usuario
    Dado que <consultante> realiza la consulta
    Cuando busca en el catálogo "PAIS" los hijos de "COL"
    Entonces obtiene el mismo resultado que un administrador

    Ejemplos:
      | consultante                                      |
      | un usuario sin rol de Administrador de Catálogos |
      | un Sistema Consumidor                            |
