# language: es
@CU-PRE-04 @rol:TECNICO_PRE
Característica: Consultar la información de identificación y descargar archivos adjuntos

  Como Técnico PRE
  Quiero consultar la información de identificación del proyecto y descargar los archivos adjuntos, sin poder editarla

  Antecedentes:
    Dado que la información de identificación del proyecto ya fue guardada al menos una vez

  Escenario: Consultar la información en modo solo lectura
    Cuando el actor accede a la pestaña "Identificación del proyecto"
    Entonces el sistema muestra la información ingresada sin permitir su edición

  Escenario: Descargar un archivo adjunto
    Dado que existe un archivo cargado en el árbol de problemas o en el árbol de objetivos
    Cuando el actor hace clic en la opción de descarga del archivo
    Entonces el sistema descarga el archivo correspondiente

  Esquema del escenario: Alcance de visibilidad según el rol del actor
    Cuando "<actor>" consulta la pestaña "Identificación del proyecto"
    Entonces el sistema muestra la información con el siguiente alcance: "<alcance>"

    Ejemplos:
      | actor                       | alcance                                                   |
      | Técnico PRE                 | la información de todas las Unidades Ejecutoras           |