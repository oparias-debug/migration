import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { LocalizacionTab } from './LocalizacionTab';

const obtenerLocalizacion = vi.fn();
const guardarLocalizacion = vi.fn();
const autocompletar = vi.fn();
const listarUbicacionesGeograficas = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    localizacionApi: {
      obtenerLocalizacion: (...a: unknown[]) => obtenerLocalizacion(...a),
      guardarLocalizacion: (...a: unknown[]) => guardarLocalizacion(...a),
      autocompletarLocalizacionDesdeAreaInfluencia: (...a: unknown[]) => autocompletar(...a),
    },
    catalogoEtapasApi: { listarUbicacionesGeograficas: (...a: unknown[]) => listarUbicacionesGeograficas(...a) },
  };
});
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

const CATALOGO = [
  { distrito: 'Santa Ana Centro', departamento: 'Santa Ana', region: 'Occidental' },
  { distrito: 'Metapán', departamento: 'Santa Ana', region: 'Occidental' },
  { distrito: 'Soyapango', departamento: 'San Salvador', region: 'Central' },
  { distrito: 'Nivel nacional', departamento: 'Nivel nacional', region: 'Nivel nacional' },
];

const montar = (puedeEditar = true) => render(<LocalizacionTab idProyecto={7} puedeEditar={puedeEditar} />);

describe('LocalizacionTab · CU-PRE-12', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    obtenerLocalizacion.mockResolvedValue({ data: { idProyecto: 7, filas: [] } });
    listarUbicacionesGeograficas.mockResolvedValue({ data: CATALOGO });
    swalFire.mockResolvedValue({ isConfirmed: true });
  });

  it('ofrece los distritos del departamento elegido', async () => {
    montar();
    await screen.findByLabelText('Departamento de la fila 1');
    fireEvent.change(screen.getByLabelText('Departamento de la fila 1'), { target: { value: 'Santa Ana' } });
    await waitFor(() => {
      const opciones = [...screen.getByLabelText<HTMLSelectElement>('Distrito de la fila 1').options].map((o) => o.text);
      expect(opciones).toEqual(['Seleccione...', 'Santa Ana Centro', 'Metapán']);
    });
  });

  // Anexo B.1: a nivel nacional no hay punto que marcar en el mapa.
  it('bloquea las coordenadas cuando el distrito es "Nivel nacional"', async () => {
    montar();
    await screen.findByLabelText('Latitud de la fila 1');
    expect(screen.getByLabelText('Latitud de la fila 1')).toBeEnabled();

    fireEvent.change(screen.getByLabelText('Distrito de la fila 1'), { target: { value: 'Nivel nacional' } });
    await waitFor(() => expect(screen.getByLabelText('Latitud de la fila 1')).toBeDisabled());
    expect(screen.getByLabelText('Longitud de la fila 1')).toBeDisabled();
  });

  // RN06 y RN07: el propietario sólo si la fila requiere terreno; el detalle sólo para algunos.
  it('encadena requiere terreno, propietario y especifique', async () => {
    montar();
    await screen.findByLabelText('Propietario del terreno de la fila 1');
    expect(screen.getByLabelText('Propietario del terreno de la fila 1')).toBeDisabled();
    expect(screen.getByLabelText('Especifique el propietario de la fila 1')).toBeDisabled();

    fireEvent.click(screen.getByLabelText('¿La ubicación 1 requiere adquirir terreno?'));
    await waitFor(() => expect(screen.getByLabelText('Propietario del terreno de la fila 1')).toBeEnabled());
    expect(screen.getByLabelText('Especifique el propietario de la fila 1')).toBeDisabled();

    fireEvent.change(screen.getByLabelText('Propietario del terreno de la fila 1'), { target: { value: 'LA_MUNICIPALIDAD' } });
    await waitFor(() => expect(screen.getByLabelText('Especifique el propietario de la fila 1')).toBeEnabled());

    // La institución propietaria del proyecto no pide detalle.
    fireEvent.change(screen.getByLabelText('Propietario del terreno de la fila 1'), {
      target: { value: 'INSTITUCION_PROPIETARIA_DEL_PROYECTO' },
    });
    await waitFor(() => expect(screen.getByLabelText('Especifique el propietario de la fila 1')).toBeDisabled());
  });

  it('no manda lo que no aplica', async () => {
    guardarLocalizacion.mockResolvedValue({ data: { idProyecto: 7, filas: [] } });
    montar();
    await screen.findByLabelText('Departamento de la fila 1');
    fireEvent.change(screen.getByLabelText('Departamento de la fila 1'), { target: { value: 'Nivel nacional' } });
    fireEvent.change(screen.getByLabelText('Distrito de la fila 1'), { target: { value: 'Nivel nacional' } });
    fireEvent.change(screen.getByLabelText('Especifique el propietario de la fila 1'), { target: { value: 'sobra' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(guardarLocalizacion).toHaveBeenCalled());
    const fila = guardarLocalizacion.mock.calls[0][0].localizacionRequest.filas[0];
    expect(fila.coordenadas).toBeUndefined();
    expect(fila.propietario).toBeUndefined();
    expect(fila.especifique).toBeUndefined();
  });

  it('manda las coordenadas completas', async () => {
    guardarLocalizacion.mockResolvedValue({ data: { idProyecto: 7, filas: [] } });
    montar();
    await screen.findByLabelText('Departamento de la fila 1');
    fireEvent.change(screen.getByLabelText('Departamento de la fila 1'), { target: { value: 'Santa Ana' } });
    fireEvent.change(screen.getByLabelText('Distrito de la fila 1'), { target: { value: 'Metapán' } });
    fireEvent.change(screen.getByLabelText('Latitud de la fila 1'), { target: { value: '14.33' } });
    fireEvent.change(screen.getByLabelText('Longitud de la fila 1'), { target: { value: '-89.45' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(guardarLocalizacion).toHaveBeenCalled());
    expect(guardarLocalizacion.mock.calls[0][0].localizacionRequest.filas[0].coordenadas).toEqual({
      latitud: 14.33,
      longitud: -89.45,
    });
  });

  it('autocompletar trae las filas del área de influencia sin guardarlas', async () => {
    autocompletar.mockResolvedValue({
      data: { idProyecto: 7, filas: [{ departamento: 'Santa Ana', distrito: 'Metapán' }] },
    });
    montar();
    await screen.findByLabelText('Departamento de la fila 1');
    fireEvent.click(screen.getByRole('button', { name: 'Autocompletar desde área de influencia' }));

    await waitFor(() => expect(screen.getByLabelText('Distrito de la fila 1')).toHaveValue('Metapán'));
    expect(guardarLocalizacion).not.toHaveBeenCalled();
  });

  it('si el área de influencia está vacía, lo dice', async () => {
    autocompletar.mockResolvedValue({ data: { idProyecto: 7, filas: [] } });
    montar();
    await screen.findByLabelText('Departamento de la fila 1');
    fireEvent.click(screen.getByRole('button', { name: 'Autocompletar desde área de influencia' }));
    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'info' })));
  });

  it('el Técnico PRE consulta pero no edita', async () => {
    obtenerLocalizacion.mockResolvedValue({
      data: { idProyecto: 7, filas: [{ departamento: 'Santa Ana', distrito: 'Metapán', direccionEspecifica: 'Cantón El Rosario' }] },
    });
    montar(false);
    expect(await screen.findByLabelText('Departamento de la fila 1')).toBeDisabled();
    expect(screen.getByLabelText('Dirección específica de la fila 1')).toHaveAttribute('readonly');
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /Autocompletar/ })).not.toBeInTheDocument();
  });
});
