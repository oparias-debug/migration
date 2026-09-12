# language: es
@CU-PRE-04 @rol:TECNICO_URP
Característica: Cargar, reemplazar y eliminar los archivos de árbol de problemas y árbol de objetivos

  Como Técnico URP
  Quiero cargar, reemplazar y eliminar el archivo del árbol de problemas y del árbol de objetivos

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pestaña "Identificación del proyecto" con la edición de campos habilitada

  Esquema del escenario: Cargar el archivo del árbol correspondiente (camino feliz)
    Cuando el Técnico URP hace clic en el ícono "<icono>"
    Y carga un archivo en formato PDF/A
    Entonces el sistema almacena el archivo
    Y el archivo cargado aparece junto al ícono, con la opción de descargar

    Ejemplos:
      | icono                          |
      | Agregar Árbol de problemas     |
      | Agregar Árbol de objetivos     |

  Esquema del escenario: Reemplazar un archivo ya cargado
    Dado que ya existe un archivo cargado en el ícono "<icono>"
    Cuando el Técnico URP carga un nuevo archivo en el mismo ícono
    Entonces el sistema reemplaza el archivo anterior con el nuevo archivo cargado

    Ejemplos:
      | icono                          |
      | Agregar Árbol de problemas     |
      | Agregar Árbol de objetivos     |

  Esquema del escenario: Eliminar un archivo cargado
    Dado que ya existe un archivo cargado en el ícono "<icono>"
    Cuando el Técnico URP hace clic en el ícono para eliminar el archivo
    Entonces el sistema elimina el archivo cargado

    Ejemplos:
      | icono                          |
      | Agregar Árbol de problemas     |
      | Agregar Árbol de objetivos     |

  # ⚠️ Escenario pendiente: RNB-1 y RNB-2 condicionan la posibilidad de recargar el archivo a que el proyecto no haya pasado al estado "En proceso de viabilidad", pero el documento no describe en qué caso de uso o momento ocurre esa transición. No se genera un escenario que dependa de verificar dicho estado (ver Datos Pendientes de Definir del CU original).