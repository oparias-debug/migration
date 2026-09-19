# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Consultar los catálogos hijos de un catálogo padre

  Como Administrador del Sistema
  Quiero consultar los catálogos hijos de un catálogo dado
  Para conocer la jerarquía de catálogos

  Escenario: Obtener la lista de catálogos hijos de un catálogo padre
    Dado un catálogo que tiene catálogos hijos
    Cuando lo consulto como padre
    Entonces el sistema retorna la lista de código y nombre de cada catálogo hijo conforme a la Regla 15

  Escenario: Obtener una lista vacía cuando el catálogo no tiene hijos
    Dado un catálogo sin catálogos hijos
    Cuando lo consulto como padre
    Entonces el sistema retorna una lista vacía

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Ver historias-CU-ADM-01.md.
