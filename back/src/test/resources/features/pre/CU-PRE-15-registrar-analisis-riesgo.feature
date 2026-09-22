# language: es
@CU-PRE-15 @rol:TECNICO_URP
Característica: Registrar el análisis de riesgo

  Como Técnico URP
  Quiero indicar si existen riesgos de desastres asociados al proyecto y, en caso afirmativo, registrar el plan de mitigación de riesgos

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Formulación del Proyecto", sección "Análisis de Riesgos" analisis-riesgo
    Y se encuentra en la pantalla "Análisis de Riesgos" (Anexo A.1) analisis-riesgo

  Escenario: Desplegar la tabla al seleccionar "Sí"
    Cuando el Técnico URP selecciona si "Sí" en "¿Se han identificado riesgos de desastres?" analisis-riesgo
    Entonces el sistema despliega la tabla "Análisis de Riesgos" (RN04) analisis-riesgo

  Escenario: No desplegar la tabla al seleccionar "No"
    Cuando el Técnico URP selecciona "No" en "¿Se han identificado riesgos de desastres?" analisis-riesgo
    Entonces el sistema no despliega la tabla "Análisis de Riesgos" (RN04) analisis-riesgo

  Esquema del escenario: Cálculo y coloreado automático de la Calificación del Riesgo
    Cuando el Técnico URP selecciona la "Probabilidad" "<probabilidad>" y el "Impacto del Riesgo" "<impacto>" en una fila analisis-riesgo
    Entonces el sistema calcula y colorea automáticamente la "Calificación del Riesgo" como "<calificacion>" según el Anexo C.1 analisis-riesgo

    Ejemplos:
      | probabilidad  | impacto        | calificacion |
      | Improbable    | Moderado       | Bajo          |
      | Probable      | Alto           | Medio         |
      | Muy probable  | Extremo        | Muy alto      |
      | Casi seguro   | Extremo        | Muy alto      |
      | Casi seguro   | Bajo           | Medio         |

  Escenario: Registrar y guardar el análisis de riesgo (camino feliz)
    Cuando el Técnico URP registra la "Descripción del riesgo", la "Probabilidad" y el "Impacto del Riesgo" de una fila analisis-riesgo
    Y hace clic en el botón "Guardar" analisis-riesgo
    Entonces el sistema muestra el mensaje "Sus datos han sido guardados exitosamente. Recuerde que los costos derivados del análisis de riesgos deben estar considerados dentro del presupuesto del proyecto." (Anexo A.2) analisis-riesgo
    Cuando el Técnico URP hace clic en "Aceptar" analisis-riesgo
    Entonces el sistema guarda la información registrada analisis-riesgo
    Y se mantiene en la sección "Análisis de Riesgos" analisis-riesgo

  Escenario: Agregar una nueva fila a la tabla de Análisis de Riesgos
    Cuando el Técnico URP adiciona una nueva fila analisis-riesgo
    Entonces el sistema agrega la fila a la tabla "Análisis de Riesgos" (RN03) analisis-riesgo

  Escenario: Eliminar una fila de la tabla de Análisis de Riesgos
    Dado una fila registrada en la tabla analisis-riesgo
    Cuando el Técnico URP elimina esa fila analisis-riesgo
    Entonces el sistema elimina la fila correspondiente (RN03) analisis-riesgo

  Escenario: Cálculo automático del Total Acciones de Mitigación
    Dado que se han registrado costos de acción de mitigación en varias filas analisis-riesgo
    Entonces el sistema calcula el "Total Acciones de Mitigación" como la sumatoria de la columna "Costo Acción de Mitigación" analisis-riesgo

  Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
    Cuando el Técnico URP hace clic en "Guardar" sin haber completado el campo "<campo>" analisis-riesgo
    Entonces el sistema sombrea en rojo el borde del campo "<campo>" (RN07) analisis-riesgo

    Ejemplos:
      | campo                  |
      | Descripción del riesgo |
      | Probabilidad           |
      | Impacto del riesgo     |

  @ui-only
  Escenario: Mostrar la imagen de la Matriz de Riesgos para interpretar los colores
    Entonces el sistema muestra la imagen de la Matriz de Riesgos descrita en el Anexo C.2, como apoyo visual para interpretar la Calificación del Riesgo (RN05) analisis-riesgo

  # ⚠️ Escenario pendiente: no se transcribe la matriz completa de 20 combinaciones Probabilidad×Impacto del Anexo C.1; se usa una muestra representativa en los Ejemplos anteriores.
  # ⚠️ Escenario pendiente: el ícono de ayuda contextual "?" (RN08) no tiene un texto de mensaje transcrito en el documento. No se genera un escenario con contenido de backend no verificable.
  # ⚠️ Escenario pendiente: existe una discrepancia no resuelta en la redacción de la pregunta inicial entre RN04 ("¿Se han identificado riesgos de desastres?") y el mockup del Anexo A.1 ("...inminentes en el área de influencia del proyecto"); se usó la redacción de RN04 en los escenarios anteriores.