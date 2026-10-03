# language: es
@CU-PRE-26 @rol:TECNICO_URP @rol:SISTEMA
Característica: Gestionar la Actualización de Opinión Técnica

  Como Técnico URP
  Quiero gestionar la Actualización de una Opinión Técnica previamente emitida

  Escenario: La opción "Opinión Técnica" está desactivada al solicitar una Actualización de OT
    Dado que se está solicitando una Actualización de Opinión Técnica para el proyecto
    Cuando se despliega el "Tipo de solicitud" del menú "OT" en "GESTIÓN"
    Entonces la opción "1. Opinión Técnica" está desactivada

  Escenario: Confirmar la gestión de una Actualización de OT
    Dado que el usuario autenticado tiene el rol "Técnico URP"
    Cuando ingresa al Módulo de Gestión
    Y selecciona en el menú de OT la opción "Actualización de OT"
    Entonces el Sistema muestra el mensaje del Anexo A.3 "¿Está seguro de gestionar una actualización de Opinión Técnica?" con los botones "SÍ" y "NO"

  Escenario: Habilitar campos para la Actualización de OT cuando existe una OT previa para la etapa
    Dado que el usuario autenticado tiene el rol "Técnico URP"
    Y existe una OT previa para la etapa que se está gestionando
    Y se muestra el mensaje del Anexo A.3
    Cuando da clic en el botón "SÍ"
    Entonces el Sistema muestra el mensaje del Anexo A.4 "Se han habilitado los campos correspondientes de F&E, para el ingreso de información actualizada."
    Y habilita los campos indicados en la hoja de cálculo CU-PRE-3.5 "Selección y registro de etapas" ANEXO según el "Tipo de Iniciativa" y "Campos a habilitar para Actualización de O.T."

  Escenario: Continuar con Viabilidad tras ajustar los campos habilitados
    Dado que el usuario autenticado tiene el rol "Técnico URP"
    Y el Sistema habilitó los campos para la Actualización de OT
    Cuando realiza ajustes en los campos habilitados
    Entonces continúa con el proceso de Viabilidad (CU-PRE-24 "Viabilidad")

  # ⚠️ Escenario pendiente: existe una contradicción sin resolver (ver CU original) sobre si el Viabilizador puede gestionar la Actualización de OT (FA04: solo Técnico URP; Anexo A – RN3: "viabilizador o Técnico URP"). No implementar hasta que se resuelva.
  # Sin escenario: resultado de la validación cuando no existe OT previa (FA04 paso 4.4) y acción de los botones "NO" (A.3) y "ACEPTAR" (A.4) — no especificados. Ver historias-CU-PRE-26.md.
