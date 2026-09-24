# language: es
@CU-PRE-18 @rol:TECNICO_URP @wip
Característica: Registrar el detalle de costos de una actividad por insumo

  Como Técnico URP
  Quiero registrar el detalle de costos de una actividad por insumo

  Antecedentes:
    Dado que el Técnico URP hace clic en el botón "Agregar Actividad"
    Y el sistema muestra la pantalla emergente "Detalle de Actividad" (Anexo A.2)
    Y la columna "Insumo Tipo" muestra por defecto los insumos del catálogo "Insumos" (RN07)

  Escenario: Registrar y guardar el detalle de una actividad (camino feliz)
    Cuando el Técnico URP registra el "Nombre de la Actividad" - presupuesto-om
    Y registra el costo de al menos un insumo en la columna "Período 1 (Precio de Mercado)"
    Entonces el sistema calcula automáticamente el "Período Año 1 (Precio Ajustado)" de cada insumo como Precio de Mercado × FC
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.3)
    Cuando el Técnico URP hace clic en "Aceptar"
    Entonces el sistema traslada el nombre de la actividad y el total de Período 1 (a precios de mercado) a la tabla correspondiente del Anexo A.1
    Y el sistema vacía los campos de la columna "Período 1" en la pantalla "Detalle de Actividad", para que el Técnico URP continúe registrando otra actividad

  Escenario: Salir sin guardar el detalle de la actividad
    Dado que el Técnico URP ha registrado información en la pantalla "Detalle de Actividad"
    Cuando hace clic en el botón "Salir"
    Entonces el sistema muestra el mensaje "Los datos no serán guardados" - presupuesto-om
    Cuando el Técnico URP hace clic en "Aceptar"
    Entonces el sistema regresa a la pestaña "Costos de Operación y Mantenimiento" sin guardar la información registrada

  Escenario: Intentar guardar sin el nombre de la actividad
    Cuando el Técnico URP hace clic en "Guardar" sin haber registrado el "Nombre de la Actividad"
    Entonces el sistema no permite guardar, ya que el campo es obligatorio

  Escenario: Intentar guardar sin ningún costo de insumo registrado
    Cuando el Técnico URP hace clic en "Guardar" sin haber registrado el costo de al menos un insumo en "Período 1"
    Entonces el sistema no permite guardar, ya que se requiere al menos un insumo con costo registrado

  # ⚠️ Escenario pendiente: la fórmula de RN06 para proyectar, POR ACTIVIDAD INDIVIDUAL, los costos de Período 2 en adelante (campo "Actividad.costosProyectadosPeriodo2EnAdelante") es ilegible en el documento fuente; no se genera aquí ningún escenario para ese campo, que queda nulo, hasta que el negocio aporte la fórmula correcta (ver Datos Pendientes de Definir del CU original). La proyección de Período 2..n a nivel de TABLA ("Costos de Operación"/"Costos de Mantenimiento", RN06/RN08) sí está implementada como [SUPUESTO] de crecimiento compuesto y cubierta en CU-PRE-18-presupuesto-om-backend.feature.
  # ⚠️ Escenario pendiente: la tabla "Costos de Operación" cita "RN07" como origen del campo "Costo por período" y la de "Costos de Mantenimiento" cita "RN06"; se usó RN06 (la regla que describe el traslado de valores) en los escenarios anteriores, sin resolver si la referencia a RN07 es un error del documento.
