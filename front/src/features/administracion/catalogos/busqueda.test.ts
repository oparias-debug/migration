import { describe, expect, it } from 'vitest';
import { coincide, normalizar } from './busqueda';

describe('búsqueda por fracciones de palabra', () => {
  // El ejemplo de la indicación del 30/09/2026.
  it('encuentra por el principio de la palabra', () => {
    expect(coincide('Cund', 'Cundinamarca')).toBe(true);
    expect(coincide('marca', 'Cundinamarca')).toBe(true);
    expect(coincide('Boyaca', 'Cundinamarca')).toBe(false);
  });

  it('ignora mayúsculas y tildes en los dos lados', () => {
    expect(coincide('jerarquico', 'CATÁLOGO JERÁRQUICO')).toBe(true);
    expect(coincide('JERÁRQUICO', 'catalogo jerarquico')).toBe(true);
    expect(normalizar('  Región  ')).toBe('region');
  });

  it('busca en cualquiera de los textos que se le den', () => {
    expect(coincide('TIPO', 'Tipos de documento', 'TIPO_DOCUMENTO')).toBe(true);
    expect(coincide('10002', 'Hospital', '10002')).toBe(true);
  });

  // Al borrar la caja vuelve todo.
  it('la búsqueda vacía no filtra nada', () => {
    expect(coincide('', 'lo que sea')).toBe(true);
    expect(coincide('   ', 'lo que sea')).toBe(true);
  });

  it('con varias palabras, todas tienen que aparecer, en cualquier orden', () => {
    expect(coincide('cund giradot', 'Cundinamarca', 'Giradot')).toBe(true);
    expect(coincide('giradot cund', 'Cundinamarca', 'Giradot')).toBe(true);
    expect(coincide('cund bogota', 'Cundinamarca', 'Giradot')).toBe(false);
  });

  it('no se cae con valores ausentes', () => {
    expect(coincide('a', null, undefined, 'casa')).toBe(true);
    expect(coincide('z', null, undefined)).toBe(false);
  });
});
