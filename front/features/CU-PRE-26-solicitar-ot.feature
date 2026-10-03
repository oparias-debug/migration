# language: es
@CU-PRE-26 @rol:TECNICO_URP @rol:VIABILIZADOR @rol:COORDINADOR_PRE @rol:SISTEMA
Característica: Solicitar Opinión Técnica

  Como Técnico URP
  Quiero solicitar a la DGICP la Opinión Técnica de mi proyecto

  Escenario: El botón "Solicitar OT" permanece inactivo sin la Nota de solicitud de OT
    Dado que el usuario autenticado tiene el rol "Técnico URP"
    Y está en la pantalla "Opinión Técnica" de un proyecto
    Y no se ha anexado ningún archivo en el campo "Nota de solicitud de OT"
    Entonces el botón "Solicitar OT" se muestra inactivo

  Escenario: El botón "Solicitar OT" se activa al anexar la Nota de solicitud de OT
    Dado que el usuario autenticado tiene el rol "Técnico URP"
    Y está en la pantalla "Opinión Técnica" de un proyecto
    Cuando anexa la Nota de Solicitud OT en el campo "Nota de solicitud de OT"
    Entonces el botón "Solicitar OT" se activa

  Esquema del escenario: El botón "Solicitar OT" es visible para Técnico URP y Viabilizador
    Dado que el usuario autenticado tiene el rol "<rol>"
    Cuando ingresa a la pantalla "Opinión Técnica" de un proyecto
    Entonces el botón "Solicitar OT" es visible

    Ejemplos:
      | rol          |
      | Técnico URP  |
      | Viabilizador |

  Escenario: Solicitar la Opinión Técnica y notificar al Coordinador PRE
    Dado que el usuario autenticado tiene el rol "Técnico URP"
    Y en el proyecto se anexó la Nota de Solicitud OT en el campo "Nota de solicitud de OT"
    Cuando da clic en el botón "Solicitar OT"
    Entonces el campo "Fecha de solicitud" muestra la fecha del clic en formato DD/MM/AAAA
    Y el campo "Fecha de solicitud" no es editable
    Y el Sistema envía al Coordinador PRE el correo electrónico del Anexo A2 a

  Escenario: La opción "Actualización de OT" está desactivada cuando la OT se solicita por primera vez
    Dado que la Opinión Técnica del proyecto se está solicitando por primera vez
    Cuando se despliega el "Tipo de solicitud" del menú "OT" en "GESTIÓN"
    Entonces la opción "2. Actualización de OT" está desactivada

  # Sin escenario: envío automático a OT al emitir Viabilidad/Elegibilidad (FB paso 1) — contradice la solicitud manual de RN04. Ver historias-CU-PRE-26.md.
  # Sin escenario: notificación al Técnico PRE en la solicitud (RN07 a vs. Anexo A2 a). Ver historias-CU-PRE-26.md.
