# language: es
@CU-PRE-18 @backend
Característica: CU-PRE-18 - presupuesto de operación y mantenimiento en el backend

  Escenario: El Técnico URP configura, registra y consulta costos de operación
    Dado un contexto autenticado exclusivo del CU-PRE-18
    Cuando el Técnico URP del CU-PRE-18 configura Operación con vida útil 3 y tasa 3
    Y registra una actividad de operación con un insumo de mercado 100 y factor 1.2
    Entonces el presupuesto del CU-PRE-18 proyecta mercado 100, 103 y 106.09
    Y el CU-PRE-18 no muestra los precios ajustados al Técnico URP (RN09)

  Escenario: El Técnico URP no puede registrar una actividad sin insumos
    Dado un contexto autenticado exclusivo del CU-PRE-18
    Cuando el Técnico URP del CU-PRE-18 intenta registrar una actividad sin insumos
    Entonces el CU-PRE-18 rechaza el registro por falta de insumos

  Escenario: El Técnico PRE puede consultar pero no modificar el presupuesto, y sí ve los precios ajustados
    Dado un presupuesto de operación registrado para consulta del CU-PRE-18
    Cuando el Técnico PRE del CU-PRE-18 consulta el presupuesto
    Entonces el CU-PRE-18 devuelve el presupuesto en modo consulta
    Y el presupuesto del CU-PRE-18 proyecta ajustado 120, 123.6 y 127.31 para el Técnico PRE (RN09)
    Cuando el Técnico PRE del CU-PRE-18 intenta modificar la configuración
    Entonces el CU-PRE-18 rechaza la modificación por rol

  Escenario: El Técnico URP elimina únicamente la actividad de la tabla indicada
    Dado un contexto autenticado exclusivo del CU-PRE-18
    Y una actividad de mantenimiento registrada en el CU-PRE-18
    Cuando el Técnico URP del CU-PRE-18 elimina la actividad de mantenimiento
    Entonces el CU-PRE-18 ya no devuelve la actividad eliminada
