# language: es
@CU-PRE-01 @rol:TECNICO_URP
Característica: Registrar y guardar un nuevo proyecto en Registro de Proyecto

  Como Técnico URP
  Quiero registrar un nuevo proyecto, programa o estudio general y guardarlo como borrador
  Para incorporarlo al listado de "Registro de Proyecto" antes de solicitar su CUP

  Antecedentes:
    Dado que el Técnico URP ingresa a la pantalla "Registro de Proyecto" (Anexo A.1)

  Escenario: Registrar y guardar un nuevo proyecto (camino feliz)
    Cuando el Técnico URP hace clic en el botón "Nuevo Registro"
    Y el sistema muestra la pantalla "Nuevo registro" (Anexo A.2)
    Y el Técnico URP selecciona una de las opciones "Programa", "Proyecto" o "Estudios Generales" en el campo "Iniciativa de inversión"
    Y registra la información de los campos obligatorios de la pantalla "Nuevo registro"
    Y hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente."
    Y el sistema almacena la información
    Y regresa a la pantalla "Registro de Proyecto" con el proyecto en estado "En Elaboración"

  Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
    Dado que el Técnico URP está registrando la información en la pantalla "Nuevo registro"
    Cuando hace clic en el botón "Guardar" sin haber completado el campo "<campo>"
    Entonces el sistema sombrea en rojo el borde del campo "<campo>"
    Y el sistema muestra el mensaje "*Campo obligatorio"

    Ejemplos:
      | campo                    |
      | Iniciativa de inversión  |
      | Nombre del proyecto      |
      | Monto Estimado de Inversión |
      | Sector                   |
      | Eje temático              |
      | Descripción del proyecto |

  @ui-only
  Escenario: Regresar sin guardar y cancelar
    Dado que el Técnico URP tiene datos sin guardar en la pantalla "Nuevo registro"
    Cuando hace clic en el botón "Regresar"
    Y el sistema muestra el mensaje "¿Está seguro? ¡Se borrarán todos los datos ingresados!" (Anexo A.2.1)
    Y el Técnico URP hace clic en "Cancelar"
    Entonces el sistema regresa a la pantalla "Nuevo registro"

  Escenario: Regresar sin guardar y aceptar
    Dado que el Técnico URP tiene datos sin guardar en la pantalla "Nuevo registro"
    Cuando hace clic en el botón "Regresar"
    Y el sistema muestra el mensaje "¿Está seguro? ¡Se borrarán todos los datos ingresados!" (Anexo A.2.1)
    Y el Técnico URP hace clic en "Aceptar"
    Entonces el sistema regresa a la pantalla "Registro de Proyecto" sin guardar los datos

  @ui-only
  Escenario: Ver la descripción de las categorías
    Cuando el Técnico URP hace clic en el botón "Ver descripción de categorías"
    Entonces el sistema muestra en una ventana emergente las tablas descritas en los Anexos C.1, C.1.5 y C.2

  Escenario: Editar un proyecto existente en estado "En Elaboración" u "Observado DGICP (Registro)"
    Dado un proyecto en el listado de "Registro de Proyecto" con estado "En Elaboración" o "Observado DGICP (Registro)"
    Cuando el Técnico URP hace clic en el nombre del proyecto
    Entonces el sistema habilita para edición los campos de la pantalla "Nuevo registro"
    Y el Técnico URP puede ajustar y/o actualizar los campos editados