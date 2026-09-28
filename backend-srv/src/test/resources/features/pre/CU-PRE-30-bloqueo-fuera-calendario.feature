# language: es
@CU-PRE-30 @rol:SISTEMA
Característica: Bloquear el ingreso de información fuera del Calendario de Eventos del PAP

  Como Sistema
  Quiero bloquear el ingreso y ajuste de información fuera de las fechas del Calendario de Eventos del PAP

  Escenario: Bloqueo fuera de la fecha establecida (camino feliz, RN-A.b)
    Dado que la fecha actual está fuera del período establecido en el Calendario de Eventos del PAP (CU-ADM XX "Gestión de Eventos de Calendario")
    Cuando el Técnico URP intenta ingresar o ajustar información
    Entonces el sistema muestra el mensaje "Periodo de ingreso de información ha finalizado" (Anexo A.4)
    Y todas las acciones de la tabla "Programación Financiera Cuatrimestral del PAP" permanecen deshabilitadas

  # [SUPUESTO] RN-A.b no define qué ocurre si el Administrador aún no configuró el evento del
  # Calendario para el año; se documenta el comportamiento vigente: se asume el período abierto.
  Escenario: Sin evento de calendario configurado para el año se asume el período abierto
    Dado que no existe un evento de "Programación PAP" en el Calendario de Eventos del PAP para el año a programar
    Cuando el Técnico URP agrega un estudio para ese año
    Entonces el sistema permite el ingreso de información, asumiendo el período abierto (RN-A.b)