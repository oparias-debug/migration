# language: es
@CU-PRE-3.5 @rol:TECNICO_URP
Característica: Generar y aceptar la Ruta de Preinversión

  Como Técnico URP
  Quiero generar y aceptar la Ruta de Preinversión sugerida para un proyecto

  Antecedentes:
    Dado que el Técnico URP ingresa a "Captura de Proyectos" (UC-PRE-03) y hace clic sobre el CUP del proyecto
    Y hace clic en el botón "Ruta de Preinversión" de la pantalla del Anexo A.1

  Escenario: Generar y aceptar la Ruta de Preinversión (camino feliz)
    Dado que es la primera vez que se ingresa al proyecto o aún no se ha generado una Ruta de Preinversión
    Cuando el sistema muestra únicamente los campos "Criterios", "Calificación" y el botón "Generar Ruta de Preinversión" (Anexo A.2)
    Y el Técnico URP selecciona una calificación para cada criterio (Tipo de capital, Tamaño del proyecto, Complejidad del proyecto)
    Y hace clic en el botón "Generar Ruta de Preinversión"
    Entonces el sistema muestra la Ruta de Preinversión sugerida según la combinación seleccionada (Anexo B.2)
    Cuando el Técnico URP hace clic en el botón "Aceptar"
    Entonces el sistema traslada las etapas de Preinversión a la pantalla del Anexo A.1

  Escenario: Las etapas Perfil y Ejecución quedan preseleccionadas por defecto
    Entonces las etapas "Perfil" y "Ejecución" aparecen seleccionadas por defecto en la Ruta de Preinversión

  Esquema del escenario: Generar la Ruta de Preinversión según la combinación de criterios (matriz completa del Anexo B.2)
    Cuando el Técnico URP selecciona "<tipo_capital>", "<tamano>" y "<complejidad>"
    Y hace clic en el botón "Generar Ruta de Preinversión"
    Entonces el sistema sugiere la ruta "<ruta_sugerida>"

    Ejemplos:
      | tipo_capital           | tamano  | complejidad       | ruta_sugerida                                                    |
      | Capital Humano         | Pequeño | Complejidad Baja  | Perfil                                                            |
      | Capital Humano         | Pequeño | Complejidad Media | Perfil                                                            |
      | Capital Humano         | Pequeño | Complejidad Alta  | Perfil                                                            |
      | Capital Humano         | Mediano | Complejidad Baja  | Perfil                                                            |
      | Capital Humano         | Mediano | Complejidad Media | Perfil                                                            |
      | Capital Humano         | Mediano | Complejidad Alta  | Perfil                                                            |
      | Capital Humano         | Grande  | Complejidad Baja  | Perfil                                                            |
      | Capital Humano         | Grande  | Complejidad Media | Perfil                                                            |
      | Capital Humano         | Grande  | Complejidad Alta  | Perfil                                                            |
      | Capital Institucional  | Pequeño | Complejidad Baja  | Perfil                                                            |
      | Capital Institucional  | Pequeño | Complejidad Media | Perfil                                                            |
      | Capital Institucional  | Pequeño | Complejidad Alta  | Perfil                                                            |
      | Capital Institucional  | Mediano | Complejidad Baja  | Perfil                                                            |
      | Capital Institucional  | Mediano | Complejidad Media | Perfil                                                            |
      | Capital Institucional  | Mediano | Complejidad Alta  | Perfil                                                            |
      | Capital Institucional  | Grande  | Complejidad Baja  | Perfil                                                            |
      | Capital Institucional  | Grande  | Complejidad Media | Perfil                                                            |
      | Capital Institucional  | Grande  | Complejidad Alta  | Perfil                                                            |
      | Otros capitales        | Pequeño | Complejidad Baja  | Perfil                                                            |
      | Otros capitales        | Pequeño | Complejidad Media | Perfil                                                            |
      | Otros capitales        | Pequeño | Complejidad Alta  | Perfil                                                            |
      | Otros capitales        | Mediano | Complejidad Baja  | Perfil                                                            |
      | Otros capitales        | Mediano | Complejidad Media | Perfil                                                            |
      | Otros capitales        | Mediano | Complejidad Alta  | Perfil                                                            |
      | Otros capitales        | Grande  | Complejidad Baja  | Perfil                                                            |
      | Otros capitales        | Grande  | Complejidad Media | Perfil                                                            |
      | Otros capitales        | Grande  | Complejidad Alta  | Perfil                                                            |
      | Capital Físico         | Pequeño | Complejidad Baja  | Perfil con Diseño Básico                                          |
      | Capital Físico         | Pequeño | Complejidad Media | Perfil con Diseño Básico                                          |
      | Capital Físico         | Pequeño | Complejidad Alta  | Perfil con Diseño Básico                                          |
      | Capital Físico         | Mediano | Complejidad Baja  | Perfil + Diseño                                                   |
      | Capital Físico         | Mediano | Complejidad Media | Perfil + Prefactibilidad + Factibilidad + Diseño                  |
      | Capital Físico         | Mediano | Complejidad Alta  | Perfil + Prefactibilidad + Factibilidad + Diseño                  |
      | Capital Físico         | Grande  | Complejidad Baja  | Perfil + Prefactibilidad + Factibilidad + Diseño                  |
      | Capital Físico         | Grande  | Complejidad Media | Perfil + Prefactibilidad + Factibilidad + Diseño                  |
      | Capital Físico         | Grande  | Complejidad Alta  | Perfil + Prefactibilidad + Factibilidad + Diseño                  |

  Esquema del escenario: Intentar generar la Ruta de Preinversión sin calificar un criterio
    Cuando el Técnico URP hace clic en "Generar Ruta de Preinversión" sin haber calificado el criterio "<criterio>"
    Entonces el sistema no genera la Ruta de Preinversión, ya que la calificación de cada criterio es obligatoria (RN01)

    Ejemplos:
      | criterio                          |
      | Tipo de capital que genera         |
      | Tamaño del proyecto según monto    |
      | Complejidad del proyecto           |

  Esquema del escenario: El botón "Ruta de Preinversión" se desactiva según el tipo de iniciativa de inversión
    Dado que el proyecto tiene como Iniciativa de Inversión "<iniciativa>" (registrada en CU-PRE-01)
    Entonces el sistema desactiva el botón "Ruta de Preinversión"
    Y muestra por defecto en Registro de Etapas las etapas PERFIL y EJECUCIÓN

    Ejemplos:
      | iniciativa         |
      | Estudios Generales |
      | Programa           |