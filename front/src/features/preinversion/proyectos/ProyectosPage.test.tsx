import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { AxiosError, AxiosHeaders } from 'axios';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { ProyectosPage } from './ProyectosPage';

const listarProyectos = vi.fn();

// Se conservan los enums reales (proyectoLabels los importa de este mismo módulo);
// sólo se sustituyen los clientes que salen a la red.
vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return { ...actual, preinversionApi: { listarProyectos: (...args: unknown[]) => listarProyectos(...args) } };
});

vi.mock('../../../auth/useAuth', () => ({ useAuth: () => ({ hasRole: () => true }) }));

const PROYECTO = {
  idProyecto: 7,
  nombre: 'Construcción de puente sobre el río Lempa',
  unidadEjecutora: { idUnidadEjecutora: 1, nombre: 'Unidad de Inversión Pública' },
  iniciativaInversion: 'PROYECTO',
  fechaIngreso: '2026-02-10T09:00:00',
  estado: 'EN_REGISTRO',
};

function respuestaConUnProyecto() {
  return {
    data: {
      contenido: [PROYECTO],
      paginacion: { pagina: 0, tamanio: 20, totalElementos: 1, totalPaginas: 1 },
    },
  };
}

function error500() {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('Request failed', '500', config, {}, {
    status: 500,
    statusText: '',
    data: '',
    headers: {},
    config,
  });
}

function renderizar() {
  return render(
    <MemoryRouter>
      <ProyectosPage />
    </MemoryRouter>,
  );
}

beforeEach(() => {
  listarProyectos.mockReset();
});

describe('ProyectosPage', () => {
  it('lista los proyectos que devuelve el back', async () => {
    listarProyectos.mockResolvedValue(respuestaConUnProyecto());

    renderizar();

    expect(await screen.findByText('Construcción de puente sobre el río Lempa')).toBeInTheDocument();
    expect(screen.getByText('Unidad de Inversión Pública')).toBeInTheDocument();
    // El estado se muestra con la etiqueta del CU, no con el código del enum.
    expect(screen.getByText('En Elaboración')).toBeInTheDocument();
  });

  it('muestra el aviso de error, y no "sin registros", cuando falla el listado', async () => {
    listarProyectos.mockRejectedValue(error500());

    renderizar();

    expect(await screen.findByRole('alert')).toHaveTextContent(
      'Ocurrió un error inesperado. Intente nuevamente más tarde.',
    );
    // Un 500 no puede leerse como "la bandeja está vacía".
    expect(screen.queryByText('No hay proyectos registrados.')).not.toBeInTheDocument();
  });

  it('reintenta la carga al pulsar "Reintentar" y limpia el aviso al lograrlo', async () => {
    listarProyectos.mockRejectedValueOnce(error500()).mockResolvedValueOnce(respuestaConUnProyecto());

    renderizar();
    fireEvent.click(await screen.findByRole('button', { name: 'Reintentar' }));

    expect(await screen.findByText('Construcción de puente sobre el río Lempa')).toBeInTheDocument();
    await waitFor(() => expect(screen.queryByRole('alert')).not.toBeInTheDocument());
    expect(listarProyectos).toHaveBeenCalledTimes(2);
  });

  it('muestra el mensaje del back cuando el error lo trae (403 por rol)', async () => {
    const config = { headers: new AxiosHeaders() };
    listarProyectos.mockRejectedValue(
      new AxiosError('Request failed', '403', config, {}, {
        status: 403,
        statusText: '',
        data: { mensaje: 'El rol TECNICO_PRE no tiene permiso para realizar esta accion.' },
        headers: {},
        config,
      }),
    );

    renderizar();

    expect(await screen.findByRole('alert')).toHaveTextContent(
      'El rol TECNICO_PRE no tiene permiso para realizar esta accion.',
    );
  });
});
