import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { CalendarioPage } from './CalendarioPage';

const crearCalendario = vi.fn();
const editarCalendario = vi.fn();
const eliminarCalendario = vi.fn();
const cambiarEstadoCalendario = vi.fn();
const agregarPeriodoLaboral = vi.fn();
const agregarPeriodoNoLaboral = vi.fn();
const registrarExcepcion = vi.fn();
const consultarTipoDia = vi.fn();
const consultarDiasLaboralesEntreFechas = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/administracionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/administracionApi')>();
  return {
    ...actual,
    calendariosApi: {
      crearCalendario: (...a: unknown[]) => crearCalendario(...a),
      editarCalendario: (...a: unknown[]) => editarCalendario(...a),
      eliminarCalendario: (...a: unknown[]) => eliminarCalendario(...a),
      cambiarEstadoCalendario: (...a: unknown[]) => cambiarEstadoCalendario(...a),
      agregarPeriodoLaboral: (...a: unknown[]) => agregarPeriodoLaboral(...a),
      agregarPeriodoNoLaboral: (...a: unknown[]) => agregarPeriodoNoLaboral(...a),
      registrarExcepcion: (...a: unknown[]) => registrarExcepcion(...a),
    },
    consultasCalendarioApi: {
      consultarTipoDia: (...a: unknown[]) => consultarTipoDia(...a),
      consultarDiasLaboralesEntreFechas: (...a: unknown[]) => consultarDiasLaboralesEntreFechas(...a),
    },
  };
});
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

const irA = (nombre: string) => fireEvent.click(screen.getByRole('tab', { name: nombre }));
const crear = () => {
  fireEvent.change(screen.getByLabelText('Código*'), { target: { value: 'CAL-2026' } });
  fireEvent.change(screen.getByLabelText('Nombre*'), { target: { value: 'Calendario 2026' } });
  fireEvent.change(screen.getByLabelText('Fecha de inicio*'), { target: { value: '2026-01-01' } });
  fireEvent.change(screen.getByLabelText('Fecha de fin*'), { target: { value: '2026-12-31' } });
  fireEvent.click(screen.getByRole('button', { name: 'Crear calendario' }));
};

describe('CalendarioPage · CU-ADM-04', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    swalFire.mockResolvedValue({ isConfirmed: true });
    crearCalendario.mockResolvedValue({ data: {} });
  });

  it('dice qué no permite todavía el API', () => {
    render(<CalendarioPage />);
    expect(screen.getByText(/ver la lista de calendarios/i)).toBeInTheDocument();
  });

  it('crea el calendario y lo deja como calendario activo', async () => {
    render(<CalendarioPage />);
    crear();
    await waitFor(() =>
      expect(crearCalendario).toHaveBeenCalledWith({
        crearCalendarioRequest: {
          codigo: 'CAL-2026',
          nombre: 'Calendario 2026',
          descripcion: undefined,
          fechaInicio: '2026-01-01',
          fechaFin: '2026-12-31',
          estado: 'ACTIVO',
        },
      }),
    );
    await waitFor(() => expect(screen.getByLabelText(/Código del calendario sobre el que/)).toHaveValue('CAL-2026'));
  });

  it('sin calendario activo no deja agregar períodos ni excepciones', () => {
    render(<CalendarioPage />);
    irA('Períodos');
    expect(screen.getByRole('button', { name: 'Agregar período' })).toBeDisabled();
    irA('Excepciones');
    expect(screen.getByRole('button', { name: 'Registrar excepción' })).toBeDisabled();
  });

  // Las tres formas de repetición del contrato mandan campos distintos.
  it('manda la repetición semanal con sus días', async () => {
    agregarPeriodoNoLaboral.mockResolvedValue({ data: {} });
    render(<CalendarioPage />);
    crear();
    await waitFor(() => expect(crearCalendario).toHaveBeenCalled());

    irA('Períodos');
    fireEvent.change(screen.getByLabelText('Código del período*'), { target: { value: 'FIN-SEMANA' } });
    fireEvent.change(screen.getByLabelText('Nombre del período*'), { target: { value: 'Fines de semana' } });
    fireEvent.change(screen.getByLabelText('Se repite*'), { target: { value: 'SEMANAL' } });
    await screen.findByText('Días de la semana');
    fireEvent.click(screen.getByLabelText('Sábado'));
    fireEvent.click(screen.getByLabelText('Domingo'));
    fireEvent.change(screen.getByLabelText('Fecha de inicio*'), { target: { value: '2026-01-01' } });
    fireEvent.change(screen.getByLabelText('Fecha de fin*'), { target: { value: '2026-12-31' } });
    fireEvent.click(screen.getByRole('button', { name: 'Agregar período' }));

    await waitFor(() => expect(agregarPeriodoNoLaboral).toHaveBeenCalled());
    const enviado = agregarPeriodoNoLaboral.mock.calls[0][0];
    expect(enviado.codigoCalendario).toBe('CAL-2026');
    expect(enviado.periodoNoLaboralRequest.recurrencia).toEqual({
      tipoRecurrencia: 'SEMANAL',
      fechaInicio: '2026-01-01',
      fechaFin: '2026-12-31',
      diasDeLaSemana: ['SATURDAY', 'SUNDAY'],
    });
  });

  it('la repetición mensual manda días y meses, sin fechas', async () => {
    agregarPeriodoNoLaboral.mockResolvedValue({ data: {} });
    render(<CalendarioPage />);
    crear();
    await waitFor(() => expect(crearCalendario).toHaveBeenCalled());

    irA('Períodos');
    fireEvent.change(screen.getByLabelText('Código del período*'), { target: { value: 'CIERRES' } });
    fireEvent.change(screen.getByLabelText('Nombre del período*'), { target: { value: 'Cierres de período' } });
    fireEvent.change(screen.getByLabelText('Se repite*'), { target: { value: 'MENSUAL' } });
    await screen.findByText('Meses');
    fireEvent.change(screen.getByLabelText('Días del mes (separados por coma)*'), { target: { value: '1, 15, 31' } });
    fireEvent.click(screen.getByLabelText('Enero'));
    fireEvent.click(screen.getByRole('button', { name: 'Agregar período' }));

    await waitFor(() => expect(agregarPeriodoNoLaboral).toHaveBeenCalled());
    expect(agregarPeriodoNoLaboral.mock.calls[0][0].periodoNoLaboralRequest.recurrencia).toEqual({
      tipoRecurrencia: 'MENSUAL',
      diasDelMes: [1, 15, 31],
      meses: ['JANUARY'],
    });
  });

  it('registra una excepción', async () => {
    registrarExcepcion.mockResolvedValue({ data: {} });
    render(<CalendarioPage />);
    crear();
    await waitFor(() => expect(crearCalendario).toHaveBeenCalled());

    irA('Excepciones');
    fireEvent.change(screen.getByLabelText('Fecha*'), { target: { value: '2026-12-25' } });
    fireEvent.change(screen.getByLabelText('Descripción*'), { target: { value: 'Navidad' } });
    fireEvent.click(screen.getByRole('button', { name: 'Registrar excepción' }));

    await waitFor(() =>
      expect(registrarExcepcion).toHaveBeenCalledWith({
        codigoCalendario: 'CAL-2026',
        excepcionRequest: { fecha: '2026-12-25', tipo: 'DIA_NO_LABORAL', descripcion: 'Navidad' },
      }),
    );
  });

  it('comprueba lo cargado preguntando qué tipo de día es', async () => {
    consultarTipoDia.mockResolvedValue({ data: { tipoDia: 'NO_LABORAL' } });
    render(<CalendarioPage />);
    crear();
    await waitFor(() => expect(crearCalendario).toHaveBeenCalled());

    irA('Comprobar');
    fireEvent.change(screen.getByLabelText('Fecha'), { target: { value: '2026-12-25' } });
    fireEvent.click(screen.getByRole('button', { name: '¿Qué tipo de día es?' }));

    await waitFor(() => expect(screen.getByText(/Ese día es: NO_LABORAL/)).toBeInTheDocument());
    expect(consultarTipoDia).toHaveBeenCalledWith({ codigoCalendario: 'CAL-2026', fecha: '2026-12-25' });
  });
});
