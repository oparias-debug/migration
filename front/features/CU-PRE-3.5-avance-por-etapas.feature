# language: es
@CU-PRE-3.5 @rol:TECNICO_URP
Característica: Avance por las etapas de la Ruta de Preinversión

  Como Técnico URP
  Quiero formular una etapa a la vez, en orden
  Para que el proyecto avance conforme cada etapa obtiene su opinión técnica

  Antecedentes:
    Dado que el proyecto tiene etapas registradas en la pantalla "Registro de Etapas"

  Escenario: Las etapas se muestran en el orden del proceso
    Entonces el sistema lista las etapas en este orden: Perfil, Prefactibilidad, Factibilidad, Diseño, Ejecución
    Y las ordena así aunque el servicio las devuelva en otro orden

  Escenario: Sólo se formula la etapa en curso
    Dado que la etapa "Perfil" está habilitada y no tiene opinión técnica
    Y la etapa "Ejecución" todavía no corresponde
    Entonces el sistema ofrece el botón "Formular" únicamente en la etapa "Perfil"
    Y muestra "Perfil" como habilitada
    Y en "Ejecución" indica que se habilita al aprobar la etapa anterior

  Escenario: Al obtener la opinión técnica se abre la etapa siguiente
    Dado que la etapa "Perfil" ya tiene opinión técnica
    Y la etapa "Prefactibilidad" está habilitada
    Entonces el sistema muestra "Perfil" como etapa con opinión técnica, sin botón "Formular"
    Y ofrece el botón "Formular" en "Prefactibilidad"

  Escenario: Formular una etapa entra a los formularios de formulación
    Cuando el Técnico URP hace clic en "Formular" en la etapa en curso
    Entonces el sistema abre la formulación del proyecto, empezando por "Identificación"

  Escenario: En un proyecto de emergencia, el Perfil lleva a su ficha
    Dado un proyecto de emergencia cuya etapa "Perfil" está habilitada
    Cuando el Técnico URP hace clic en "Formular" en la etapa "Perfil"
    Entonces el sistema abre la "Ficha de proyectos de emergencia"

  Escenario: Guardar la ficha de emergencia habilita el envío a viabilidad
    Dado que el Técnico URP diligenció la "Ficha de proyectos de emergencia"
    Cuando hace clic en "Guardar"
    Entonces el sistema guarda la ficha y permanece en la pantalla
    Y muestra el botón "Enviar a viabilidad"
    # Pendiente del back: CU-PRE-03.5 todavía no expone el envío a viabilidad,
    # así que el botón se muestra desactivado y el sistema explica por qué.
