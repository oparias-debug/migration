# language: es
@CU-PRE-31 @rol:TECNICO_URP
Característica: Registrar la programación de metas físicas de un nuevo estudio y enviarla a revisión

  Como Técnico URP
  Quiero registrar la programación de metas físicas de un nuevo estudio y enviar la programación PAP a revisión de la DGICP

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Programación por Meta Física Cuatrimestral del PAP" (Anexo A.1) nuevo-estudio

  Escenario: Registrar y guardar la programación de metas de un nuevo estudio (camino feliz, SF-2)
    Cuando el Técnico URP selecciona el código de un proyecto nuevo cargado desde CU-PRE-30
    Entonces el sistema muestra el Anexo A.4 con los campos vacíos, salvo "CUP"
    Cuando el Técnico URP registra la programación cuatrimestral en los campos habilitados
    Y hace clic en el botón "Guardar"
    Entonces el sistema valida los datos según RN-B literal a.2
    Y traslada automáticamente el registro a la tabla del Anexo A.1

  Escenario: Enviar la programación PAP a revisión DGICP (camino feliz)
    Dado que la programación de metas físicas ya fue registrada y guardada
    Cuando el Técnico URP hace clic en el botón "Enviar a revisión DGICP"
    Entonces el sistema notifica al Técnico PRE que la programación PAP, tanto financiera como de metas físicas, fue enviada para su revisión

  Escenario: El botón Enviar a revisión DGICP es visible y se habilita únicamente para el Técnico URP
    Entonces el botón "ENVIAR A REVISIÓN DGICP" es visible solo para el Técnico URP
    Y se habilita únicamente durante el período de ingreso de información o cuando se presenten modificaciones al PAP (RN-E)

  Escenario: El monto programado no puede superar el 100%
    Dado que el "Total Programado Año" supera el 100%
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "Monto Programado supera el 100%" (Anexo A.2)
    Y se mantiene en la pantalla del Anexo A.4 nuevo-estudio

  Escenario: El campo Entregable es obligatorio (Anexo B.1)
    Dado que el Técnico URP no seleccionó el "Entregable" de la etapa
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema indica que el campo "entregable" de la etapa es obligatorio
    Y se mantiene en la pantalla del Anexo A.4 nuevo-estudio

  Escenario: Debe registrar la programación en al menos un cuatrimestre (Anexo B.1)
    Dado que el Técnico URP no registró porcentaje en ningún cuatrimestre de la etapa
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema indica que el campo "programacionCuatrimestral" de la etapa es obligatorio
    Y se mantiene en la pantalla del Anexo A.4 nuevo-estudio

  Escenario: Cada cuatrimestre debe estar entre 0.00% y 100% (Anexo B.1)
    Dado que el Técnico URP registró un cuatrimestre con un porcentaje mayor a 100%
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema indica que el campo "montoCuatrimestre1" de la etapa está fuera del rango permitido
    Y se mantiene en la pantalla del Anexo A.4 nuevo-estudio

  Escenario: Salir sin guardar
    Cuando el Técnico URP hace clic en el botón "Salir"
    Entonces el sistema regresa a la pantalla del Anexo A.1 sin guardar los cambios metas-fisicas-nuevo-estudio