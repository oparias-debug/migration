import { describe, expect, it } from 'vitest';
import { MODULOS, destinoDe, ubicarEnMenu } from './navegacion';

const conRoles = (...roles: string[]) => (rol: string) => roles.includes(rol);
const sub = (clave: string) => MODULOS.flatMap((m) => m.submenu ?? []).find((s) => s.clave === clave)!;

describe('menú según el árbol del sistema', () => {
  it('sigue el orden del árbol, sin Búsquedas', () => {
    expect(MODULOS.map((m) => m.clave)).toEqual([
      'inicio', 'banco', 'preinversion', 'programacion', 'ejecucion', 'seguimiento', 'convenios', 'reportes', 'admin',
    ]);
  });

  it('Preinversión tiene los procesos 1.1 a 1.5', () => {
    expect(MODULOS.find((m) => m.clave === 'preinversion')?.submenu?.map((s) => s.clave)).toEqual([
      'asignacion-cup', 'creacion-ruta', 'formulacion', 'programacion-proyecto', 'gestion-proyecto',
    ]);
  });

  it('Programación empieza por Priorización y tiene el PAP', () => {
    expect(MODULOS.find((m) => m.clave === 'programacion')?.submenu?.map((s) => s.clave)).toEqual([
      'priorizacion', 'pap', 'pripme', 'paip',
    ]);
  });
});

// Medido contra el back el 18/09/2026: el resto de los roles del realm recibe 401
// en los endpoints de preinversión, así que verían opciones que no llevan a nada.
describe('quién ve Preinversión', () => {
  const preinversion = MODULOS.find((m) => m.clave === 'preinversion')!;

  it('todas sus opciones exigen rol', () => {
    expect(preinversion.submenu?.every((s) => s.rolesRequeridos?.length)).toBe(true);
  });

  it('la abren los roles que el back acepta', () => {
    for (const rol of ['TECNICO_URP', 'TECNICO_PRE', 'COORDINADOR_PRE', 'ADMINISTRADOR']) {
      expect(preinversion.submenu?.every((s) => s.rolesRequeridos?.includes(rol))).toBe(true);
    }
  });

  it('no la ven los roles a los que el back responde 401', () => {
    for (const rol of ['VIABILIZADOR', 'TECNICO_LEGAL', 'TECNICO_PROG', 'TECNICO_SYMP', 'JEFE_DGI', 'SUBJEFE_DGI']) {
      expect(preinversion.submenu?.some((s) => s.rolesRequeridos?.includes(rol))).toBe(false);
    }
  });
});

describe('destinoDe', () => {
  it('Asignación CUP lleva al Técnico URP a Registro de Proyecto', () => {
    expect(destinoDe(sub('asignacion-cup'), conRoles('TECNICO_URP'))).toBe('/preinversion/proyectos');
  });

  it('y a la DGICP a la Bandeja', () => {
    expect(destinoDe(sub('asignacion-cup'), conRoles('COORDINADOR_PRE'))).toBe('/preinversion/bandeja');
    expect(destinoDe(sub('asignacion-cup'), conRoles('TECNICO_PRE'))).toBe('/preinversion/bandeja');
  });

  it('sin rol preferente, lleva al destino por defecto', () => {
    expect(destinoDe(sub('asignacion-cup'), conRoles('ADMINISTRADOR'))).toBe('/preinversion/proyectos');
    expect(destinoDe(sub('formulacion'), conRoles('TECNICO_URP'))).toBe('/preinversion/formulacion');
  });
});

describe('ubicarEnMenu', () => {
  it('Inicio', () => {
    expect(ubicarEnMenu('/')).toMatchObject({ modulo: { clave: 'inicio' }, sub: null });
  });

  it('las dos pantallas de Asignación CUP marcan la misma opción, cada una con su título', () => {
    expect(ubicarEnMenu('/preinversion/proyectos')).toMatchObject({
      sub: { clave: 'asignacion-cup' },
      titulo: 'menu.registroProyecto',
    });
    expect(ubicarEnMenu('/preinversion/bandeja')).toMatchObject({ sub: { clave: 'asignacion-cup' }, titulo: 'menu.bandeja' });
  });

  it('la ficha de un proyecto es un detalle de Registro de Proyecto', () => {
    const u = ubicarEnMenu('/preinversion/proyectos/4');
    expect(u).toMatchObject({ sub: { clave: 'asignacion-cup' }, titulo: 'menu.registroProyecto' });
    expect(u?.tramos.at(-1)).toEqual({ texto: 'ruta.detalle' });
  });

  it('un paso cuelga del proceso de su grupo, no de Registro de Proyecto', () => {
    expect(ubicarEnMenu('/preinversion/proyectos/4/identificacion')).toMatchObject({
      sub: { clave: 'formulacion' },
      titulo: 'pasos.identificacion',
    });
    expect(ubicarEnMenu('/preinversion/proyectos/4/etapas')).toMatchObject({ sub: { clave: 'creacion-ruta' } });
  });

  it('la ruta vieja de Captura cuelga de Creación ruta de preinversión', () => {
    expect(ubicarEnMenu('/preinversion/captura')).toMatchObject({ sub: { clave: 'creacion-ruta' }, titulo: 'menu.captura' });
  });

  it('módulos sin submenú, como Banco de Proyectos', () => {
    expect(ubicarEnMenu('/banco-proyectos')).toMatchObject({ modulo: { clave: 'banco' }, sub: null });
  });

  it('una ruta que no está en el menú', () => {
    expect(ubicarEnMenu('/no-existe')).toBeNull();
  });
  it('Catálogos sólo aparece para el rol que el back del CU-ADM-01 acepta', () => {
    const admin = MODULOS.find((m) => m.clave === 'admin');
    const catalogos = admin?.submenu?.find((s) => s.clave === 'catalogos');
    expect(catalogos?.rolesRequeridos).toEqual(['ADMINISTRADOR_DE_CATALOGOS']);
  });
});
