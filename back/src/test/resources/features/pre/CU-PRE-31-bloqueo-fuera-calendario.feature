# language: es
@CU-PRE-31 @rol:SISTEMA
Característica: Bloquear el ingreso de información fuera del Calendario de Eventos del PAP

  Como Sistema
  Quiero bloquear el ingreso y ajuste de información fuera de las fechas del Calendario de Eventos del PAP

  Escenario: Bloqueo fuera de la fecha establecida (camino feliz, RN-A.b)
    Dado que la fecha actual está fuera del período establecido en el Calendario de Eventos del PAP (CU-ADM-04)
    Cuando el Técnico URP intenta ingresar o ajustar información metas-fisicas
    Entonces el sistema muestra el mensaje "Periodo de ingreso de información ha finalizado" (Anexo A.3)
    Y todas las acciones de la tabla "Programación Física Cuatrimestral del PAP" permanecen deshabilitadas

  Escenario: Bloqueo de la respuesta de la Institución fuera de la fecha establecida (RN-A.b, SF-3 pasos 3-4)
    Dado que la fecha actual está fuera del período establecido en el Calendario de Eventos del PAP (CU-ADM-04)
    Cuando el Técnico URP intenta registrar o enviar la "Respuesta Institución" fuera del período metas-fisicas
    Entonces el sistema rechaza ambas acciones con el mensaje "Periodo de ingreso de información ha finalizado" (Anexo A.3)

  Escenario: Bloqueo del botón Revisión Finalizada fuera de la fecha establecida (RN-E, SF-6)
    Dado que la fecha actual está fuera del período establecido en el Calendario de Eventos del PAP (CU-ADM-04)
    Cuando el Técnico PRE intenta finalizar la revisión fuera del período metas-fisicas
    Entonces el sistema rechaza la finalización con el mensaje "Periodo de ingreso de información ha finalizado" (Anexo A.3)

  Escenario: Con modificaciones habilitadas fuera de plazo se permiten la respuesta y la revisión finalizada (SF-8/SF-9)
    Dado que la fecha actual está fuera del período establecido en el Calendario de Eventos del PAP (CU-ADM-04)
    Y el Administrador del Sistema habilitó modificaciones fuera de plazo para la Unidad Ejecutora metas-fisicas
    Cuando el Técnico URP envía la "Respuesta Institución" y el Técnico PRE finaliza la revisión metas-fisicas
    Entonces el sistema permite ambas acciones y el estado queda en "PAP Revisado"