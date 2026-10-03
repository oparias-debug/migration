# language: es
@CU-PRE-22.1 @rol:TECNICO_URP
Característica: Registrar la programación financiera de la preinversión

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Programación", sección "Programación financiera de la Preinversión" - programacion-financiera-preinversion
    Y la columna "Etapa" se muestra autocompletada con las etapas registradas en CU-PRE-03.5 (RN03) - programacion-financiera-preinversion

  Escenario: Generar las columnas de período
    Cuando el Técnico URP registra la cantidad de "Períodos a programar (preinversión)" - programacion-financiera-preinversion
    Y hace clic en el botón "Aceptar" - programacion-financiera-preinversion
    Entonces el sistema muestra en la sección "Programación" una cantidad de columnas igual a la registrada (RN04, RN05) - programacion-financiera-preinversion

  Escenario: Registrar y guardar la programación por etapa y período
    Cuando el Técnico URP registra el monto de "Programación" para una etapa en un período habilitado - programacion-financiera-preinversion
    Y hace clic en el botón "Guardar" - programacion-financiera-preinversion
    Entonces el sistema guarda los registros realizados - programacion-financiera-preinversion

  Esquema del escenario: Cálculo automático del Total programación
    Dado la etapa "<etapa>" con costo de etapa "<costo_etapa>" distribuido en los períodos registrados - programacion-financiera-preinversion
    Entonces el sistema calcula el "Total" de esa fila como "<total_fila>" - programacion-financiera-preinversion

    Ejemplos:
      | etapa  | costo_etapa | total_fila |
      | Perfil | $2,000.00  | $2,000.00 |
      | Diseño | $15,000.00 | $15,000.00 |

  Escenario: Los períodos anteriores al actual aparecen deshabilitados
    Entonces las columnas de período anteriores al período actual se muestran deshabilitadas (RN09) - programacion-financiera-preinversion

  Escenario: Se puede programar más de una etapa en el mismo período
    Cuando el Técnico URP registra "Programación" para dos etapas distintas en el mismo período - programacion-financiera-preinversion
    Entonces el sistema permite ambos registros (RN09) - programacion-financiera-preinversion

  Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
    Cuando el Técnico URP hace clic en "Guardar" sin haber completado el campo "<campo>" - programacion-financiera-preinversion
    Entonces el sistema sombrea en rojo el borde del campo "<campo>" (RN07) - programacion-financiera-preinversion

    Ejemplos:
      | campo                               |
      | Períodos a programar (preinversión) |
      | Programación                         |

  Escenario: Sincronización del Costo de la etapa con CU-PRE-03.5
    Dado que existe un valor de "Costo de la etapa" para una etapa de preinversión - programacion-financiera-preinversion
    Entonces dicho valor se mantiene actualizado en el campo "Costo de la etapa" del CU-PRE-03.5 "Selección y registro de etapas" (RN06) - programacion-financiera-preinversion
