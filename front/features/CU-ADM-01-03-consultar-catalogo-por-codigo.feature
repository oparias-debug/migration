# language: es
@CU-ADM-01 @HU-ADM-01-03 @rol:USUARIO @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Consultar un catálogo por código

  Como cualquier usuario
  Quiero recuperar un catálogo por su código
  Para ver su definición completa

  # Reglas: RN-04, RN-22, RN-23, RN-25 · Supuestos: S-08, S-09 · Subflujo: SF-02
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-03-consultar-catalogo-por-codigo.yaml

  Antecedentes:
    Dado que existe en el catalogMaster el catálogo "PAIS" con nombre "Países", estado "ACTIVE", sin vigencia y los campos:
      | nombre     | calificador | tipo                     | posicion |
      | continente | FIELD       | ENUM{"AMERICA","EUROPA"} | 3        |
      | codPais    | KEY         | STRING{3}                | 1        |
      | nombre     | FIELD       | STRING{60}               | 2        |
    Y que existe el catálogo "DEPTO" con catálogo padre "PAIS"

  @RN-22 @RN-23
  Escenario: Consultar un catálogo existente
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"
    Cuando consulto el catálogo con código "PAIS"
    Entonces obtengo los descriptores del catálogo:
      | codigo | nombre | padre | hijo  | estado | desde | hasta |
      | PAIS   | Países |       | DEPTO | ACTIVE |       |       |
    Y obtengo los campos ordenados por posición:
      | nombre     | calificador | tipo                     | posicion |
      | codPais    | KEY         | STRING{3}                | 1        |
      | nombre     | FIELD       | STRING{60}               | 2        |
      | continente | FIELD       | ENUM{"AMERICA","EUROPA"} | 3        |

  @RN-22 @E-10
  Escenario: Reportar error al consultar un código inexistente
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"
    Y que no existe en el catalogMaster un catálogo con código "XYZ"
    Cuando consulto el catálogo con código "XYZ"
    Entonces el sistema reporta el error "E-10" con el mensaje "El catálogo XYZ no existe."

  @RN-25 @S-08
  Escenario: Un usuario sin rol de administrador consulta un catálogo inactivo
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Y que el catálogo "PAIS" está en estado "INACTIVE"
    Cuando consulto el catálogo con código "PAIS"
    Entonces el sistema permite la consulta
    Y obtengo el catálogo "PAIS" con estado "INACTIVE"

  @RN-04 @S-09
  Escenario: Un campo definido sin tipo se muestra con su tipo efectivo STRING{80}
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"
    Y que existe en el catalogMaster el catálogo "MONEDA" con los campos:
      | nombre      | calificador | tipo      | posicion |
      | codMoneda   | KEY         | STRING{3} | 1        |
      | descripcion | FIELD       |           | 2        |
    Cuando consulto el catálogo con código "MONEDA"
    Entonces obtengo los campos ordenados por posición:
      | nombre      | calificador | tipo       | posicion |
      | codMoneda   | KEY         | STRING{3}  | 1        |
      | descripcion | FIELD       | STRING{80} | 2        |
