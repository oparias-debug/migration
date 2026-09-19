# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Editar un calendario existente

  Como ADMINISTRADOR o ADMINISTRADOR_CALENDARIO
  Quiero editar los datos generales (nombre, descripción, fecha de inicio, fecha de fin) de un calendario ya existente
  Para mantener actualizada la definición del calendario

  Escenario: Editar el nombre y la descripción de un calendario existente
    Dado un calendario existente
    Cuando el actor edita su nombre y su descripción con datos válidos
    Entonces el calendario queda actualizado con el nuevo nombre y la nueva descripción

  Escenario: Rechazar la edición si la nueva fecha de inicio es posterior a la nueva fecha de fin
    Dado un calendario existente
    Cuando el actor edita el calendario indicando una fecha de inicio posterior a la fecha de fin
    Entonces el sistema rechaza la operación

  Escenario: Rechazar la edición de un calendario a un actor sin el rol adecuado
    Dado que el actor autenticado no tiene el rol ADMINISTRADOR ni ADMINISTRADOR_CALENDARIO
    Cuando el actor intenta editar un calendario existente
    Entonces el sistema rechaza la operación por falta de permisos

  Escenario: Rechazar a un ADMINISTRADOR_CALENDARIO editar un calendario que no es de su responsabilidad
    Dado un calendario cuyo administrador responsable es otro usuario
    Cuando un actor con rol ADMINISTRADOR_CALENDARIO, distinto del administrador responsable, intenta editar ese calendario
    Entonces el sistema rechaza la operación por falta de permisos

  # ⚠️ Escenario pendiente: ni el CU ni el modelo de dominio especifican qué ocurre si se edita el
  # rango de fechas de un calendario de forma que deje fuera de rango algún período ya definido
  # (RN10), ni si el código del calendario (RN14) puede modificarse una vez creado. No implementar
  # ninguna de estas dos validaciones hasta que se resuelva con el Analista Funcional / Gestor de
  # Requisitos.
