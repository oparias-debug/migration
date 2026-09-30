import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';

const navigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

const { BotonRegresar } = await import('./BotonRegresar');
const { ContextoContenido, contenidoAplica } = await import('./contenidoIniciativa');
const { ANEXO_F } = await import('./anexoF.fixture');

const montar = (paso: string, iniciativa?: 'PROGRAMA') =>
  render(
    <MemoryRouter>
      <ContextoContenido.Provider value={iniciativa ? (cu) => contenidoAplica(ANEXO_F, iniciativa, cu) : () => true}>
        <BotonRegresar idProyecto={7} paso={paso} />
      </ContextoContenido.Provider>
    </MemoryRouter>,
  );

beforeEach(() => navigate.mockReset());

const CASOS: { caso: string; paso: string; iniciativa?: 'PROGRAMA'; destino: string }[] = [
  { caso: 'sigue el orden del árbol', paso: 'legal', destino: '/preinversion/proyectos/7/analisis-riesgo' },
  // Antes cada capítulo llevaba su propio destino y el primero terminaba en el
  // formulario de solicitud de CUP, que es de otro proceso.
  {
    caso: 'desde el primer capítulo vuelve a la Ruta de Preinversión',
    paso: 'seleccion-etapa',
    destino: '/preinversion/proyectos/7/ruta-preinversion',
  },
  {
    caso: 'en un proyecto no salta ningún capítulo',
    paso: 'diagnostico',
    destino: '/preinversion/proyectos/7/alternativas-solucion',
  },
  // Alternativas de Solución queda en medio y no se formula en un programa.
  {
    caso: 'en un programa salta los capítulos que no se formulan',
    paso: 'diagnostico',
    iniciativa: 'PROGRAMA',
    destino: '/preinversion/proyectos/7/identificacion',
  },
];

describe('BotonRegresar', () => {
  it.each(CASOS)('$caso', ({ paso, iniciativa, destino }) => {
    montar(paso, iniciativa);
    fireEvent.click(screen.getByRole('button', { name: 'Regresar' }));
    expect(navigate).toHaveBeenCalledWith(destino);
  });
});
