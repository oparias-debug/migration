# language: es
@CU-PRE-33 @rol:TECNICO_URP @rol:TECNICO_PRE @rol:COORDINADOR_PRE
Característica: Informe de avance cuatrimestral de metas del PAP

  Como Técnico URP
  Quiero informar el avance físico alcanzado en el cuatrimestre
  Para dar seguimiento a las metas del PAP

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Informe de avance cuatrimestral de metas del PAP" (Anexo A.1)

  Escenario: Consultar el avance de metas del cuatrimestre (camino feliz)
    Entonces el sistema muestra una fila por estudio y etapa
    Y muestra lo programado y lo ejecutado del año y del cuatrimestre, y el total de la meta ejecutada
    Y muestra el estado del avance de cada etapa

  Escenario: Registrar el avance de un estudio
    Cuando el Técnico URP hace clic en el CUP de un estudio
    Entonces el sistema abre el avance de metas de ese estudio (Anexo A.2)
    Cuando escribe el avance del cuatrimestre y su observación
    Y hace clic en "Guardar"
    Entonces el sistema guarda el avance
    Y muestra el total de la meta ejecutada y el estado que calcula el servidor

  Escenario: La DGICP observa el avance y la institución responde (SF-2)
    Dado que quien entra es Técnico PRE o Coordinador PRE
    Cuando escribe sus observaciones y hace clic en "Enviar observaciones"
    Entonces el Técnico URP puede escribir y enviar su respuesta
    Y solo la DGICP puede hacer clic en "Finalizar revisión"

  Escenario: Generar el reporte del avance de metas
    Cuando el actor hace clic en "Generar reporte"
    Entonces el sistema genera el reporte del año y el cuatrimestre en pantalla
