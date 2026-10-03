# language: es
@CU-PRE-23
Característica: Indicadores del proyecto

  Escenario: Rechazar una programación cuya suma no coincide con la meta global
    Dado que existe un producto de CU-PRE-11 para indicadores -cu-pre-23
    Cuando el Técnico URP registra una meta global de 70 con períodos 25, 30 y 10 -cu-pre-23
    Entonces el sistema rechaza la suma distinta de la meta global -cu-pre-23

  Escenario: Materializar el indicador principal de cada producto al guardar
    Dado que existe un producto de CU-PRE-11 y un indicador de resultado -cu-pre-23
    Cuando el Técnico URP confirma los indicadores del proyecto -cu-pre-23
    Entonces el sistema crea el indicador principal del catálogo C.1 -cu-pre-23

  Escenario: Bloquear la confirmación con productos incompletos de CU-PRE-11
    Dado que existe un producto de CU-PRE-11 sin cantidad -cu-pre-23
    Cuando el Técnico URP confirma los indicadores del proyecto -cu-pre-23
    Entonces el sistema exige completar código y cantidad del producto -cu-pre-23
