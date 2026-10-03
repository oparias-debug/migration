# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Ver y definir un calendario administrativo en forma de calendario

  Como Administrador o Administrador de Calendario
  Quiero ver el calendario como un calendario, con sus días clasificados
  Para saber de un vistazo qué días son laborales y por qué, y corregirlos ahí mismo

  Antecedentes:
    Dado que existe el calendario "INVERSION_2027", del 2027-03-01 al 2027-10-31
    Y tiene un período laboral "HABILES" que cubre todo ese rango
    Y tiene un período no laboral "FIN_DE_SEMANA" los sábados y domingos
    Y tiene un período no laboral "SEMANA_AGOSTINA" del 2027-08-01 al 2027-08-06
    Y tiene una excepción de día no laboral el 2027-05-01, "Día del Trabajo"

  Escenario: La rejilla del mes clasifica cada día
    Cuando el actor abre la ficha del calendario "INVERSION_2027"
    Entonces el sistema muestra la rejilla del mes, de lunes a domingo
    Y pinta en verde los días laborales, con la letra "L"
    Y pinta en rojo los días no laborales, con la letra "N"
    Y deja en blanco los días que no están cubiertos por ningún período
    Y marca con un asterisco los días que tienen una excepción
    Y los días fuera del rango del calendario se muestran apagados y no se pueden elegir

  Esquema del escenario: Un día cubierto por varios períodos (RN02)
    Cuando el actor elige el día "<fecha>" en la rejilla
    Entonces la ficha del día indica que es "<tipo>"
    Y enumera los períodos que lo cubren

    Ejemplos:
      | fecha      | tipo           |
      | 2027-03-01 | Día laboral    |
      | 2027-03-06 | Día no laboral |
      | 2027-08-02 | Día no laboral |
      | 2027-05-01 | Día no laboral |

  Escenario: La excepción manda sobre los períodos
    Cuando el actor elige el día 2027-05-01 en la rejilla
    Entonces la ficha del día lo muestra como no laboral
    Y entre lo que lo cubre aparece la excepción "Día del Trabajo"

  Escenario: Reclasificar un día desde la rejilla
    Cuando el actor elige un día laboral en la rejilla
    Y hace clic en "Marcar como no laboral"
    Y escribe el motivo de la excepción y acepta
    Entonces el sistema registra la excepción sobre esa fecha
    Y la rejilla vuelve a pintarse con ese día en rojo y con su asterisco

  Escenario: Un día fuera de todo período no tiene tipo (RN01, RN16)
    Dado un calendario cuyos períodos no cubren el 2027-04-15
    Cuando el actor elige ese día en la rejilla
    Entonces la ficha del día indica que está sin definir
    Y advierte que el día no está dentro de ningún período definido
