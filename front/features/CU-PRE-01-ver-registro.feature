# language: es
@CU-PRE-01 @rol:TECNICO_URP @rol:TECNICO_PRE @rol:ADMINISTRADOR_DEL_SISTEMA @rol:USUARIOS_INTERNOS_EXTERNOS
Característica: Consultar el detalle de un proyecto en modo solo lectura

  Como Técnico URP, Técnico PRE, Administrador del Sistema o Usuarios Internos/Externos
  Quiero consultar el detalle de un proyecto sin poder editarlo

  Escenario: Consultar un proyecto del listado (camino feliz)
    Dado que el actor se encuentra en la pantalla "Registro de Proyecto"
    Cuando el actor hace clic en el nombre de un proyecto del listado
    Entonces el sistema muestra la pantalla "Nuevo registro" sin autorización de editar información
    Y el actor visualiza la pantalla sin poder modificar ningún campo