# language: es
@CU-PRE-12 @rol:TECNICO_URP
Característica: Avanzar a la sección Tamaño

  Como Técnico URP
  Quiero avanzar a la sección "Tamaño" tras completar la localización del proyecto

  Escenario: Avanzar a la sección Tamaño (camino feliz, FA-02)
    Dado que el Técnico URP se encuentra en la pantalla "Localización" localizacion
    Cuando hace clic en el botón "Siguiente" localizacion
    Entonces el sistema avanza a la sección "Tamaño" localizacion

  # ⚠️ Escenario pendiente: el documento no especifica el código formal del caso de uso al que corresponde la sección "Tamaño" (Datos Pendientes de Definir del CU original); el escenario se refiere a ella únicamente por su nombre, sin código CU-PRE-XX.