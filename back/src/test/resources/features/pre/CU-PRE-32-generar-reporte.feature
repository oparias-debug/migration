# language: es
@CU-PRE-32 @rol:TECNICO_URP @rol:TECNICO_PRE
Característica: Generar el reporte del Avance Cuatrimestral Financiero

  Como Técnico URP o Técnico PRE
  Quiero generar el reporte del Avance Cuatrimestral Financiero del PAP

  Esquema del escenario: Generar el reporte en el formato seleccionado (camino feliz, SF-3)
    Cuando el actor hace clic en el botón "Generar Reporte" avance-financiero
    Y selecciona el formato "<formato>" avance-financiero
    Entonces el sistema genera el reporte (Anexo A.6)

    Ejemplos:
      | formato |
      | Excel   |
      | PDF     |

  Esquema del escenario: Contenido del reporte (Anexo A.6) según el actor
    Dado que existe avance financiero registrado y "Comentarios al reporte financiero DGICP" para el período
    Cuando el "<actor>" genera el reporte en Excel avance-financiero
    Entonces el reporte muestra la "Institución Ejecutora" y las columnas del Anexo A.1 con su fila "TOTAL" (Anexo A.6)
    Y el campo "Comentarios al reporte financiero DGICP" "<visibilidad>" en el reporte (solo actores internos DGICP)

    Ejemplos:
      | actor       | visibilidad   |
      | TECNICO_PRE | se muestra    |
      | TECNICO_URP | no se muestra |