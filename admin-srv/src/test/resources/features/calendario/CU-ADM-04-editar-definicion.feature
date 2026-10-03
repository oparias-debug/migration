# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Editar la definición de un calendario

  Como Administrador o Administrador de Calendario
  Quiero corregir los períodos y las excepciones de un calendario ya creado
  Para mantener su definición al día sin rehacerlo (RN23)

  Antecedentes:
    Dado que existe un calendario con un período no laboral "FIN_DE_SEMANA" y una excepción

  Escenario: Editar un período existente
    Cuando el actor hace clic en "Editar" en la fila del período "FIN_DE_SEMANA"
    Entonces el formulario se carga con el tipo, el código, el nombre y la repetición de ese período
    Cuando el actor cambia su nombre y hace clic en "Guardar cambios"
    Entonces el sistema envía la definición completa del calendario con ese período modificado
    Y el período conserva su identificador, de modo que no se da de alta uno nuevo
    Y los demás ítems del calendario viajan sin cambios y sin su estado, que heredan del calendario (RN20)

  Escenario: Editar una excepción existente
    Cuando el actor hace clic en "Editar" en la fila de una excepción
    Y cambia su descripción y guarda
    Entonces el sistema conserva la fecha y el identificador de la excepción
    Y la tabla de definición muestra la descripción nueva

  Escenario: Cancelar una edición
    Cuando el actor entra a editar un período
    Y hace clic en "Cancelar"
    Entonces el formulario vuelve a quedar en blanco, listo para un alta
    Y la definición del calendario no cambia

  Escenario: Quitar un ítem de la definición
    Cuando el actor hace clic en "Quitar" en la fila de un período
    Y confirma la acción
    Entonces el sistema envía la definición sin ese ítem
    Y la tabla deja de mostrarlo

  Escenario: Un calendario no se elimina, se inactiva (RN20)
    Entonces el sistema no ofrece ninguna acción de eliminar el calendario
    Y ofrece "Inactivar" mientras está activo, y "Activar" mientras está inactivo
