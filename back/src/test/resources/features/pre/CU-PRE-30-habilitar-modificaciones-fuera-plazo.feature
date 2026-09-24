# language: es
@CU-PRE-30 @rol:ADMINISTRADOR
Característica: Habilitar modificaciones al PAP fuera del período de elaboración

  Como Administrador del Sistema
  Quiero habilitar modificaciones al PAP fuera del período de elaboración, a solicitud del Coordinador PRE

  Escenario: Habilitar el sistema para agregar un nuevo estudio fuera de plazo (camino feliz, SF-4)
    Dado que el Coordinador PRE solicitó la modificación del PAP con nota de solicitud remitida por la Institución
    Cuando el Administrador del Sistema habilita el sistema
    Entonces el Técnico URP puede agregar un nuevo estudio y registrar su programación, siguiendo los mismos pasos de SF-2

  Escenario: Habilitar el sistema para modificar un estudio existente fuera de plazo (camino feliz, SF-5)
    Dado que el Coordinador PRE solicitó la modificación del PAP con nota de solicitud remitida por la Institución
    Cuando el Administrador del Sistema habilita el sistema
    Y el Técnico URP hace clic en el CUP del estudio a modificar
    Entonces el sistema muestra el Anexo A.2 para registrar los ajustes correspondientes
    Y aplica las mismas validaciones de suma cuatrimestral y cálculo de porcentajes que en SF-2

  # ⚠️ Escenario pendiente: no se especifica ningún flujo o pantalla para la "solicitud" que el Coordinador PRE hace al Administrador del Sistema (solo se menciona la existencia de una "nota de solicitud de modificación del PAP remitida por la Institución"); no se modela ese flujo previo.