# language: es
@CU-ADM-01 @HU-ADM-01-10 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Actualizar un registro de un catálogo

  Como Administrador de Catálogos
  Quiero modificar los campos no llave de un registro
  Para corregir o actualizar su información

  # Reglas: RN-11, RN-18 · Subflujo: SF-08
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-10-actualizar-registro.yaml

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"
    Y que existe en el catalogMaster el catálogo "PAIS" con los campos:
      | nombre    | calificador | tipo                  | posicion |
      | codPais   | KEY         | STRING{3}             | 1        |
      | nombre    | FIELD       | STRING{60}            | 2        |
      | poblacion | FIELD       | NUMERIC{0:2000000000} | 3        |
    Y que el catálogo "PAIS" contiene el registro:
      | codPais | nombre   | poblacion |
      | COL     | Colombia | 52000000  |

  @RN-18 @RN-11
  Escenario: Actualización de campos no KEY
    Cuando actualizo el registro "COL" de "PAIS" con los valores:
      | campo     | valor                 |
      | nombre    | República de Colombia |
      | poblacion | 53000000              |
    Entonces el registro "COL" de "PAIS" queda con los valores en versión STRING:
      | codPais | nombre                | poblacion |
      | COL     | República de Colombia | 53000000  |

  @RN-18 @E-19
  Escenario: Intento de cambiar el KEY
    Cuando intento cambiar codPais de "COL" a "CO"
    Entonces el sistema rechaza la operación con el error "E-19" y el mensaje "El campo llave no es modificable."
    Y el registro conserva el valor de KEY "COL"

  @RN-11 @E-14
  Esquema del escenario: Nuevo valor inválido
    Cuando intento asignar al campo "<campo>" del registro "COL" el valor "<valor>"
    Entonces el sistema rechaza la operación con el error "E-14" y el mensaje "El valor <valor> no es válido para el campo <campo> (<tipo>)."
    Y el registro "COL" conserva sus valores

    Ejemplos:
      | campo     | valor                                                          | tipo                  |
      | nombre    | Un texto que supera claramente los sesenta caracteres de largo | STRING{60}            |
      | poblacion | -1                                                             | NUMERIC{0:2000000000} |

  # Decisión del modelo de dominio v4.0 (CatalogAdminService.updateRecord, CatalogRecord.updateValue).
  @E-21 @modelo-dominio
  Escenario: Rechazar la actualización de un campo no definido
    Cuando intento asignar al campo "moneda" del registro "COL" el valor "COP"
    Entonces el sistema rechaza la operación con el error "E-21" y el mensaje "El campo moneda no está definido en el catálogo PAIS."

  @S-08 @E-25
  Escenario: Rechazar la actualización a un usuario sin rol de Administrador de Catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Cuando intento cambiar "nombre" del registro "COL" a "República de Colombia"
    Entonces el sistema rechaza la operación con el error "E-25" y el mensaje "No tiene permisos para modificar catálogos o registros."

  # Decisión del modelo de dominio v4.0 (CatalogAdminService.findRecord). Ver pendiente P-05.
  @E-22 @modelo-dominio
  Escenario: Rechazar la actualización de un registro inexistente
    Cuando intento asignar al campo "nombre" del registro "ZZZ" el valor "Otro"
    Entonces el sistema rechaza la operación con el error "E-22" y el mensaje "No existe un registro con la llave ZZZ."

  # ℹ️ Sin escenario (ver historias-CU-ADM-01.md, pendiente P-05): el CU no define si se permite actualizar
  # un registro INACTIVE (el modelo de dominio v4.0 lo permite).
