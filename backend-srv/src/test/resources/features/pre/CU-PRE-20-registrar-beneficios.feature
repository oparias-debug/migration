# language: es
@CU-PRE-20 @rol:TECNICO_URP
Característica: Registrar los beneficios de un proyecto

  Como Técnico URP
  Quiero registrar los beneficios del proyecto y su valor de rescate
  Para conformar el flujo de beneficios de la evaluación

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Beneficios del Proyecto" (Anexo A.1)
    Y el proyecto tiene vida útil configurada en el Presupuesto de Operación y Mantenimiento

  Escenario: Consultar la pantalla (camino feliz)
    Entonces el sistema muestra las secciones "Beneficios directos", "Beneficios indirectos" y "Externalidades"
    Y cada beneficio con su parámetro, su tipo de ingreso y su monto en cada período
    Y muestra el flujo de beneficios del proyecto a precios de mercado
    Y las columnas de período corresponden a la vida útil de CU-PRE-18 (RN05)

  Escenario: Registrar un beneficio en modo Automático
    Cuando el Técnico URP hace clic en "Agregar beneficio" de una sección
    Y escribe el nombre del beneficio, elige el parámetro y el tipo de ingreso "Automático"
    Y registra el monto del período 1 y la tasa de crecimiento proyectado
    Y hace clic en "Guardar"
    Entonces el sistema registra el beneficio en la sección de su tipo de beneficio (RN08)
    Y proyecta el monto de los períodos siguientes a partir del período 1 y la tasa

  Escenario: Registrar un beneficio en modo Manual
    Cuando el Técnico URP elige el tipo de ingreso "Manual"
    Entonces el sistema pide un monto por cada período de la vida útil
    Cuando registra los montos y hace clic en "Guardar"
    Entonces el sistema registra el beneficio con los montos indicados

  Esquema del escenario: El sistema no permite guardar un beneficio incompleto
    Cuando el Técnico URP intenta guardar un beneficio <sin>
    Entonces el sistema no lo guarda y lo indica

    Ejemplos:
      | sin                                             |
      | sin parámetro                                   |
      | sin tipo de ingreso                             |
      | en modo Manual y sin el monto de ningún período |

  Escenario: Salir del detalle sin guardar
    Cuando el Técnico URP hace clic en "Salir" en el detalle del beneficio
    Entonces no se registra ningún beneficio y la pantalla queda como estaba

  Escenario: Quitar un beneficio registrado
    Cuando el Técnico URP hace clic en "Quitar" en un beneficio
    Y confirma la acción
    Entonces el beneficio desaparece de su sección y del flujo de beneficios

  Escenario: Registrar el valor de rescate
    Cuando el Técnico URP escribe el valor de rescate y elige el tipo de bien
    Y hace clic en "Guardar"
    Entonces el sistema guarda ambos datos

  Escenario: El proyecto todavía no tiene vida útil (RN05)
    Dado que el proyecto no tiene vida útil configurada en CU-PRE-18
    Entonces el sistema lo indica y no muestra columnas de período

  Escenario: Los precios ajustados son solo para usuarios internos (RN09, DN-01)
    Dado que quien consulta es el Técnico URP
    Entonces no se muestran el flujo a precios ajustados, el factor de corrección ni el valor de rescate ajustado
    Pero sí se muestran cuando quien consulta es el Técnico PRE
