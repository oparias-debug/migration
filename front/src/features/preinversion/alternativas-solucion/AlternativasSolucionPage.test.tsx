import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { AxiosError, AxiosHeaders } from 'axios';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { AlternativasSolucionPage } from './AlternativasSolucionPage';

const obtenerAlternativasSolucion = vi.fn();
const guardarAlternativasSolucion = vi.fn();
const avanzarAAnalisisInteresados = vi.fn();
const navigate = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    alternativasSolucionApi: {
      obtenerAlternativasSolucion: (...a: unknown[]) => obtenerAlternativasSolucion(...a),
      guardarAlternativasSolucion: (...a: unknown[]) => guardarAlternativasSolucion(...a),
      avanzarAAnalisisInteresados: (...a: unknown[]) => avanzarAAnalisisInteresados(...a),
    },
  };
});

let rolesActivos: string[] = ['TECNICO_URP'];
vi.mock('../../../auth/useAuth', () => ({
  useAuth: () => ({ hasRole: (rol: string) => rolesActivos.includes(rol) }),
}));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

function registro(overrides: Partial<Record<string, unknown>> = {}) {
  return {
    idProyecto: 7,
    alternativas: [],
    justificacion: null,
    fechaUltimoGuardado: null,
    ...overrides,
  };
}

function error400(mensaje: string, codigo: string) {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('Request failed', '400', config, {}, {
    status: 400,
    statusText: '',
    data: { codigo, mensaje, detalles: [] },
    headers: {},
    config,
  });
}

function renderizar() {
  return render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/7/alternativas-solucion']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/alternativas-solucion" element={<AlternativasSolucionPage />} />
      </Routes>
    </MemoryRouter>,
  );
}

beforeEach(() => {
  [obtenerAlternativasSolucion, guardarAlternativasSolucion, avanzarAAnalisisInteresados, navigate, swalFire].forEach((mock) =>
    mock.mockReset(),
  );
  swalFire.mockResolvedValue({ isConfirmed: true });
  rolesActivos = ['TECNICO_URP'];
});

describe('AlternativasSolucionPage', () => {
  it('registra y guarda alternativas de solución seleccionando la más conveniente (camino feliz)', async () => {
    obtenerAlternativasSolucion.mockResolvedValue({ data: registro() });
    guardarAlternativasSolucion.mockResolvedValue({
      data: registro({
        alternativas: [
          { nombreAlternativa: 'Alternativa 1', montoAlternativa: 1000, descripcionAlternativa: 'Desc 1', seleccionada: true },
          { nombreAlternativa: 'Alternativa 2', montoAlternativa: 2000, descripcionAlternativa: 'Desc 2', seleccionada: false },
        ],
        justificacion: 'Es la más viable.',
      }),
    });

    renderizar();
    await screen.findByRole('button', { name: '+ Agregar alternativa' });

    // RN2-1: una fila por defecto ya visible antes de guardar.
    expect(screen.getAllByRole('textbox', { name: 'Nombre de la alternativa' })).toHaveLength(1);

    fireEvent.click(screen.getByRole('button', { name: '+ Agregar alternativa' }));

    const nombres = screen.getAllByRole('textbox', { name: 'Nombre de la alternativa' });
    expect(nombres).toHaveLength(2);
    fireEvent.change(nombres[0], { target: { value: 'Alternativa 1' } });
    fireEvent.change(nombres[1], { target: { value: 'Alternativa 2' } });

    fireEvent.click(screen.getAllByRole('radio')[0]);
    fireEvent.change(screen.getByLabelText('Justificación'), { target: { value: 'Es la más viable.' } });

    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(guardarAlternativasSolucion).toHaveBeenCalled());
    const solicitud = guardarAlternativasSolucion.mock.calls[0][0];
    expect(solicitud.idProyecto).toBe(7);
    expect(solicitud.registroAlternativasRequest.alternativas).toEqual([
      { nombreAlternativa: 'Alternativa 1', montoAlternativa: undefined, descripcionAlternativa: undefined, seleccionada: true },
      { nombreAlternativa: 'Alternativa 2', montoAlternativa: undefined, descripcionAlternativa: undefined, seleccionada: false },
    ]);
    expect(solicitud.registroAlternativasRequest.justificacion).toBe('Es la más viable.');
    expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'success' }));
  });

  it('agrega una nueva fila de alternativa (RN2-1)', async () => {
    obtenerAlternativasSolucion.mockResolvedValue({ data: registro() });

    renderizar();
    await screen.findByRole('button', { name: '+ Agregar alternativa' });

    fireEvent.click(screen.getByRole('button', { name: '+ Agregar alternativa' }));

    expect(screen.getAllByRole('textbox', { name: 'Nombre de la alternativa' })).toHaveLength(2);
  });

  it('elimina una alternativa (RN2-2)', async () => {
    obtenerAlternativasSolucion.mockResolvedValue({
      data: registro({
        alternativas: [
          { nombreAlternativa: 'Alternativa 1', montoAlternativa: null, descripcionAlternativa: null, seleccionada: false },
          { nombreAlternativa: 'Alternativa 2', montoAlternativa: null, descripcionAlternativa: null, seleccionada: false },
        ],
      }),
    });

    renderizar();
    await screen.findByDisplayValue('Alternativa 1');

    fireEvent.click(screen.getAllByRole('button', { name: 'x' })[0]);

    expect(screen.queryByDisplayValue('Alternativa 1')).not.toBeInTheDocument();
    expect(screen.getByDisplayValue('Alternativa 2')).toBeInTheDocument();
  });

  it('solo puede seleccionarse una alternativa como la más conveniente (RN2-6)', async () => {
    obtenerAlternativasSolucion.mockResolvedValue({
      data: registro({
        alternativas: [
          { nombreAlternativa: 'Alternativa 1', montoAlternativa: null, descripcionAlternativa: null, seleccionada: true },
          { nombreAlternativa: 'Alternativa 2', montoAlternativa: null, descripcionAlternativa: null, seleccionada: false },
        ],
      }),
    });

    renderizar();
    await screen.findByDisplayValue('Alternativa 1');

    const radios = screen.getAllByRole('radio');
    expect(radios[0]).toBeChecked();

    fireEvent.click(radios[1]);

    expect(radios[0]).not.toBeChecked();
    expect(radios[1]).toBeChecked();
  });

  it('muestra la alerta "Debe ingresar al menos una alternativa" al avanzar (RN2-4)', async () => {
    obtenerAlternativasSolucion.mockResolvedValue({ data: registro() });
    avanzarAAnalisisInteresados.mockRejectedValue(error400('Debe ingresar al menos una alternativa', 'SIN_ALTERNATIVAS'));

    renderizar();
    await screen.findByRole('button', { name: 'Siguiente' });

    fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));

    await waitFor(() =>
      expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'error', text: 'Debe ingresar al menos una alternativa' })),
    );
  });

  it('muestra la alerta "Debe completarse el campo Justificación" al avanzar (RN2-3)', async () => {
    obtenerAlternativasSolucion.mockResolvedValue({ data: registro() });
    avanzarAAnalisisInteresados.mockRejectedValue(
      error400('Debe completarse el campo Justificación', 'JUSTIFICACION_REQUERIDA'),
    );

    renderizar();
    await screen.findByRole('button', { name: 'Siguiente' });

    fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));

    await waitFor(() =>
      expect(swalFire).toHaveBeenCalledWith(
        expect.objectContaining({ icon: 'error', text: 'Debe completarse el campo Justificación' }),
      ),
    );
  });

  it('no permite editar en modo de solo lectura (Técnico PRE)', async () => {
    rolesActivos = ['TECNICO_PRE'];
    obtenerAlternativasSolucion.mockResolvedValue({
      data: registro({
        alternativas: [{ nombreAlternativa: 'Alternativa 1', montoAlternativa: null, descripcionAlternativa: null, seleccionada: false }],
        justificacion: 'Justificación previa.',
      }),
    });

    renderizar();
    await screen.findByDisplayValue('Alternativa 1');

    screen.getAllByRole('textbox').forEach((input) => expect(input).toBeDisabled());
    screen.getAllByRole('radio').forEach((input) => expect(input).toBeDisabled());
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Siguiente' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: '+ Agregar alternativa' })).not.toBeInTheDocument();
  });
});
