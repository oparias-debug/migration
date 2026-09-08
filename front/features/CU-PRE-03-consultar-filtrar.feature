# language: es
@UC-PRE-03 @rol:TECNICO_URP @rol:TECNICO_PRE @rol:COORDINADOR_PRE @rol:VIABILIZADOR @rol:USUARIOS_INTERNOS_EXTERNOS @rol:SISTEMA
Característica: Consultar, buscar y filtrar el listado de proyectos con CUP

  Como Técnico URP, Técnico PRE, Coordinador PRE, Viabilizador o Usuarios Internos/Externos
  Quiero consultar, buscar y filtrar el listado de proyectos con CUP en la pantalla "Captura de Proyectos"

  Antecedentes:
    Dado que el Sistema ha registrado en "Captura de Proyectos" los proyectos con CUP

  Esquema del escenario: Consultar el listado de proyectos según el alcance de visibilidad del actor
    Cuando "<actor>" accede a la pantalla "Captura de Proyectos" (Anexo A.1)
    Entonces el sistema muestra el listado de proyectos con CUP con el siguiente alcance: "<alcance>"

    Ejemplos:
      | actor                        | alcance                                                    |
      | Técnico URP                  | únicamente los proyectos según sus credenciales (RN01)     |
      | Viabilizador                 | únicamente los proyectos según sus credenciales (RN01)     |
      | Usuarios Internos/Externos   | únicamente los proyectos según sus credenciales (RN01)     |
      | Técnico PRE                  | los proyectos de todas las Unidades Ejecutoras (RN02)      |
      | Coordinador PRE              | los proyectos de todas las Unidades Ejecutoras (RN02)      |

  Escenario: Buscar proyectos por CUP, Nombre o Unidad Ejecutora
    Cuando el actor ingresa un término en el campo "Buscador" (placeholder "CUP; NOMBRE; UNIDAD EJECUTORA")
    Y hace clic en el botón "BUSCAR"
    Entonces el sistema muestra los proyectos cuyo CUP, Nombre o Unidad Ejecutora coincidan con el término ingresado

  Esquema del escenario: Filtrar el listado por columna
    Cuando el actor hace clic en el ícono de filtro (▼) de la columna "<columna>"
    Y aplica un valor de filtro
    Entonces el sistema muestra únicamente los proyectos que coinciden con el filtro aplicado en la columna "<columna>"

    Ejemplos:
      | columna                  |
      | CUP                      |
      | Nombre del proyecto      |
      | Iniciativa de inversión  |
      | Estado                   |
      | Unidad Ejecutora         |

  # ⚠️ Escenario pendiente: existe una discrepancia no resuelta entre el catálogo oficial de 13 estados definido en RN04 y los valores de ejemplo mostrados en el mockup del Anexo A.1 ("En análisis DGICP", "Viabilizado"), que no coinciden textualmente con ningún estado del catálogo. No se genera ningún escenario que use esos valores del mockup como estado válido de filtro (ver Datos Pendientes de Definir del CU original, ítem 3).