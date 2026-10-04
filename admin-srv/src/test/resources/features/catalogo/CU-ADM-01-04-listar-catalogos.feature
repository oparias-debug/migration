# language: es
@CU-ADM-01 @HU-ADM-01-04 @rol:USUARIO @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Buscar y listar catálogos del maestro

  Como cualquier usuario
  Quiero buscar un catálogo por código o nombre y listar todos los catálogos
  Para saber qué catálogos están disponibles

  # Reglas: RN-09, RN-25 · Supuesto: S-08 · Subflujo: SF-03
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-04-listar-catalogos.yaml
  # Esta historia reúne el antiguo CU-ADM-01-verificar-existencia-catalogo.feature (búsqueda por nombre).

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"
    Y que el catalogMaster contiene los catálogos:
      | codigo | nombre  | estado   |
      | PAIS   | Países  | ACTIVE   |
      | MONEDA | Monedas | INACTIVE |

  @RN-09
  Escenario: Buscar un catálogo por nombre
    Cuando busco un catálogo con nombre "Países"
    Entonces el sistema indica que el catálogo existe
    Y muestra el catálogo "PAIS"

  @RN-09
  Escenario: Buscar un catálogo por código
    Cuando busco un catálogo con código "MONEDA"
    Entonces el sistema indica que el catálogo existe
    Y muestra el catálogo "MONEDA" con estado "INACTIVE"

  # Decisión del modelo de dominio v4.0 (CatalogRepository.findByNameIgnoreCase). Ver pendiente P-04.
  @RN-09 @modelo-dominio
  Escenario: La búsqueda por nombre no distingue mayúsculas de minúsculas
    Cuando busco un catálogo con nombre "PAÍSES"
    Entonces el sistema indica que el catálogo existe
    Y muestra el catálogo "PAIS"

  @RN-09
  Esquema del escenario: Búsqueda sin coincidencias
    Cuando busco un catálogo con <criterio> "<valor>"
    Entonces el sistema indica que no existe un catálogo con ese <criterio>

    Ejemplos:
      | criterio | valor     |
      | código   | NOEXISTE  |
      | nombre   | No existe |

  @RN-09
  Escenario: Listado completo, activos e inactivos
    Cuando solicito el listado de catálogos
    Entonces obtengo todos los catálogos del catalogMaster:
      | codigo | nombre  | estado   |
      | MONEDA | Monedas | INACTIVE |
      | PAIS   | Países  | ACTIVE   |

  @RN-25 @S-08
  Esquema del escenario: Un usuario sin rol de administrador busca y lista catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Cuando <consulta>
    Entonces el sistema permite la consulta

    Ejemplos:
      | consulta                              |
      | busco un catálogo con código "MONEDA" |
      | busco un catálogo con nombre "Países" |
      | solicito el listado de catálogos      |
