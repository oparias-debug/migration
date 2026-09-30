# language: es
@CU-PRE-26 @rol:VIABILIZADOR @rol:TECNICO_URP @rol:TECNICO_PRE @rol:SISTEMA
Característica: Reemitir Viabilidad y Elegibilidad tras los ajustes

  Como Viabilizador
  Quiero revisar la información ajustada por el Técnico URP y reemitir Viabilidad y, si hubo observaciones a la Elegibilidad, ajustarla y reemitirla

  Escenario: El Viabilizador ve sin editar lo registrado por el Técnico URP en la pantalla de OT
    Dado que el usuario autenticado tiene el rol "Viabilizador"
    Y el Técnico URP registró información en "Justificación Institución"
    Cuando ingresa a la pantalla "Opinión Técnica" del proyecto
    Entonces puede ver la información registrada por el Técnico URP
    Pero no puede editarla

  Escenario: Sin comentarios a Elegibilidad el proyecto vuelve a OT sin pasar por Elegibilidad
    Dado que el usuario autenticado tiene el rol "Viabilizador"
    Y el Técnico PRE no emitió comentarios a los criterios de elegibilidad
    Y el Técnico URP envió los ajustes del proyecto
    Cuando emite Viabilidad en el CU-PRE-24 "Viabilidad" sobre la información ajustada
    Entonces el estado del proyecto cambia a "Proyecto viable"
    Y el proyecto vuelve a Opinión Técnica sin pasar por Elegibilidad

  Escenario: Con comentarios a Elegibilidad el Viabilizador ajusta y reemite la Elegibilidad
    Dado que el usuario autenticado tiene el rol "Viabilizador"
    Y el Técnico PRE emitió comentarios a los criterios de elegibilidad
    Cuando ajusta la selección de criterios en el CU-PRE-25 "Elegibilidad"
    Y emite nuevamente Elegibilidad
    Entonces el estado del proyecto cambia a "Proyecto Elegible"
    Y el proyecto vuelve a Opinión Técnica

  # Sin escenario: respuesta del Viabilizador a los comentarios en la pantalla del Anexo A.1 (FA03.1 vs. RN03). Ver historias-CU-PRE-26.md.
