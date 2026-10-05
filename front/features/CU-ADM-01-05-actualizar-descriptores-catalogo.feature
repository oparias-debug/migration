# language: es
@CU-ADM-01 @HU-ADM-01-05 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Actualizar los descriptores de un catálogo

  Como Administrador de Catálogos
  Quiero modificar el nombre, padre, estado y vigencia de un catálogo
  Para mantener su información al día

  # Reglas: RN-19, RN-23 · Subflujo: SF-04
  # Fuentes: CU-ADM-01 v1.3 · modelo de dominio v4.0 · Contrato: CU-ADM-01-05-actualizar-descriptores-catalogo.yaml
  # El cambio de padre se prueba en CU-ADM-01-06-establecer-jerarquia-catalogo.feature;
  # el cambio de estado, en CU-ADM-01-07-inactivar-catalogo.feature y CU-ADM-01-08-reactivar-catalogo.feature.

  Antecedentes:
    Dado que he iniciado sesión con el rol "Administrador de Catálogos"
    Y que existe en el catalogMaster el catálogo "PAIS" con nombre "Países" y estado "ACTIVE"

  @RN-19 @RN-23
  Escenario: Cambiar el nombre de un catálogo
    Cuando cambio el nombre del catálogo "PAIS" a "Países del mundo"
    Entonces el catálogo "PAIS" queda con nombre "Países del mundo"

  @RN-19
  Escenario: Fijar una vigencia futura mantiene el catálogo activo
    Cuando cambio la vigencia del catálogo "PAIS" a "2026-01-01:2099-12-31"
    Entonces el catálogo "PAIS" queda con vigencia "2026-01-01:2099-12-31"
    Y el catálogo "PAIS" queda con estado "ACTIVE"

  @RN-19 @E-11
  Escenario: Rechazar el cambio de código
    Cuando intento cambiar el código "PAIS" por "PAISES"
    Entonces el sistema rechaza la operación con el error "E-11" y el mensaje "El código del catálogo no es modificable."
    Y el catálogo conserva el código "PAIS"

  @E-09
  Escenario: Rechazar un rango de vigencia invertido
    Cuando intento cambiar la vigencia del catálogo "PAIS" a "2027-01-01:2026-01-01"
    Entonces el sistema rechaza la operación con el error "E-09" y el mensaje "Rango de vigencia inválido."
    Y la vigencia del catálogo "PAIS" no cambia

  @RN-23 @E-10
  Escenario: Reportar error al actualizar un catálogo inexistente
    Dado que no existe en el catalogMaster un catálogo con código "XYZ"
    Cuando intento cambiar el nombre del catálogo "XYZ" a "Otro"
    Entonces el sistema reporta el error "E-10" con el mensaje "El catálogo XYZ no existe."

  @S-08 @E-25
  Escenario: Rechazar la actualización a un usuario sin rol de Administrador de Catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador de Catálogos"
    Cuando intento cambiar el nombre del catálogo "PAIS" a "Países del mundo"
    Entonces el sistema rechaza la operación con el error "E-25" y el mensaje "No tiene permisos para modificar catálogos o registros."
