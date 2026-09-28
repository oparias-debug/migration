# language: es
@CU-PRE-33 @rol:TECNICO_URP @rol:TECNICO_PRE
Característica: Generar el reporte del Avance Cuatrimestral por Metas Físicas

  Como Técnico URP o Técnico PRE
  Quiero generar el reporte del Avance Cuatrimestral por Metas Físicas del PAP

  Esquema del escenario: Generar el reporte en el formato seleccionado (camino feliz, SF-4)
    Cuando el actor hace clic en el botón "Generar Reporte" avance-metas
    Y selecciona el formato "<formato>" avance-metas
    Entonces el sistema genera el reporte

    Ejemplos:
      | formato |
      | Excel   |
      | PDF     |

  Escenario: El Técnico URP solo puede generar el reporte de su propia unidad ejecutora
    Dado que el Técnico URP pertenece a una unidad ejecutora y existe otra unidad ejecutora avance-metas
    Cuando el Técnico URP solicita el reporte indicando la otra unidad ejecutora avance-metas
    Entonces el sistema genera el reporte de la unidad ejecutora del Técnico URP, ignorando la solicitada avance-metas
