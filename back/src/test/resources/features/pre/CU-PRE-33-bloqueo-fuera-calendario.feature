# language: es
@CU-PRE-33 @rol:SISTEMA
Característica: Bloquear el ingreso de información fuera del Calendario de Eventos del PAP

  Como Sistema
  Quiero bloquear el ingreso y ajuste de información fuera de las fechas del Calendario de Eventos del PAP

  Escenario: Bloqueo fuera de la fecha establecida (camino feliz, RN-A.a)
    Dado que la fecha actual está fuera del período establecido en el Calendario de Eventos del PAP (CU-ADM-04) avance-metas
    Cuando el Técnico URP intenta ingresar o ajustar información avance-metas
    Entonces el sistema muestra el mensaje del Anexo A.3 avance-metas
    Y todas las acciones de los Anexos A.1 y A.4 quedan deshabilitadas, salvo "Generar Reporte"