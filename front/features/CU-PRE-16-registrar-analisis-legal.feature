# language: es
@CU-PRE-16 @rol:TECNICO_URP
Característica: Registrar el análisis legal de un proyecto

  Como Técnico URP
  Quiero registrar los requisitos legales del proyecto y su estado
  Para dejar constancia de lo que falta para poder ejecutarlo

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Análisis Legal" de un proyecto con CUP

  Escenario: Registrar un requisito legal (camino feliz)
    Cuando el Técnico URP agrega una fila con el requisito, la institución responsable y el plazo
    Y hace clic en "Guardar"
    Entonces el sistema guarda el análisis legal

  Escenario: Un proyecto sin análisis legal previo
    Dado que el proyecto nunca ha guardado su análisis legal
    Cuando el Técnico URP entra a la pantalla
    Entonces el sistema muestra un análisis vacío y listo para registrar

  Escenario: Quien no registra el proyecto lo consulta sin editar
    Dado que quien entra no es Técnico URP
    Entonces la pantalla se muestra en solo lectura y sin el botón "Guardar"
