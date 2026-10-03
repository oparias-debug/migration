# language: es
@CU-PRE-31 @rol:TECNICO_URP
Característica: Registrar la programación de metas físicas de un estudio de arrastre

  Como Técnico URP
  Quiero registrar la programación de metas físicas de un estudio de arrastre

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Programación por Meta Física Cuatrimestral del PAP" (Anexo A.1) arrastre

  Escenario: Registrar y guardar la programación de metas de un estudio de arrastre (camino feliz, SF-1)
    Cuando el Técnico URP hace clic en el CUP de un proyecto de arrastre
    Entonces el sistema muestra el Anexo A.4 con "CUP", "Etapa", "Meta", "Entregable" y "Ejecutado años Anteriores" deshabilitados y precargados del ejercicio anterior
    Cuando el Técnico URP registra la programación cuatrimestral para cada etapa
    Y hace clic en el botón "Guardar"
    Entonces el sistema valida los datos según RN-B literal a.1
    Y guarda automáticamente el registro en la tabla del Anexo A.1

  Escenario: Salir sin guardar
    Cuando el Técnico URP hace clic en el botón "Salir"
    Entonces el sistema regresa a la pantalla del Anexo A.1 sin guardar los cambios metas-fisicas-arrastre

  Escenario: El porcentaje programado no puede superar el porcentaje pendiente de ejecutar
    Dado que el "Total Programado Año" supera el porcentaje pendiente de ejecutar del estudio
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "Porcentaje Programado supera el 100% de la etapa" (Anexo A.2)
    Y se mantiene en la pantalla del Anexo A.4 arrastre