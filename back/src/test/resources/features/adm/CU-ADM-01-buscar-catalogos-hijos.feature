# language: es
@CU-ADM-01 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Buscar Catálogos Hijos

  Como Administrador de Catálogos
  Quiero consultar la lista de catálogos hijos de un catálogo padre

  Escenario: Consultar los catálogos hijos de un catálogo padre existente
    Dado que existen catálogos que referencian a un catálogo padre determinado como PARENT
    Cuando se provee el código de ese catálogo padre
    Entonces el sistema retorna la lista de {código, nombre} de todos los catálogos que lo referencian como PARENT

  # ⚠️ Nota: el CU no especifica qué ocurre si el código de catálogo padre provisto no existe,
  # ni si un padre sin catálogos hijos retorna una lista vacía o un error. No se generaron
  # escenarios para esos casos por no estar descritos en el CU.
