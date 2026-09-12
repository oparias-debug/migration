# language: es
@CU-PRE-3.5 @rol:TECNICO_URP
Característica: Registrar la Ficha de proyectos de emergencia

  Como Técnico URP
  Quiero registrar la Ficha de proyectos de emergencia de un proyecto categorizado como emergencia

  Antecedentes:
    Dado que el Técnico URP ingresa a "Captura de Proyectos" (UC-PRE-03) y da clic sobre el CUP de un proyecto de emergencia
    Y el sistema muestra la pantalla del Anexo A.1 con únicamente la etapa "Perfil" disponible

  Escenario: Registrar la Ficha de proyectos de emergencia (camino feliz)
    Cuando el Técnico URP hace clic en el botón "Perfil"
    Entonces el sistema muestra el formulario del Anexo A.4 con todos los campos habilitados
    Cuando el Técnico URP diligencia todos los campos obligatorios del formulario
    Y hace clic en el botón "Guardar"
    Entonces el sistema valida que todos los campos estén diligenciados
    Y guarda la información
    Y remite el proyecto a "Viabilidad" (CU-PRE-24)

  Esquema del escenario: Intentar guardar el formulario con un campo obligatorio sin diligenciar
    Cuando el Técnico URP hace clic en "Guardar" sin haber completado el campo "<campo>"
    Entonces el sistema muestra el mensaje "Existen campos sin diligenciar"
    Y marca en rojo el contorno del campo "<campo>"

    Ejemplos:
      | campo                        |
      | Planteamiento del problema   |
      | Producto                     |
      | Departamento                 |
      | Distrito                     |
      | Población objetivo           |

  Escenario: Solo la etapa "Perfil" está disponible para proyectos de emergencia
    Entonces el sistema muestra únicamente la etapa "Perfil" en la sección "Registro de Etapas" para un proyecto de emergencia (RN09)