import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { AxiosError, AxiosHeaders } from 'axios';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { AreaInfluenciaTab } from './AreaInfluenciaTab';

const obtenerAreaInfluencia = vi.fn();
const guardarAreaInfluencia = vi.fn();
const autocompletar = vi.fn();
const listarUbicacionesGeograficas = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    areaInfluenciaApi: {
      obtenerAreaInfluencia: (...a: unknown[]) => obtenerAreaInfluencia(...a),
      guardarAreaInfluencia: (...a: unknown[]) => guardarAreaInfluencia(...a),
      autocompletarAreaInfluenciaDesdePoblacionObjetivo: (...a: unknown[]) => autocompletar(...a),
    },
    catalogoEtapasApi: { listarUbicacionesGeograficas: (...a: unknown[]) => listarUbicacionesGeograficas(...a) },
  };
});
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

const CATALOGO = [
  { distrito: 'Santa Ana Centro', departamento: 'Santa Ana', region: 'Occidental' },
  { distrito: 'Metapán', departamento: 'Santa Ana', region: 'Occidental' },
];

function error404() {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('Request failed', '404', config, {}, {
    status: 404,
    statusText: '',
    data: { codigo: 'RECURSO_NO_ENCONTRADO', mensaje: 'Proyecto o distrito inexistente', detalles: [] },
    headers: {},
    config,
  });
}

describe('AreaInfluenciaTab · CU-PRE-08', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    obtenerAreaInfluencia.mockResolvedValue({ data: { idProyecto: 7, filas: [] } });
    listarUbicacionesGeograficas.mockResolvedValue({ data: CATALOGO });
    swalFire.mockResolvedValue({ isConfirmed: true });
  });

  const montar = (puedeEditar = true) => render(<AreaInfluenciaTab idProyecto={7} puedeEditar={puedeEditar} />);

  it('región y departamento llegan del servidor y no se editan', async () => {
    obtenerAreaInfluencia.mockResolvedValue({
      data: {
        idProyecto: 7,
        filas: [{ distrito: 'Metapán', departamento: 'Santa Ana', region: 'Occidental', ubicacionEspecifica: 'Cantón El Rosario' }],
      },
    });
    montar();
    expect(await screen.findByLabelText('Distrito de la fila 1')).toHaveValue('Metapán');
    expect(screen.getByText('Santa Ana')).toBeInTheDocument();
    expect(screen.getByText('Occidental')).toBeInTheDocument();
    expect(screen.queryByLabelText(/Departamento de la fila/)).not.toBeInTheDocument();
  });

  it('manda sólo distrito y ubicación específica, y omite las filas sin distrito', async () => {
    guardarAreaInfluencia.mockResolvedValue({ data: { idProyecto: 7, filas: [] } });
    montar();
    await screen.findByLabelText('Distrito de la fila 1');
    fireEvent.change(screen.getByLabelText('Distrito de la fila 1'), { target: { value: 'Metapán' } });
    fireEvent.change(screen.getByLabelText('Ubicación específica de la fila 1'), { target: { value: 'Cantón El Rosario' } });
    fireEvent.click(screen.getByRole('button', { name: 'Agregar ubicación' }));
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(guardarAreaInfluencia).toHaveBeenCalledWith({
        idProyecto: 7,
        areaInfluenciaRequest: { filas: [{ distrito: 'Metapán', ubicacionEspecifica: 'Cantón El Rosario' }] },
      }),
    );
  });

  it('limita la ubicación específica a 1000 caracteres', async () => {
    montar();
    expect(await screen.findByLabelText('Ubicación específica de la fila 1')).toHaveAttribute('maxlength', '1000');
  });

  it('autocompletar trae las filas de población objetivo sin guardarlas', async () => {
    autocompletar.mockResolvedValue({
      data: { idProyecto: 7, filas: [{ distrito: 'Santa Ana Centro', departamento: 'Santa Ana', region: 'Occidental' }] },
    });
    montar();
    await screen.findByLabelText('Distrito de la fila 1');
    fireEvent.click(screen.getByRole('button', { name: 'Autocompletar desde población objetivo' }));

    await waitFor(() => expect(screen.getByLabelText('Distrito de la fila 1')).toHaveValue('Santa Ana Centro'));
    expect(guardarAreaInfluencia).not.toHaveBeenCalled();
  });

  it('si población objetivo está vacía, lo dice', async () => {
    autocompletar.mockResolvedValue({ data: { idProyecto: 7, filas: [] } });
    montar();
    await screen.findByLabelText('Distrito de la fila 1');
    fireEvent.click(screen.getByRole('button', { name: 'Autocompletar desde población objetivo' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'info' })));
  });

  // El 404 de este guardado puede ser el proyecto o un distrito: no se afirma cuál.
  it('ante un 404 al guardar, no culpa al proyecto', async () => {
    guardarAreaInfluencia.mockRejectedValue(error404());
    montar();
    await screen.findByLabelText('Distrito de la fila 1');
    fireEvent.change(screen.getByLabelText('Distrito de la fila 1'), { target: { value: 'Metapán' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(swalFire).toHaveBeenCalledWith(
        expect.objectContaining({ icon: 'error', text: expect.stringContaining('distritos') }),
      ),
    );
  });

  it('el Técnico PRE consulta pero no edita ni autocompleta', async () => {
    obtenerAreaInfluencia.mockResolvedValue({
      data: { idProyecto: 7, filas: [{ distrito: 'Metapán', departamento: 'Santa Ana', region: 'Occidental' }] },
    });
    montar(false);
    expect(await screen.findByLabelText('Distrito de la fila 1')).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Autocompletar desde población objetivo' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
  });
});
