# language: es
@CU-ADM-01 @HU-ADM-01-02 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Definir campos y tipos del catálogo

  Como Administrador de Catálogos
  Quiero definir los campos con su cabecera (KEY/FIELD), nombre, tipo (NUMERIC, STRING, FECHA, ENUM; opcional, por defecto STRING{80}) y posición
  Para que los registros solo acepten valores válidos

  # Reglas: RN-03, RN-04, RN-20, RN-21, RN-22 · Supuestos: S-05, S-09 · Subflujos: SF-01, SF-04
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-02-actualizar-campos-catalogo.yaml
  # La columna "tipo" vacía indica un campo definido sin tipo (RN-04: se asigna STRING{80}).

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"

  @RN-21
  Escenario: Modificar el tipo de un campo de un catálogo sin registros
    Dado que existe en el catalogMaster el catálogo "PAIS" sin registros, con los campos:
      | nombre  | calificador | tipo       | posicion |
      | codPais | KEY         | STRING{3}  | 1        |
      | nombre  | FIELD       | STRING{60} | 2        |
    Cuando cambio el campo "nombre" del catálogo "PAIS" a "STRING{80}"
    Entonces la definición del campo "nombre" del catálogo "PAIS" queda como "STRING{80}"

  @RN-21
  Escenario: Reemplazar la definición de campos de un catálogo sin registros
    Dado que existe en el catalogMaster el catálogo "PAIS" sin registros, con los campos:
      | nombre  | calificador | tipo       | posicion |
      | codPais | KEY         | STRING{3}  | 1        |
      | nombre  | FIELD       | STRING{60} | 2        |
    Cuando actualizo los campos del catálogo "PAIS" a:
      | nombre     | calificador | tipo                         | posicion |
      | codPais    | KEY         | STRING{3}                    | 1        |
      | nombre     | FIELD       | STRING{60}                   | 2        |
      | continente | FIELD       | ENUM{"AMERICA","EUROPA"}     | 3        |
      | poblacion  | FIELD       | NUMERIC{0:2000000000}        | 4        |
      | fundacion  | FIELD       | FECHA{1000-01-01:2100-12-31} | 5        |
    Entonces el catálogo "PAIS" queda con los campos ordenados por posición:
      | nombre     |
      | codPais    |
      | nombre     |
      | continente |
      | poblacion  |
      | fundacion  |

  @RN-04 @S-09
  Escenario: Un campo sin tipo definido queda como STRING{80}
    Dado que existe en el catalogMaster el catálogo "PAIS" sin registros, con los campos:
      | nombre  | calificador | tipo       | posicion |
      | codPais | KEY         | STRING{3}  | 1        |
      | nombre  | FIELD       | STRING{60} | 2        |
    Cuando actualizo los campos del catálogo "PAIS" a:
      | nombre      | calificador | tipo       | posicion |
      | codPais     | KEY         | STRING{3}  | 1        |
      | nombre      | FIELD       | STRING{60} | 2        |
      | descripcion | FIELD       |            | 3        |
    Entonces la definición del campo "descripcion" del catálogo "PAIS" queda como "STRING{80}"
    Y al consultar el catálogo "PAIS" el campo "descripcion" se muestra con tipo "STRING{80}"

  @RN-04 @RN-11 @S-09 @E-14
  Escenario: Rechazar un valor que excede el tipo por defecto STRING{80}
    Dado que existe en el catalogMaster el catálogo "PAIS" con los campos:
      | nombre      | calificador | tipo      | posicion |
      | codPais     | KEY         | STRING{3} | 1        |
      | descripcion | FIELD       |           | 2        |
    Cuando intento crear en "PAIS" el registro "COL" con un valor de 81 caracteres en "descripcion"
    Entonces el sistema rechaza la operación con el error "E-14" y un mensaje que indica el campo "descripcion" y el tipo "STRING{80}"

  @RN-04 @E-04
  Escenario: Rechazar nombres de campo repetidos
    Dado que existe en el catalogMaster el catálogo "PAIS" sin registros
    Cuando intento actualizar los campos del catálogo "PAIS" a:
      | nombre  | calificador | tipo       | posicion |
      | codPais | KEY         | STRING{3}  | 1        |
      | nombre  | FIELD       | STRING{60} | 2        |
      | nombre  | FIELD       | STRING{80} | 3        |
    Entonces el sistema rechaza la operación con el error "E-04" y el mensaje "El nombre de campo nombre está repetido."
    Y los campos del catálogo "PAIS" no cambian

  @RN-04 @E-05
  Esquema del escenario: Rechazar una definición de tipo inválida
    Dado que existe en el catalogMaster el catálogo "PAIS" sin registros
    Cuando intento definir en el catálogo "PAIS" el campo FIELD "campo" con tipo "<tipo>"
    Entonces el sistema rechaza la operación con el error "E-05" y el mensaje "Definición de tipo inválida en el campo campo."
    Y los campos del catálogo "PAIS" no cambian

    Ejemplos:
      | tipo                         | motivo                 |
      | NUMERIC{100:1}               | mínimo mayor al máximo |
      | STRING{0}                    | longitud no positiva   |
      | FECHA{2026-12-31:2026-01-01} | fechas invertidas      |
      | ENUM{}                       | lista vacía            |
      | ENUM{"A","A"}                | valores duplicados     |

  # Decisión del modelo de dominio v4.0: el valor KEY se replica en catalog_record.key_value (VARCHAR(255))
  # (ck_catalog_field_key_length).
  @RN-04 @E-05 @modelo-dominio
  Escenario: Rechazar un campo KEY de tipo STRING con longitud mayor a 255
    Cuando intento crear el catálogo "PAIS" con los campos:
      | nombre  | calificador | tipo        | posicion |
      | codPais | KEY         | STRING{256} | 1        |
    Entonces el sistema rechaza la operación con el error "E-05" y el mensaje "Definición de tipo inválida en el campo codPais."

  # Decisión del modelo de dominio v4.0: los valores se guardan en catalog_record_value.valor (VARCHAR(4000))
  # (ck_catalog_field_string).
  @RN-04 @E-05 @modelo-dominio
  Escenario: Rechazar un campo STRING con longitud mayor a 4000
    Dado que existe en el catalogMaster el catálogo "PAIS" sin registros
    Cuando intento definir en el catálogo "PAIS" el campo FIELD "campo" con tipo "STRING{4001}"
    Entonces el sistema rechaza la operación con el error "E-05" y el mensaje "Definición de tipo inválida en el campo campo."
    Y los campos del catálogo "PAIS" no cambian

  @S-05
  Esquema del escenario: Rechazar posiciones de campo repetidas o no positivas
    Dado que existe en el catalogMaster el catálogo "PAIS" sin registros
    Cuando intento actualizar los campos del catálogo "PAIS" a:
      | nombre  | calificador | tipo       | posicion          |
      | codPais | KEY         | STRING{3}  | 1                 |
      | nombre  | FIELD       | STRING{60} | <posicion_nombre> |
    Entonces el sistema rechaza la operación con el error "S-05"
    Y los campos del catálogo "PAIS" no cambian

    Ejemplos:
      | posicion_nombre |
      | 1               |
      | 0               |

  @RN-03 @E-03
  Escenario: Rechazar una actualización que deja el catálogo sin campo KEY
    Dado que existe en el catalogMaster el catálogo "PAIS" sin registros
    Cuando intento actualizar los campos del catálogo "PAIS" a:
      | nombre  | calificador | tipo       | posicion |
      | codPais | FIELD       | STRING{3}  | 1        |
      | nombre  | FIELD       | STRING{60} | 2        |
    Entonces el sistema rechaza la operación con el error "E-03" y el mensaje "El catálogo debe tener un campo KEY."
    Y los campos del catálogo "PAIS" no cambian

  @RN-03 @E-23
  Escenario: Rechazar una actualización que deja el catálogo con dos campos KEY
    Dado que existe en el catalogMaster el catálogo "PAIS" sin registros
    Cuando intento actualizar los campos del catálogo "PAIS" a:
      | nombre  | calificador | tipo      | posicion |
      | codPais | KEY         | STRING{3} | 1        |
      | codIso  | KEY         | STRING{2} | 2        |
    Entonces el sistema rechaza la operación con el error "E-23" y el mensaje "El catálogo solo admite un campo KEY."
    Y los campos del catálogo "PAIS" no cambian

  @RN-20 @E-02
  Escenario: Rechazar una actualización que deja el catálogo sin campos
    Dado que existe en el catalogMaster el catálogo "PAIS" sin registros
    Cuando intento actualizar los campos del catálogo "PAIS" a una lista vacía
    Entonces el sistema rechaza la operación con el error "E-02" y el mensaje "El catálogo debe tener al menos un campo."
    Y los campos del catálogo "PAIS" no cambian

  @RN-21 @E-12
  Esquema del escenario: Rechazar cambios de campos en un catálogo que contiene registros
    Dado que existe en el catalogMaster el catálogo "PAIS" con al menos un registro
    Cuando intento <operacion> en el catálogo "PAIS"
    Entonces el sistema rechaza la operación con el error "E-12" y el mensaje "No se pueden modificar los campos: el catálogo contiene registros."
    Y los campos del catálogo "PAIS" no cambian

    Ejemplos:
      | operacion                    |
      | agregar un campo             |
      | modificar un campo existente |
      | quitar un campo              |

  @RN-22 @E-10
  Escenario: Reportar error al definir campos de un catálogo inexistente
    Dado que no existe en el catalogMaster un catálogo con código "XYZ"
    Cuando intento actualizar los campos del catálogo "XYZ"
    Entonces el sistema reporta el error "E-10" con el mensaje "El catálogo XYZ no existe."

  @S-08 @E-25
  Escenario: Rechazar cambios de campos a un usuario sin rol de Administrador de Catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Y que existe en el catalogMaster el catálogo "PAIS" sin registros
    Cuando intento agregar un campo en el catálogo "PAIS"
    Entonces el sistema rechaza la operación con el error "E-25" y el mensaje "No tiene permisos para modificar catálogos o registros."
