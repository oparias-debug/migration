import { beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { AxiosError, AxiosHeaders } from 'axios';
import { MemoryRouter } from 'react-router-dom';
import i18n from '../../../i18n/i18n';

const listarProyectosCaptura = vi.fn();
const listarSectores = vi.fn().mockResolvedValue({ data: [] });

vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  capturaApi: { listarProyectosCaptura: (...a: unknown[]) => listarProyectosCaptura(...a) },
  catalogoPreinversionApi: { listarSectores: () => listarSectores() },
}));

const navigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => ({
  ...(await importOriginal<typeof import('react-router-dom')>()),
  useNavigate: () => navigate,
}));

const { CapturaPage } = await import('./CapturaPage');

const RESPUESTA = {
  data: {
    contenido: [{
      idProyecto: 201, cup: '10001',
      nombreProyecto: 'Ampliación de red de agua potable, Santa Ana',
      iniciativaInversion: 'PROYECTO', estado: 'EN_FORMULACION',
      unidadEjecutora: { idUnidadEjecutora: 4501, nombre: 'MINSAL' },
      etapaActual: 'PREFACTIBILIDAD',
    }],
    paginacion: { pagina: 0, tamanio: 20, totalElementos: 1, totalPaginas: 1 },
  },
};

const montar = () => render(<MemoryRouter><CapturaPage /></MemoryRouter>);

describe('CapturaPage', () => {
  beforeEach(() => {
    listarProyectosCaptura.mockReset().mockResolvedValue(RESPUESTA);
  });

  // Las cinco columnas del Anexo A.1, más "Etapa actual", que el cliente pidió
  // el 11/09/2026 y entró en el contrato como CU-PRE-03 v1.1.0.
  it('muestra las columnas del caso de uso', async () => {
    montar();
    await screen.findByText('Ampliación de red de agua potable, Santa Ana');
    const cabeceras = [...document.querySelectorAll('table thead th')].map((c) => c.textContent);
    expect(cabeceras).toEqual([
      'CUP', 'Nombre del proyecto', 'Iniciativa de inversión', 'Estado', 'Etapa actual', 'Unidad Ejecutora',
    ]);
  });

  it('muestra la etapa actual del proyecto', async () => {
    montar();
    expect(await screen.findByText('Prefactibilidad')).toBeInTheDocument();
  });

  // etapaActual es nulo mientras el proyecto no tenga una Ruta de Preinversión
  // aceptada; la celda no puede quedar vacía sin más.
  it('muestra un guion cuando el proyecto todavía no tiene etapa', async () => {
    listarProyectosCaptura.mockResolvedValue({
      data: {
        ...RESPUESTA.data,
        contenido: [{ ...RESPUESTA.data.contenido[0], etapaActual: null }],
      },
    });
    montar();
    await screen.findByText('Ampliación de red de agua potable, Santa Ana');
    const celdas = [...document.querySelectorAll('table tbody td')].map((c) => c.textContent);
    expect(celdas).toContain('—');
  });

  // RN03: la búsqueda libre la resuelve el servidor, no el navegador.
  it('la búsqueda se aplica al pulsar BUSCAR y viaja al back', async () => {
    montar();
    await screen.findByText('Ampliación de red de agua potable, Santa Ana');
    fireEvent.change(screen.getByLabelText('Buscador'), { target: { value: 'lempa' } });

    // Teclear no dispara la consulta.
    expect(listarProyectosCaptura).toHaveBeenCalledTimes(1);

    fireEvent.click(screen.getByRole('button', { name: 'BUSCAR' }));
    await waitFor(() =>
      expect(listarProyectosCaptura).toHaveBeenLastCalledWith(expect.objectContaining({ busqueda: 'lempa' })),
    );
  });

  // RN05: cada filtro de columna se manda como parámetro propio.
  it('el filtro por columna viaja como parámetro', async () => {
    montar();
    await screen.findByText('Ampliación de red de agua potable, Santa Ana');
    fireEvent.change(screen.getByLabelText('Iniciativa de inversión'), { target: { value: 'PROGRAMA' } });

    await waitFor(() =>
      expect(listarProyectosCaptura).toHaveBeenLastCalledWith(
        expect.objectContaining({ iniciativaInversion: 'PROGRAMA' }),
      ),
    );
  });

  it('Limpiar quita todos los filtros', async () => {
    montar();
    await screen.findByText('Ampliación de red de agua potable, Santa Ana');
    fireEvent.change(screen.getByLabelText('CUP'), { target: { value: '10001' } });
    await waitFor(() => expect(listarProyectosCaptura).toHaveBeenLastCalledWith(expect.objectContaining({ cup: '10001' })));

    fireEvent.click(screen.getByRole('button', { name: 'Limpiar' }));
    await waitFor(() => {
      const ultima = listarProyectosCaptura.mock.calls.at(-1)![0] as Record<string, unknown>;
      expect(ultima).not.toHaveProperty('cup');
    });
  });

  it('un fallo del back se ve como error, no como listado vacío', async () => {
    const config = { headers: new AxiosHeaders() };
    listarProyectosCaptura.mockRejectedValue(
      new AxiosError('fallo', '500', config, null, { status: 500, statusText: '', headers: {}, config, data: undefined }),
    );
    montar();
    expect(await screen.findByRole('alert')).toHaveTextContent(i18n.t('errores.servidor'));
  });
});

// CU-PRE-03-navegar-registro-etapas.feature (FA-01): el CUP abre "Registro de
// Etapas", que es la entrada a los pasos del proyecto, no la ficha del registro.
describe('CapturaPage · navegación desde el CUP', () => {
  it('el CUP lleva a Registro de Etapas del proyecto', async () => {
    navigate.mockReset();
    listarProyectosCaptura.mockResolvedValue(RESPUESTA);
    render(
      <MemoryRouter>
        <CapturaPage />
      </MemoryRouter>,
    );
    fireEvent.click(await screen.findByRole('button', { name: '10001' }));
    expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/201/etapas');
  });
});

// Procesos 1.2 a 1.5 del árbol del sistema: el CUP lleva a los pasos del proceso.
describe('CapturaPage · entrada a cada proceso', () => {
  it('desde Formulación y evaluación, el CUP abre Identificación', async () => {
    navigate.mockReset();
    listarProyectosCaptura.mockResolvedValue(RESPUESTA);
    render(
      <MemoryRouter>
        <CapturaPage proceso="formulacion" />
      </MemoryRouter>,
    );
    fireEvent.click(await screen.findByRole('button', { name: '10001' }));
    expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/201/identificacion');
  });

  it('desde un proceso sin pantallas todavía, abre la barra en ese proceso', async () => {
    navigate.mockReset();
    listarProyectosCaptura.mockResolvedValue(RESPUESTA);
    render(
      <MemoryRouter>
        <CapturaPage proceso="gestion" />
      </MemoryRouter>,
    );
    fireEvent.click(await screen.findByRole('button', { name: '10001' }));
    expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/201/etapas?grupo=gestion');
  });
});
