# language: es
@CU-PRE-3.5 @rol:TECNICO_URP
Característica: Registrar el costo y las fechas de las etapas de la Ruta de Preinversión

  Como Técnico URP
  Quiero registrar el costo y las fechas de cada etapa de la Ruta de Preinversión

  Antecedentes:
    Dado que el Técnico URP se encuentra en la tabla "Registro de Etapas" del Anexo A.1, con las etapas ya trasladadas o seleccionadas

  Escenario: Registrar costo y fechas de una etapa (camino feliz)
    Cuando el Técnico URP registra el "Costo de la etapa" (con la coma como separador de unidades)
    Y registra la "Fecha estimada de inicio" y la "Fecha estimada de finalización" en formato dd/mm/aaaa
    Y hace clic en el botón "Guardar"
    Entonces el sistema guarda la información registrada de la etapa
    Y se mantiene en la pantalla del Anexo A.1
    Y habilita los botones de las etapas para visualización o registro de información

  Esquema del escenario: Intentar guardar una etapa con un campo obligatorio incompleto
    Cuando el Técnico URP hace clic en "Guardar" sin haber completado el campo "<campo>" de una etapa
    Entonces el sistema marca en rojo el borde del campo "<campo>" (RN19)

    Ejemplos:
      | campo                          |
      | Costo de la etapa              |
      | Fecha estimada de inicio       |
      | Fecha estimada de finalización |

  Esquema del escenario: Intentar registrar una fecha en un formato distinto a dd/mm/aaaa
    Cuando el Técnico URP intenta registrar "Fecha estimada de inicio" o "Fecha estimada de finalización" en un formato distinto de dd/mm/aaaa
    Entonces el sistema no acepta el valor, ya que el formato obligatorio es dd/mm/aaaa (RN04)

  Esquema del escenario: Habilitación por defecto de los botones de etapa según el tipo de iniciativa
    Dado un proyecto con Iniciativa de Inversión "<iniciativa>"
    Entonces el sistema habilita por defecto los botones de las etapas "<etapas_por_defecto>"

    Ejemplos:
      | iniciativa          | etapas_por_defecto     |
      | Proyecto            | Perfil, Ejecución       |
      | Programa            | Perfil, Ejecución       |
      | Estudio General     | Perfil, Ejecución       |
      | Proyecto de emergencia | Perfil, Ejecución    |

  Escenario: Habilitación de etapas posteriores para iniciativa tipo Proyecto tras emitir Opinión Técnica
    Dado un proyecto de iniciativa "Proyecto" con la etapa Perfil habilitada por defecto
    Cuando se emite Opinión Técnica para la etapa Perfil
    Entonces el sistema habilita el botón de la siguiente etapa correspondiente (Prefactibilidad, Factibilidad o Diseño, según la Ruta de Preinversión)

  Escenario: Actualización automática del costo de la etapa de Ejecución al emitir Opinión Técnica
    Cuando se emite una Opinión Técnica o una Actualización de Opinión Técnica al proyecto
    Entonces el sistema actualiza automáticamente el campo "Costo de la etapa" de Ejecución
    Y toma el valor del campo "Total inversión" del Anexo A.1 de CU-PRE-17 "Presupuesto de inversión"

  Escenario: El costo de las etapas de Preinversión procede de la Programación Financiera de la Preinversión
    Entonces el "Costo de la etapa" de cada etapa de Preinversión (Perfil, Prefactibilidad, Factibilidad, Diseño, Estudio General) procede del campo "Monto (US$)" del CU-PRE-22.1 "Programación financiera de la preinversión del Proyecto" (RN22)
    Y el monto se actualiza según la suma de los totales por etapa registrados en la columna "Total" de CU-PRE-22.1 (RN12)

  Esquema del escenario: Habilitación de campos de identificación/formulación/evaluación/programación según la etapa e iniciativa (muestra representativa del Anexo F, correspondencia con RN20 confirmada)
    Dado que el Técnico URP guarda la información de la etapa "<etapa>" para una iniciativa de tipo "<iniciativa>"
    Entonces el sistema habilita el campo "<contenido>" de "<ubicacion_cu>" según la matriz del Anexo F (RN20)

    Ejemplos:
      | etapa           | iniciativa      | contenido                          | ubicacion_cu |
      | Perfil          | Proyecto        | Antecedentes                       | CUPRE-04      |
      | Prefactibilidad | Proyecto        | Análisis de Alternativas de Solución | CUPRE-05    |
      | Factibilidad    | Proyecto        | Descripción Técnica                | CUPRE-11      |
      | Diseño          | Estudio General | Programación Financiera Preinversión | CUPRE-22.1  |
      | Perfil          | Programa        | Presupuesto de Inversión           | CUPRE-17      |

  Esquema del escenario: El símbolo "-" en la columna de Actualización de O.T. significa que el campo no aplica
    Dado una fila del Anexo F con el símbolo "-" en la columna "Campos a habilitar para Actualización de O.T."
    Entonces ese campo no aplica al proceso de Actualización de Opinión Técnica (distinto de una celda vacía, que indica ausencia de dato)

  # ⚠️ Escenario pendiente: no se transcribe la matriz completa de 30 filas del Anexo F; se usa una muestra representativa en los Ejemplos anteriores. Sigue pendiente si "Fuentes de Financiamiento" debería tener su propia referencia de "Ubicación en caso de uso" distinta de "Presupuesto de Inversión" (celda combinada con CU-PRE-17 en el Anexo F).