# language: es
@CU-PRE-01 @rol:ADMINISTRADOR_DEL_SISTEMA
Característica: Cambiar la Unidad Ejecutora de un proyecto

  Como Administrador del Sistema
  Quiero cambiar la Unidad Ejecutora de un proyecto en cualquier etapa

  Escenario: Cambiar la Unidad Ejecutora de un proyecto (camino feliz)
    Dado un proyecto registrado en cualquier etapa
    Cuando el Administrador del Sistema cambia la Unidad Ejecutora del proyecto
    Entonces el proyecto queda asociado a la nueva Unidad Ejecutora

  # ⚠️ Escenario pendiente: el CU no especifica el flujo o pantalla concretos de esta acción, ni los permisos completos de este actor (ver Datos Pendientes de Definir del CU original: "¿Quién es el administrador del sistema en la DGICP y cuáles son sus permisos completos?"). No se inventa un flujo de UI para ello.