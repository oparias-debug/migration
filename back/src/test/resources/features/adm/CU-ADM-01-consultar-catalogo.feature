# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Consultar un catálogo por código

  Como Administrador del Sistema
  Quiero recuperar un catálogo indicando su código
  Para revisar o utilizar su definición

  Escenario: Consultar un catálogo existente por su código
    Dado un código de catálogo existente en el catalogMaster
    Cuando consulto el catálogo por ese código
    Entonces el sistema retorna su definición completa, incluyendo nombre, padre, estado, vigencia y campos

  Escenario: Rechazar la consulta de un catálogo con un código inexistente
    Dado un código de catálogo que no existe en el catalogMaster
    Cuando consulto el catálogo por ese código
    Entonces el sistema reporta un error conforme a la Regla 21

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Ver historias-CU-ADM-01.md.
