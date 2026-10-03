# language: es
@CU-PRE-26.5 @rol:TECNICO_SYMP @rol:COORDINADOR_SYMP @rol:SISTEMA
Característica: Calificar el criterio 5 de priorización

  Como Técnico SYMP
  Quiero calificar los subcriterios del criterio 5 de la matriz multicriterio del proyecto

  Antecedentes:
    Dado un proyecto con Viabilidad (CU-PRE-24), Elegibilidad (CU-PRE-25) y Opinión Técnica (CU-PRE-26) emitidas
    Y se completó la calificación de los criterios 1, 2, 3 y 4 del proyecto
    Y el usuario autenticado tiene el rol "Técnico SYMP"

  Escenario: Acceder al formulario de priorización desde el menú Gestiones
    Cuando ingresa a la pantalla "Captura de proyectos"
    Y da clic en el proyecto a priorizar
    Y selecciona en el menú "Gestiones" la opción "Priorización"
    Entonces el Sistema muestra el formulario "Priorización: Matriz multicriterio" del Anexo A.1

  Esquema del escenario: El Técnico SYMP solo califica el criterio 5
    Dado que está en el formulario "Priorización: Matriz multicriterio" del proyecto
    Entonces la columna "Calificación" de los subcriterios del criterio <criterio> <estado>

    Ejemplos:
      | criterio | estado             |
      | 1        | no está habilitada |
      | 2        | no está habilitada |
      | 3        | no está habilitada |
      | 4        | no está habilitada |
      | 5        | está habilitada    |

  Escenario: Guardar la calificación en progreso
    Dado que está en el formulario "Priorización: Matriz multicriterio" del proyecto
    Y seleccionó la calificación de uno o más subcriterios del criterio 5
    Cuando da clic en el botón "Guardar"
    Entonces el Sistema muestra el mensaje emergente del Anexo A.5 "¡Guardado!" con el texto "Sus datos han sido guardados correctamente"
    Cuando da clic en "Aceptar"
    Entonces el Sistema guarda la información registrada
    Y se mantiene en la pantalla "Priorización"

  Esquema del escenario: Error al calificar con un subcriterio del criterio 5 sin puntaje
    Dado que está en el formulario "Priorización: Matriz multicriterio" del proyecto
    Y seleccionó un puntaje para todos los subcriterios del criterio 5 excepto el subcriterio "<subcriterio>"
    Cuando da clic en el botón para calificar la priorización
    Entonces el Sistema muestra el mensaje "Error. Debe seleccionar un puntaje para cada subcriterio"

    Ejemplos:
      | subcriterio |
      | 5.1         |
      | 5.2         |
      | 5.3         |

  Escenario: Enviar la calificación del criterio 5 a revisión del Coordinador SYMP
    Dado que está en el formulario "Priorización: Matriz multicriterio" del proyecto
    Y seleccionó un puntaje para cada subcriterio del criterio 5
    Cuando da clic en el botón para calificar la priorización
    Entonces el Sistema notifica al Coordinador SYMP que es necesario revisar la calificación de la prioridad realizada por el Técnico SYMP

  # ⚠️ Escenario pendiente: existe una contradicción sin resolver (ver CU original) sobre la obligatoriedad de calificar el criterio 5 ("cuando sea necesario" en la Descripción vs. RN07 y mensaje A.3; Observaciones ítem 40). No implementar hasta que se resuelva.
  # Nota: el nombre del botón de calificación está en disputa ("Calificar Prioridad" vs. "Calificar Priorización"); los pasos usan redacción neutral.
