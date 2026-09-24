@CU-PRE-29 @rol:TECNICO_URP @rol:TECNICO_PRE @rol:COORDINADOR_PRE @rol:VIABILIZADOR @rol:USUARIOS_INTERNOS
Feature: Banco de Proyectos (CU-PRE-29)

  # HU-PRE-29-01
  # Como actor autorizado (Técnico URP, Técnico PRE, Coordinador PRE, Viabilizador o Usuario Interno)
  # Quiero ver el listado del Banco de Proyectos con todos los proyectos registrados en el sistema y su estado

  Scenario: El Banco de Proyectos muestra el listado completo de proyectos con su estado
    Given existen proyectos registrados en el sistema en distintas etapas de gestión (viabilizados, priorizados, con Opinión Técnica, en ejecución o finalizados)
    When un actor autorizado ingresa a la pantalla "Banco de Proyectos"
    Then el Sistema muestra todos los proyectos registrados en el sistema
    And el Sistema muestra el estado de cada proyecto

  Scenario Outline: Los actores visualizan el Banco de Proyectos según sus credenciales
    Given el actor autenticado es "<actor>"
    When el actor ingresa a la pantalla "Banco de Proyectos"
    Then el Sistema muestra el listado de proyectos según las credenciales de ese actor

    Examples:
      | actor              |
      | Técnico URP         |
      | Técnico PRE         |
      | Coordinador PRE     |
      | Viabilizador        |
      | Usuarios Internos   |

  # HU-PRE-29-02
  # Como Técnico URP o Técnico PRE
  # Quiero filtrar el listado del Banco de Proyectos por Unidad Ejecutora, según corresponda a mi rol

  Scenario: El selector de Unidad Ejecutora está bloqueado para el Técnico URP
    Given el actor autenticado es "Técnico URP"
    When el actor ingresa a la pantalla "Banco de Proyectos"
    Then el selector "Unidad Ejecutora" se muestra bloqueado en la Unidad Ejecutora propia del Técnico URP
    And el listado de proyectos corresponde únicamente a esa Unidad Ejecutora

  Scenario: El selector de Unidad Ejecutora está habilitado para todas las unidades para el Técnico PRE
    Given el actor autenticado es "Técnico PRE"
    When el actor ingresa a la pantalla "Banco de Proyectos"
    Then el selector "Unidad Ejecutora" se muestra habilitado
    And el Técnico PRE puede seleccionar y consultar cualquier Unidad Ejecutora disponible en el SIIP

  # HU-PRE-29-03
  # Como actor autorizado
  # Quiero buscar proyectos por Código o por Nombre en el campo de búsqueda

  Scenario Outline: Buscar proyectos por Código o por Nombre
    Given existe un proyecto registrado con "<criterio>" igual a "<valor>"
    When el actor busca proyectos usando "<criterio>" igual a "<valor>"
    Then el Sistema muestra el proyecto correspondiente en el listado del Banco de Proyectos

    Examples:
      | criterio | valor                          |
      | Código   | 9010                            |
      | Nombre   | Ampliación carretera CA02E      |

  # ⚠️ Escenario pendiente: RN02 indica que también podrá buscarse "por Institución", pero el mockup del
  # Anexo A.1 solo muestra un selector "UNIDAD EJECUTORA", sin un campo o selector adicional etiquetado
  # "Institución", y el negocio confirmó (RQ-T-01, ronda 3) que "Institución Ejecutora" y "Unidad
  # Ejecutora" no son sinónimos. No se puede determinar si RN02 debió decir "Unidad Ejecutora" o si falta
  # un campo adicional para "Institución". No implementar la búsqueda "por Institución" hasta que se
  # resuelva con el negocio.

  # HU-PRE-29-04
  # Como actor autorizado
  # Quiero dar clic en el CUP de un proyecto para ver su Ficha del proyecto y descargarla en PDF o en Excel

  # @wip: la Ficha del proyecto y su descarga PDF/Excel pertenecen a CU-PRE-3.6, que aún no está
  # implementado (sin endpoint ni servicio). Quitar el tag cuando exista ese CU.
  @wip
  Scenario: Ver la Ficha del proyecto al hacer clic en el CUP
    Given un proyecto listado en el "Banco de Proyectos"
    When el actor da clic en el CUP de ese proyecto
    Then el Sistema muestra la Ficha del proyecto (CU-PRE-3.6)

  @wip
  Scenario Outline: Descargar la Ficha del proyecto en PDF o en Excel
    Given el actor está visualizando la Ficha del proyecto
    When el actor descarga la ficha en formato "<formato>"
    Then el Sistema genera el archivo de la Ficha del proyecto en formato "<formato>"

    Examples:
      | formato |
      | PDF      |
      | Excel    |

  Scenario: Los proyectos del Banco de Proyectos no tienen botones de ajuste
    Given un actor autorizado está consultando el Banco de Proyectos
    When el actor visualiza los controles disponibles para un proyecto del listado
    Then el Sistema solo muestra botones de ingreso a consulta y de descarga de ficha
    And el Sistema no muestra botones que permitan ajustar o editar la información del proyecto

  # ⚠️ Escenario pendiente: RN04 también menciona un botón de "descarga de bitácora transaccional", que
  # no se describe en el Flujo Alternativo FA-01 (el cual solo cubre la descarga de la Ficha en PDF o
  # Excel) ni se representa en el mockup del Anexo A.1. No implementar esta descarga hasta que el
  # documento especifique su mecanismo, formato y contenido.

  # ⚠️ Escenario pendiente: el Anexo B.1 documenta un campo "Total" que totalizaría los montos de
  # inversión de los proyectos listados, pero no aparece representado en el mockup del Anexo A.1 ni se
  # indica en qué parte de la pantalla se ubicaría. No implementar hasta que se resuelva su ubicación.
