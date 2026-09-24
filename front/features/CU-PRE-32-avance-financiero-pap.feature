# language: es
@CU-PRE-32 @rol:TECNICO_URP
Característica: Avance Cuatrimestral Financiero del PAP

  Como Técnico URP
  Quiero informar cuánto se ejecutó de lo programado en el cuatrimestre
  Para dar seguimiento financiero al PAP

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Avance Cuatrimestral Financiero del PAP" (Anexo A.1)

  Escenario: Consultar el avance del cuatrimestre (camino feliz)
    Entonces el sistema muestra una fila por etapa y fuente de financiamiento
    Y muestra lo programado y lo ejecutado del año y del cuatrimestre, con su porcentaje
    Y toma por defecto el año y el cuatrimestre vigentes (RN-A.a)

  Escenario: Cambiar de cuatrimestre
    Cuando el Técnico URP elige otro cuatrimestre
    Entonces el sistema muestra el avance de ese cuatrimestre

  Escenario: Registrar lo ejecutado de un estudio
    Cuando el Técnico URP hace clic en el CUP de un estudio
    Entonces el sistema abre el avance de ese estudio con sus fuentes (Anexo A.2)
    Cuando escribe el monto ejecutado del cuatrimestre y su observación
    Y hace clic en "Guardar"
    Entonces el sistema guarda el avance
    Y muestra el porcentaje ejecutado que calcula el servidor

  Escenario: Se avisa cuando lo ejecutado excede lo programado
    Dado que una fuente ejecutó más de lo programado
    Entonces el sistema señala esa fila con la alerta de exceso

  Escenario: Generar el reporte del avance financiero (SF-3)
    Cuando el actor hace clic en "Generar reporte"
    Entonces el sistema genera el reporte del año y el cuatrimestre en pantalla
    Y lo hace aun fuera del período del Calendario de Eventos del PAP (RN-A.b)
