# language: es
@CU-ADM-01 @HU-ADM-01-01 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Crear un catálogo

  Como Administrador de Catálogos
  Quiero crear un catálogo con su código, nombre, padre, estado, vigencia y campos
  Para disponer de una nueva tabla de referencia en el sistema

  # Reglas: RN-02, RN-03, RN-04, RN-05, RN-10, RN-12, RN-15, RN-16, RN-20 · Supuesto: S-09 · Subflujo: SF-01
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-01-crear-catalogo.yaml
  # Las definiciones de tipo de campo (E-05) se prueban en CU-ADM-01-02-actualizar-campos-catalogo.feature
  # y la asignación de padre (E-07, E-08, E-15) en CU-ADM-01-06-establecer-jerarquia-catalogo.feature.

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"

  @RN-10 @RN-15
  Escenario: Creación exitosa de un catálogo plano
    Dado que no existe en el catalogMaster un catálogo con código "PAIS"
    Cuando creo el catálogo "PAIS" con nombre "Países", estado "ACTIVE", sin vigencia y los campos:
      | nombre  | calificador | tipo       | posicion |
      | codPais | KEY         | STRING{3}  | 1        |
      | nombre  | FIELD       | STRING{60} | 2        |
    Entonces el catálogo "PAIS" queda registrado en el catalogMaster
    Y el catálogo "PAIS" queda con estado "ACTIVE"
    Y el catálogo "PAIS" no tiene catálogo padre

  @RN-04 @S-09
  Escenario: Un campo sin tipo definido se crea como STRING{80}
    Cuando creo el catálogo "PAIS" con nombre "Países", estado "ACTIVE", sin vigencia y los campos:
      | nombre      | calificador | tipo      | posicion |
      | codPais     | KEY         | STRING{3} | 1        |
      | descripcion | FIELD       |           | 2        |
    Entonces el catálogo "PAIS" queda registrado en el catalogMaster
    Y el campo "descripcion" del catálogo "PAIS" queda definido con tipo "STRING{80}"

  @RN-02 @E-01
  Escenario: Rechazar un código de catálogo duplicado
    Dado que existe en el catalogMaster el catálogo "PAIS"
    Cuando intento crear otro catálogo con código "PAIS"
    Entonces el sistema rechaza la operación con el error "E-01" y el mensaje "Ya existe un catálogo con el código PAIS."

  @RN-20 @E-02
  Escenario: Rechazar un catálogo sin campos
    Cuando intento crear el catálogo "PAIS" con nombre "Países" sin indicar campos
    Entonces el sistema rechaza la operación con el error "E-02" y el mensaje "El catálogo debe tener al menos un campo."
    Y el catálogo "PAIS" no existe en el catalogMaster

  @RN-03 @E-03
  Escenario: Rechazar un catálogo sin campo KEY
    Cuando intento crear el catálogo "PAIS" con nombre "Países" y los campos:
      | nombre  | calificador | tipo       | posicion |
      | codPais | FIELD       | STRING{3}  | 1        |
      | nombre  | FIELD       | STRING{60} | 2        |
    Entonces el sistema rechaza la operación con el error "E-03" y el mensaje "El catálogo debe tener un campo KEY."
    Y el catálogo "PAIS" no existe en el catalogMaster

  @RN-03 @E-23
  Escenario: Rechazar un catálogo con más de un campo KEY
    Cuando intento crear el catálogo "PAIS" con nombre "Países" y los campos:
      | nombre  | calificador | tipo      | posicion |
      | codPais | KEY         | STRING{3} | 1        |
      | codIso  | KEY         | STRING{2} | 2        |
    Entonces el sistema rechaza la operación con el error "E-23" y el mensaje "El catálogo solo admite un campo KEY."
    Y el catálogo "PAIS" no existe en el catalogMaster

  @RN-04 @E-04
  Escenario: Rechazar nombres de campo repetidos
    Cuando intento crear el catálogo "PAIS" con nombre "Países" y los campos:
      | nombre  | calificador | tipo       | posicion |
      | codPais | KEY         | STRING{3}  | 1        |
      | nombre  | FIELD       | STRING{60} | 2        |
      | nombre  | FIELD       | STRING{80} | 3        |
    Entonces el sistema rechaza la operación con el error "E-04" y el mensaje "El nombre de campo nombre está repetido."
    Y el catálogo "PAIS" no existe en el catalogMaster

  @E-09
  Escenario: Rechazar un rango de vigencia invertido
    Cuando intento crear el catálogo "PAIS" con vigencia "2027-01-01:2026-01-01" y campos válidos
    Entonces el sistema rechaza la operación con el error "E-09" y el mensaje "Rango de vigencia inválido."

  @RN-16
  Escenario: Un catálogo creado con TO DATE en el pasado queda INACTIVE
    Cuando creo el catálogo "PAIS" con estado "ACTIVE", vigencia "2020-01-01:2021-01-01" y campos válidos
    Entonces el catálogo "PAIS" queda registrado en el catalogMaster
    Y el catálogo "PAIS" queda con estado "INACTIVE"

  @RN-12
  Escenario: Un catálogo creado con estado INACTIVE y sin TO DATE recibe la fecha actual como TO DATE
    Cuando creo el catálogo "PAIS" con estado "INACTIVE", sin vigencia y campos válidos
    Entonces el catálogo "PAIS" queda con estado "INACTIVE"
    Y la TO DATE del catálogo "PAIS" queda fijada en la fecha actual

  @RN-05
  Escenario: Crear un catálogo hijo de un catálogo padre activo
    Dado que existe en el catalogMaster el catálogo "PAIS" con estado "ACTIVE" y sin catálogo hijo
    Cuando creo el catálogo "DEPTO" con nombre "Departamentos", padre "PAIS" y campos válidos
    Entonces el catálogo "DEPTO" referencia a "PAIS" como padre
    Y el catálogo "PAIS" registra a "DEPTO" como su hijo
    Y los registros que se creen en "DEPTO" deberán indicar el id de un registro padre de "PAIS"

  @RN-05 @E-06
  Escenario: Rechazar un catálogo padre inexistente
    Dado que no existe en el catalogMaster un catálogo con código "NOEXISTE"
    Cuando intento crear el catálogo "DEPTO" con padre "NOEXISTE" y campos válidos
    Entonces el sistema rechaza la operación con el error "E-06" y el mensaje "El catálogo padre NOEXISTE no existe."
    Y el catálogo "DEPTO" no existe en el catalogMaster

  @S-08 @E-25
  Escenario: Rechazar la creación a un usuario sin rol de Administrador de Catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Cuando intento crear el catálogo "PAIS" con campos válidos
    Entonces el sistema rechaza la operación con el error "E-25" y el mensaje "No tiene permisos para modificar catálogos o registros."
