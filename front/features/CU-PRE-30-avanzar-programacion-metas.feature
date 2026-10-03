# language: es
@wip @CU-PRE-30 @rol:TECNICO_URP
# SF-6 es pura navegación de UI hacia CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la
# Preinversión", que no está implementado en el repositorio (ver CU-PRE-30.openapi.yaml: no expone
# ningún endpoint para este subflujo, a propósito). Sin backend que ejercitar, se deja pendiente.
Característica: Avanzar a Programación de Metas

  Como Técnico URP
  Quiero avanzar a la Programación Cuatrimestral de Metas Físicas de la Preinversión

  Escenario: Avanzar a Programación de Metas (camino feliz, SF-6)
    Dado que el Técnico URP se encuentra en la pantalla "Programación Financiera Cuatrimestral del PAP"
    Cuando hace clic en el botón "Siguiente"
    Entonces el sistema muestra el Anexo A.1 de CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión"