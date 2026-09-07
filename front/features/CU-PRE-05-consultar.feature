# language: es
@CU-PRE-05 @rol:TECNICO_PRE @rol:USUARIOS_INTERNOS_EXTERNOS
Característica: Consultar la información de alternativas de solución

  Como Técnico PRE o Usuarios Internos/Externos
  Quiero consultar la información de alternativas de solución, sin poder editarla

  Antecedentes:
    Dado que la información de "Registro de Alternativas" ya fue guardada al menos una vez

  Escenario: Consultar la información en modo solo lectura
    Cuando el actor accede a la sección "Registro de Alternativas"
    Entonces el sistema muestra la información de alternativas ingresada sin permitir su edición

  Esquema del escenario: Alcance de visibilidad según el rol del actor
    Cuando "<actor>" consulta la sección "Registro de Alternativas"
    Entonces el sistema muestra la información de alternativas con el siguiente alcance: "<alcance>"

    Ejemplos:
      | actor                       | alcance                                           |
      | Técnico PRE                 | la información de todas las Unidades Ejecutoras  |
      | Usuarios Internos/Externos  | únicamente la información según sus credenciales |