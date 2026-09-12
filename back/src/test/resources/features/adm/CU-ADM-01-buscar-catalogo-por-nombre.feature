# language: es
@CU-ADM-01 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Buscar Catálogo por Nombre (catalogMaster)

  Como Administrador de Catálogos
  Quiero verificar si existe un catálogo definido con un nombre dado

  Esquema del escenario: Verificar existencia de un catálogo por nombre
    Dado que se provee un nombre de catálogo
    Cuando "<condicion>"
    Entonces el sistema indica "<resultado>"

    Ejemplos:
      | condicion                                          | resultado                          |
      | el nombre corresponde a un catálogo ya definido     | que el catálogo existe             |
      | el nombre no corresponde a ningún catálogo definido | que el catálogo no existe          |
