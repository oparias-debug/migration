# language: es
@CU-PRE-29
Característica: Consultar el Banco de Proyectos

  Como cualquier usuario del Ministerio de Hacienda
  Quiero consultar los proyectos que ya tienen CUP y su situación
  Para saber en qué etapa y con qué prioridad está cada uno (RQ-C-03)

  Antecedentes:
    Dado que el actor se encuentra en la pantalla "Banco de Proyectos" (Anexo A.1)

  Escenario: Consultar el listado (camino feliz)
    Entonces el sistema muestra cada proyecto con su CUP, nombre, etapa, inversión estimada, estado y prioridad
    Y muestra solo los proyectos viabilizados, priorizados, con Opinión Técnica, en ejecución o finalizados

  Escenario: Buscar por CUP o por nombre (RN02)
    Cuando el actor escribe un CUP o parte del nombre de un proyecto
    Y hace clic en "Buscar"
    Entonces el sistema muestra únicamente los proyectos que coinciden

  Escenario: Entrar a la ficha de un proyecto
    Cuando el actor hace clic en el nombre de un proyecto
    Entonces el sistema abre la ficha de ese proyecto

  Escenario: El Técnico URP solo ve su institución (RN01)
    Dado que el actor es Técnico URP
    Entonces el listado se limita a los proyectos de su unidad ejecutora

  Escenario: Un fallo del servidor no se confunde con un banco vacío
    Dado que la consulta falla
    Entonces el sistema muestra el error y no un listado vacío
