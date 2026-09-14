/**
 * Pasos de un proyecto de preinversión, agrupados como la columna C del árbol
 * funcional (arbol de ejecucion.xlsx) y ordenados según la cadena de botones
 * "Siguiente" de los casos de uso: cada .feature de "avanzar" dice a qué
 * sección lleva, y de ahí sale el orden, no de una suposición.
 *
 * Son pasos de UN proyecto: sus endpoints cuelgan de /proyectos/{idProyecto},
 * así que viven dentro del proyecto y no en el menú lateral, que no sabe de
 * qué proyecto se trata.
 *
 * Un paso sin `rutas` no tiene pantalla todavía: se muestra en su sitio de la
 * secuencia, pero no enlaza. Así se ve el recorrido que describen los casos de
 * uso y cuánto falta, en vez de una barra que salte de Alternativas a
 * Presupuesto como si no hubiera nada en medio.
 *
 * Dos decisiones que dependen de lo que confirme el cliente, y que se cambian
 * aquí mismo sin tocar componentes:
 *   - "Tamaño" no va como paso propio: el CU-PRE-12 lo trata como sección de
 *     Localización y el árbol no lo lista.
 *   - Viabilidad, Elegibilidad, Opinión técnica y Priorización se agrupan como
 *     en el árbol, bajo Viabilidad.
 */
export interface PasoProyecto {
  readonly clave: string;
  /** Clave de i18n. */
  readonly texto: string;
  readonly cu: string;
  /**
   * Sufijos de ruta bajo /preinversion/proyectos/:id/ que pertenecen a este
   * paso. El primero es el destino del enlace. Sin rutas = sin pantalla aún.
   */
  readonly rutas?: readonly string[];
}

export interface GrupoPasos {
  readonly clave: string;
  readonly texto: string;
  readonly pasos: readonly PasoProyecto[];
}

export const GRUPOS_PASOS: readonly GrupoPasos[] = [
  {
    clave: 'formulacion',
    texto: 'pasos.grupo.formulacion',
    pasos: [
      // CU-PRE-03 FA-01: el CUP de Captura abre "Registro de Etapas". Ruta de
      // Preinversión y las dos fichas son pantallas de ese mismo paso.
      {
        clave: 'registro-etapas',
        texto: 'pasos.registroEtapas',
        cu: 'CU-PRE-3.5',
        rutas: ['etapas', 'ruta-preinversion', 'ficha-informacion-general', 'ficha-emergencia'],
      },
      { clave: 'identificacion', texto: 'pasos.identificacion', cu: 'CU-PRE-04', rutas: ['identificacion'] },
      { clave: 'alternativas', texto: 'pasos.alternativas', cu: 'CU-PRE-05', rutas: ['alternativas-solucion'] },
      { clave: 'interesados', texto: 'pasos.interesados', cu: 'CU-PRE-06' },
      { clave: 'poblacion', texto: 'pasos.poblacion', cu: 'CU-PRE-07' },
      { clave: 'area-influencia', texto: 'pasos.areaInfluencia', cu: 'CU-PRE-08' },
      { clave: 'mercado', texto: 'pasos.mercado', cu: 'CU-PRE-09' },
    ],
  },
  {
    clave: 'analisis-tecnico',
    texto: 'pasos.grupo.analisisTecnico',
    pasos: [
      { clave: 'descripcion-tecnica', texto: 'pasos.descripcionTecnica', cu: 'CU-PRE-11' },
      { clave: 'localizacion', texto: 'pasos.localizacion', cu: 'CU-PRE-12' },
      { clave: 'ambiental', texto: 'pasos.ambiental', cu: 'CU-PRE-14' },
      { clave: 'riesgo', texto: 'pasos.riesgo', cu: 'CU-PRE-15' },
      { clave: 'legal', texto: 'pasos.legal', cu: 'CU-PRE-16' },
    ],
  },
  {
    clave: 'presupuesto',
    texto: 'pasos.grupo.presupuesto',
    pasos: [
      { clave: 'presupuesto-inversion', texto: 'pasos.presupuestoInversion', cu: 'CU-PRE-17', rutas: ['presupuesto'] },
      { clave: 'flujo-costos', texto: 'pasos.flujoCostos', cu: 'CU-PRE-18' },
    ],
  },
  {
    clave: 'evaluacion',
    texto: 'pasos.grupo.evaluacion',
    pasos: [
      { clave: 'beneficios', texto: 'pasos.beneficios', cu: 'CU-PRE-20' },
      { clave: 'flujo-caja', texto: 'pasos.flujoCaja', cu: 'CU-PRE-21' },
      { clave: 'flujo-financiero', texto: 'pasos.flujoFinanciero', cu: 'CU-PRE-21.5' },
      { clave: 'indicadores', texto: 'pasos.indicadores', cu: 'CU-PRE-23' },
      { clave: 'programacion-financiera', texto: 'pasos.programacionFinanciera', cu: 'CU-PRE-22.1' },
    ],
  },
  {
    clave: 'viabilidad',
    texto: 'pasos.grupo.viabilidad',
    pasos: [
      { clave: 'viabilidad', texto: 'pasos.viabilidad', cu: 'CU-PRE-24' },
      { clave: 'elegibilidad', texto: 'pasos.elegibilidad', cu: 'CU-PRE-25' },
      { clave: 'opinion-tecnica', texto: 'pasos.opinionTecnica', cu: 'CU-PRE-26' },
      { clave: 'priorizacion', texto: 'pasos.priorizacion', cu: 'CU-PRE-26.5' },
    ],
  },
];

const RUTA_DE_PASO = /^\/preinversion\/proyectos\/(\d+)\/([^/]+)/;

export interface UbicacionPaso {
  readonly idProyecto: number;
  readonly grupo: GrupoPasos;
  readonly paso: PasoProyecto;
}

/** Qué paso de qué proyecto corresponde a la URL; null si no es un paso. */
export function ubicarPaso(pathname: string): UbicacionPaso | null {
  const coincidencia = RUTA_DE_PASO.exec(pathname);
  if (!coincidencia) return null;
  const [, id, sufijo] = coincidencia;
  for (const grupo of GRUPOS_PASOS) {
    const paso = grupo.pasos.find((p) => p.rutas?.includes(sufijo));
    if (paso) return { idProyecto: Number(id), grupo, paso };
  }
  return null;
}

/** Destino del enlace de un paso, o null si el paso aún no tiene pantalla. */
export function rutaDePaso(idProyecto: number, paso: PasoProyecto): string | null {
  const primera = paso.rutas?.[0];
  return primera ? `/preinversion/proyectos/${idProyecto}/${primera}` : null;
}

/** Número de cada paso, correlativo a través de los grupos (1, 2, 3…). */
export const NUMERO_DE_PASO: ReadonlyMap<string, number> = new Map(
  GRUPOS_PASOS.flatMap((g) => g.pasos).map((paso, i) => [paso.clave, i + 1]),
);
