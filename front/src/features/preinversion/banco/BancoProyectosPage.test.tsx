import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';

const listarBancoProyectos = vi.fn();
const navigate = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  bancoProyectosApi: { listarBancoProyectos: (...a: unknown[]) => listarBancoProyectos(...a) },
}));
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

const { BancoProyectosPage } = await import('./BancoProyectosPage');

const RESPUESTA = {
  data: {
    contenido: [
      { idProyecto: 4, cup: '10001', nombreProyecto: 'Hospital de Santa Ana', etapa: 'Factibilidad', inversionEstimada: 12500000, estado: 'Proyecto con OT' },
      { idProyecto: 5, cup: '10002', nombreProyecto: 'Carretera Longitudinal', etapa: 'Diseño', inversionEstimada: null, estado: 'En formulación' },
    ],
    paginacion: { pagina: 0, tamanio: 20, totalElementos: 2, totalPaginas: 1 },
  },
};

const montar = () => render(<MemoryRouter><BancoProyectosPage /></MemoryRouter>);

beforeEach(() => {
  listarBancoProyectos.mockReset().mockResolvedValue(RESPUESTA);
  navigate.mockReset();
});

describe('Banco de Proyectos (CU-PRE-29)', () => {
  it('lista los proyectos con su etapa, inversión y estado', async () => {
    montar();
    expect(await screen.findByText('Hospital de Santa Ana')).toBeInTheDocument();
    expect(screen.getByText('Factibilidad')).toBeInTheDocument();
    expect(screen.getByText(/12,500,000/)).toBeInTheDocument();
    // Sin inversión estimada la celda no queda en blanco.
    expect(screen.getAllByText('—').length).toBeGreaterThan(0);
  });

  // RN02: la búsqueda filtra por CUP o por nombre, y la resuelve el servidor.
  it('busca por CUP o nombre contra el servidor', async () => {
    montar();
    await screen.findByText('Hospital de Santa Ana');

    fireEvent.change(screen.getByLabelText('Buscar'), { target: { value: '10001' } });
    fireEvent.click(screen.getByRole('button', { name: 'Buscar' }));

    await waitFor(() =>
      expect(listarBancoProyectos).toHaveBeenLastCalledWith(expect.objectContaining({ busqueda: '10001' })),
    );
  });

  it('el nombre abre la ficha del proyecto', async () => {
    montar();
    fireEvent.click(await screen.findByText('Hospital de Santa Ana'));
    expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/4');
  });

  it('un fallo del servidor se ve como error, no como listado vacío', async () => {
    listarBancoProyectos.mockRejectedValue(new Error('falla'));
    montar();
    expect(await screen.findByRole('alert')).toBeInTheDocument();
    expect(screen.queryByText('No hay proyectos que mostrar.')).not.toBeInTheDocument();
  });
});
