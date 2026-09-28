# language: es
@CU-PRE-31 @rol:SISTEMA
Característica: Sincronización automática con la Programación Financiera

  Como Sistema
  Quiero desactivar automáticamente en la programación de metas físicas los códigos o etapas que se desactiven en la Programación Financiera

  Escenario: Desactivar un código automáticamente (camino feliz, SF-4)
    Dado que un código fue desactivado en la Programación Financiera CU-PRE-30
    Entonces el sistema lo desactiva automáticamente en la programación de Metas Físicas

  Escenario: Desactivar una etapa automáticamente (camino feliz, SF-5)
    Dado que una etapa fue eliminada en la Programación Financiera CU-PRE-30
    Entonces el sistema la desactiva automáticamente en la programación de Metas Físicas