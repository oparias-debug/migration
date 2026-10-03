# language: es
@CU-PRE-26.5 @rol:TECNICO_PRE @rol:COORDINADOR_PRE @rol:SISTEMA
Característica: Calificar los criterios 1, 2, 3 y 4 de priorización

  Como Técnico PRE
  Quiero calificar los subcriterios de los criterios 1, 2, 3 y 4 de la matriz multicriterio del proyecto

  Antecedentes:
    Dado un proyecto con Viabilidad (CU-PRE-24), Elegibilidad (CU-PRE-25) y Opinión Técnica (CU-PRE-26) emitidas
    Y el usuario autenticado tiene el rol "Técnico PRE"

  Escenario: Acceder al formulario de priorización desde Captura de proyectos
    Cuando ingresa a la pantalla "Captura de proyectos"
    Y da clic en el proyecto a priorizar
    Entonces el Sistema muestra el formulario "Priorización: Matriz multicriterio" del Anexo A.1

  Escenario: Valores disponibles en la columna "Calificación"
    Dado que está en el formulario "Priorización: Matriz multicriterio" del proyecto
    Cuando despliega la columna "Calificación" de un subcriterio
    Entonces el Sistema muestra los valores "N/A", "0", "1", "2", "3", "4" y "5"
    Y muestra el botón "Ver Escala de Calificación"

  Escenario: Consultar la escala de calificación de un subcriterio
    Dado que está en el formulario "Priorización: Matriz multicriterio" del proyecto
    Y desplegó la columna "Calificación" del subcriterio "1.1"
    Cuando da clic en el botón "Ver Escala de Calificación"
    Entonces el Sistema muestra una ventana emergente con la escala de calificación del subcriterio "1.1" definida en el Anexo C

  Esquema del escenario: El Técnico PRE solo califica los criterios 1, 2, 3 y 4
    Dado que está en el formulario "Priorización: Matriz multicriterio" del proyecto
    Entonces la columna "Calificación" de los subcriterios del criterio <criterio> <estado>

    Ejemplos:
      | criterio | estado             |
      | 1        | está habilitada    |
      | 2        | está habilitada    |
      | 3        | está habilitada    |
      | 4        | está habilitada    |
      | 5        | no está habilitada |

  Escenario: Guardar la calificación en progreso
    Dado que está en el formulario "Priorización: Matriz multicriterio" del proyecto
    Y seleccionó la calificación de uno o más subcriterios de los criterios 1, 2, 3 y 4
    Cuando da clic en el botón "Guardar"
    Entonces el Sistema muestra el mensaje emergente del Anexo A.5 "¡Guardado!" con el texto "Sus datos han sido guardados correctamente"
    Cuando da clic en "Aceptar"
    Entonces el Sistema guarda la información registrada
    Y se mantiene en la pantalla "Priorización"

  Esquema del escenario: Error al calificar con un subcriterio sin puntaje
    Dado que está en el formulario "Priorización: Matriz multicriterio" del proyecto
    Y seleccionó un puntaje para todos los subcriterios de los criterios 1, 2, 3 y 4 excepto el subcriterio "<subcriterio>"
    Cuando da clic en el botón para calificar la priorización
    Entonces el Sistema muestra el mensaje "Error. Debe seleccionar un puntaje para cada subcriterio"

    Ejemplos:
      | subcriterio |
      | 1.1         |
      | 2.4         |
      | 3.3         |
      | 4.3         |

  Escenario: Enviar la calificación completa a revisión del Coordinador PRE
    Dado que está en el formulario "Priorización: Matriz multicriterio" del proyecto
    Y seleccionó un puntaje para cada subcriterio de los criterios 1, 2, 3 y 4
    Cuando da clic en el botón para calificar la priorización
    Entonces el Sistema notifica al Coordinador PRE que es necesario revisar la calificación de la prioridad realizada por el Técnico PRE

  # Sin escenario: Opción 1 (redirección automática al formulario tras emitir OT) — en conflicto con CU-PRE-26 (botón "IR A PRIORIZACIÓN"). Ver historias-CU-PRE-26.5.md.
  # Sin escenario: momento del bloqueo de la pantalla (RN05 vs. FB1 paso 8) y visibilidad de "Guardar" por rol (RN03 "según corresponda"). Ver historias-CU-PRE-26.5.md.
  # Nota: el nombre del botón de calificación está en disputa ("Calificar Prioridad" vs. "Calificar Priorización"); los pasos usan redacción neutral.
