# language: es
@CU-ADM-01 @HU-ADM-01-09 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Crear un registro de un catálogo

  Como Administrador de Catálogos
  Quiero crear registros con valores para todos los campos
  Para poblar el catálogo

  # Reglas: RN-01, RN-04, RN-05, RN-11, RN-14, RN-15, RN-16 · Supuestos: S-06, S-09 · Subflujo: SF-07
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-09-crear-registro.yaml

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"
    Y que existe en el catalogMaster el catálogo plano "PAIS" con estado "ACTIVE" y los campos:
      | nombre     | calificador | tipo                         | posicion |
      | codPais    | KEY         | STRING{3}                    | 1        |
      | nombre     | FIELD       | STRING{60}                   | 2        |
      | continente | FIELD       | ENUM{"AMERICA","EUROPA"}     | 3        |
      | poblacion  | FIELD       | NUMERIC{0:2000000000}        | 4        |
      | fundacion  | FIELD       | FECHA{1000-01-01:2100-12-31} | 5        |

  @RN-01 @RN-11 @RN-15
  Escenario: Registro válido en catálogo plano
    Cuando creo en "PAIS", sin vigencia, el registro:
      | codPais | nombre   | continente | poblacion | fundacion  |
      | COL     | Colombia | AMERICA    | 52000000  | 1810-07-20 |
    Entonces el registro "COL" queda almacenado en "PAIS" con sus valores en versión STRING
    Y el registro "COL" queda con estado "ACTIVE"

  @RN-05
  Escenario: Registro hijo con id de registro padre
    Dado que "PAIS" es padre de "DEPTO" y existe el registro "COL" ACTIVE en "PAIS"
    Cuando creo en "DEPTO" el registro {codDepto:"ANT", nombre:"Antioquia"} con registro padre "COL"
    Entonces el registro "ANT" queda vinculado al registro padre "COL"

  @RN-05 @E-18
  Esquema del escenario: Rechazar un id de registro padre faltante, inexistente o inactivo
    Dado que "PAIS" es padre de "DEPTO"
    Y que existe el registro "ECU" con estado "INACTIVE" en "PAIS"
    Cuando intento crear en "DEPTO" el registro {codDepto:"ANT", nombre:"Antioquia"} <registro_padre>
    Entonces el sistema rechaza la operación con el error "E-18" y el mensaje "<mensaje>"
    Y no se crea el registro "ANT" en "DEPTO"

    Ejemplos:
      | registro_padre           | mensaje                                                     |
      | sin id de registro padre | Registro padre (no informado) inválido en el catálogo PAIS. |
      | con registro padre "ZZZ" | Registro padre ZZZ inválido en el catálogo PAIS.            |
      | con registro padre "ECU" | Registro padre ECU inválido en el catálogo PAIS.            |

  # Decisión del modelo de dominio v4.0 (CatalogAdminService.createRecord).
  @RN-05 @E-18 @modelo-dominio
  Escenario: Rechazar un id de registro padre en un catálogo plano
    Cuando intento crear en "PAIS" el registro "BRA" con valores válidos y registro padre "COL"
    Entonces el sistema rechaza la operación con el error "E-18" y el mensaje "Registro padre COL inválido: el catálogo PAIS no tiene catálogo padre."
    Y no se crea el registro "BRA" en "PAIS"

  @RN-11 @E-14
  Esquema del escenario: Rechazar un valor faltante o fuera de tipo o rango
    Cuando intento crear en "PAIS" un registro con valores válidos salvo el campo "<campo>" con el valor "<valor>"
    Entonces el sistema rechaza la operación con el error "E-14" y el mensaje "El valor <valor> no es válido para el campo <campo> (<tipo>)."
    Y no se crea el registro en "PAIS"

    Ejemplos:
      | campo      | valor      | tipo                         |
      | poblacion  | -5         | NUMERIC{0:2000000000}        |
      | poblacion  | muchos     | NUMERIC{0:2000000000}        |
      | continente | ASIA       | ENUM{AMERICA,EUROPA}         |
      | codPais    | COLO       | STRING{3}                    |
      | fundacion  | 0999-12-31 | FECHA{1000-01-01:2100-12-31} |
      | fundacion  | 20/07/1810 | FECHA{1000-01-01:2100-12-31} |

  @RN-04 @RN-11 @S-09
  Escenario: Un campo definido sin tipo acepta hasta 80 caracteres
    Dado que existe en el catalogMaster el catálogo plano "MONEDA" con estado "ACTIVE" y los campos:
      | nombre      | calificador | tipo      | posicion |
      | codMoneda   | KEY         | STRING{3} | 1        |
      | descripcion | FIELD       |           | 2        |
    Cuando creo en "MONEDA" el registro "USD" con un valor de 80 caracteres en "descripcion"
    Entonces el registro "USD" queda almacenado en "MONEDA" con sus valores en versión STRING

  @RN-04 @RN-11 @S-09 @E-14
  Escenario: Rechazar un valor que excede el tipo por defecto STRING{80}
    Dado que existe en el catalogMaster el catálogo plano "MONEDA" con estado "ACTIVE" y los campos:
      | nombre      | calificador | tipo      | posicion |
      | codMoneda   | KEY         | STRING{3} | 1        |
      | descripcion | FIELD       |           | 2        |
    Cuando intento crear en "MONEDA" el registro "USD" con un valor de 81 caracteres en "descripcion"
    Entonces el sistema rechaza la operación con el error "E-14" y un mensaje que indica el campo "descripcion" y el tipo "STRING{80}"
    Y no se crea el registro "USD" en "MONEDA"

  @RN-11 @E-14
  Escenario: Rechazar un registro al que le falta el valor de un campo
    Cuando intento crear en "PAIS" un registro sin valor para el campo "nombre"
    Entonces el sistema rechaza la operación con el error "E-14"
    Y no se crea el registro en "PAIS"

  # Decisión del modelo de dominio v4.0 (CatalogAdminService.createRecord, CatalogRecord.create).
  @RN-11 @E-21 @modelo-dominio
  Escenario: Rechazar un valor para un campo no definido en el catálogo
    Cuando intento crear en "PAIS" un registro con valores válidos y el campo adicional "moneda"
    Entonces el sistema rechaza la operación con el error "E-21" y el mensaje "El campo moneda no está definido en el catálogo PAIS."

  @RN-14 @E-13
  Escenario: Catálogo inactivo
    Dado que el catálogo "PAIS" está en estado "INACTIVE"
    Cuando intento crear un registro en "PAIS" con valores válidos
    Entonces el sistema rechaza la operación con el error "E-13" y el mensaje "No se pueden crear registros en un catálogo inactivo."

  @S-06 @E-17
  Escenario: Llave duplicada
    Dado que existe el registro "COL" en "PAIS"
    Cuando intento crear otro registro con codPais "COL" en "PAIS"
    Entonces el sistema rechaza la operación con el error "E-17" y el mensaje "Ya existe un registro con la llave COL."

  @RN-16
  Escenario: Un registro creado con TO DATE en el pasado queda INACTIVE
    Cuando creo en "PAIS" el registro "COL" con valores válidos y vigencia "2020-01-01:2021-01-01"
    Entonces el registro "COL" queda con estado "INACTIVE"

  @E-09
  Escenario: Rechazar un rango de vigencia invertido
    Cuando intento crear en "PAIS" el registro "COL" con valores válidos y vigencia "2027-01-01:2026-01-01"
    Entonces el sistema rechaza la operación con el error "E-09" y el mensaje "Rango de vigencia inválido."

  @S-08 @E-25
  Escenario: Rechazar la creación a un usuario sin rol de Administrador de Catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Cuando intento crear un registro en "PAIS" con valores válidos
    Entonces el sistema rechaza la operación con el error "E-25" y el mensaje "No tiene permisos para modificar catálogos o registros."
