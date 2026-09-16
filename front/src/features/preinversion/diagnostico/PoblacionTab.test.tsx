import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { AxiosError, AxiosHeaders } from 'axios';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { PoblacionTab } from './PoblacionTab';

const obtenerAnalisisPoblacion = vi.fn();
const guardarAnalisisPoblacion = vi.fn();
const listarUbicacionesGeograficas = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    poblacionApi: {
      obtenerAnalisisPoblacion: (...a: unknown[]) => obtenerAnalisisPoblacion(...a),
      guardarAnalisisPoblacion: (...a: unknown[]) => guardarAnalisisPoblacion(...a),
    },
    catalogoEtapasApi: { listarUbicacionesGeograficas: (...a: unknown[]) => listarUbicacionesGeograficas(...a) },
  };
});
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

const CATALOGO = [
  { distrito: 'Santa Ana Centro', departamento: 'Santa Ana', region: 'Occidental' },
  { distrito: 'Metapán', departamento: 'Santa Ana', region: 'Occidental' },
];

const fila = (personas: number, porcentaje: number | null, descripcion: string | null = null) => ({
  descripcion,
  ubicaciones: [{ ubicacion: 'Metapán', numeroPersonas: personas, porcentaje }],
  totalNumeroPersonas: personas,
  totalPorcentaje: porcentaje,
});

const ANALISIS = {
  idProyecto: 7,
  poblacionReferencia: fila(1000, null),
  poblacionAfectada: fila(400, 100, 'Afectados por el desbordamiento'),
  poblacionObjetivo: fila(300, 75, 'Atendidos en la primera fase'),
  poblacionEnEspera: { ...fila(100, 25), descripcion: null },
};

function error400(codigo: string) {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('Request failed', '400', config, {}, {
    status: 400,
    statusText: '',
    data: { codigo, mensaje: 'texto del servidor', detalles: [] },
    headers: {},
    config,
  });
}

describe('PoblacionTab · CU-PRE-07', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    obtenerAnalisisPoblacion.mockResolvedValue({ data: ANALISIS });
    listarUbicacionesGeograficas.mockResolvedValue({ data: CATALOGO });
    swalFire.mockResolvedValue({ isConfirmed: true });
  });

  const montar = (puedeEditar = true) => render(<PoblacionTab idProyecto={7} puedeEditar={puedeEditar} />);

  it('muestra las cuatro poblaciones con lo que calcula el servidor', async () => {
    montar();
    expect(await screen.findByText('Población de referencia')).toBeInTheDocument();
    expect(screen.getByText('Población en espera')).toBeInTheDocument();
    expect(screen.getByText('1,000')).toBeInTheDocument();
    expect(screen.getByText('75 %')).toBeInTheDocument();
    expect(screen.getByText('25 %')).toBeInTheDocument();
  });

  // RN05: la fila de población en espera es del sistema, no se escribe.
  it('no deja escribir la población en espera', async () => {
    montar();
    await screen.findByText('Población en espera');
    expect(screen.queryByLabelText(/de Población en espera/)).not.toBeInTheDocument();
  });

  // RN06: la descripción sólo aplica a afectada y objetivo.
  it('bloquea la descripción de la población de referencia', async () => {
    montar();
    expect(await screen.findByLabelText('Descripción de Población de referencia')).toBeDisabled();
    expect(screen.getByLabelText('Descripción de Población afectada')).toBeEnabled();
  });

  it('agrega una columna de ubicación a las tres filas', async () => {
    montar();
    await screen.findByLabelText('Ubicación 1 de Población afectada');
    expect(screen.queryByLabelText('Ubicación 2 de Población afectada')).not.toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: 'Agregar ubicación' }));
    await waitFor(() => expect(screen.getByLabelText('Ubicación 2 de Población afectada')).toBeInTheDocument());
    expect(screen.getByLabelText('Ubicación 2 de Población de referencia')).toBeInTheDocument();
    expect(screen.getByLabelText('Ubicación 2 de Población objetivo')).toBeInTheDocument();
  });

  it('manda las tres filas y no la población en espera', async () => {
    guardarAnalisisPoblacion.mockResolvedValue({ data: ANALISIS });
    montar();
    await screen.findByLabelText('Personas en la ubicación 1 de Población afectada');
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(guardarAnalisisPoblacion).toHaveBeenCalled());
    const enviado = guardarAnalisisPoblacion.mock.calls[0][0].analisisPoblacionRequest;
    expect(Object.keys(enviado)).toEqual(['poblacionReferencia', 'poblacionAfectada', 'poblacionObjetivo']);
    expect(enviado.poblacionAfectada.ubicaciones).toEqual([{ ubicacion: 'Metapán', numeroPersonas: 400 }]);
    // RN06: la de referencia no manda descripción.
    expect(enviado.poblacionReferencia.descripcion).toBeUndefined();
  });

  it('explica el rechazo cuando la afectada supera a la de referencia', async () => {
    guardarAnalisisPoblacion.mockRejectedValue(error400('POBLACION_AFECTADA_MAYOR_QUE_REFERENCIA'));
    montar();
    await screen.findByLabelText('Personas en la ubicación 1 de Población afectada');
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(swalFire).toHaveBeenCalledWith(
        expect.objectContaining({ icon: 'error', text: expect.stringContaining('no puede ser mayor que la de referencia') }),
      ),
    );
  });

  it('explica el rechazo cuando la objetivo supera a la afectada', async () => {
    guardarAnalisisPoblacion.mockRejectedValue(error400('POBLACION_OBJETIVO_MAYOR_QUE_AFECTADA'));
    montar();
    await screen.findByLabelText('Personas en la ubicación 1 de Población afectada');
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(swalFire).toHaveBeenCalledWith(
        expect.objectContaining({ icon: 'error', text: expect.stringContaining('no puede ser mayor que la afectada') }),
      ),
    );
  });

  it('el Técnico PRE consulta pero no edita', async () => {
    montar(false);
    expect(await screen.findByLabelText('Personas en la ubicación 1 de Población afectada')).toHaveAttribute('readonly');
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Agregar ubicación' })).not.toBeInTheDocument();
  });
});
