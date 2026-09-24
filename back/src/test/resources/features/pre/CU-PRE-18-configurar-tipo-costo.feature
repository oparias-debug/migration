# language: es
@CU-PRE-18 @rol:TECNICO_URP @wip
Característica: Seleccionar el tipo de costo y configurar vida útil y tasa de crecimiento

  Como Técnico URP
  Quiero seleccionar el tipo de costo a registrar y configurar la vida útil y la tasa de crecimiento de los costos

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Formulación", sección "Presupuesto de Operación y Mantenimiento"

  Esquema del escenario: Desplegar campos y tablas según el tipo de costo seleccionado
    Cuando el Técnico URP selecciona "<tipo_costo>" en "Seleccione el tipo de costo a registrar"
    Entonces el sistema despliega los campos "Vida útil (periodos a proyectar)" y "Tasa de crecimiento de costos"
    Y despliega "<tablas>", mostrando por defecto solo el Período 1 (RN04)

    Ejemplos:
      | tipo_costo    | tablas                                              |
      | Operación     | la tabla "Costos de Operación"                       |
      | Mantenimiento | la tabla "Costos de Mantenimiento"                   |
      | O&M           | las tablas "Costos de Operación" y "Costos de Mantenimiento" |

  Escenario: "No aplica" no despliega campos ni tablas
    Cuando el Técnico URP selecciona "No aplica" en "Seleccione el tipo de costo a registrar"
    Entonces el sistema no despliega los campos "Vida útil" ni "Tasa de crecimiento de costos", ni ninguna tabla
    Y el Técnico URP puede continuar directamente con los botones "Guardar" y "Siguiente" (RN04)

  Escenario: Generar columnas de período según la Vida útil registrada
    Cuando el Técnico URP registra "15" en el campo "Vida útil (periodos a proyectar)"
    Y hace clic en el botón "Aceptar"
    Entonces el sistema agrega 15 columnas de período en las tablas correspondientes, tituladas según el período (RN05)

  Esquema del escenario: Intentar continuar con un campo obligatorio incompleto
    Cuando el Técnico URP no ha completado el campo "<campo>"
    Entonces el sistema sombrea en rojo el borde del campo "<campo>" al hacer clic en "Guardar" (RN11)

    Ejemplos:
      | campo                          |
      | Vida útil (periodos a proyectar) |
      | Tasa de crecimiento de costos  |

  Escenario: La Tasa de crecimiento de costos tiene un rango de referencia parametrizable
    Entonces el campo "Tasa de crecimiento de costos" acepta valores de referencia entre 0% y 3%
    Y dicho rango puede ser actualizado en el tiempo por el Administrador del Sistema

  # ⚠️ Escenario pendiente: el texto exacto del mensaje mostrado al hacer clic en "Guardar" con campos pendientes (RN11) no está especificado en el documento; solo se describe el sombreado en rojo.
