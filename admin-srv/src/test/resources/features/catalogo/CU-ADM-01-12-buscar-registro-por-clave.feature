# language: es
@CU-ADM-01 @HU-ADM-01-12 @rol:USUARIO @rol:SISTEMA_CONSUMIDOR
Característica: Buscar un registro por llave

  Como cualquier usuario o Sistema Consumidor
  Quiero buscar un registro por su valor KEY indicando opcionalmente los campos a retornar
  Para obtener solo la información que necesito

  # Reglas: RN-07, RN-25 · Supuesto: S-08 · Subflujo: SF-10 · Algoritmo: 4.1
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-12-buscar-registro-por-clave.yaml
  # Contrato de resultado: SF-10 (esquema KeySearchResult).

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
      | ESP     | España   | EUROPA     | INACTIVE |

  @RN-07
  Escenario: Sin conjunto de campos
    Cuando busco "COL" en el catálogo "PAIS" sin indicar campos
    Entonces el sistema retorna:
      """json
      {
        "argumentoBusqueda": "COL",
        "codigoCatalogo": "PAIS",
        "fieldSet": ["codPais", "nombre"],
        "resultSet": [
          { "valores": ["COL", "Colombia"], "estado": "ACTIVE" }
        ]
      }
      """

  @RN-07
  Escenario: Conjunto de campos sin KEY
    Cuando busco "COL" en el catálogo "PAIS" con los campos "continente"
    Entonces el Field Set es "codPais, continente"
    Y el Result Set contiene:
      | valores      | estado |
      | COL, AMERICA | ACTIVE |

  @RN-07
  Escenario: Conjunto de campos que incluye el KEY se usa tal cual
    Cuando busco "COL" en el catálogo "PAIS" con los campos "continente, codPais"
    Entonces el Field Set es "continente, codPais"
    Y el Result Set contiene:
      | valores      | estado |
      | AMERICA, COL | ACTIVE |

  @RN-07 @E-21
  Escenario: Campo inexistente
    Cuando busco "COL" en el catálogo "PAIS" con los campos "moneda"
    Entonces el sistema reporta el error "E-21" con el mensaje "El campo moneda no está definido en el catálogo PAIS."

  @RN-07
  Escenario: Registro no encontrado
    Cuando busco "ZZZ" en el catálogo "PAIS" sin indicar campos
    Entonces el sistema retorna:
      """json
      {
        "argumentoBusqueda": "ZZZ",
        "codigoCatalogo": "PAIS",
        "fieldSet": ["codPais", "nombre"],
        "resultSet": []
      }
      """

  @RN-07 @S-08
  Escenario: Un registro inactivo se retorna con su estado
    Cuando busco "ESP" en el catálogo "PAIS" sin indicar campos
    Entonces el Result Set contiene:
      | valores     | estado   |
      | ESP, España | INACTIVE |

  @RN-07 @E-10
  Escenario: Catálogo inexistente
    Cuando busco "COL" en el catálogo "NOEXISTE" sin indicar campos
    Entonces el sistema reporta el error "E-10" con el mensaje "El catálogo NOEXISTE no existe."

  # Decisión del modelo de dominio v4.0 (Catalog.buildFieldSet): si el catálogo solo tiene el campo KEY,
  # el Field Set por defecto es [KEY]. Ver pendiente P-09.
  @RN-07 @modelo-dominio
  Escenario: Catálogo con un único campo
    Dado que existe en el catalogMaster el catálogo "MONEDA" con el único campo KEY "codMoneda" STRING{3}
    Y que el catálogo "MONEDA" contiene un registro con KEY "USD"
    Cuando busco "USD" en el catálogo "MONEDA" sin indicar campos
    Entonces el sistema retorna:
      """json
      {
        "argumentoBusqueda": "USD",
        "codigoCatalogo": "MONEDA",
        "fieldSet": ["codMoneda"],
        "resultSet": [
          { "valores": ["USD"], "estado": "ACTIVE" }
        ]
      }
      """

  @RN-25 @S-08
  Esquema del escenario: Búsqueda por cualquier usuario
    Dado que <consultante> realiza la consulta
    Cuando busca "COL" en el catálogo "PAIS"
    Entonces obtiene el mismo resultado que un administrador

    Ejemplos:
      | consultante                                      |
      | un usuario sin rol de Administrador de Catálogos |
      | un Sistema Consumidor                            |
