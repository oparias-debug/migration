# language: es
@CU-PRE-20 @rol:TECNICO_PRE
Característica: Visualizar la información a precios ajustados

  Como Usuarios Internos
  Quiero visualizar la fila de Flujo de Beneficios a precios ajustados y los campos de valor de rescate ajustado

  Escenario: Visualizar el Flujo de Beneficios a precios ajustados (camino feliz)
    Dado que el actor pertenece al grupo "Usuarios Internos" - flujo-beneficios
    Cuando accede a la pantalla "Beneficios del Proyecto" - flujo-beneficios
    Entonces el sistema muestra la fila "Flujo de beneficios (precios ajustados)" (FA1.1.4, FA2.1.4)

  Escenario: Visualizar el FC y el Valor de rescate ajustado
    Dado que el actor pertenece al grupo "Usuarios Internos" - flujo-beneficios
    Entonces el sistema muestra los campos "FC" y "Valor de rescate ajustado" en la tabla "Beneficios del proyecto" (RN09)
