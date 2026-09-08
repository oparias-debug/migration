# language: es
@UC-PRE-03 @rol:TECNICO_URP @rol:TECNICO_PRE @rol:COORDINADOR_PRE @rol:VIABILIZADOR @rol:USUARIOS_INTERNOS_EXTERNOS @rol:SISTEMA
Característica: Navegar al Registro de Etapas desde el CUP de un proyecto

  Como Técnico URP, Técnico PRE, Coordinador PRE, Viabilizador o Usuarios Internos/Externos
  Quiero hacer clic en el CUP de un proyecto para ir a la pantalla de Registro de Etapas
  Para continuar el flujo del proyecto en CU-PRE-3.5 "Selección y registro de etapas"

  Antecedentes:
    Dado que el actor visualiza el listado de la pantalla "Captura de Proyectos" (Anexo A.1)

  Escenario: Clic en el CUP de un proyecto (camino feliz, FA-01)
    Cuando el actor hace clic en el CUP de un proyecto del listado
    Entonces el sistema muestra la pantalla "Registro de Etapas" del caso de uso CU-PRE-3.5 "Selección y registro de etapas"

  # ⚠️ Escenario pendiente: el documento no especifica una "Condición" ni un "Resultado" explícitos para el Flujo Alternativo FA-01 más allá del paso de navegación en sí; no se agrega ningún comportamiento adicional no documentado (ver Datos Pendientes de Definir del CU original, ítem 6).