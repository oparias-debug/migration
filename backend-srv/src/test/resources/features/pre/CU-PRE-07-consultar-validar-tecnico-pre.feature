# language: es
@CU-PRE-07 @rol:TECNICO_PRE
Característica: Consultar y validar el análisis de la población (Técnico PRE)

  Como Técnico PRE
  Quiero consultar la información de todas las Unidades Ejecutoras y validar que la ubicación registrada de la Población Afectada y Objetivo esté dentro del área geográfica de la Población de Referencia

  Escenario: Consultar la información de todas las Unidades Ejecutoras (camino feliz)
    Cuando el Técnico PRE accede a la pantalla "Análisis de la Población" de cualquier Unidad Ejecutora
    Entonces el sistema muestra la información registrada por el Técnico URP en modo solo lectura

  # ⚠️ Escenario pendiente: el documento indica que la ubicación de la Población Afectada y de la Población Objetivo "será validada por el Técnico PRE" (Anexo B.1), pero no describe ningún flujo, pantalla o acción concreta del sistema para realizar esa validación. No se inventa ese mecanismo (¿revisión manual externa al sistema?, ¿acción de aprobar/rechazar dentro del sistema?) hasta que el negocio lo aclare.