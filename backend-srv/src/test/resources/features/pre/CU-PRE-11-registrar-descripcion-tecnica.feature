# language: es
@CU-PRE-11 @rol:TECNICO_URP
Característica: Registrar la descripción técnica del proyecto

  Como Técnico URP
  Quiero registrar la descripción técnica del proyecto (descripción general y detalle por producto)

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Formulación del Proyecto", sección "Estudio Técnico del Proyecto – Descripción Técnica" desc-tecnica
    Y se encuentra en la pantalla "Descripción Técnica" (Anexo A.1) desc-tecnica

  Escenario: Registrar y guardar la descripción técnica (camino feliz)
    Dado que el campo "Descripción del proyecto" se autocompletó con la descripción registrada en CU-PRE-01 "Registro de Proyectos"
    Y la tabla "Descripción Técnica" se autocompletó con los productos registrados en CU-PRE-09 "Análisis de Mercado"
    Cuando el Técnico URP selecciona el "Componente" correspondiente a cada producto
    Y registra la "Descripción" del producto, la "Cantidad" y la "Unidad de Medida"
    Y hace clic en el botón "Guardar" desc-tecnica
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.2) desc-tecnica
    Cuando el Técnico URP hace clic en "Aceptar" desc-tecnica
    Entonces el sistema guarda la información registrada (CU-PRE-11)
    Y se mantiene en la sección "Descripción Técnica" (CU-PRE-11)

  Escenario: Ajustar la descripción general autocompletada del proyecto
    Dado que el campo "Descripción del proyecto" se autocompletó automáticamente
    Cuando el Técnico URP ajusta o complementa el texto de ese campo
    Entonces el sistema permite la edición (RN03)

  Escenario: La descripción general se autocompleta desde la última Opinion Tecnica (O.T.) emitida, si existe
    Dado que el proyecto ya cuenta con una O.T. emitida
    Entonces el campo "Descripción del proyecto" se autocompleta con la descripción contenida en la última O.T. emitida, en lugar de la de CU-PRE-01 (RN03)

  Escenario: Agregar una fila para un producto adicional
    Cuando el Técnico URP hace clic en el botón emergente + "+" desc-tecnica
    Entonces el sistema agrega una nueva fila para seleccionar un producto adicional (RN05)

  Escenario: Eliminar una fila creada, manteniendo al menos una fila completa
    Dado más de una fila registrada en la tabla, con al menos una fila completamente diligenciada
    Cuando el Técnico URP hace clic en el botón emergente x "x" de una fila que él mismo creó desc-tecnica
    Entonces el sistema elimina esa fila (RN06)
    Y se mantiene al menos una fila con toda la información registrada

  Escenario: Respetar el límite de caracteres de la Descripción del producto
    Cuando el Técnico URP registra información en el campo "Descripción del producto"
    Entonces el sistema permite hasta 500 caracteres para ese campo

  Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
    Cuando el Técnico URP hace clic en "Guardar" sin haber completado el campo "<campo>" desc-tecnica
    Entonces el sistema sombrea en rojo el borde del campo "<campo>" (RN07) desc-tecnica

    Ejemplos:
      | campo                  |
      | Componente             |
      | Descripción del producto |
      | Cantidad               |
      | Unidad de medida       |

  # ⚠️ Escenario pendiente: existe una contradicción no resuelta sobre la editabilidad del campo "Producto": el Anexo B.1 lo marca "Editable: Sí", pero su propio Detalle indica que los productos trasladados desde CU-PRE-09 "permanecerán bloqueados para edición". Se modeló el comportamiento bloqueado para los productos autocompletados; no se genera un escenario que permita editarlos.
  # ⚠️ Escenario pendiente: el mockup muestra un valor de componente "Ambiental" que no existe en el Catálogo de componentes del proyecto (Anexo C.1). No se usa ese valor en ningún escenario.
  # ⚠️ Escenario pendiente: la columna "Capacidad de Producción" visible en el mockup no tiene tipo, formato, obligatoriedad ni editabilidad especificados en el Anexo B.1; no se modela.
  # ⚠️ Escenario pendiente: el ícono de ayuda contextual "?" (RN08) no tiene un texto de mensaje transcrito en el documento. No se genera un escenario con contenido de backend no verificable.