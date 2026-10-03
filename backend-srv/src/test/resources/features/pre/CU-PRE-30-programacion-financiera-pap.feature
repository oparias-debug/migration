# language: es
@CU-PRE-30 @rol:TECNICO_URP
Característica: Programación Financiera Cuatrimestral del PAP

  Como Técnico URP
  Quiero programar cuánto costará cada estudio en cada cuatrimestre del año
  Para conformar el PAP de mi institución

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Programación Financiera Cuatrimestral del PAP" (Anexo A.1)

  Escenario: Consultar la programación del año (camino feliz)
    Entonces el sistema muestra una fila por etapa y fuente de financiamiento
    Y muestra el costo de la etapa, lo ejecutado en años anteriores, los tres cuatrimestres, el total del año y los años posteriores
    Y toma por defecto el año vigente y la unidad ejecutora del Técnico URP (RN-A.a)

  Escenario: Buscar un estudio (RN-E)
    Cuando el Técnico URP escribe un CUP o parte del nombre del proyecto
    Y hace clic en "Buscar"
    Entonces el sistema muestra únicamente los estudios que coinciden

  Escenario: Programar los cuatrimestres de un estudio
    Cuando el Técnico URP hace clic en el CUP de un estudio
    Entonces el sistema abre la programación de ese estudio con sus etapas y fuentes (Anexo A.2)
    Cuando escribe el monto de cada cuatrimestre
    Y hace clic en "Guardar"
    Entonces el sistema guarda la programación
    Y muestra el total del año y los años posteriores que calcula el servidor (RN-B.c)

  Escenario: Agregar una fuente de financiamiento a una etapa
    Dado que el Técnico URP está en la programación de un estudio
    Cuando hace clic en "Agregar fila"
    Y elige la fuente de financiamiento y escribe sus montos
    Y hace clic en "Guardar"
    Entonces la etapa queda con la nueva fuente

  Escenario: Generar el reporte del PAP
    Cuando el Técnico URP hace clic en "Generar reporte"
    Entonces el sistema genera el reporte del año para su unidad ejecutora

  Escenario: Habilitar modificaciones fuera del plazo
    Dado que el período de elaboración del PAP ya cerró
    Cuando la DGICP habilita las modificaciones fuera de plazo
    Entonces el Técnico URP puede volver a modificar la programación
