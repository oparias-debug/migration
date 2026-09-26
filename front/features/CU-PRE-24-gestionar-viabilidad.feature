# language: es
@CU-PRE-24 @rol:TECNICO_URP @rol:VIABILIZADOR
Característica: Gestionar la Viabilidad de un proyecto

  Como Técnico URP
  Quiero solicitar la viabilidad de un proyecto formulado
  Para que sea revisado y, si procede, declarado viable

  Antecedentes:
    Dado que el proyecto está formulado y tiene CUP
    Y el actor se encuentra en la pantalla "Viabilidad del Proyecto" (Anexo A.1)

  Escenario: Consultar la ficha del proyecto (camino feliz)
    Entonces el sistema muestra el objetivo general, la descripción, los productos y la población objetivo
    Y muestra la inversión estimada, el costo de operación, el costo de mantenimiento y los indicadores de evaluación
    Y todos esos datos provienen de los capítulos ya registrados y no se editan aquí

  Escenario: Cargar el Documento de Preinversión
    Cuando el Técnico URP elige el tipo de documento y el archivo
    Y hace clic en "Cargar documento"
    Entonces el documento aparece en la lista con su tipo y su fecha de carga

  Escenario: Intentar cargar sin elegir archivo
    Cuando el Técnico URP hace clic en "Cargar documento" sin haber elegido archivo
    Entonces el sistema lo indica y no llama al servicio

  Escenario: Solicitar la viabilidad (RN02, RN04, RN06)
    Dado que el proyecto tiene cargado el Documento de Preinversión y no hay una solicitud en curso
    Cuando el Técnico URP hace clic en "Solicitar viabilidad"
    Y confirma la acción
    Entonces el sistema registra la solicitud y la ficha refleja el nuevo estado

  Escenario: Comentar la ficha campo por campo (RN01)
    Dado que quien revisa tiene la ficha habilitada
    Cuando escribe un comentario en uno o varios campos y la justificación general
    Y hace clic en "Guardar"
    Entonces el sistema guarda únicamente los campos con comentario
    Y al volver a abrir la ficha los comentarios siguen ahí

  Escenario: Enviar los comentarios a la institución (RN07)
    Dado que hay una solicitud de viabilidad vigente
    Cuando quien revisa hace clic en "Enviar comentarios"
    Y confirma la acción
    Entonces el sistema envía los comentarios y el proyecto queda observado

  Escenario: Emitir la viabilidad
    Cuando quien revisa hace clic en "Emitir viabilidad"
    Y confirma la acción
    Entonces el proyecto queda viable
    Y la ficha deja de admitir cambios
    Y se ofrece continuar a Elegibilidad

  Escenario: Cancelar una acción en la confirmación
    Cuando el actor pide una acción y cancela en el aviso de confirmación
    Entonces no se ejecuta nada y la ficha queda como estaba

  Escenario: Qué puede hacer cada actor lo resuelve el servidor
    Dado que el actor no tiene ninguna acción habilitada para el estado actual
    Entonces la ficha se muestra en sólo lectura, sin botones de acción ni campos de comentario

  Escenario: Un fallo al consultar la ficha se ve como error
    Dado que la consulta falla
    Entonces el sistema muestra el error en vez de una ficha vacía
