/**
 * Pasos de un proyecto de preinversión, según el árbol del sistema que envió el
 * cliente el 14/09/2026 ("árbol del sistema (2)"). Columnas del árbol:
 *
 *   B MACROPROCESO → C PROCESO → D SUBPROCESO → E CAPÍTULO → F PESTAÑA
 *
 * Aquí cada GRUPO es un proceso de Preinversión (1.2 a 1.5), cada SECCIÓN un
 * subproceso y cada PASO un capítulo. Las pestañas de un capítulo se guardan
 * para cuando exista su pantalla. Los pasos muestran el código del árbol
 * (1.3.2.6…), que es como el cliente se ubica.
 *
 * No están 1.1 Asignación CUP, que es anterior al CUP y se trabaja desde Registro
 * de Proyecto y la Bandeja, ni Priorización, que el cliente movió a
 * Programación (2.1), fuera de los pasos del proyecto.
 *
 * Son pasos de UN proyecto: sus endpoints cuelgan de /proyectos/{idProyecto},
 * así que viven dentro del proyecto y no en el menú lateral. Cada grupo guarda la
 * ruta de su opción de menú, que es donde se elige el proyecto.
 *
 * Un paso sin `rutas` no tiene pantalla todavía: se ve en su sitio, sin enlace.
 */
export interface PasoProyecto {
  readonly clave: string;
  /** Código del árbol del sistema. */
  readonly codigo: string;
  /** Clave de i18n. */
  readonly texto: string;
  readonly cu: string;
  /** Claves de i18n de las pestañas del capítulo (columna F del árbol). */
  readonly pestanas?: readonly string[];
  /**
   * Sufijos de ruta bajo /preinversion/proyectos/:id/ que pertenecen a este
   * paso. El primero es el destino del enlace. Sin rutas = sin pantalla aún.
   */
  readonly rutas?: readonly string[];
}

export interface SeccionPasos {
  /** Subproceso del árbol. Sin código ni texto cuando el proceso no se subdivide. */
  readonly codigo?: string;
  readonly texto?: string;
  readonly pasos: readonly PasoProyecto[];
}

export type ClaveGrupo = 'creacion-ruta' | 'formulacion' | 'programacion' | 'gestion';

export interface GrupoPasos {
  readonly clave: ClaveGrupo;
  readonly codigo: string;
  readonly texto: string;
  /** Opción del menú lateral de este proceso, donde se elige el proyecto. */
  readonly ruta: string;
  readonly secciones: readonly SeccionPasos[];
}

/**
 * Código del árbol a partir de sus niveles: arbol(1, 3, 2, 6) → '1.3.2.6'. Escrito
 * como texto, Sonar confunde los códigos de cuatro niveles con direcciones IP.
 */
const arbol = (...niveles: number[]) => niveles.join('.');

export const GRUPOS_PASOS: readonly GrupoPasos[] = [
  {
    clave: 'creacion-ruta',
    codigo: '1.2',
    texto: 'pasos.grupo.creacionRuta',
    ruta: '/preinversion/creacion-ruta',
    secciones: [
      {
        pasos: [
          // CU-PRE-03 FA-01: el CUP de Captura abre "Registro de Etapas". Ruta de
          // Preinversión y las dos fichas son pantallas de este mismo capítulo.
          {
            clave: 'seleccion-etapa',
            codigo: arbol(1, 2, 1, 1),
            texto: 'pasos.seleccionEtapa',
            cu: 'CU-PRE-03.5',
            pestanas: ['pasos.pestana.criteriosRuta', 'pasos.pestana.fichaInformacion'],
            // La Ruta va primero: al entrar al proyecto por este proceso, lo
            // primero que se ve es la matriz de criterios (Rocío, 22/09/2026).
            rutas: ['ruta-preinversion', 'etapas', 'ficha-informacion-general', 'ficha-emergencia'],
          },
        ],
      },
    ],
  },
  {
    clave: 'formulacion',
    codigo: '1.3',
    texto: 'pasos.grupo.formulacion',
    ruta: '/preinversion/formulacion',
    secciones: [
      {
        codigo: '1.3.1',
        texto: 'pasos.seccion.identificacion',
        pasos: [
          { clave: 'identificacion', codigo: arbol(1, 3, 1, 1), texto: 'pasos.identificacion', cu: 'CU-PRE-04', rutas: ['identificacion'] },
          { clave: 'alternativas', codigo: arbol(1, 3, 1, 2), texto: 'pasos.alternativas', cu: 'CU-PRE-05', rutas: ['alternativas-solucion'] },
        ],
      },
      {
        codigo: '1.3.2',
        texto: 'pasos.seccion.formulacion',
        pasos: [
          {
            clave: 'diagnostico',
            codigo: arbol(1, 3, 2, 1),
            texto: 'pasos.diagnostico',
            cu: 'CU-PRE-06 a 09',
            pestanas: ['pasos.pestana.interesados', 'pasos.pestana.poblacion', 'pasos.pestana.areaInfluencia', 'pasos.pestana.mercado'],
            rutas: ['diagnostico'],
          },
          {
            clave: 'estudio-tecnico',
            codigo: arbol(1, 3, 2, 2),
            texto: 'pasos.estudioTecnico',
            cu: 'CU-PRE-11 y 12',
            pestanas: ['pasos.pestana.descripcionTecnica', 'pasos.pestana.localizacion'],
            rutas: ['estudio-tecnico'],
          },
          { clave: 'ambiental', codigo: arbol(1, 3, 2, 3), texto: 'pasos.ambiental', cu: 'CU-PRE-14', rutas: ['analisis-ambiental'] },
          { clave: 'riesgos', codigo: arbol(1, 3, 2, 4), texto: 'pasos.riesgos', cu: 'CU-PRE-15', rutas: ['analisis-riesgo'] },
          { clave: 'legal', codigo: arbol(1, 3, 2, 5), texto: 'pasos.legal', cu: 'CU-PRE-16', rutas: ['analisis-legal'] },
          { clave: 'presupuesto-inversion', codigo: arbol(1, 3, 2, 6), texto: 'pasos.presupuestoInversion', cu: 'CU-PRE-17', rutas: ['presupuesto'] },
          { clave: 'presupuesto-operacion', codigo: arbol(1, 3, 2, 7), texto: 'pasos.presupuestoOperacion', cu: 'CU-PRE-18', rutas: ['presupuesto-om'] },
        ],
      },
      {
        codigo: '1.3.3',
        texto: 'pasos.seccion.evaluacion',
        pasos: [
          { clave: 'beneficios', codigo: arbol(1, 3, 3, 1), texto: 'pasos.beneficios', cu: 'CU-PRE-20' },
          { clave: 'flujo-socioeconomico', codigo: arbol(1, 3, 3, 2), texto: 'pasos.flujoSocioeconomico', cu: 'CU-PRE-21' },
          { clave: 'flujo-financiero', codigo: arbol(1, 3, 3, 3), texto: 'pasos.flujoFinanciero', cu: 'CU-PRE-21.5' },
        ],
      },
    ],
  },
  {
    clave: 'programacion',
    codigo: '1.4',
    texto: 'pasos.grupo.programacion',
    ruta: '/preinversion/programacion-proyecto',
    secciones: [
      {
        pasos: [
          {
            clave: 'indicadores',
            codigo: '1.4.1',
            texto: 'pasos.indicadores',
            cu: 'CU-PRE-23',
            pestanas: ['pasos.pestana.indicadoresResultado', 'pasos.pestana.indicadoresProducto'],
          },
          { clave: 'programacion-financiera', codigo: '1.4.2', texto: 'pasos.programacionFinanciera', cu: 'CU-PRE-22.1' },
        ],
      },
    ],
  },
  {
    clave: 'gestion',
    codigo: '1.5',
    texto: 'pasos.grupo.gestion',
    ruta: '/preinversion/gestion-proyecto',
    secciones: [
      {
        pasos: [
          { clave: 'viabilidad', codigo: '1.5.1', texto: 'pasos.viabilidad', cu: 'CU-PRE-24' },
          { clave: 'elegibilidad', codigo: '1.5.2', texto: 'pasos.elegibilidad', cu: 'CU-PRE-25' },
          { clave: 'opinion-tecnica', codigo: '1.5.3', texto: 'pasos.opinionTecnica', cu: 'CU-PRE-26' },
        ],
      },
    ],
  },
];

/** Los pasos de un grupo, en orden, sin las secciones. */
export const pasosDe = (grupo: GrupoPasos): PasoProyecto[] => grupo.secciones.flatMap((s) => s.pasos);

export const esClaveGrupo = (valor: string | null): valor is ClaveGrupo =>
  GRUPOS_PASOS.some((g) => g.clave === valor);

const RUTA_DE_PASO = /^\/preinversion\/proyectos\/(\d+)\/([^/?#]+)/;
const raizProyecto = (idProyecto: number) => `/preinversion/proyectos/${idProyecto}`;

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
    const paso = pasosDe(grupo).find((p) => p.rutas?.includes(sufijo));
    if (paso) return { idProyecto: Number(id), grupo, paso };
  }
  return null;
}

/** Destino del enlace de un paso, o null si el paso aún no tiene pantalla. */
export function rutaDePaso(idProyecto: number, paso: PasoProyecto): string | null {
  const primera = paso.rutas?.[0];
  return primera ? `${raizProyecto(idProyecto)}/${primera}` : null;
}

/**
 * Adónde lleva elegir un proyecto desde la opción de menú de un proceso: al
 * primer capítulo del grupo que ya tenga pantalla. Si el grupo todavía no tiene
 * ninguna, a Selección de la etapa con ?grupo=, que abre la barra en ese proceso
 * para que al menos se vean sus capítulos.
 */
export function entradaDeGrupo(idProyecto: number, clave: ClaveGrupo): string {
  const grupo = GRUPOS_PASOS.find((g) => g.clave === clave) ?? GRUPOS_PASOS[0];
  const conPantalla = pasosDe(grupo).find((p) => p.rutas?.length);
  if (conPantalla?.rutas) return `${raizProyecto(idProyecto)}/${conPantalla.rutas[0]}`;
  const inicial = pasosDe(GRUPOS_PASOS[0])[0].rutas?.[0] ?? 'etapas';
  return `${raizProyecto(idProyecto)}/${inicial}?grupo=${grupo.clave}`;
}
