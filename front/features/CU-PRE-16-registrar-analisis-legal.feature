# language: es
@CU-PRE-16 @rol:TECNICO_URP
Característica: Registrar el análisis legal

  Como Técnico URP
  Quiero indicar si se requiere algún análisis o gestión legal previo a la ejecución del proyecto y, en caso afirmativo, registrar el detalle y costo de cada gestión

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Formulación del Proyecto", sección "Análisis Legal" analisis-legal
    Y se encuentra en la pantalla "Análisis Legal" (Anexo A.1) analisis-legal

  Escenario: Desplegar la tabla al seleccionar "Sí"
    Cuando el Técnico URP selecciona  si "Sí" en "¿Se requiere algún análisis o gestión legal previo a la ejecución?" analisis-legal
    Entonces el sistema despliega la tabla "Análisis Legal" (RN04) analisis-legal

  Escenario: No desplegar la tabla al seleccionar "No"
    Cuando el Técnico URP selecciona no "No" en "¿Se requiere algún análisis o gestión legal previo a la ejecución?" analisis-legal
    Entonces el sistema no despliega la tabla "Análisis Legal" (RN04) analisis-legal

  Escenario: Registrar y guardar el análisis legal (camino feliz)
    Dado que la tabla "Análisis Legal" está desplegada analisis-legal
    Cuando el Técnico URP registra el "Análisis o Gestión Legal Requerida" y el "Entregable" de una fila analisis-legal
    Y registra el "Costo del Entregable" analisis-legal
    Y hace clic en el botón "Guardar" analisis-legal
    Entonces el sistema muestra el mensaje "Sus datos han sido guardados exitosamente. Recuerde que los costos derivados del análisis legal deben estar considerados dentro del presupuesto del proyecto." (Anexo A.2) analisis-legal
    Cuando el Técnico URP hace clic en "Aceptar" analisis-legal
    Entonces el sistema guarda la información registrada analisis-legal
    Y se mantiene en la sección "Análisis Legal" analisis-legal

  Escenario: Agregar una nueva fila a la tabla de Análisis Legal
    Cuando el Técnico URP adiciona una nueva fila analisis-legal
    Entonces el sistema agrega la fila a la tabla "Análisis Legal" (RN03) analisis-legal

  Escenario: Eliminar una fila de la tabla de Análisis Legal
    Dado una fila registrada en la tabla analisis-legal
    Cuando el Técnico URP elimina esa fila analisis-legal
    Entonces el sistema elimina la fila correspondiente (RN03) analisis-legal

  Escenario: Cálculo automático del Total Costo Entregables
    Dado que se han registrado costos de entregable en varias filas analisis-legal
    Entonces el sistema calcula el "Total Costo Entregables" como la sumatoria de la columna "Costo del Entregable" analisis-legal

  Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
    Cuando el Técnico URP hace clic en "Guardar" sin haber completado el campo "<campo>" analisis-legal
    Entonces el sistema sombrea en rojo el borde del campo "<campo>" (RN05) analisis-legal

    Ejemplos:
      | campo                              |
      | Análisis o gestión legal requerida |
      | Entregable                          |

  Escenario: El campo Costo del entregable no es obligatorio
    Dado que "Análisis o gestión legal requerida" y "Entregable" están completos en una fila analisis-legal
    Cuando el Técnico URP guarda sin registrar el "Costo del Entregable" analisis-legal
    Entonces el sistema permite guardar la fila analisis-legal

  # ⚠️ Escenario pendiente: el ícono de ayuda contextual "?" (RN06) no tiene un texto de mensaje transcrito en el documento. No se genera un escenario con contenido de backend no verificable.
  # ⚠️ Escenario pendiente: no se especifica una amplitud máxima de caracteres para "Análisis o gestión legal requerida", a diferencia de campos de texto similares en otros CUs de la serie (ver Observaciones del CU original). No se inventa un límite.