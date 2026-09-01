import { ROLES_CON_ACCESO_REGISTRO_PROYECTO } from '../features/preinversion/proyectos/proyectoLabels';

/**
 * Estructura del menú lateral.
 *
 * Reproduce los nueve módulos del diseño aprobado (siip-INICIO-NUEVA-GRIS),
 * pero cuelga de ellos las RUTAS REALES de esta aplicación: no se inventa
 * ningún destino ni se retira ninguno de los que ya existían en el sidebar
 * anterior. Todas están registradas en App.tsx — las que aún no tienen
 * pantalla caen en PlaceholderPage.
 */
export interface SubModulo {
  clave: string;
  texto: string;
  ruta: string;
  /** Si se indica, el ítem sólo se pinta cuando el usuario tiene alguno de estos roles. */
  rolesRequeridos?: readonly string[];
}

export interface Modulo {
  clave: string;
  icono: string;
  texto: string;
  ruta?: string;
  submenu?: SubModulo[];
}

export const MODULOS: readonly Modulo[] = [
  { clave: 'inicio', icono: 'menu-inicio', texto: 'menu.inicio', ruta: '/' },
  {
    clave: 'admin',
    icono: 'menu-administracion',
    texto: 'menu.administracion',
    submenu: [
      { clave: 'catalogos-generales', texto: 'menu.catalogosGenerales', ruta: '/catalogos-generales' },
      { clave: 'tablas-rangos', texto: 'menu.tablasRangos', ruta: '/tablas-rangos' },
      { clave: 'usuarios', texto: 'menu.usuarios', ruta: '/usuarios' },
    ],
  },
  { clave: 'busquedas', icono: 'menu-busquedas', texto: 'menu.busquedas' },
  { clave: 'banco', icono: 'menu-banco', texto: 'menu.banco' },
  {
    clave: 'preinversion',
    icono: 'menu-preinversion',
    texto: 'menu.preinversion',
    ruta: '/preinversion/proyectos',
    submenu: [
      // Único ítem con control de rol en el sidebar anterior; se conserva igual.
      {
        clave: 'registro-proyecto',
        texto: 'menu.registroProyecto',
        ruta: '/preinversion/proyectos',
        rolesRequeridos: ROLES_CON_ACCESO_REGISTRO_PROYECTO,
      },
      { clave: 'programacion-pre', texto: 'menu.programacion', ruta: '/programacion' },
      { clave: 'seguimiento-pre', texto: 'menu.seguimiento', ruta: '/seguimiento' },
    ],
  },
  {
    clave: 'programacion',
    icono: 'menu-programacion',
    texto: 'menu.programacion',
    submenu: [
      { clave: 'ingreso', texto: 'menu.ingreso', ruta: '/ingreso' },
      { clave: 'pripme', texto: 'menu.pripme', ruta: '/pripme' },
    ],
  },
  { clave: 'ejecucion', icono: 'menu-ejecucion', texto: 'menu.ejecucion' },
  {
    clave: 'seguimiento',
    icono: 'menu-seguimiento',
    texto: 'menu.seguimiento',
    submenu: [
      { clave: 'financiero', texto: 'menu.financiero', ruta: '/financiero' },
      { clave: 'geografico', texto: 'menu.geografico', ruta: '/geografico' },
      { clave: 'fisico', texto: 'menu.fisico', ruta: '/fisico' },
      { clave: 'procesos', texto: 'menu.procesos', ruta: '/procesos' },
    ],
  },
  { clave: 'reportes', icono: 'menu-reportes', texto: 'menu.reportes' },
];
