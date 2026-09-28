# language: es
@CU-PRE-08 @rol:TECNICO_URP
Característica: Registrar el área de influencia del proyecto

  Como Técnico URP
  Quiero registrar el área de influencia del proyecto, autocompletando la ubicación desde la Población Objetivo y agregando las ubicaciones específicas necesarias

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Formulación del Proyecto", sección "Diagnóstico de la Situación Actual – Área de Influencia"
    Y se encuentra en la pantalla "Área de Influencia" (Anexo A.1)

  Escenario: Autocompletar la ubicación desde Población Objetivo (camino feliz)
    Dado que el proyecto ya cuenta con ubicaciones registradas en CU-PRE-07 "Población Objetivo"
    Cuando el Técnico URP hace clic en el botón "Traer ubicación de Población Objetivo"
    Entonces el sistema completa los campos "Región", "Departamento", "Distrito" y "Ubicación Específica" según lo registrado en CU-PRE-07
    Y se mantiene en la pantalla "Área de Influencia" (RN07, FA-03)

  Escenario: Región, Departamento y Distrito permanecen bloqueados para edición
    Dado que los campos "Región", "Departamento" y "Distrito" ya fueron autocompletados
    Entonces dichos campos permanecen bloqueados para edición (RN08)
    Y si se requiere agregar otra Región, Departamento o Distrito, debe hacerse en CU-PRE-07 "Población Objetivo"

  Escenario: Agregar una fila de ubicación específica
    Cuando el Técnico URP acerca el cursor a un punto definido de la tabla
    Y hace clic en el botón emergente "+"
    Entonces el sistema agrega una nueva fila para registrar otra "Ubicación específica" (RN03)

  Escenario: Eliminar una fila de ubicación específica
    Dado una fila de "Ubicación específica" ya registrada
    Cuando el Técnico URP hace clic en el botón "x" de esa fila
    Entonces el sistema elimina la fila correspondiente (RN04)

  Escenario: Guardar la información del área de influencia
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.2)
    Cuando el Técnico URP hace clic en "Aceptar"
    Entonces el sistema guarda la información registrada
    Y se mantiene en la pantalla "Área de Influencia"

  Escenario: Intentar guardar con campos pendientes de completar
    Cuando el Técnico URP hace clic en el botón "Guardar" sin haber completado los campos requeridos
    Entonces el sistema sombrea en color rojo los bordes de los campos pendientes de completar (RN05)

  # ⚠️ Escenario pendiente: existe una contradicción no resuelta entre RN07 (sugiere que los cuatro campos autocompletados, incluida Región/Departamento/Distrito, podrían editarse) y RN08 (indica explícitamente que Región/Departamento/Distrito permanecen bloqueados). Se modeló la interpretación de RN08, consistente con la tabla de Campos del Anexo B.1; no se genera un escenario que permita editar esos tres campos (ver Datos Pendientes de Definir del CU original).
  # ⚠️ Escenario pendiente: el ícono de ayuda contextual "?" (RN06) no tiene un texto de mensaje transcrito en el documento. No se genera un escenario con contenido de backend no verificable.
  # ⚠️ Escenario pendiente: la tabla de Validaciones no especifica cuáles campos son exactamente obligatorios ni el texto del mensaje asociado al resaltado en rojo (RN05); no se inventa esa información.