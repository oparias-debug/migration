import { beforeEach, describe, expect, it, vi } from 'vitest';

const consultarCatalogoPorCodigo = vi.fn();
vi.mock('../../../api/administracionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/administracionApi')>()),
  catalogosApi: { consultarCatalogoPorCodigo: (...a: unknown[]) => consultarCatalogoPorCodigo(...a) },
}));

const { cadenaDeAncestros } = await import('./cadenaDeCatalogos');

/** REGIÓN → DEPARTAMENTO → DISTRITO, el ejemplo del 30/09/2026. */
const ARBOL: Record<string, { code: string; name: string; parent: string | null }> = {
  REGION: { code: 'REGION', name: 'Región', parent: null },
  DEPARTAMENTO: { code: 'DEPARTAMENTO', name: 'Departamento', parent: 'REGION' },
  DISTRITO: { code: 'DISTRITO', name: 'Distrito', parent: 'DEPARTAMENTO' },
};

beforeEach(() => {
  consultarCatalogoPorCodigo.mockReset();
  consultarCatalogoPorCodigo.mockImplementation(({ code }: { code: string }) =>
    ARBOL[code] ? Promise.resolve({ data: ARBOL[code] }) : Promise.reject(new Error('no existe')),
  );
});

describe('cadena de catálogos por encima de uno', () => {
  it('va del más alto al padre directo', async () => {
    expect(await cadenaDeAncestros('DEPARTAMENTO')).toEqual([
      { code: 'REGION', name: 'Región' },
      { code: 'DEPARTAMENTO', name: 'Departamento' },
    ]);
  });

  it('sin padre no hay cadena', async () => {
    expect(await cadenaDeAncestros(null)).toEqual([]);
    expect(await cadenaDeAncestros('   ')).toEqual([]);
    expect(consultarCatalogoPorCodigo).not.toHaveBeenCalled();
  });

  /**
   * El padre se escribe a mano en la ficha, así que nada impide dejar un ciclo.
   * Sin la cuenta de visitados esto no terminaría.
   */
  it('un ciclo no la deja dando vueltas', async () => {
    consultarCatalogoPorCodigo.mockImplementation(({ code }: { code: string }) =>
      Promise.resolve({ data: { code, name: code, parent: code === 'A' ? 'B' : 'A' } }),
    );
    const cadena = await cadenaDeAncestros('A');
    expect(cadena.map((c) => c.code)).toEqual(['B', 'A']);
  });

  it('un catálogo que se apunta a sí mismo tampoco', async () => {
    consultarCatalogoPorCodigo.mockResolvedValue({ data: { code: 'A', name: 'A', parent: 'A' } });
    expect(await cadenaDeAncestros('A')).toEqual([{ code: 'A', name: 'A' }]);
  });

  // Una miga de pan incompleta sigue sirviendo.
  it('si un eslabón no se puede leer, devuelve lo reunido', async () => {
    expect(await cadenaDeAncestros('DESCONOCIDO')).toEqual([]);
    consultarCatalogoPorCodigo.mockImplementation(({ code }: { code: string }) =>
      code === 'DEPARTAMENTO'
        ? Promise.resolve({ data: { code, name: 'Departamento', parent: 'ROTO' } })
        : Promise.reject(new Error('no existe')),
    );
    expect(await cadenaDeAncestros('DEPARTAMENTO')).toEqual([{ code: 'DEPARTAMENTO', name: 'Departamento' }]);
  });
});
