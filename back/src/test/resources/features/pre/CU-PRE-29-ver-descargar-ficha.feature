# language: es
# @wip: todo este feature depende de la Ficha del proyecto (CU-PRE-3.6), aún no implementada. El
# escenario de RN04 (sin botones de edición) ya se cubre en CU-PRE-29-consultar-buscar.feature.
@wip @CU-PRE-29 @rol:TECNICO_URP @rol:TECNICO_PRE @rol:COORDINADOR_PRE @rol:VIABILIZADOR @rol:USUARIOS_INTERNOS
Característica: Ver y descargar la Ficha del proyecto

  Como Técnico URP, Técnico PRE, Coordinador PRE, Viabilizador o Usuarios Internos
  Quiero ver y descargar la Ficha de un proyecto del Banco de Proyectos

  Antecedentes:
    Dado que el actor se encuentra en la pantalla "Banco de Proyectos"

  Escenario: Ver la Ficha del proyecto (camino feliz, FA-01)
    Cuando el actor hace clic en el CUP de un proyecto del listado
    Entonces el sistema muestra la Ficha del proyecto (CU-PRE-3.6)

  Esquema del escenario: Descargar la Ficha del proyecto en el formato seleccionado
    Dado que el actor está visualizando la Ficha del proyecto
    Cuando descarga la ficha en formato "<formato>"
    Entonces el sistema genera la descarga correspondiente

    Ejemplos:
      | formato |
      | PDF     |
      | Excel   |

  Escenario: Ningún actor cuenta con botones de ajuste sobre los proyectos listados
    Entonces los proyectos del "Banco de Proyectos" solo muestran botones de consulta (ver Ficha, descargar), sin botones de edición (RN04)

  # ⚠️ Escenario pendiente: RN04 también exige un botón de "descarga de bitácora transaccional", que no aparece en el mockup del Anexo A.1 ni se describe en el Flujo Alternativo FA-01 (que solo menciona PDF/Excel de la Ficha). No se genera un escenario para esa descarga, por falta de mecanismo, formato o botón documentados (ver Datos Pendientes de Definir del CU original).