import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { PresupuestoPage } from './PresupuestoPage';

const obtenerPresupuesto = vi.fn();
const configurarPeriodosEjecucion = vi.fn();
const registrarMacroactividad = vi.fn();
const guardarPresupuesto = vi.fn();
const obtenerFuentesFinanciamiento = vi.fn();
const guardarFuentesFinanciamiento = vi.fn();
const listarInsumosTipo = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    presupuestoApi: {
      obtenerPresupuesto: (...a: unknown[]) => obtenerPresupuesto(...a),
      configurarPeriodosEjecucion: (...a: unknown[]) => configurarPeriodosEjecucion(...a),
      registrarMacroactividad: (...a: unknown[]) => registrarMacroactividad(...a),
      guardarPresupuesto: (...a: unknown[]) => guardarPresupuesto(...a),
      obtenerFuentesFinanciamiento: (...a: unknown[]) => obtenerFuentesFinanciamiento(...a),
      guardarFuentesFinanciamiento: (...a: unknown[]) => guardarFuentesFinanciamiento(...a),
    },
    catalogoInsumosApi: { listarInsumosTipo: (...a: unknown[]) => listarInsumosTipo(...a) },
  };
});

let rolesActivos: string[] = ['TECNICO_URP'];
vi.mock('../../../auth/useAuth', () => ({
  useAuth: () => ({ hasRole: (rol: string) => rolesActivos.includes(rol) }),
}));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

const INSUMOS = [
  { codigo: 'MANO_OBRA', nombre: 'Mano de obra', factorCorreccion: 0.8 },
  { codigo: 'MATERIALES', nombre: 'Materiales', factorCorreccion: 0.9 },
];

const PRESUPUESTO_BASE = {
  idProyecto: 7,
  periodosEstimados: 2,
  productos: [
    {
      numero: 1,
      producto: { codigoProducto: 'P1', producto: 'Aulas construidas' },
      macroactividades: [],
      costoProductoPorPeriodo: [0, 0],
      costoProductoTotal: 0,
    },
  ],
  inversionEstimadaPreciosMercado: { porPeriodo: [0, 0], total: 0 },
  inversionEstimadaPreciosAjustados: null,
  resumenPorComponente: [{ componente: { codigo: 'OBRA', nombre: 'Obra civil' }, costo: 0 }],
  totalResumenPorComponente: 0,
};

const montar = () =>
  render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/7/presupuesto']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/presupuesto" element={<PresupuestoPage />} />
      </Routes>
    </MemoryRouter>,
  );

describe('PresupuestoPage · CU-PRE-17', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['TECNICO_URP'];
    swalFire.mockResolvedValue({ isConfirmed: true });
    obtenerPresupuesto.mockResolvedValue({ data: PRESUPUESTO_BASE });
    listarInsumosTipo.mockResolvedValue({ data: INSUMOS });
    obtenerFuentesFinanciamiento.mockResolvedValue({ data: { fuentesFinanciamiento: [], fuenteRecursos: '' } });
  });

  it('pinta una columna por período configurado (RN06)', async () => {
    montar();
    expect(await screen.findByText('PERÍODO 0')).toBeInTheDocument();
    expect(screen.getByText('PERÍODO 1')).toBeInTheDocument();
    expect(screen.queryByText('PERÍODO 2')).not.toBeInTheDocument();
  });

  it('genera las columnas al aceptar la cantidad de períodos', async () => {
    configurarPeriodosEjecucion.mockResolvedValue({ data: { ...PRESUPUESTO_BASE, periodosEstimados: 3 } });
    montar();
    await screen.findByText('PERÍODO 0');

    fireEvent.change(screen.getByLabelText('Períodos estimados para la ejecución'), { target: { value: '3' } });
    fireEvent.click(screen.getByRole('button', { name: 'Aceptar' }));

    await waitFor(() =>
      expect(configurarPeriodosEjecucion).toHaveBeenCalledWith({
        idProyecto: 7,
        configurarPeriodosEjecucionRequest: { periodosEstimados: 3 },
      }),
    );
    expect(await screen.findByText('PERÍODO 2')).toBeInTheDocument();
  });

  // RN16
  it('el producto se muestra pero no se puede editar ni borrar', async () => {
    montar();
    expect(await screen.findByText('Aulas construidas')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /eliminar producto/i })).not.toBeInTheDocument();
  });

  it('exige al menos una macroactividad por producto antes de guardar', async () => {
    montar();
    await screen.findByText('Aulas construidas');

    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'warning' })),
    );
    expect(guardarPresupuesto).not.toHaveBeenCalled();
  });

  it('guarda cuando cada producto tiene macroactividad', async () => {
    obtenerPresupuesto.mockResolvedValue({
      data: {
        ...PRESUPUESTO_BASE,
        productos: [
          {
            ...PRESUPUESTO_BASE.productos[0],
            macroactividades: [
              {
                idMacroactividad: 10,
                numero: '1.1',
                nombreMacroactividad: 'Cimentación',
                insumos: [],
                totalPeriodoPrecioMercado: [100, 200],
              },
            ],
          },
        ],
      },
    });
    guardarPresupuesto.mockResolvedValue({ data: {} });
    guardarFuentesFinanciamiento.mockResolvedValue({ data: {} });
    montar();
    await screen.findByText('Cimentación');

    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(guardarPresupuesto).toHaveBeenCalledWith({ idProyecto: 7 }));
    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'success' })));
  });

  // RN10/RN11: el contrato manda null cuando el actor no los puede ver
  it('oculta los precios ajustados si el contrato los devuelve nulos', async () => {
    montar();
    await screen.findByText('Aulas construidas');
    expect(screen.queryByText('Inversión estimada (precios ajustados)')).not.toBeInTheDocument();
  });

  it('muestra los precios ajustados cuando sí vienen', async () => {
    obtenerPresupuesto.mockResolvedValue({
      data: { ...PRESUPUESTO_BASE, inversionEstimadaPreciosAjustados: { porPeriodo: [10, 20], total: 30 } },
    });
    montar();
    expect(await screen.findByText('Inversión estimada (precios ajustados)')).toBeInTheDocument();
  });

  // "Consultar el presupuesto en modo solo lectura"
  it('el Técnico PRE consulta pero no edita', async () => {
    rolesActivos = ['TECNICO_PRE'];
    montar();
    await screen.findByText('Aulas construidas');
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Agregar macroactividad' })).not.toBeInTheDocument();
    expect(screen.getByLabelText('Períodos estimados para la ejecución')).toHaveAttribute('readonly');
  });

  it('el resumen por componente sale del servidor, no se recalcula', async () => {
    montar();
    expect(await screen.findByText('Obra civil')).toBeInTheDocument();
  });

  describe('fuentes de financiamiento (RN14)', () => {
    it('permite agregar una fuente adicional con el botón +', async () => {
      montar();
      await screen.findByText('Aulas construidas');
      expect(screen.queryByLabelText('Fuente de financiamiento 1')).not.toBeInTheDocument();

      fireEvent.click(screen.getByRole('button', { name: 'Agregar fuente de financiamiento' }));
      expect(await screen.findByLabelText('Fuente de financiamiento 1')).toBeInTheDocument();

      fireEvent.click(screen.getByRole('button', { name: 'Agregar fuente de financiamiento' }));
      expect(await screen.findByLabelText('Fuente de financiamiento 2')).toBeInTheDocument();
    });

    it('ofrece los siete valores del catálogo cerrado', async () => {
      obtenerFuentesFinanciamiento.mockResolvedValue({
        data: { fuentesFinanciamiento: ['FONDO_GENERAL'], fuenteRecursos: '' },
      });
      montar();
      const selector = await screen.findByLabelText('Fuente de financiamiento 1');
      expect(selector.querySelectorAll('option')).toHaveLength(7);
    });
  });
});
