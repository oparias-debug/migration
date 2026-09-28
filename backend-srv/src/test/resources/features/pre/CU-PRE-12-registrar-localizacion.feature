# language: es
@CU-PRE-12 @rol:TECNICO_URP
Característica: Registrar la localización del proyecto

  Como Técnico URP
  Quiero registrar la macro y microlocalización del proyecto, visualizarla en un mapa y registrar la propiedad del terreno o inmueble por cada ubicación

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Localización" (Anexo A.1) localizacion

  Escenario: Autocompletar una fila de ubicación desde Área de Influencia (camino feliz, FA-03)
    Dado que el proyecto ya cuenta con ubicaciones registradas en CU-PRE-08 "Área de Influencia" localizacion
    Cuando el Técnico URP hace clic en el botón "Traer ubicación de Área de Influencia" localizacion
    Entonces el sistema completa los campos "Departamento" y "Distrito" de esa fila localizacion
    Y completa, cuando corresponda, los campos "Dirección Específica" y "Coordenadas" de esa fila localizacion
    Y se mantiene en la pantalla "Localización" (RN03) localizacion

  Escenario: Agregar una fila adicional de localización
    Cuando el Técnico URP hace clic en el botón emergente para adicionar fila localizacion
    Entonces el sistema agrega una nueva fila, con los campos "Departamento" y "Distrito" editables (RN04) localizacion

  Escenario: El distrito de una fila adicionada debe estar dentro de los departamentos de Área de Influencia
    Dado una fila adicionada por el Técnico URP  localizacion
    Cuando el Técnico URP selecciona un distrito perteneciente a uno de los departamentos registrados en la tabla "Área de Influencia" de CU-PRE-08  localizacion
    Entonces el sistema permite el registro (RN04) localizacion

  Escenario: Eliminar una fila de localización
    Dado una fila registrada en la tabla de "Localización" localizacion
    Cuando el Técnico URP hace clic en el botón emergente ubicado a un costado de esa fila localizacion
    Entonces el sistema elimina la fila correspondiente (RN05) localizacion

  Escenario: Registrar coordenadas ubica automáticamente un marcador en el mapa
    Cuando el Técnico URP registra las "Coordenadas" de una fila en formato DD (Grados Decimales) localizacion
    Entonces el sistema ubica automáticamente un marcador en el mapa del Anexo A.1 localizacion

  Escenario: Agregar un marcador en el mapa autocompleta las coordenadas
    Cuando el Técnico URP agrega un marcador en una ubicación específica del mapa localizacion
    Entonces el sistema autocompleta el campo "Coordenadas" de la fila correspondiente en formato DD localizacion

  Escenario: El campo Coordenadas se bloquea cuando el Distrito de esa fila es "Nivel nacional"
    Dado que el Técnico URP selecciona "Nivel nacional" en el campo "Distritos" de una fila localizacion
    Entonces el sistema bloquea el ingreso de coordenadas de esa fila, mostrando la celda sombreada localizacion

  Escenario: Desplegar el campo del propietario en una fila al requerir adquisición de terreno
    Cuando el Técnico URP selecciona si "Sí" en "¿El proyecto requiere de la adquisición de un terreno o inmueble?" de una fila localizacion
    Entonces el sistema si despliega, para esa misma fila, el campo "¿Quién es el propietario del terreno o inmueble?" (RN06) localizacion

  Escenario: No desplegar el campo del propietario en una fila cuando no se requiere adquisición
    Cuando el Técnico URP selecciona "No" en "¿El proyecto requiere de la adquisición de un terreno o inmueble?" de una fila localizacion
    Entonces el sistema no despliega, para esa fila, el campo "¿Quién es el propietario del terreno o inmueble?" (RN06) localizacion

  Esquema del escenario: Habilitar el campo "Especifique" de una fila según la opción de propietario seleccionada
    Dado que el campo "¿Quién es el propietario del terreno o inmueble?" de una fila está desplegado  localizacion
    Cuando el Técnico URP selecciona, en esa fila, la opción "<opcion>"  localizacion
    Entonces el sistema habilita el campo "Especifique" de esa misma fila (RN07)  localizacion

    Ejemplos:
      | opcion                      |
      | Otra Institución Pública    |
      | La Municipalidad             |
      | Comodato                     |
      | Otros                        |

  Escenario: No habilitar Especifique cuando el propietario de la fila es la institución propietaria del proyecto
    Dado que el campo "¿Quién es el propietario del terreno o inmueble?" de una fila está desplegado localizacion
    Cuando el Técnico URP selecciona, en esa fila, la opción "La Institución propietaria del proyecto" localizacion
    Entonces el sistema no habilita el campo "Especifique" de esa fila localizacion

  Escenario: Respetar el límite de caracteres del campo Especifique
    Dado que el campo "Especifique" de una fila está habilitado localizacion
    Cuando el Técnico URP registra información en ese campo localizacion
    Entonces el sistema permite hasta 100 caracteres localizacion

  Escenario: Guardar la información de Localización (camino feliz)
    Cuando el Técnico URP hace clic en el botón "Guardar"  localizacion
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.2) localizacion
    Cuando el Técnico URP hace clic en "Aceptar" localizacion
    Entonces el sistema guarda la información registrada localizacion
    Y se mantiene en la pantalla "Localización" localizacion

  Escenario: Intentar guardar con campos pendientes de completar
    Cuando el Técnico URP hace clic en el botón "Guardar" sin haber completado los campos requeridos localizacion
    Entonces el sistema sombrea en color rojo los bordes de los campos pendientes de completar (RN08) localizacion

  # ⚠️ Escenario pendiente: no se especifica el mensaje que debería mostrarse cuando un distrito de una fila adicionada queda fuera de los departamentos permitidos de CU-PRE-08 "Área de Influencia" (RN04, a contrario). Solo se modela el caso válido.
  # ⚠️ Escenario pendiente: el ícono de ayuda contextual "?" (RN09) no tiene un texto de mensaje transcrito en el documento. No se genera un escenario con contenido de backend no verificable.
  # ⚠️ Escenario pendiente: la tabla de Validaciones no especifica cuáles campos son exactamente obligatorios ni el texto del mensaje asociado al resaltado en rojo (RN08); no se inventa esa información.