# language: es
@CU-PRE-04 @rol:TECNICO_URP
Característica: Registrar y guardar la información de identificación del proyecto

  Como Técnico URP
  Quiero registrar y guardar la información de identificación del proyecto

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Identificación del proyecto" (Anexo A.1)
    Y el sistema muestra los campos no editables "Unidad Ejecutora", "Nombre del proyecto" y "CUP"

  Escenario: Registrar y guardar la información (camino feliz)
    Cuando el Técnico URP registra información en los campos "Antecedentes", "Problema Central", "Objetivo General" y "Objetivos Específicos"
    Y hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.2)
    Cuando el Técnico URP hace clic en "Aceptar"
    Entonces el sistema guarda la información registrada
    Y permanece en la sección "Identificación del proyecto"

  Escenario: Intentar guardar con campos pendientes de completar
    Cuando el Técnico URP hace clic en el botón "Guardar" sin haber completado todos los campos
    Entonces el sistema sombrea en color rojo los bordes de los campos pendientes de completar (RNC-2)

  Esquema del escenario: Respetar el límite de caracteres de cada campo
    Cuando el Técnico URP registra información en el campo "<campo>"
    Entonces el sistema permite hasta "<limite>" caracteres para ese campo

    Ejemplos:
      | campo                 | limite |
      | Antecedentes          | 3000   |
      | Problema central      | 500    |
      | Objetivo general      | 500    |
      | Objetivos Específicos | 500    |

  Escenario: Agregar una fila de Objetivo Específico
    Cuando el Técnico URP hace clic en el botón para adicionar fila en la sección "Objetivos Específicos"
    Entonces el sistema agrega una nueva fila para registrar un objetivo específico adicional

  Escenario: Eliminar una fila de Objetivo Específico
    Dado una fila registrada en la sección "Objetivos Específicos"
    Cuando el Técnico URP hace clic en el ícono para eliminar esa fila
    Entonces el sistema elimina la fila correspondiente

  Escenario: Regresar a la pantalla "Ruta de Preinversión"
    Cuando el Técnico URP hace clic en el botón "Regresar"
    Entonces el sistema navega a la pantalla "Ruta de Preinversión"

  # ⚠️ Escenario pendiente: el ícono de ayuda contextual "?" (RNC-1) no tiene un texto de mensaje transcrito en el documento; solo se indica que "indicará qué información se debe completar en dicho campo". No se genera un escenario que asegure un texto literal de backend, ya que no hay contenido verificable (ver Datos Pendientes de Definir del CU original).
  # ⚠️ Escenario pendiente: no se especifica si existe un límite en la cantidad de filas que pueden agregarse en "Objetivos Específicos"; no se inventa dicho límite.