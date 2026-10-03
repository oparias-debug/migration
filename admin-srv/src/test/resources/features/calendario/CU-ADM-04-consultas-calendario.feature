# language: es
@CU-ADM-04
Característica: Consultas sobre un calendario administrativo

  Como cualquier usuario del sistema
  Quiero preguntarle al calendario por tipos de día, duraciones y plazos
  Para planificar actividades sin depender de un rol administrativo (RN18)

  Antecedentes:
    Dado que existe el calendario "INVERSION_2027" con sus períodos y excepciones definidos

  Esquema del escenario: Las consultas responden sobre la definición cargada
    Cuando el actor ejecuta la consulta "<consulta>"
    Entonces el sistema muestra la respuesta del servicio

    Ejemplos:
      | consulta                                             |
      | ¿Qué tipo de día es una fecha?                       |
      | ¿La fecha pertenece a un período determinado?        |
      | ¿Cuánto dura un período?                             |
      | ¿Cuántos días le quedan a un período laboral?        |
      | ¿Cuántos días laborales hay entre dos fechas?        |
      | ¿En qué fecha cae un plazo en días hábiles?          |
      | ¿Qué rango de fechas cubre el calendario?            |

  Escenario: La duración de un período laboral descuenta sus intersecciones (RN04)
    Cuando el actor consulta la duración del período laboral "HABILES"
    Entonces el resultado no cuenta los días en que "HABILES" se cruza con un período no laboral

  Escenario: Una consulta que el servicio no puede resolver muestra su error (RN16, RN17)
    Cuando el actor consulta el tipo de día de una fecha que no cae en ningún período
    Entonces el sistema muestra el mensaje de error del servicio
    Y no muestra un resultado inventado

  Escenario: Las consultas no exigen rol administrativo (RN18)
    Dado que el actor es un Técnico URP
    Cuando abre la ficha de un calendario
    Entonces puede ver la rejilla y ejecutar las consultas
    Y el sistema no le ofrece agregar períodos, registrar excepciones ni cambiar el estado
