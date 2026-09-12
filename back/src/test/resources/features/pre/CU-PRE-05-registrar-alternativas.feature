# language: es
@CU-PRE-05 @rol:TECNICO_URP
Característica: Registrar y guardar las alternativas de solución

  Como Técnico URP
  Quiero registrar y guardar una o varias alternativas de solución, seleccionar la más conveniente y justificar dicha selección

  Antecedentes:
    Dado que el Técnico URP ingresa a la sección "Registro de Alternativas" de la pestaña "Identificación del proyecto"
    Y el sistema muestra una fila por defecto en la tabla "Registro de Alternativas de Solución" (RN2-1)

  Escenario: Registrar y guardar alternativas de solución (camino feliz)
    Cuando el Técnico URP registra el "Nombre de la alternativa", el "Monto de la alternativa" y la "Descripción de la alternativa" en una o más filas
    Y selecciona, mediante el botón radial, la alternativa más conveniente
    Y registra la "Justificación" de dicha selección
    Y hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.2)
    Cuando el Técnico URP hace clic en "Aceptar"
    Entonces el sistema guarda la información registrada de las alternativas
    Y se mantiene en la sección "Registro de alternativas"
    Y el sistema resalta el texto de la alternativa seleccionada

  Escenario: Agregar una nueva fila de alternativa
    Cuando el Técnico URP hace clic en el botón emergente "+"
    Entonces el sistema agrega una nueva fila a la tabla "Registro de Alternativas de Solución" (RN2-1)

  Escenario: Eliminar una alternativa
    Dado una alternativa registrada en la tabla
    Cuando el Técnico URP hace clic en el botón emergente "x" junto a esa alternativa
    Entonces el sistema elimina la alternativa correspondiente (RN2-2)

  Escenario: Solo puede seleccionarse una alternativa como la más conveniente
    Dado que una alternativa ya está seleccionada mediante el botón radial
    Cuando el Técnico URP selecciona el botón radial de otra alternativa
    Entonces el sistema deja seleccionada únicamente la nueva alternativa (RN2-6)

  Escenario: Intentar guardar con campos pendientes de completar
    Cuando el Técnico URP hace clic en el botón "Guardar" sin haber completado todos los campos de las alternativas
    Entonces el sistema sombrea en color rojo los bordes de los campos pendientes de completar (RN3-2)

  # ⚠️ Escenario pendiente: existe una contradicción no resuelta entre RN2-3 (justificación obligatoria solo cuando se registra una única alternativa) y RN2-7 (justificación obligatoria de forma general, sin condición aparente). No se genera un escenario que asuma la interpretación general de RN2-7 por encima de la condición específica de RN2-3; solo se modela el caso documentado con mensaje literal (ver CU-PRE-05-avanzar-analisis-interesados.feature). No se resuelve esta contradicción aquí (ver Datos Pendientes de Definir del CU original).
  # ⚠️ Escenario pendiente: el ícono de ayuda contextual "?" (RN3-1) no tiene un texto de mensaje transcrito en el documento. No se genera un escenario que asegure contenido de backend no verificable.
  # ⚠️ Escenario pendiente: el campo "Descripción de la alternativa" no cuenta con Tipo, Formato ni límite de caracteres especificados en el Anexo B.1, a pesar de aparecer en el mockup. No se genera un escenario de validación para ese campo hasta que el negocio lo defina.