# language: es
@CU-PRE-06 @rol:TECNICO_URP
Característica: Registrar y gestionar la matriz de gestión de interesados

  Como Técnico URP
  Quiero registrar y gestionar la matriz de gestión de interesados del proyecto

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Formulación del Proyecto", sección "Diagnóstico de la Situación Actual"
    Y se encuentra en la tabla "Matriz de gestión de interesados" (Anexo A.1)

  Escenario: Registrar y guardar un interesado (camino feliz)
    Cuando el Técnico URP registra el "Nombre del interesado"
    Y selecciona el "Tipo" (Cooperante, Oponente, Beneficiario o Perjudicado)
    Y selecciona el "Nivel de influencia" (Alto o Bajo)
    Y selecciona el "Nivel de interés" (Alto o Bajo)
    Y registra la "Estrategia de gestión"
    Y hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.2)
    Cuando el Técnico URP hace clic en "Aceptar"
    Entonces el sistema guarda la información registrada
    Y se mantiene en la pantalla "Matriz de gestión de interesados"

  Escenario: Agregar una nueva fila de interesado
    Cuando el Técnico URP hace clic en el botón "+"
    Entonces el sistema agrega una nueva fila a la tabla "Matriz de gestión de interesados" (RN03)

  Escenario: Eliminar una fila de interesado
    Dado un interesado registrado en la tabla
    Cuando el Técnico URP hace clic en el botón "x" junto a ese interesado
    Entonces el sistema elimina la fila del interesado correspondiente (RN04)

  Escenario: Registrar el mismo interesado más de una vez con al menos una columna distinta
    Dado un interesado ya registrado con un "Tipo", "Nivel de influencia", "Nivel de interés" y "Estrategia de gestión" específicos
    Cuando el Técnico URP agrega una nueva fila con el mismo "Nombre del interesado" pero un valor distinto en al menos una de las demás columnas
    Entonces el sistema permite el nuevo registro (RN05)

  Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
    Cuando el Técnico URP hace clic en "Guardar" sin haber completado el campo "<campo>"
    Entonces el sistema sombrea en rojo el borde del campo "<campo>" (RN06)

    Ejemplos:
      | campo                  |
      | Nombre del interesado  |
      | Tipo                   |
      | Nivel de influencia    |
      | Nivel de interés       |
      | Estrategia de gestión  |

  # ⚠️ Escenario pendiente: el documento no especifica qué ocurre (mensaje, bloqueo) si se intenta registrar el mismo interesado con exactamente los mismos valores en las cuatro columnas (caso no permitido según RN05, a contrario). No se inventa ese comportamiento de rechazo.
  # ⚠️ Escenario pendiente: el ícono de ayuda contextual "?" (RN07) no tiene un texto de mensaje transcrito en el documento. No se genera un escenario que asegure contenido de backend no verificable.