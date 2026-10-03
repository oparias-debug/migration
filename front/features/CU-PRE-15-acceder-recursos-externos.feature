# language: es
@CU-PRE-15 @rol:TECNICO_URP
Característica: Acceder a recursos externos de apoyo al análisis de riesgo

  Como Técnico URP
  Quiero acceder a los recursos externos VIGEA y Mapa de Riesgo Departamental desde la pantalla de Análisis de Riesgos

  Esquema del escenario: Acceder a un recurso externo
    Dado que el Técnico URP se encuentra en la pantalla "Análisis de Riesgos" analisis-riesgo-recursos
    Cuando hace clic en el botón "<boton>" analisis-riesgo-recursos
    Entonces el sistema dirige al enlace externo "<url>" analisis-riesgo-recursos

    Ejemplos:
      | boton                          | url                                                                          |
      | VIGEA                          | https://mapas.marn.gob.sv/VIGEA/entry.aspx                                  |
      | MAPA DE RIESGO DEPARTAMENTAL    | https://portafolio.snet.gob.sv/digitalizacion/pdf/spa/doc00073/doc00073.htm  |