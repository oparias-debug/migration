# language: es
@CU-PRE-02 @rol:COORDINADOR_PRE
Característica: Consultar las solicitudes activas de la Bandeja Preinversión

  Como Coordinador PRE
  Quiero consultar el estado de las solicitudes activas de la Bandeja Preinversión, filtrando por tipo de solicitud

  Antecedentes:
    Dado que el Coordinador PRE accede a la pantalla "Bandeja Preinversión / Solicitudes Activas" (Anexo A.1)

  Escenario: Consultar la tabla de solicitudes activas (camino feliz)
    Entonces el sistema muestra la tabla "Solicitudes Activas" con las columnas Unidad Ejecutora, Tipo de Solicitud, CUP, Nombre del Proyecto, Fecha de Solicitud, Estado y Asignado a

  Esquema del escenario: Filtrar las solicitudes activas por tipo de solicitud
    Cuando el Coordinador PRE aplica el filtro de la columna "Tipo de Solicitud" con el valor "<tipo>"
    Entonces el sistema muestra únicamente las solicitudes cuyo "Tipo de Solicitud" sea "<tipo>"

    Ejemplos:
      | tipo             |
      | CUP              |
      | Opinión Técnica  |

  Escenario: Visualizar el conteo de casos asignados por Técnico PRE
    Entonces el sistema muestra, en el pie de la tabla "Solicitudes Activas", el conteo de casos asignados a cada Técnico PRE
    Y dicho conteo se contabiliza por separado para solicitudes de CUP y para solicitudes de Opinión Técnica

  # ⚠️ Escenario pendiente: el mockup del Anexo A.1 muestra un valor de estado "En análisis" en una fila de ejemplo, que no forma parte del catálogo de estados definido por RN07 ("Enviado a DGICP", "Observado DGICP"). No se genera ningún escenario que use o verifique el estado "En análisis" hasta que el negocio confirme si es un estado adicional real o un valor de ejemplo sin relación con el catálogo (ver Datos Pendientes de Definir del CU original).