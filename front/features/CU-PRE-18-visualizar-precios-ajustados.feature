# language: es
@CU-PRE-18 @rol:TECNICO_PRE @wip
Característica: Visualizar la tabla de presupuesto a precios ajustados

  Como Usuarios Internos
  Quiero visualizar la tabla de presupuesto de Operación y Mantenimiento a precios ajustados

  Escenario: Visualizar la tabla de precios ajustados (camino feliz, RN09)
    Dado que el actor pertenece al grupo "Usuarios Internos"
    Cuando accede a la pantalla "Presupuesto de Operación y Mantenimiento" - presupuesto-om
    Entonces el sistema muestra las columnas "Total (Precios Ajustados)" de las tablas "Costos de Operación" y "Costos de Mantenimiento"

  # ⚠️ Escenario pendiente: a diferencia de CU-PRE-17 (donde RQ-C-03 resolvió la definición de "Usuarios Internos"), este documento no aclara a qué rol corresponde exactamente este grupo (la tabla de Permisos lo marca explícitamente como "no especificado"). No se resuelve aquí.
