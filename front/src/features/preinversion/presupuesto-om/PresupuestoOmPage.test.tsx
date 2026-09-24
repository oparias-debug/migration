import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';

const obtenerPresupuestoOM = vi.fn();
const configurarPresupuestoOM = vi.fn();
const registrarActividad = vi.fn();
const eliminarActividad = vi.fn();
const guardarPresupuestoOM = vi.fn();
const listarInsumosTipo = vi.fn();
const swalFire = vi.fn();
const navigate = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  presupuestoOmApi: {
    obtenerPresupuestoOM: (...a: unknown[]) => obtenerPresupuestoOM(...a),
    configurarPresupuestoOM: (...a: unknown[]) => configurarPresupuestoOM(...a),
    registrarActividad: (...a: unknown[]) => registrarActividad(...a),
    eliminarActividad: (...a: unknown[]) => eliminarActividad(...a),
    guardarPresupuestoOM: (...a: unknown[]) => guardarPresupuestoOM(...a),
  },
  catalogoInsumosApi: { listarInsumosTipo: (...a: unknown[]) => listarInsumosTipo(...a) },
}));

let rolesActivos: string[] = ['TECNICO_URP'];
vi.mock('../../../auth/useAuth', () => ({ useAuth: () => ({ hasRole: (r: string) => rolesActivos.includes(r) }) }));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

const { PresupuestoOmPage } = await import('./PresupuestoOmPage');

beforeAll(() => {
  HTMLDialogElement.prototype.showModal = function abrir() { this.open = true; };
  HTMLDialogElement.prototype.close = function cerrar() { this.open = false; };
});

const actividad = (extra: Record<string, unknown> = {}) => ({
  idActividad: 1,
  numero: 1,
  nombreActividad: 'Vigilancia',
  insumos: [],
  totalPeriodo1PrecioMercado: 1200,
  ...extra,
});

const presupuesto = (extra: Record<string, unknown> = {}) => ({
  idProyecto: 7,
  tipoCosto: 'OPERACION',
  vidaUtil: 3,
  tasaCrecimientoCostos: 1.5,
  costosOperacion: { actividades: [actividad()], totalPorPeriodoPrecioMercado: { periodo1: 1200 } },
  ...extra,
});

const renderizar = () =>
  render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/7/presupuesto-om']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/presupuesto-om" element={<PresupuestoOmPage />} />
      </Routes>
    </MemoryRouter>,
  );

beforeEach(() => {
  [obtenerPresupuestoOM, configurarPresupuestoOM, registrarActividad, eliminarActividad, guardarPresupuestoOM, listarInsumosTipo, swalFire, navigate]
    .forEach((m) => m.mockReset());
  rolesActivos = ['TECNICO_URP'];
  swalFire.mockResolvedValue({ isConfirmed: true });
  listarInsumosTipo.mockResolvedValue({ data: [{ codigo: 'MO', nombre: 'Mano de obra' }] });
});

describe('PresupuestoOmPage (CU-PRE-18)', () => {
  it('muestra la tabla del tipo de costo configurado, con una columna por período', async () => {
    obtenerPresupuestoOM.mockResolvedValue({ data: presupuesto() });
    renderizar();

    expect(await screen.findByText('Costos de operación', { selector: 'h2' })).toBeInTheDocument();
    expect(screen.getByText('Vigilancia')).toBeInTheDocument();
    // vidaUtil = 3 → tres columnas de período.
    ['Período 1', 'Período 2', 'Período 3'].forEach((c) => expect(screen.getByText(c)).toBeInTheDocument());
    // No se dibuja la tabla de mantenimiento: el contrato la deja nula (RN04).
    expect(screen.queryByRole('heading', { name: 'Costos de mantenimiento' })).not.toBeInTheDocument();
  });

  it('configura el tipo de costo, la vida útil y la tasa', async () => {
    obtenerPresupuestoOM.mockResolvedValue({ data: presupuesto({ tipoCosto: null, vidaUtil: null, costosOperacion: null }) });
    configurarPresupuestoOM.mockResolvedValue({ data: presupuesto() });
    renderizar();

    fireEvent.change(await screen.findByLabelText(/tipo de costo/i), { target: { value: 'O_M' } });
    fireEvent.change(screen.getByLabelText(/Períodos a proyectar/i), { target: { value: '5' } });
    fireEvent.change(screen.getByLabelText(/Tasa de crecimiento/i), { target: { value: '2' } });
    fireEvent.click(screen.getByRole('button', { name: 'Aceptar' }));

    await waitFor(() => expect(configurarPresupuestoOM).toHaveBeenCalled());
    expect(configurarPresupuestoOM.mock.calls[0][0].configurarPresupuestoOMRequest).toMatchObject({
      tipoCosto: 'O_M',
      vidaUtil: 5,
      tasaCrecimientoCostos: 2,
    });
  });

  it('registra una actividad con el costo del primer período', async () => {
    obtenerPresupuestoOM.mockResolvedValue({ data: presupuesto() });
    registrarActividad.mockResolvedValue({ data: {} });
    renderizar();

    fireEvent.click(await screen.findByRole('button', { name: 'Agregar actividad' }));
    fireEvent.change(screen.getByLabelText('Nombre de la actividad*'), { target: { value: 'Limpieza' } });
    fireEvent.change(screen.getByLabelText(/Costo de Mano de obra/i), { target: { value: '800' } });
    fireEvent.click(document.querySelector('.modal-detalle .btn.primario') as HTMLButtonElement);

    await waitFor(() => expect(registrarActividad).toHaveBeenCalled());
    const enviado = registrarActividad.mock.calls[0][0];
    expect(enviado.tipoCostoTabla).toBe('OPERACION');
    expect(enviado.actividadRequest).toMatchObject({
      nombreActividad: 'Limpieza',
      insumos: [{ tipoInsumo: 'MO', costoPeriodo1PrecioMercado: 800 }],
    });
  });

  it('no registra una actividad sin nombre', async () => {
    obtenerPresupuestoOM.mockResolvedValue({ data: presupuesto() });
    renderizar();

    fireEvent.click(await screen.findByRole('button', { name: 'Agregar actividad' }));
    fireEvent.change(screen.getByLabelText(/Costo de Mano de obra/i), { target: { value: '800' } });
    fireEvent.click(document.querySelector('.modal-detalle .btn.primario') as HTMLButtonElement);

    expect(await screen.findByRole('alert')).toHaveTextContent('nombre de la actividad es obligatorio');
    expect(registrarActividad).not.toHaveBeenCalled();
  });

  // RN09: el contrato pone en null los ajustados cuando el actor no los ve.
  it('oculta los precios ajustados cuando el contrato no los trae', async () => {
    obtenerPresupuestoOM.mockResolvedValue({ data: presupuesto() });
    renderizar();
    await screen.findByText('Vigilancia');
    expect(screen.queryByText('Total (precios ajustados)')).not.toBeInTheDocument();
  });

  it('un actor que no edita no ve las acciones', async () => {
    rolesActivos = ['TECNICO_PRE'];
    obtenerPresupuestoOM.mockResolvedValue({ data: presupuesto() });
    renderizar();

    await screen.findByText('Vigilancia');
    expect(screen.queryByRole('button', { name: 'Agregar actividad' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Aceptar' })).not.toBeInTheDocument();
  });
});
