# language: es
@CU-PRE-01.5 @rol:TECNICO_PRE
Característica: Emitir el Código Único de Proyecto (CUP)

  Como Técnico PRE
  Quiero emitir el Código Único de Proyecto (CUP) de una solicitud procedente

  Antecedentes:
    Dado que el Técnico PRE ingresó a la pantalla "Nuevo Registro" desde el caso asignado en la Bandeja Preinversión (CU-PRE-02)
    Y el proyecto se encuentra en estado "Enviado a DGICP (Registro)"

  Escenario: Emitir el CUP (camino feliz)
    Cuando el Técnico PRE hace clic en el botón "Emitir CUP"
    Entonces el sistema asigna al proyecto un CUP consecutivo numérico de 5 dígitos, partiendo desde el número 10000 en adelante, saltando un número cada 53 códigos emitidos
    Y el sistema informa al Técnico URP por correo electrónico según el modelo del Anexo A.3.4
    Y el sistema cambia el estado del proyecto a "CUP asignado"
    Y el sistema envía el proyecto a la pantalla "Captura de Proyectos" (UC-PRE-03)
    Y el proyecto ingresa inmediatamente al banco de proyectos, quedando disponible para su búsqueda mediante CU-PRE-29
    Y el sistema pasa a la pantalla "Nuevo registro"
    Y en la tabla de origen "Registro de Proyecto" (CU-PRE-01) aparece una fila con el proyecto en estado "CUP asignado"

  # ⚠️ Escenario pendiente: el mecanismo/reglas exactas del salto de un número cada 53 códigos emitidos no está completamente especificado más allá del ejemplo del documento original ("10053", "10106", "10159"); no se genera un Scenario Outline verificando números concretos de salto, para no inventar el mecanismo (ver Datos Pendientes de Definir del CU original).
  # ⚠️ Escenario pendiente: no se especifica qué ocurre si se intenta emitir un CUP a un proyecto que ya tiene uno asignado (RN 4: "el proceso de creación de CUP se hace por una única vez en todo el horizonte del proyecto"); el documento no describe mensaje de error ni si el botón queda deshabilitado. No se genera este escenario hasta que el negocio lo aclare.