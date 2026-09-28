# language: es
@CU-PRE-32 @rol:SISTEMA
Característica: Bloquear el ingreso de información fuera del Calendario de Eventos del PAP

  Como Sistema
  Quiero bloquear el ingreso y ajuste de información fuera de las fechas del Calendario de Eventos del PAP

  Escenario: Bloqueo fuera de la fecha establecida (camino feliz, RN-A.b)
    Dado que la fecha actual está fuera del período establecido en el Calendario de Eventos del PAP (CU-ADM-04) avance-financiero
    Cuando el Técnico URP intenta ingresar o ajustar información avance-financiero
    Entonces el sistema muestra el mensaje del Anexo A.3
    Y todas las acciones de las tablas de los Anexos A.1 y A.5 quedan deshabilitadas, salvo "Generar reporte"