# language: es
@CU-PRE-30 @rol:TECNICO_URP
Característica: Registrar la programación cuatrimestral de un estudio de arrastre

  Como Técnico URP
  Quiero registrar la programación cuatrimestral de un estudio de arrastre

  Antecedentes:
    Dado que el Técnico URP selecciona su Unidad Ejecutora y el Año en la pantalla "Programación Financiera Cuatrimestral del PAP" (Anexo A.1)
    Y el sistema muestra el listado de estudios de arrastre con ejecución pendiente

  Escenario: Registrar y guardar la programación de un estudio de arrastre (camino feliz, SF-1)
    Cuando el Técnico URP hace clic en el código de un proyecto
    Entonces el sistema muestra la pantalla "Programación Financiera por Etapa de Preinversión" (Anexo A.2)
    Y los campos "Fuente de Financiamiento", "Fuente de Recursos" y "Convenio" se muestran bloqueados con la información del ejercicio anterior
    Cuando el Técnico URP registra los montos en "I Cuatrimestre", "II Cuatrimestre" y "III Cuatrimestre"
    Y hace clic en el botón "Guardar"
    Entonces el sistema valida los datos según RN-B literal c.2
    Y guarda automáticamente el registro
    Y traslada los valores de cada cuatrimestre a la pantalla del Anexo A.1

  Escenario: Agregar otra fuente de financiamiento a un estudio de arrastre
    Cuando el Técnico URP hace clic en el botón "(+)"
    Entonces el sistema permite registrar los montos de cada cuatrimestre para esa fuente adicional

  Escenario: Salir sin guardar
    Cuando el Técnico URP hace clic en el botón "Salir"
    Entonces el sistema regresa a la pantalla del Anexo A.1 sin guardar los cambios

  Escenario: El monto programado no puede superar el monto pendiente de ejecutar
    Dado que la suma de los cuatrimestres registrados es mayor al monto pendiente de ejecutar (Costo de la etapa menos lo Ejecutado en años anteriores)
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "Monto Programado supera el costo de la etapa" (Anexo A.3)
    Y se mantiene en la pantalla del Anexo A.2

  Escenario: El remanente se registra en Años posteriores
    Dado que la suma de los cuatrimestres registrados es inferior al monto pendiente de ejecutar
    Entonces el sistema coloca el remanente en la columna "Años posteriores", calculado como Costo de la etapa menos Ejecutado años anteriores menos Total Año (RN-B.c)

  Escenario: Cálculo automático de porcentajes por cuatrimestre
    Dado que se registraron montos en los tres cuatrimestres
    Entonces el sistema calcula el "% Cuatrimestre" de cada uno como (Monto Programado del cuatrimestre / Total Programado Año) × 100 (RN-B.d)
    Y la suma de los tres porcentajes es igual al 100%