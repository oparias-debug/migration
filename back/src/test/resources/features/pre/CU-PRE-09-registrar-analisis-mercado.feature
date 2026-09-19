# language: es
@CU-PRE-09 @rol:TECNICO_URP
Característica: Registrar el análisis de mercado

  Como Técnico URP
  Quiero registrar el análisis de mercado (oferta, demanda y déficit del año base y proyectado) por cada producto del proyecto

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Formulación del Proyecto", sección "Diagnóstico de la Situación Actual – Análisis de Mercado"
    Y se encuentra en la pantalla "Análisis de Mercado" (Anexo A.1)

  Escenario: Registrar y guardar el análisis de mercado de un producto (camino feliz)
    Cuando el Técnico URP selecciona un "Producto" del catálogo
    Entonces el sistema autocompleta el campo "Unidad de Medida" correspondiente
    Cuando el Técnico URP registra la "Demanda" y la "Oferta" del año base
    Entonces el sistema calcula el campo "Déficit" como Demanda menos Oferta
    Cuando el Técnico URP registra los "Años a Proyectar", la "Tasa Demanda" y la "Tasa Oferta"
    Entonces el sistema calcula el "Promedio Demanda" y el "Promedio Oferta" según las fórmulas de proyección definidas
    Y calcula el "Promedio Déficit" como Promedio Demanda menos Promedio Oferta
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.2)
    Cuando el Técnico URP hace clic en "Aceptar"
    Entonces el sistema guarda la información registrada
    Y se mantiene en la sección "Análisis de Mercado"

  Escenario: Agregar una nueva fila de producto
    Cuando el Técnico URP hace clic en el botón emergente "+"
    Entonces el sistema agrega una nueva fila a la tabla "Análisis de Mercado" (RN03)

  Escenario: Eliminar una fila de producto
    Dado una fila de producto ya registrada
    Cuando el Técnico URP hace clic en el botón emergente "x" de esa fila
    Entonces el sistema elimina la fila correspondiente (RN03)

  Escenario: Intentar guardar sin ninguna fila completamente diligenciada
    Dado que ninguna fila de la tabla está completamente diligenciada
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema no permite guardar, ya que debe existir al menos una fila completamente diligenciada (RN04)

  Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
    Cuando el Técnico URP hace clic en "Guardar" sin haber completado el campo "<campo>"
    Entonces el sistema sombrea en rojo el borde del campo "<campo>" (RN05)

    Ejemplos:
      | campo   |
      | Producto |
      | Demanda  |
      | Oferta   |

  # ⚠️ Escenario pendiente: el "Catálogo de Productos e Indicadores" referido en RN07 no está incluido en este documento, por lo que no se pueden enumerar valores concretos del campo "Producto" en los Ejemplos anteriores.
  # ⚠️ Escenario pendiente: el Anexo B.1 marca "Promedio Demanda" y "Promedio Oferta" como "Editable: Sí" pero también los describe como calculados por fórmula, igual que "Déficit" y "Promedio Déficit" (marcados "No" editables). No se genera un escenario que permita editar manualmente estos dos campos tras su cálculo; se modelan solo como campos calculados (ver Datos Pendientes de Definir del CU original).
  # ⚠️ Escenario pendiente: no se especifica el texto exacto del mensaje de validación de campos obligatorios (RN05), ni un mensaje específico documentado para el rechazo de valores negativos en "Demanda"/"Oferta" (solo se indica el rango "valores positivos" en el Anexo B.1). No se genera un escenario que asegure contenido de backend no verificable. El ícono de ayuda contextual "?" (RN06) tampoco tiene un texto de mensaje transcrito.