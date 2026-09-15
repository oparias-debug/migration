import type { Tramo } from './BandaRuta';
import { ubicarPaso } from '../features/preinversion/pasos/pasosProyecto';

/**
 * Estructura del menú lateral, según el árbol del sistema que envió el cliente el
 * 14/09/2026 y las decisiones de ese día: Banco de Proyectos como opción propia,
 * Priorización y PAP en Programación, Reportes se queda y Búsquedas sale porque
 * el árbol no la tiene. Administración no está en el árbol, pero el sistema la
 * necesita (Catálogos ya existe).
 *
 * Dos niveles: módulo (columna B) y proceso (columna C). Lo que trabaja sobre un
 * proyecto no está aquí: son los pasos de PasosProyectoLayout.
 *
 * Las rutas sin pantalla todavía caen en PlaceholderPage (ver App.tsx).
 */
export interface Pantalla {
  readonly ruta: string;
  /** Clave de i18n del título de esa pantalla. */
  readonly texto: string;
  /** Si el usuario tiene alguno de estos roles, la opción del menú lleva aquí. */
  readonly rolesPreferentes?: readonly string[];
}

export interface SubModulo {
  readonly clave: string;
  readonly texto: string;
  /** Destino por defecto de la opción. */
  readonly ruta: string;
  /**
   * Cuando un proceso del árbol reúne varias pantallas. La opción del menú lleva a
   * la preferente para el rol del usuario, y todas marcan la opción como activa.
   */
  readonly pantallas?: readonly Pantalla[];
  /** Si se indica, el ítem sólo se pinta cuando el usuario tiene alguno de estos roles. */
  readonly rolesRequeridos?: readonly string[];
}

export interface Modulo {
  readonly clave: string;
  readonly icono: string;
  readonly texto: string;
  readonly ruta?: string;
  readonly submenu?: readonly SubModulo[];
}

export const MODULOS: readonly Modulo[] = [
  { clave: 'inicio', icono: 'menu-inicio', texto: 'menu.inicio', ruta: '/' },
  // 0. BANCO DE PROYECTOS (CU-PRE-29)
  { clave: 'banco', icono: 'menu-banco', texto: 'menu.banco', ruta: '/banco-proyectos' },
  // 1. PREINVERSIÓN
  {
    clave: 'preinversion',
    icono: 'menu-preinversion',
    texto: 'menu.preinversion',
    submenu: [
      // 1.1 Asignación CUP: el Técnico URP crea la solicitud (Registro de Proyecto)
      // y la DGICP asigna y aprueba (Bandeja). Cada rol entra por la suya.
      {
        clave: 'asignacion-cup',
        texto: 'menu.asignacionCup',
        ruta: '/preinversion/proyectos',
        pantallas: [
          { ruta: '/preinversion/proyectos', texto: 'menu.registroProyecto', rolesPreferentes: ['TECNICO_URP'] },
          { ruta: '/preinversion/bandeja', texto: 'menu.bandeja', rolesPreferentes: ['COORDINADOR_PRE', 'TECNICO_PRE'] },
        ],
      },
      // 1.2 a 1.5 trabajan sobre un proyecto: cada opción abre la lista de
      // proyectos con CUP y, al elegir uno, se entra a sus pasos.
      {
        clave: 'creacion-ruta',
        texto: 'menu.creacionRuta',
        ruta: '/preinversion/creacion-ruta',
        // La ruta vieja de Captura sigue funcionando y cuelga de aquí.
        pantallas: [
          { ruta: '/preinversion/creacion-ruta', texto: 'menu.creacionRuta' },
          { ruta: '/preinversion/captura', texto: 'menu.captura' },
        ],
      },
      { clave: 'formulacion', texto: 'menu.formulacionEvaluacion', ruta: '/preinversion/formulacion' },
      { clave: 'programacion-proyecto', texto: 'menu.programacionProyecto', ruta: '/preinversion/programacion-proyecto' },
      {
        clave: 'gestion-proyecto',
        texto: 'menu.gestionProyecto',
        ruta: '/preinversion/gestion-proyecto',
        // La entrada a Opinión Técnica desde la Bandeja es 1.5.3 del árbol.
        pantallas: [
          { ruta: '/preinversion/gestion-proyecto', texto: 'menu.gestionProyecto' },
          { ruta: '/preinversion/opinion-tecnica', texto: 'pasos.opinionTecnica' },
        ],
      },
    ],
  },
  // 2. PROGRAMACIÓN
  {
    clave: 'programacion',
    icono: 'menu-programacion',
    texto: 'menu.programacion',
    submenu: [
      { clave: 'priorizacion', texto: 'menu.priorizacion', ruta: '/programacion/priorizacion' },
      { clave: 'pap', texto: 'menu.pap', ruta: '/programacion/pap' },
      { clave: 'pripme', texto: 'menu.pripme', ruta: '/programacion/pripme' },
      { clave: 'paip', texto: 'menu.paip', ruta: '/programacion/paip' },
    ],
  },
  // 3. EJECUCIÓN
  {
    clave: 'ejecucion',
    icono: 'menu-ejecucion',
    texto: 'menu.ejecucion',
    submenu: [{ clave: 'actualizaciones', texto: 'menu.actualizaciones', ruta: '/ejecucion/actualizaciones' }],
  },
  // 4. SEGUIMIENTO
  {
    clave: 'seguimiento',
    icono: 'menu-seguimiento',
    texto: 'menu.seguimiento',
    submenu: [
      { clave: 'seguimiento-proyectos', texto: 'menu.seguimientoProyectos', ruta: '/seguimiento/proyectos' },
      { clave: 'seguimiento-pap', texto: 'menu.seguimientoPap', ruta: '/seguimiento/pap' },
      { clave: 'seguimiento-paip', texto: 'menu.seguimientoPaip', ruta: '/seguimiento/paip' },
    ],
  },
  // 5. CONVENIOS
  {
    clave: 'convenios',
    icono: 'menu-convenios',
    texto: 'menu.convenios',
    submenu: [{ clave: 'gestion-convenios', texto: 'menu.gestionConvenios', ruta: '/convenios/gestion' }],
  },
  // 6. REPORTES
  { clave: 'reportes', icono: 'menu-reportes', texto: 'menu.reportes', ruta: '/reportes' },
  // Administración: no está en el árbol del sistema, pero el sistema la necesita.
  {
    clave: 'admin',
    icono: 'menu-administracion',
    texto: 'menu.administracion',
    submenu: [
      { clave: 'seguridad', texto: 'menu.seguridad', ruta: '/administracion/seguridad' },
      // Sólo para el rol que el back del CU-ADM-01 acepta; a cualquier otro le respondería 403.
      { clave: 'catalogos', texto: 'menu.catalogos', ruta: '/catalogos-generales', rolesRequeridos: ['ADMINISTRADOR_DE_CATALOGOS'] },
    ],
  },
];

/** Adónde lleva una opción del menú, según el rol del usuario. */
export function destinoDe(sub: SubModulo, hasRole: (rol: string) => boolean): string {
  const preferida = sub.pantallas?.find((p) => p.rolesPreferentes?.some(hasRole));
  return preferida?.ruta ?? sub.ruta;
}

export interface UbicacionMenu {
  readonly modulo: Modulo;
  readonly sub: SubModulo | null;
  /** Clave de i18n del título de la pantalla. */
  readonly titulo: string;
  readonly tramos: readonly Tramo[];
}

const coincide = (pathname: string, ruta: string) => pathname === ruta || pathname.startsWith(`${ruta}/`);
const pantallasDe = (sub: SubModulo): readonly Pantalla[] => sub.pantallas ?? [{ ruta: sub.ruta, texto: sub.texto }];

/** Un paso de proyecto cuelga de la opción de menú de su proceso. */
function ubicarComoPaso(pathname: string): UbicacionMenu | null {
  const paso = ubicarPaso(pathname);
  if (!paso) return null;
  for (const modulo of MODULOS) {
    const sub = modulo.submenu?.find((s) => s.ruta === paso.grupo.ruta);
    if (sub) {
      return {
        modulo,
        sub,
        titulo: paso.paso.texto,
        tramos: [{ texto: modulo.texto }, { texto: sub.texto, ruta: sub.ruta }, { texto: paso.paso.texto }],
      };
    }
  }
  return null;
}

/** Pantallas del menú: gana la ruta más larga que coincida. */
function ubicarComoPantalla(pathname: string): UbicacionMenu | null {
  let mejor: { modulo: Modulo; sub: SubModulo; pantalla: Pantalla } | null = null;
  for (const modulo of MODULOS) {
    for (const sub of modulo.submenu ?? []) {
      const pantalla = pantallasDe(sub).find((p) => coincide(pathname, p.ruta));
      if (pantalla && (!mejor || pantalla.ruta.length > mejor.pantalla.ruta.length)) {
        mejor = { modulo, sub, pantalla };
      }
    }
  }
  if (!mejor) return null;
  const { modulo, sub, pantalla } = mejor;
  const base: Tramo[] = [{ texto: modulo.texto }];
  if (sub.texto !== pantalla.texto) base.push({ texto: sub.texto });
  if (pathname === pantalla.ruta) {
    return { modulo, sub, titulo: pantalla.texto, tramos: [...base, { texto: pantalla.texto }] };
  }
  // Detalle bajo un listado: alta o edición de un registro.
  const hoja = pathname.endsWith('/nuevo') ? 'ruta.nuevo' : 'ruta.detalle';
  return {
    modulo,
    sub,
    titulo: pantalla.texto,
    tramos: [...base, { texto: pantalla.texto, ruta: pantalla.ruta }, { texto: hoja }],
  };
}

/**
 * Dónde está la URL dentro del menú: módulo, opción, título y banda de ruta. Lo
 * usan el menú lateral y la barra superior, para que nunca discrepen.
 */
export function ubicarEnMenu(pathname: string): UbicacionMenu | null {
  const inicio = MODULOS[0];
  if (pathname === '/') return { modulo: inicio, sub: null, titulo: inicio.texto, tramos: [{ texto: inicio.texto }] };
  const ubicacion = ubicarComoPaso(pathname) ?? ubicarComoPantalla(pathname);
  if (ubicacion) return ubicacion;
  const suelto = MODULOS.find((m) => !m.submenu && m.ruta && m.ruta !== '/' && coincide(pathname, m.ruta));
  return suelto ? { modulo: suelto, sub: null, titulo: suelto.texto, tramos: [{ texto: suelto.texto }] } : null;
}
