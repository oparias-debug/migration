# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Actualizar los descriptores de un catálogo

  Como Administrador del Sistema
  Quiero modificar el nombre, el catálogo padre, el estado activo o la vigencia de un catálogo
  Para mantener actualizada su definición

  Esquema del escenario: Guardar la actualización de un descriptor permitido del catálogo
    Dado un catálogo existente
    Cuando actualizo su descriptor "<descriptor>"
    Entonces el cambio se guarda correctamente

    Ejemplos:
      | descriptor |
      | name       |
      | parent     |
      | active     |
      | valid      |

  Escenario: Rechazar la actualización del código de un catálogo
    Dado un catálogo existente
    Cuando intento modificar su código
    Entonces el sistema rechaza la operación conforme a la Regla 17

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Ver historias-CU-ADM-01.md.
