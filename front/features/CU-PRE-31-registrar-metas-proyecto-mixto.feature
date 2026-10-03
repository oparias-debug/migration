# language: es
@CU-PRE-31 @rol:TECNICO_URP
Característica: Registrar la programación de metas físicas de un proyecto con etapas de arrastre y nuevas

  Como Técnico URP
  Quiero que cada etapa de un mismo proyecto se valide según su propia condición (arrastre o nueva)
  Para recibir el mensaje de RN-B literal a.1 o a.2 que corresponde a la etapa que incumple

  # Caso mostrado en el mockup del Anexo A.1: un mismo CUP con una etapa ya programada en
  # ejercicios anteriores (arrastre) y otra etapa agregada en el ejercicio actual (nueva).

  Antecedentes:
    Dado que existe un proyecto con la etapa "Perfil" de arrastre y la etapa "Prefactibilidad" nueva

  Escenario: Cada etapa se clasifica por separado como arrastre o nueva
    Cuando el Técnico URP hace clic en el CUP del proyecto mixto
    Entonces la etapa "Perfil" se muestra como de arrastre con "Entregable" y "Ejecutado años Anteriores" precargados
    Y la etapa "Prefactibilidad" se muestra como nueva con los campos vacíos

  Escenario: La etapa nueva que supera el 100% recibe el mensaje de estudios nuevos (RN-B a.2)
    Cuando el Técnico URP guarda la etapa "Perfil" dentro de su pendiente y la etapa "Prefactibilidad" por encima del 100%
    Entonces el sistema rechaza el guardado del proyecto mixto con el código "MONTO_SUPERA_100_NUEVO"

  Escenario: La etapa de arrastre que supera su pendiente recibe el mensaje de estudios de arrastre (RN-B a.1)
    Cuando el Técnico URP guarda la etapa "Perfil" por encima de su pendiente y la etapa "Prefactibilidad" dentro del 100%
    Entonces el sistema rechaza el guardado del proyecto mixto con el código "PORCENTAJE_SUPERA_100_ARRASTRE"

  Escenario: Guardar ambas etapas dentro de sus límites (camino feliz)
    Cuando el Técnico URP guarda ambas etapas del proyecto mixto dentro de sus límites
    Entonces el sistema guarda la programación de ambas etapas del proyecto mixto
