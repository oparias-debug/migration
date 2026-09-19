# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Crear un catálogo

  Como Administrador del Sistema
  Quiero crear un nuevo catálogo indicando código, nombre, catálogo padre (opcional), estado, vigencia y al menos un campo KEY
  Para disponer de una nueva estructura de datos maestros en el sistema

  Escenario: Crear un catálogo con código, nombre y al menos un campo KEY
    Dado que indico código, nombre y al menos un campo con calificador KEY
    Cuando confirmo la creación del catálogo
    Entonces el catálogo se crea correctamente

  Escenario: Rechazar la creación de un catálogo sin ningún campo KEY
    Dado que defino campos para el catálogo pero ninguno tiene calificador KEY
    Cuando intento crear el catálogo
    Entonces el sistema rechaza la operación conforme a la Regla 2

  Escenario: Rechazar la creación de un catálogo con nombres de campo duplicados
    Dado que dos o más campos del catálogo tienen el mismo nombre
    Cuando intento crear el catálogo
    Entonces el sistema rechaza la operación conforme a la Regla 3

  Escenario: Rechazar la creación de un catálogo sin ningún campo
    Dado que no indico ningún campo para el catálogo
    Cuando intento crear el catálogo
    Entonces el sistema rechaza la operación conforme a la Regla 18

  Escenario: Fijar el catálogo como ACTIVE cuando no se indican fechas de vigencia
    Dado que no indico fechas de vigencia al crear el catálogo
    Cuando el catálogo se crea
    Entonces su estado queda ACTIVE conforme a la Regla 13

  Escenario: Rechazar la creación de un catálogo cuando el catálogo padre indicado no existe
    Dado que indico un catálogo PARENT para el nuevo catálogo
    Cuando el código del catálogo padre no existe en el catalogMaster
    Entonces el sistema reporta un error

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso
  # (solo declara la precondición general en la sección 2). Ver historias-CU-ADM-01.md.
