import { describe, expect, it } from 'vitest';
import { contenidoAplica, pasoAplica, casosDeUsoDe } from './contenidoIniciativa';
import { GRUPOS_PASOS, pasosDe, type PasoProyecto } from './pasosProyecto';
import { ANEXO_F } from './anexoF.fixture';

const capitulos = GRUPOS_PASOS.flatMap((g) => pasosDe(g));
const capitulo = (clave: string) => capitulos.find((p) => p.clave === clave) as PasoProyecto;
const seFormula = (iniciativa: 'PROGRAMA' | 'PROYECTO' | 'ESTUDIO_GENERAL') => (paso: PasoProyecto) =>
  pasoAplica(paso, (cu) => contenidoAplica(ANEXO_F, iniciativa, cu));

/** Los capítulos con pantalla que se formulan en esa iniciativa. */
const conPantalla = (iniciativa: 'PROGRAMA' | 'PROYECTO' | 'ESTUDIO_GENERAL') =>
  capitulos.filter((p) => p.rutas?.length && seFormula(iniciativa)(p)).map((p) => p.clave);

describe('qué se formula en cada iniciativa (Anexo F)', () => {
  // La lista que pidió el cliente el 21/09/2026 para un programa: 1.1 a 1.4,
  // 2.2, 2.6, 2.7, 2.10, 2.12, 2.13, 2.14 y los indicadores.
  it('un programa no formula alternativas, ambiental, legal ni beneficios', () => {
    expect(conPantalla('PROGRAMA')).toEqual([
      'seleccion-etapa',
      'identificacion',
      'diagnostico',
      'estudio-tecnico',
      'riesgos',
      'presupuesto-inversion',
      'presupuesto-operacion',
      'viabilidad',
    ]);
  });

  it('un estudio general tampoco formula riesgos ni operación y mantenimiento', () => {
    expect(conPantalla('ESTUDIO_GENERAL')).toEqual([
      'seleccion-etapa',
      'identificacion',
      'diagnostico',
      'estudio-tecnico',
      'presupuesto-inversion',
      'viabilidad',
    ]);
  });

  it('un proyecto formula todos los capítulos que tienen pantalla', () => {
    expect(conPantalla('PROYECTO')).toHaveLength(capitulos.filter((p) => p.rutas?.length).length);
  });

  // Los dos capítulos que agrupan varios contenidos del anexo.
  it('el diagnóstico se abre si al menos una de sus cuatro pestañas aplica', () => {
    expect(casosDeUsoDe(capitulo('diagnostico'))).toEqual(['CU-PRE-06', 'CU-PRE-07', 'CU-PRE-08', 'CU-PRE-09']);
    // En un programa sólo aplica Análisis de la Población (CU-PRE-07).
    expect(contenidoAplica(ANEXO_F, 'PROGRAMA', 'CU-PRE-07')).toBe(true);
    for (const cu of ['CU-PRE-06', 'CU-PRE-08', 'CU-PRE-09']) {
      expect(contenidoAplica(ANEXO_F, 'PROGRAMA', cu)).toBe(false);
    }
    expect(seFormula('PROGRAMA')(capitulo('diagnostico'))).toBe(true);
  });

  // El anexo escribe los casos de uso sin el segundo guion.
  it('CU-PRE-22.1 y CUPRE-22.1 son el mismo caso de uso', () => {
    expect(contenidoAplica(ANEXO_F, 'ESTUDIO_GENERAL', 'CU-PRE-22.1')).toBe(true);
    expect(contenidoAplica(ANEXO_F, 'PROGRAMA', 'CU-PRE-22.1')).toBe(false);
  });

  // Un caso de uso con dos filas: Presupuesto de Inversión y Fuentes de Financiamiento.
  it('basta que una de las filas del caso de uso aplique', () => {
    const filas = ANEXO_F.filter((f) => f.ubicacionCasoUso === 'CUPRE-17');
    expect(filas).toHaveLength(2);
    expect(contenidoAplica(ANEXO_F, 'PROGRAMA', 'CU-PRE-17')).toBe(true);
  });
});

// Esconder un capítulo es quitarle trabajo a quien formula: sólo se esconde
// cuando el anexo lo dice.
describe('sin respuesta del anexo se muestra todo', () => {
  it('sin catálogo cargado todavía', () => {
    expect(contenidoAplica(null, 'PROGRAMA', 'CU-PRE-05')).toBe(true);
  });

  it('sin iniciativa conocida', () => {
    expect(contenidoAplica(ANEXO_F, null, 'CU-PRE-05')).toBe(true);
  });

  // Al catálogo del servidor le faltan 4 de las 30 filas del anexo; sus casos de
  // uso no pueden desaparecer de la pantalla por eso.
  it('con un caso de uso que el catálogo no menciona', () => {
    expect(ANEXO_F.some((f) => f.ubicacionCasoUso === 'CUPRE-13')).toBe(false);
    expect(contenidoAplica(ANEXO_F, 'PROGRAMA', 'CU-PRE-13')).toBe(true);
    // Y los capítulos que el anexo no numera, como la Ruta de Preinversión.
    expect(contenidoAplica(ANEXO_F, 'PROGRAMA', 'CU-PRE-03.5')).toBe(true);
  });
});
