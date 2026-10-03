# language: es
@CU-PRE-14 @rol:TECNICO_URP
Característica: Registrar el análisis ambiental

  Como Técnico URP
  Quiero indicar si el proyecto tiene impactos ambientales asociados y, en caso afirmativo, registrar la matriz de gestión ambiental

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Formulación del Proyecto", sección "Análisis Ambiental" análisis-ambiental
    Y se encuentra en la pantalla "Análisis Ambiental" (Anexo A.1) análisis-ambiental

  Escenario: Desplegar la tabla al seleccionar "Sí"
    Cuando el Técnico URP selecciona si "Sí" en "¿Existen impactos ambientales asociados al proyecto?" análisis-ambiental
    Entonces el sistema despliega la tabla "Análisis Ambiental" (RN04) análisis-ambiental

  Escenario: No desplegar la tabla al seleccionar "No"
    Cuando el Técnico URP selecciona no "No" en "¿Existen impactos ambientales asociados al proyecto?" análisis-ambiental
    Entonces el sistema no despliega la tabla "Análisis Ambiental" (RN04) análisis-ambiental

  Escenario: Registrar y guardar la matriz de gestión ambiental (camino feliz)
    Dado que la tabla "Análisis Ambiental" está desplegada análisis-ambiental
    Cuando el Técnico URP selecciona el "Medio", el "Tipo de Impacto", la "Magnitud", la "Duración" y la "Reversibilidad" análisis-ambiental
    Y registra el "Impacto" y la "Medida de gestión" análisis-ambiental
    Y registra el "Costo de Medida de Gestión" análisis-ambiental
    Y hace clic en el botón "Guardar" análisis-ambiental
    Entonces el sistema muestra el mensaje "Sus datos han sido guardados exitosamente. Recuerde que los costos derivados del análisis ambiental deben estar considerados dentro del presupuesto del proyecto." (Anexo A.2) análisis-ambiental
    Cuando el Técnico URP hace clic en "Aceptar" análisis-ambiental
    Entonces el sistema guarda la información registrada análisis-ambiental
    Y se mantiene en la sección "Análisis Ambiental" análisis-ambiental

  Escenario: Cálculo automático del Total Costo Medidas de Gestión
    Dado que se han registrado costos de medida de gestión en varias filas análisis-ambiental
    Entonces el sistema calcula el "Total Costo Medidas de Gestión" como la sumatoria de la columna "Costo de Medida de Gestión" análisis-ambiental

  Escenario: Agregar una nueva fila a la matriz de gestión ambiental
    Cuando el Técnico URP adiciona una nueva fila análisis-ambiental
    Entonces el sistema agrega la fila a la tabla "Análisis Ambiental" (RN03) análisis-ambiental

  Escenario: Eliminar una fila de la matriz de gestión ambiental
    Dado una fila registrada en la tabla análisis-ambiental
    Cuando el Técnico URP elimina esa fila análisis-ambiental
    Entonces el sistema elimina la fila correspondiente (RN03) análisis-ambiental

  Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
    Cuando el Técnico URP hace clic en "Guardar" sin haber completado el campo "<campo>" análisis-ambiental
    Entonces el sistema sombrea en rojo el borde del campo "<campo>" (RN05) análisis-ambiental

    Ejemplos:
      | campo           |
      | Medio           |
      | Impacto         |
      | Tipo de impacto |
      | Magnitud        |
      | Duración        |
      | Reversibilidad  |
      | Medida de gestión |

  Escenario: Respetar el límite de caracteres de Impacto y Medida de gestión
    Cuando el Técnico URP registra información en el campo "Impacto" o en el campo "Medida de gestión" análisis-ambiental
    Entonces el sistema permite hasta 200 caracteres para cada uno análisis-ambiental

  Escenario: El campo Costo de Medida de Gestión no es obligatorio
    Dado que todos los demás campos obligatorios de una fila están completos análisis-ambiental
    Cuando el Técnico URP guarda sin registrar el "Costo de Medida de Gestión" análisis-ambiental
    Entonces el sistema permite guardar la fila análisis-ambiental

  # ⚠️ Escenario pendiente: RN07 indica que "el Sistema permitirá añadir filas anidadas por cada tipo de medio que se seleccione", pero ni el Flujo Básico, ni los Flujos Alternos, ni el mockup del Anexo A.1 desarrollan qué es una "fila anidada" ni cómo se visualiza. No se modela esta funcionalidad hasta que el negocio la aclare (ver Datos Pendientes de Definir del CU original).
  # ⚠️ Escenario pendiente: el ícono de ayuda contextual "?" (RN06) no tiene un texto de mensaje transcrito en el documento. No se genera un escenario con contenido de backend no verificable.
  # ⚠️ Escenario pendiente: el mockup del Anexo A.1 usa un valor de "Medio" combinado ("Biológico-Flora y Fauna") que no coincide con el Catálogo Medio (Anexo C.1), y clasifica como "Positivo" impactos que describen efectos adversos; ninguna de las dos discrepancias se reproduce en los escenarios anteriores.