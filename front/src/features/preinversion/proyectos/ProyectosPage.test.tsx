import { beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { AxiosError, AxiosHeaders } from 'axios';
import { MemoryRouter } from 'react-router-dom';
import i18n from '../../../i18n/i18n';

const listarProyectos = vi.fn();

// Se conservan los exports reales (EstadoProyecto/IniciativaInversion los usa
// proyectoLabels); sólo se sustituye el cliente que sale a la red.
vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  preinversionApi: {
    listarProyectos: (...args: unknown[]) => listarProyectos(...args),
  },
}));

vi.mock('../../../auth/useAuth', () => ({
  useAuth: () => ({ hasRole: () => true }),
}));

const { ProyectosPage } = await import('./ProyectosPage');

const respuestaVacia = {
  data: { contenido: [], paginacion: { pagina: 0, tamanio: 20, totalElementos: 0, totalPaginas: 0 } },
};

const unProyecto = {
  data: {
    contenido: [
      {
        idProyecto: 1,
        nombre: 'Equipamiento del hospital regional de Santa Ana',
        unidadEjecutora: { idUnidadEjecutora: 4501, nombre: 'MINSAL' },
        iniciativaInversion: 'PROYECTO',
        fechaIngreso: '2026-08-18T09:00:00Z',
        estado: 'ENVIADO_DGICP_REGISTRO',
      },
    ],
    paginacion: { pagina: 0, tamanio: 20, totalElementos: 1, totalPaginas: 1 },
  },
};

function fallo(status: number) {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('fallo', String(status), config, null, {
    status,
    statusText: '',
    headers: {},
    config,
    data: undefined,
  });
}

const montar = () =>
  render(
    <MemoryRouter>
      <ProyectosPage />
    </MemoryRouter>,
  );

describe('ProyectosPage', () => {
  beforeEach(() => {
    listarProyectos.mockReset();
  });

  it('pide la primera página con el tamaño del contrato (base 0, 20 por página)', async () => {
    listarProyectos.mockResolvedValue(respuestaVacia);
    montar();
    await waitFor(() => expect(listarProyectos).toHaveBeenCalledWith({ pagina: 0, tamanio: 20 }));
  });

  it('muestra los proyectos que devuelve el back', async () => {
    listarProyectos.mockResolvedValue(unProyecto);
    montar();
    expect(await screen.findByText('Equipamiento del hospital regional de Santa Ana')).toBeInTheDocument();
    expect(screen.getByText('MINSAL')).toBeInTheDocument();
  });

  // El defecto que se corrige: antes, un 500 dejaba la tabla en "no hay
  // registros", indistinguible de un listado legítimamente vacío.
  it('un fallo del servidor se ve como error, no como listado vacío', async () => {
    listarProyectos.mockRejectedValue(fallo(500));
    montar();
    const aviso = await screen.findByRole('alert');
    expect(aviso).toHaveTextContent(i18n.t('errores.servidor'));
    expect(screen.queryByText(/no hay registros/i)).not.toBeInTheDocument();
  });

  it('distingue un fallo de red de un error del servidor', async () => {
    listarProyectos.mockRejectedValue(new AxiosError('Network Error'));
    montar();
    expect(await screen.findByRole('alert')).toHaveTextContent(i18n.t('errores.red'));
  });

  it('permite reintentar tras un fallo', async () => {
    listarProyectos.mockRejectedValueOnce(fallo(500)).mockResolvedValueOnce(unProyecto);
    montar();
    await screen.findByRole('alert');
    fireEvent.click(screen.getByRole('button', { name: 'Reintentar' }));
    expect(await screen.findByText('Equipamiento del hospital regional de Santa Ana')).toBeInTheDocument();
    expect(screen.queryByRole('alert')).not.toBeInTheDocument();
  });
});
