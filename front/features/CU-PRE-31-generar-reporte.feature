# language: es
@CU-PRE-31 @rol:TECNICO_URP @rol:TECNICO_PRE
Característica: Generar el reporte de la Programación Cuatrimestral por Meta Física

  Como Técnico URP o Técnico PRE
  Quiero generar el reporte de la Programación Cuatrimestral por Meta Física de la Preinversión

  Esquema del escenario: Generar el reporte en el formato seleccionado (camino feliz, SF-7)
    Cuando el actor hace clic en el botón "Generar Reporte" metas-fisicas
    Y selecciona el formato "<formato>" metas-fisicas
    Entonces el sistema genera el reporte (Anexo A.5)

    Ejemplos:
      | formato |
      | Excel   |
      | PDF     |

  Escenario: El botón Generar Reporte está habilitado para todos los actores
    Entonces el botón "GENERAR REPORTE" es visible y está habilitado para todos los actores (RN-E) metas-fisicas