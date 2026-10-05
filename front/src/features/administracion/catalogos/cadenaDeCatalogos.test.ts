import { beforeEach, describe, expect, it, vi } from 'vitest';

const consultarCatalogo = vi.fn();
vi.mock('../../../api/administracionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/administracionApi')>()),
  catalogosApi: { consultarCatalogo: (...a: unknown[]) => consultarCatalogo(...a) },
}));

const { cadenaDeAncestros } = await import('./cadenaDeCatalogos');

/** REGIÓN → DEPARTAMENTO → DISTRITO, el ejemplo del 30/09/2026. */
const ARBOL: Record<string, { codigo: string; nombre: string; padre: string | null }> = {
  REGION: { codigo: 'REGION', nombre: 'Región', padre: null },
  DEPARTAMENTO: { codigo: 'DEPARTAMENTO', nombre: 'Departamento', padre: 'REGION' },
  DISTRITO: { codigo: 'DISTRITO', nombre: 'Distrito', padre: 'DEPARTAMENTO' },
};

beforeEach(() => {
  consultarCatalogo.mockReset();
  consultarCatalogo.mockImplementation(({ codigo }: { codigo: string }) =>
    ARBOL[codigo] ? Promise.resolve({ data: ARBOL[codigo] }) : Promise.reject(new Error('no existe')),
  );
});

describe('cadena de catálogos por encima de uno', () => {
  it('va del más alto al padre directo', async () => {
    expect(await cadenaDeAncestros('DEPARTAMENTO')).toEqual([
      { codigo: 'REGION', nombre: 'Región' },
      { codigo: 'DEPARTAMENTO', nombre: 'Departamento' },
    ]);
  });

  it('sin padre no hay cadena', async () => {
    expect(await cadenaDeAncestros(null)).toEqual([]);
    expect(await cadenaDeAncestros('   ')).toEqual([]);
    expect(consultarCatalogo).not.toHaveBeenCalled();
  });

  /**
   * El padre se escribe a mano en la ficha, así que nada impide dejar un ciclo.
   * Sin la cuenta de visitados esto no terminaría.
   */
  it('un ciclo no la deja dando vueltas', async () => {
    consultarCatalogo.mockImplementation(({ codigo }: { codigo: string }) =>
      Promise.resolve({ data: { codigo, nombre: codigo, padre: codigo === 'A' ? 'B' : 'A' } }),
    );
    const cadena = await cadenaDeAncestros('A');
    expect(cadena.map((c) => c.codigo)).toEqual(['B', 'A']);
  });

  it('un catálogo que se apunta a sí mismo tampoco', async () => {
    consultarCatalogo.mockResolvedValue({ data: { codigo: 'A', nombre: 'A', padre: 'A' } });
    expect(await cadenaDeAncestros('A')).toEqual([{ codigo: 'A', nombre: 'A' }]);
  });

  // Una miga de pan incompleta sigue sirviendo.
  it('si un eslabón no se puede leer, devuelve lo reunido', async () => {
    expect(await cadenaDeAncestros('DESCONOCIDO')).toEqual([]);
    consultarCatalogo.mockImplementation(({ codigo }: { codigo: string }) =>
      codigo === 'DEPARTAMENTO'
        ? Promise.resolve({ data: { codigo, nombre: 'Departamento', padre: 'ROTO' } })
        : Promise.reject(new Error('no existe')),
    );
    expect(await cadenaDeAncestros('DEPARTAMENTO')).toEqual([{ codigo: 'DEPARTAMENTO', nombre: 'Departamento' }]);
  });
});
