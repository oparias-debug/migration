# language: es
@CU-PRE-3.5 @rol:COORDINADOR_SYMP
Característica: Usar el botón de selección radial "Co-ejecutor"

  Como Coordinador SYMP
  Quiero usar el botón de selección radial "Co-ejecutor" en la Ficha de información general

  Antecedentes:
    Dado que el actor se encuentra en la Ficha de información general (Anexo A.3) de un proyecto

  Escenario: El botón Co-ejecutor es visible únicamente para el Coordinador SYMP
    Cuando el Coordinador SYMP accede a la Ficha de información general
    Entonces el sistema muestra el botón de selección radial "Co-ejecutor" (RN16)

  Escenario: Usar el botón "Co-ejecutor" (camino feliz)
    Cuando el Coordinador SYMP hace clic en el botón de selección radial "Co-ejecutor"
    Entonces el sistema habilita el listado de Unidades Ejecutoras para selección

  Escenario: El botón Co-ejecutor no es visible para ningún otro actor
    Cuando un actor distinto del Coordinador SYMP (por ejemplo, Técnico URP) accede a la Ficha de información general
    Entonces el sistema no muestra el botón de selección radial "Co-ejecutor" (RN16)