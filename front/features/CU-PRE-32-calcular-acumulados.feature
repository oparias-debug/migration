# language: es
@CU-PRE-32 @rol:SISTEMA
Característica: Calcular acumulados y porcentajes del avance financiero

  Como Sistema
  Quiero calcular automáticamente los acumulados y porcentajes de avance financiero

  Escenario: Cálculo del Avance Anual Programado
    Entonces el sistema calcula "AAP" como la suma de lo Programado en los Cuatrimestres I, II y III (RN-E)

  Escenario: Cálculo del Avance Anual Ejecutado (monto)
    Entonces el sistema calcula el "Avance Anual Ejecutado" de cada cuatrimestre como la suma acumulada de los montos ejecutados hasta ese cuatrimestre (RN-E)

  Escenario: Cálculo del Avance Anual Ejecutado (porcentaje)
    Entonces el sistema calcula el "Avance Anual Ejecutado %" de cada cuatrimestre dividiendo el monto ejecutado acumulado entre el Avance Anual Programado, multiplicado por 100 (RN-E)

  Escenario: Cálculo del Avance al Cuatrimestre (monto y porcentaje)
    Entonces el sistema calcula el "Avance al Cuatrimestre Ejecutado" como el monto acumulado hasta el cuatrimestre en vigencia
    Y calcula su porcentaje dividiendo dicho monto entre lo programado acumulado hasta ese cuatrimestre, multiplicado por 100 (RN-E)

  Escenario: Cálculo del Avance del Cuatrimestre Ejecutado (porcentaje)
    Entonces el sistema calcula el porcentaje del "Avance del Cuatrimestre" dividiendo el monto ejecutado del cuatrimestre entre el monto programado de ese mismo cuatrimestre, multiplicado por 100 (RN-E)