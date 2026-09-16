import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { MercadoTab } from './MercadoTab';

const obtenerAnalisisMercado = vi.fn();
const guardarAnalisisMercado = vi.fn();
const listarProductosIndicadores = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    analisisMercadoApi: {
      obtenerAnalisisMercado: (...a: unknown[]) => obtenerAnalisisMercado(...a),
      guardarAnalisisMercado: (...a: unknown[]) => guardarAnalisisMercado(...a),
    },
    catalogoEtapasApi: { listarProductosIndicadores: (...a: unknown[]) => listarProductosIndicadores(...a) },
  };
});
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

const CATALOGO = [
  { codigoProducto: 'P1', producto: 'Aulas construidas', codigoIndicador: 'I1', indicador: 'Aulas', unidadMedida: 'Unidad', esIndicadorPrincipal: true },
  { codigoProducto: 'P1', producto: 'Aulas construidas', codigoIndicador: 'I2', indicador: 'Metros', unidadMedida: 'm2', esIndicadorPrincipal: false },
  { codigoProducto: 'P2', producto: 'Camas hospitalarias', codigoIndicador: 'I3', indicador: 'Camas', unidadMedida: 'Unidad', esIndicadorPrincipal: true },
];

const llenarFila = () => {
  fireEvent.change(screen.getByLabelText('Producto de la fila 1'), { target: { value: 'P1' } });
  fireEvent.change(screen.getByLabelText('Demanda de la fila 1'), { target: { value: '100' } });
  fireEvent.change(screen.getByLabelText('Oferta de la fila 1'), { target: { value: '60' } });
  fireEvent.change(screen.getByLabelText('Años a proyectar de la fila 1'), { target: { value: '5' } });
  fireEvent.change(screen.getByLabelText('Tasa de demanda de la fila 1'), { target: { value: '2.5' } });
  fireEvent.change(screen.getByLabelText('Tasa de oferta de la fila 1'), { target: { value: '1.5' } });
};

describe('MercadoTab · CU-PRE-09', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    obtenerAnalisisMercado.mockResolvedValue({ data: { idProyecto: 7, filas: [] } });
    listarProductosIndicadores.mockResolvedValue({ data: CATALOGO });
    swalFire.mockResolvedValue({ isConfirmed: true });
  });

  const montar = (puedeEditar = true) => render(<MercadoTab idProyecto={7} puedeEditar={puedeEditar} />);

  it('ofrece cada producto una sola vez aunque el catálogo traiga varios indicadores', async () => {
    montar();
    const select = await screen.findByLabelText('Producto de la fila 1');
    expect(within(select).getAllByRole('option').map((o) => o.textContent)).toEqual([
      'Seleccione...',
      'Aulas construidas',
      'Camas hospitalarias',
    ]);
  });

  it('muestra lo calculado por el servidor y no lo deja editar', async () => {
    obtenerAnalisisMercado.mockResolvedValue({
      data: {
        idProyecto: 7,
        filas: [
          {
            producto: { codigoProducto: 'P1', producto: 'Aulas construidas' },
            unidadMedida: 'Unidad',
            demanda: 100,
            oferta: 60,
            deficit: 40,
            aniosAProyectar: 5,
            tasaDemanda: 2.5,
            tasaOferta: 1.5,
            promedioDemanda: 110,
            promedioOferta: 64,
            promedioDeficit: 46,
          },
        ],
      },
    });
    montar();
    expect(await screen.findByLabelText('Demanda de la fila 1')).toHaveValue(100);
    expect(screen.getByText('Unidad')).toBeInTheDocument();
    expect(screen.getByText('40')).toBeInTheDocument();
    expect(screen.getByText('46')).toBeInTheDocument();
    expect(screen.queryByLabelText(/Déficit de la fila/)).not.toBeInTheDocument();
  });

  // RN04: sin una fila completa el back rechaza todo el guardado.
  it('no llama al servidor si ninguna fila está completa', async () => {
    montar();
    await screen.findByLabelText('Producto de la fila 1');
    fireEvent.change(screen.getByLabelText('Demanda de la fila 1'), { target: { value: '100' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'error' })));
    expect(guardarAnalisisMercado).not.toHaveBeenCalled();
  });

  it('guarda sólo los campos que el contrato acepta', async () => {
    guardarAnalisisMercado.mockResolvedValue({ data: { idProyecto: 7, filas: [] } });
    montar();
    await screen.findByLabelText('Producto de la fila 1');
    llenarFila();
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(guardarAnalisisMercado).toHaveBeenCalledWith({
        idProyecto: 7,
        analisisMercadoRequest: {
          filas: [
            {
              producto: { codigoProducto: 'P1' },
              demanda: 100,
              oferta: 60,
              aniosAProyectar: 5,
              tasaDemanda: 2.5,
              tasaOferta: 1.5,
            },
          ],
        },
      }),
    );
  });

  it('si el catálogo de productos falla, avisa y deja el resto de la tabla', async () => {
    listarProductosIndicadores.mockRejectedValue(new Error('405'));
    montar();
    expect(await screen.findByText(/catálogo de productos no está disponible/)).toBeInTheDocument();
    expect(screen.getByLabelText('Producto de la fila 1')).toBeDisabled();
    expect(screen.getByLabelText('Demanda de la fila 1')).toBeInTheDocument();
  });

  it('el Técnico PRE consulta pero no edita', async () => {
    obtenerAnalisisMercado.mockResolvedValue({
      data: { idProyecto: 7, filas: [{ producto: { codigoProducto: 'P1' }, demanda: 100 }] },
    });
    montar(false);
    expect(await screen.findByLabelText('Demanda de la fila 1')).toHaveAttribute('readonly');
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
  });
});
