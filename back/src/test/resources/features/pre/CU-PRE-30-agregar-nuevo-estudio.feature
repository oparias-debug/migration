# language: es
@CU-PRE-30 @rol:TECNICO_URP
Característica: Agregar un nuevo estudio y registrar su programación

  Como Técnico URP
  Quiero agregar un nuevo estudio y registrar su programación cuatrimestral

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Programación Financiera Cuatrimestral del PAP" (Anexo A.1)

  Escenario: Agregar un nuevo estudio y registrar su programación (camino feliz, SF-2)
    Cuando el Técnico URP hace clic en el botón "Agregar estudio"
    Entonces el sistema muestra únicamente el campo "Código" y el botón "Aceptar"
    Cuando el Técnico URP registra el "Código" y hace clic en "Aceptar"
    Entonces el sistema valida la Ruta de Preinversión y las etapas finalizadas en períodos anteriores (RN-B.b, RN-B.e)
    Y muestra las etapas de la Ruta de Preinversión con "Costo de la Etapa" prediligenciado desde CU-PRE-03.5
    Cuando el Técnico URP selecciona la Fuente de Financiamiento, la Fuente de Recursos y el Convenio
    Y registra los montos de cada cuatrimestre
    Y hace clic en el botón "Guardar"
    Entonces el sistema valida los datos según RN-B literal c.1
    Y traslada la información a la tabla del Anexo A.1

  Escenario: Debe programarse al menos una etapa
    Cuando el Técnico URP intenta guardar sin haber registrado la programación de ninguna etapa
    Entonces el sistema no permite continuar, ya que es obligatorio programar al menos una etapa (RN-B.b)

  Escenario: Alerta al saltarse una etapa de la Ruta de Preinversión
    Dado que el Técnico URP intenta registrar programación de una etapa posterior sin haber registrado una etapa anterior incluida en la Ruta de Preinversión
    Cuando intenta guardar
    Entonces el sistema muestra el mensaje "No puede saltarse la Ruta de Preinversión" (Anexo A.7)
    Y le indica que debe programar la etapa saltada o ajustar la Ruta de Preinversión en CU-PRE-03.5

  Escenario: Valores iniciales al registrar por primera vez
    Entonces los campos "I Cuatrimestre", "II Cuatrimestre" y "III Cuatrimestre" muestran "$0.00" por defecto
    Y los campos "Fuente de Financiamiento", "Fuente de Recursos" y "Convenio" muestran "Seleccione" por defecto (RN-B.a)

  Escenario: Las etapas ya finalizadas no se muestran para registro de nuevos estudios
    Dado que una etapa de la preinversión ya fue finalizada física y financieramente en años anteriores
    Entonces el sistema no la muestra en el Anexo A.2 (RN-B.e)

  # RN-B.c: el "Costo de la etapa" es único por etapa; con 2+ fuentes de financiamiento (botón "+"
  # del Anexo A.2) se valida la suma de todas ellas, no cada fuente por separado.
  Escenario: El costo de la etapa se valida sumando las fuentes enviadas en la misma solicitud
    Cuando el Técnico URP registra en la etapa "Perfil" una fuente de financiamiento con 6000 y otra con 5000 y hace clic en "Guardar"
    Entonces el sistema muestra el mensaje "Monto Programado supera el costo de la etapa" sin guardar ninguna fuente (Anexo A.3, RN-B.c)

  Escenario: El costo de la etapa se valida sumando las fuentes ya guardadas de la etapa
    Dado que la etapa "Perfil" ya tiene guardada una fuente de financiamiento con 6000 programados en el año
    Cuando el Técnico URP agrega con el botón "+" otra fuente de financiamiento con 5000 y hace clic en "Guardar"
    Entonces el sistema muestra el mensaje "Monto Programado supera el costo de la etapa" sin guardar ninguna fuente (Anexo A.3, RN-B.c)

  Escenario: Al editar una fuente guardada su monto previo se reemplaza y no se cuenta dos veces
    Dado que la etapa "Perfil" ya tiene guardada una fuente de financiamiento con 6000 programados en el año
    Cuando el Técnico URP modifica esa fuente a 4000 y agrega con el botón "+" otra fuente de financiamiento con 6000
    Entonces el sistema guarda ambas fuentes y el total programado de la etapa es igual a su "Costo de la etapa" (RN-B.c)

  Escenario: En estudios de arrastre se descuenta lo ejecutado en años anteriores por todas las fuentes de la etapa
    Dado que la etapa "Perfil" tiene una fuente de financiamiento con 5000 ejecutados en años anteriores
    Cuando el Técnico URP agrega con el botón "+" otra fuente de financiamiento con 6000 y hace clic en "Guardar"
    Entonces el sistema muestra el mensaje "Monto Programado supera el costo de la etapa" sin guardar ninguna fuente (Anexo A.3, RN-B.c)