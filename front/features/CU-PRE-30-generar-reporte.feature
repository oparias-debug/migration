# language: es
@CU-PRE-30 @rol:TECNICO_URP @rol:TECNICO_PRE
Característica: Generar el reporte de la Programación Financiera de la Preinversión

  Como Técnico URP o Técnico PRE
  Quiero generar el reporte de la Programación Financiera de la Preinversión en Excel o PDF

  Esquema del escenario: Generar el reporte en el formato seleccionado (camino feliz, SF-3)
    Cuando el actor hace clic en el botón "Generar Reporte"
    Y selecciona el formato "<formato>"
    Entonces el sistema genera el reporte (Anexo A.8)

    Ejemplos:
      | formato |
      | Excel   |
      | PDF     |

  Escenario: El botón Generar Reporte está habilitado para todos los actores sin condición previa
    Entonces el botón "GENERAR REPORTE" es visible y está habilitado para todos los actores (RN-E)