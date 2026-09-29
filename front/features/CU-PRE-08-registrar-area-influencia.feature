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

  Escenario: Autocompletar una ubicación de Población Objetivo que no es un distrito del catálogo
    # En CU-PRE-07 la ubicación de la Población Objetivo se registra como texto libre (mockup: "Comunidad Río Mar").
    Dado que la Población Objetivo tiene registrada la ubicación "Comunidad Río Mar", que no es un distrito del catálogo
    Cuando el Técnico URP hace clic en el botón "Traer ubicación de Población Objetivo"
    Entonces el sistema propone "Comunidad Río Mar" como "Ubicación Específica" y deja "Región", "Departamento" y "Distrito" vacíos para que los complete

  Escenario: Región, Departamento y Distrito autocompletados pueden editarse (RN07)
    # Decisión de negocio sobre la contradicción RN07/RN08 del CU: prevalece RN07, que permite "editar dicha
    # información o agregar otra ubicación". RN08 (Región, Departamento y Distrito bloqueados) no se aplica.
    Dado que los campos "Región", "Departamento" y "Distrito" ya fueron autocompletados
    Cuando el Técnico URP cambia el "Distrito" de la fila por otro distrito del catálogo y guarda
    Entonces el sistema guarda la fila con el nuevo distrito, su departamento y su región

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

  Escenario: Guardar con campos pendientes de completar
    # Decisión de negocio: el guardado incompleto se admite. El sombreado en rojo de los campos pendientes
    # (RN05) lo hace el cliente; el servidor no lo rechaza.
    Cuando el Técnico URP hace clic en el botón "Guardar" sin haber completado los campos requeridos
    Entonces el sistema guarda la información aunque la "Ubicación específica" quede pendiente

  # ⚠️ Escenario pendiente: el ícono de ayuda contextual "?" (RN06) no tiene un texto de mensaje transcrito en el documento. No se genera un escenario con contenido de backend no verificable.
  # ⚠️ Escenario pendiente: el CU no especifica el texto del mensaje asociado al resaltado en rojo (RN05); no se inventa esa información.