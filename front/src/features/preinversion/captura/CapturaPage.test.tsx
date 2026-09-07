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

const { CapturaPage } = await import('./CapturaPage');

const RESPUESTA = {
  data: {
    contenido: [{
      idProyecto: 201, cup: '10001',
      nombreProyecto: 'Ampliación de red de agua potable, Santa Ana',
      iniciativaInversion: 'PROYECTO', estado: 'EN_FORMULACION',
      unidadEjecutora: { idUnidadEjecutora: 4501, nombre: 'MINSAL' },
    }],
    paginacion: { pagina: 0, tamanio: 20, totalElementos: 1, totalPaginas: 1 },
  },
};

const montar = () => render(<MemoryRouter><CapturaPage /></MemoryRouter>);

describe('CapturaPage', () => {
  beforeEach(() => {
    listarProyectosCaptura.mockReset().mockResolvedValue(RESPUESTA);
  });

  // Las cinco columnas del Anexo A.1.
  it('muestra las columnas del caso de uso', async () => {
    montar();
    await screen.findByText('Ampliación de red de agua potable, Santa Ana');
    const cabeceras = [...document.querySelectorAll('table thead th')].map((c) => c.textContent);
    expect(cabeceras).toEqual([
      'CUP', 'Nombre del proyecto', 'Iniciativa de inversión', 'Estado', 'Unidad Ejecutora',
    ]);
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
