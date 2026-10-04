# language: es
@CU-ADM-01 @HU-ADM-01-14 @rol:USUARIO
Característica: Consultar el catálogo hijo de un catálogo padre

  Como cualquier usuario
  Quiero saber cuál es el catálogo hijo de un catálogo padre
  Para navegar la jerarquía

  # Reglas: RN-05, RN-17, RN-25 · Supuesto: S-08 · Subflujo: SF-12
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-14-consultar-catalogo-hijo.yaml
  # RN-05: un catálogo padre tiene como máximo un hijo; el resultado es un objeto o nulo, no una lista.
  # Archivo renombrado: antes CU-ADM-01-consultar-catalogos-hijos.feature (plural).

  Antecedentes:
    Dado que he iniciado sesión con un usuario autenticado

  @RN-17
  Escenario: Catálogo con hijo
    Dado que "PAIS" es padre de "DEPTO" (nombre "Departamentos")
    Cuando consulto el hijo de "PAIS"
    Entonces el sistema retorna:
      """json
      { "codigo": "DEPTO", "nombre": "Departamentos" }
      """

  @RN-17
  Escenario: Catálogo sin hijo
    Dado que existe en el catalogMaster el catálogo "MONEDA" sin catálogo hijo
    Cuando consulto el hijo de "MONEDA"
    Entonces el resultado es nulo

  @RN-17 @S-08
  Escenario: El catálogo hijo inactivo también se retorna
    Dado que "PAIS" es padre de "DEPTO" (nombre "Departamentos")
    Y que el catálogo "DEPTO" está en estado "INACTIVE"
    Cuando consulto el hijo de "PAIS"
    Entonces el sistema retorna:
      """json
      { "codigo": "DEPTO", "nombre": "Departamentos" }
      """

  @RN-17 @E-10
  Escenario: Catálogo padre inexistente
    Cuando consulto el hijo de "NOEXISTE"
    Entonces el sistema reporta el error "E-10" con el mensaje "El catálogo NOEXISTE no existe."

  @RN-25 @S-08
  Escenario: Consulta por un usuario sin rol de administrador
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Y que "PAIS" es padre de "DEPTO" (nombre "Departamentos")
    Cuando consulto el hijo de "PAIS"
    Entonces el sistema permite la consulta
