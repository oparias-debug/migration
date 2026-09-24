# language: es
@CU-PRE-31 @rol:TECNICO_URP @rol:TECNICO_PRE @rol:COORDINADOR_PRE
Característica: Programación por Meta Física Cuatrimestral del PAP

  Como Técnico URP
  Quiero programar qué porcentaje de la meta física alcanzará cada estudio en cada cuatrimestre
  Para que el PAP institucional quede completo y pueda revisarlo la DGICP

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Programación por Meta Física Cuatrimestral del PAP" (Anexo A.1)

  Escenario: Consultar la programación de metas del año (camino feliz)
    Entonces el sistema muestra una fila por estudio y etapa
    Y muestra el entregable, lo ejecutado en años anteriores, el total del año y los años posteriores en porcentaje

  Escenario: Programar las metas de un estudio
    Cuando el Técnico URP hace clic en el CUP de un estudio
    Entonces el sistema abre la programación de metas de ese estudio (Anexo A.2)
    Y las etapas son las de la Ruta de Preinversión del proyecto, sin agregar ni quitar filas
    Cuando escribe el porcentaje de cada cuatrimestre
    Y hace clic en "Guardar"
    Entonces el sistema guarda la programación
    Y muestra el total del año y los años posteriores que calcula el servidor

  Escenario: El entregable de un estudio de arrastre no se cambia (RN-B.a.1)
    Dado que la etapa ya tuvo programación de metas en un año anterior
    Entonces su entregable no admite un valor distinto

  Escenario: Enviar el PAP a revisión de la DGICP (SF-2, RN-E)
    Cuando el Técnico URP hace clic en "Enviar a revisión DGICP"
    Entonces el sistema envía a revisión la programación financiera y la de metas físicas juntas
    Y notifica al Técnico PRE

  Escenario: La DGICP observa la programación (SF-3)
    Dado que quien entra es Técnico PRE o Coordinador PRE
    Cuando escribe sus observaciones y hace clic en "Guardar"
    Y hace clic en "Enviar observaciones"
    Entonces el sistema registra las observaciones con su fecha
    Y habilita al Técnico URP el campo "Respuesta de la institución"

  Escenario: La institución responde las observaciones
    Dado que quien entra es Técnico URP
    Entonces no puede escribir las observaciones de la DGICP
    Cuando escribe su respuesta y hace clic en "Guardar"
    Y hace clic en "Enviar respuesta"
    Entonces el sistema registra la respuesta con su fecha

  Escenario: La DGICP finaliza la revisión (SF-6)
    Dado que quien entra es Técnico PRE o Coordinador PRE
    Cuando hace clic en "Finalizar revisión"
    Entonces el PAP queda en estado "PAP revisado"

  Escenario: Generar el reporte de metas físicas
    Cuando el actor hace clic en "Generar reporte"
    Entonces el sistema genera el reporte del año para la unidad ejecutora

  # ⚠️ Pendiente con el equipo de backend: el contrato no expone una consulta del recurso de
  # revisión —las observaciones y la respuesta solo vuelven como resultado de las propias
  # acciones—, así que al entrar a la pantalla no se puede mostrar lo ya escrito.
