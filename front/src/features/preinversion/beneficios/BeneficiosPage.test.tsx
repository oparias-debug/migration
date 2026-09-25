import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';

const obtenerBeneficiosProyecto = vi.fn();
const registrarBeneficio = vi.fn();
const eliminarBeneficio = vi.fn();
const guardarBeneficiosProyecto = vi.fn();
const listarParametrosBeneficio = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  beneficiosApi: {
    obtenerBeneficiosProyecto: (...a: unknown[]) => obtenerBeneficiosProyecto(...a),
    registrarBeneficio: (...a: unknown[]) => registrarBeneficio(...a),
    eliminarBeneficio: (...a: unknown[]) => eliminarBeneficio(...a),
    guardarBeneficiosProyecto: (...a: unknown[]) => guardarBeneficiosProyecto(...a),
  },
  catalogoParametrosApi: { listarParametrosBeneficio: (...a: unknown[]) => listarParametrosBeneficio(...a) },
}));

let rolesActivos: string[] = ['TECNICO_URP'];
vi.mock('../../../auth/useAuth', () => ({ useAuth: () => ({ hasRole: (r: string) => rolesActivos.includes(r) }) }));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

const { BeneficiosPage } = await import('./BeneficiosPage');

const PARAMETROS = {
  data: [
    { codigo: 'DIVISA', nombre: 'Precio social de la divisa', factorCorreccion: 1.1 },
    { codigo: 'MANO_OBRA', nombre: 'Valor social de la mano de obra', factorCorreccion: 0.9 },
  ],
};

const beneficio = (extra: Record<string, unknown> = {}) => ({
  idBeneficio: 1,
  tipoBeneficio: 'BENEFICIOS_DIRECTOS',
  nombreBeneficio: 'Consultas médicas atendidas',
  parametro: { codigo: 'DIVISA', nombre: 'Precio social de la divisa', factorCorreccion: 1.1 },
  tipoIngreso: 'AUTOMATICO',
  montosPorPeriodo: [
    { periodo: 1, montoPrecioMercado: 800, montoPrecioAjustado: 880 },
    { periodo: 2, montoPrecioMercado: 840, montoPrecioAjustado: 924 },
  ],
  ...extra,
});

const respuesta = (extra: Record<string, unknown> = {}) => ({
  data: {
    idProyecto: 14,
    vidaUtil: 2,
    beneficiosDirectos: [beneficio()],
    beneficiosIndirectos: [],
    externalidades: [],
    flujoBeneficiosPrecioMercadoPorPeriodo: [800, 840],
    flujoBeneficiosPrecioAjustadoPorPeriodo: null,
    valorRescate: null,
    tipoBien: null,
    fcTipoBien: null,
    valorRescateAjustado: null,
    ...extra,
  },
});

const montar = () =>
  render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/14/beneficios']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/beneficios" element={<BeneficiosPage />} />
      </Routes>
    </MemoryRouter>,
  );

const abrirModal = async (indice = 0) => {
  const botones = await screen.findAllByRole('button', { name: 'Agregar beneficio' });
  fireEvent.click(botones[indice]);
};
const guardarModal = () => fireEvent.click(document.querySelector('.modal-detalle .btn.primario') as HTMLButtonElement);

beforeEach(() => {
  rolesActivos = ['TECNICO_URP'];
  obtenerBeneficiosProyecto.mockReset().mockResolvedValue(respuesta());
  listarParametrosBeneficio.mockReset().mockResolvedValue(PARAMETROS);
  registrarBeneficio.mockReset().mockResolvedValue({ data: {} });
  eliminarBeneficio.mockReset().mockResolvedValue({ data: {} });
  guardarBeneficiosProyecto.mockReset().mockResolvedValue({ data: {} });
  swalFire.mockReset().mockResolvedValue({ isConfirmed: true });
  // Sin el atributo `open`, jsdom deja el <dialog> en display:none y las
  // consultas por rol no ven su contenido.
  HTMLDialogElement.prototype.showModal = function abrir(this: HTMLDialogElement) { this.open = true; };
  HTMLDialogElement.prototype.close = function cerrar(this: HTMLDialogElement) { this.open = false; };
});

describe('Beneficios del Proyecto (CU-PRE-20)', () => {
  it('lista las tres secciones con sus montos por período', async () => {
    montar();
    expect(await screen.findByText('Beneficios directos')).toBeInTheDocument();
    expect(screen.getByText('Beneficios indirectos')).toBeInTheDocument();
    expect(screen.getByText('Externalidades')).toBeInTheDocument();
    expect(screen.getByText('Consultas médicas atendidas')).toBeInTheDocument();
    // El mismo importe sale en la fila del beneficio y en la del flujo.
    expect(screen.getAllByText('$800.00')).toHaveLength(2);
    expect(screen.getAllByText('Todavía no hay beneficios registrados.')).toHaveLength(2);
  });

  // RN05: los períodos salen de la vida útil de CU-PRE-18.
  it('sin vida útil configurada lo dice, en vez de una tabla sin columnas', async () => {
    obtenerBeneficiosProyecto.mockResolvedValue(respuesta({ vidaUtil: null, beneficiosDirectos: [] }));
    montar();
    expect(await screen.findByText(/no tiene vida útil configurada/)).toBeInTheDocument();
    expect(screen.queryByText('Período 1')).not.toBeInTheDocument();
  });

  // RN09/DN-01: el Técnico URP no recibe los precios ajustados.
  it('los precios ajustados sólo se pintan si el servidor los manda', async () => {
    montar();
    expect(await screen.findByText('Flujo a precios de mercado')).toBeInTheDocument();
    expect(screen.queryByText('Flujo a precios ajustados')).not.toBeInTheDocument();
    expect(screen.queryByText('Factor de corrección')).not.toBeInTheDocument();
  });

  it('a quien sí los recibe se le muestran, con el factor y el rescate ajustado', async () => {
    obtenerBeneficiosProyecto.mockResolvedValue(
      respuesta({ flujoBeneficiosPrecioAjustadoPorPeriodo: [880, 924], fcTipoBien: 1.1, valorRescateAjustado: 16500 }),
    );
    montar();
    expect(await screen.findByText('Flujo a precios ajustados')).toBeInTheDocument();
    expect(screen.getByText('$880.00')).toBeInTheDocument();
    expect(screen.getByText('1.1')).toBeInTheDocument();
    expect(screen.getByText('$16,500.00')).toBeInTheDocument();
  });

  it('el Técnico PRE la consulta sin poder editar', async () => {
    rolesActivos = ['TECNICO_PRE'];
    montar();
    await screen.findByText('Consultas médicas atendidas');
    expect(screen.queryByRole('button', { name: 'Agregar beneficio' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Quitar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
  });

  it('guarda el valor de rescate y el tipo de bien', async () => {
    montar();
    await screen.findByText('Consultas médicas atendidas');

    fireEvent.change(screen.getByLabelText('Valor de rescate (US$)'), { target: { value: '15000' } });
    fireEvent.change(screen.getByLabelText('Tipo de bien'), { target: { value: 'EDIFICIOS' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(guardarBeneficiosProyecto).toHaveBeenCalledWith({
        idProyecto: 14,
        guardarBeneficiosProyectoRequest: { valorRescate: 15000, tipoBien: 'EDIFICIOS' },
      }),
    );
  });

  it('quitar un beneficio se confirma antes', async () => {
    montar();
    fireEvent.click(await screen.findByRole('button', { name: 'Quitar' }));
    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'warning' })));
    await waitFor(() => expect(eliminarBeneficio).toHaveBeenCalledWith({ idProyecto: 14, idBeneficio: 1 }));
  });

  // El servidor devuelve hoy el parámetro como texto, no como el objeto del contrato.
  it('el parámetro se lee venga como objeto o como texto', async () => {
    obtenerBeneficiosProyecto.mockResolvedValue(
      respuesta({ beneficiosDirectos: [beneficio({ parametro: 'Precio social de la divisa' })] }),
    );
    montar();
    expect(await screen.findByText('Precio social de la divisa')).toBeInTheDocument();
  });
});

describe('Detalle del beneficio (CU-PRE-20, Anexo A.2)', () => {
  it('el modo Automático manda el monto del período 1 y la tasa', async () => {
    montar();
    await abrirModal();

    fireEvent.change(screen.getByLabelText('Nombre del beneficio'), { target: { value: 'Consultas atendidas' } });
    fireEvent.change(screen.getByLabelText('Parámetro*'), { target: { value: 'DIVISA' } });
    fireEvent.change(screen.getByLabelText('Tipo de ingreso*'), { target: { value: 'AUTOMATICO' } });
    fireEvent.change(screen.getByLabelText('Monto del período 1 (US$)'), { target: { value: '800' } });
    fireEvent.change(screen.getByLabelText('Tasa de crecimiento proyectado (%)'), { target: { value: '5' } });
    guardarModal();

    await waitFor(() =>
      expect(registrarBeneficio).toHaveBeenCalledWith({
        idProyecto: 14,
        beneficioRequest: {
          tipoBeneficio: 'BENEFICIOS_DIRECTOS',
          nombreBeneficio: 'Consultas atendidas',
          parametro: 'DIVISA',
          tipoIngreso: 'AUTOMATICO',
          montoPeriodo1: 800,
          tasaCrecimientoProyectado: 5,
        },
      }),
    );
  });

  it('el modo Manual manda un monto por período de la vida útil', async () => {
    montar();
    await abrirModal();

    fireEvent.change(screen.getByLabelText('Parámetro*'), { target: { value: 'MANO_OBRA' } });
    fireEvent.change(screen.getByLabelText('Tipo de ingreso*'), { target: { value: 'MANUAL' } });
    fireEvent.change(screen.getByLabelText('Monto del período 1'), { target: { value: '1000' } });
    fireEvent.change(screen.getByLabelText('Monto del período 2'), { target: { value: '2000' } });
    guardarModal();

    await waitFor(() =>
      expect(registrarBeneficio).toHaveBeenCalledWith(
        expect.objectContaining({
          beneficioRequest: expect.objectContaining({ montosPrecioMercadoPorPeriodo: [1000, 2000] }),
        }),
      ),
    );
  });

  // Los tres casos que el servidor rechaza con 400; se dicen en el propio modal.
  it('exige el parámetro', async () => {
    montar();
    await abrirModal();
    guardarModal();
    expect(await screen.findByRole('alert')).toHaveTextContent('Elija el parámetro del beneficio.');
    expect(registrarBeneficio).not.toHaveBeenCalled();
  });

  it('exige el tipo de ingreso', async () => {
    montar();
    await abrirModal();
    fireEvent.change(screen.getByLabelText('Parámetro*'), { target: { value: 'DIVISA' } });
    guardarModal();
    expect(await screen.findByRole('alert')).toHaveTextContent('Elija el tipo de ingreso.');
  });

  it('en Manual exige el monto de al menos un período', async () => {
    montar();
    await abrirModal();
    fireEvent.change(screen.getByLabelText('Parámetro*'), { target: { value: 'DIVISA' } });
    fireEvent.change(screen.getByLabelText('Tipo de ingreso*'), { target: { value: 'MANUAL' } });
    guardarModal();
    expect(await screen.findByRole('alert')).toHaveTextContent('Registre el monto de al menos un período.');
  });

  it('"Salir" descarta sin llamar al servidor', async () => {
    montar();
    await abrirModal();
    fireEvent.click(screen.getByRole('button', { name: 'Salir' }));
    await waitFor(() => expect(document.querySelector('.modal-detalle')).not.toBeInTheDocument());
    expect(registrarBeneficio).not.toHaveBeenCalled();
  });
});
