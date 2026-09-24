# language: es
@CU-PRE-31 @rol:ADMINISTRADOR
Característica: Habilitar modificaciones a la programación de metas fuera del período de elaboración

  Como Administrador del Sistema
  Quiero habilitar modificaciones a la programación de metas físicas fuera del período de elaboración del PAP, a solicitud del Coordinador PRE

  Antecedentes:
    Dado que ya se registró previamente la modificación correspondiente en la Programación Financiera (CU-PRE-30)

  Escenario: Habilitar el sistema para agregar un nuevo estudio fuera de plazo (camino feliz, SF-8)
    Dado que el Coordinador PRE solicitó la modificación del PAP con nota de solicitud remitida por la Institución metas-fisicas
    Cuando el Administrador del Sistema habilita el sistema metas-fisicas
    Entonces el Técnico URP puede registrar la programación de metas de un nuevo estudio, siguiendo los mismos pasos de SF-2

  Escenario: Habilitar el sistema para modificar un estudio existente fuera de plazo (camino feliz, SF-9)
    Dado que el Coordinador PRE solicitó la modificación del PAP con nota de solicitud remitida por la Institución metas-fisicas
    Cuando el Administrador del Sistema habilita el sistema metas-fisicas
    Y el Técnico URP hace clic en el CUP del estudio a modificar metas-fisicas
    Entonces el sistema muestra el Anexo A.4 para registrar los ajustes correspondientes

  # ⚠️ Escenario pendiente: el paso 6 de SF-9 remite genéricamente a "RN B literal a", sin precisar si corresponde a a.1 (estudios de arrastre) o a.2 (estudios nuevos), ya que este subflujo aplica a un "estudio existente" que podría ser cualquiera de los dos. No se decide cuál validación aplica en este escenario.