import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { EtapasPage } from './EtapasPage';

const obtenerProyecto = vi.fn();
const listarEtapas = vi.fn();
const actualizarEtapas = vi.fn();
const navigate = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    preinversionApi: { obtenerProyecto: (...a: unknown[]) => obtenerProyecto(...a) },
    etapasApi: {
      listarEtapas: (...a: unknown[]) => listarEtapas(...a),
      actualizarEtapas: (...a: unknown[]) => actualizarEtapas(...a),
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

function etapa(overrides: Partial<Record<string, unknown>> = {}) {
  return {
    nombreEtapa: 'PERFIL',
    costo: null,
    fechaInicio: null,
    fechaFin: null,
    habilitadoParaRegistro: true,
    tieneOpinionTecnica: false,
    bloqueadaPorModificacion: false,
    ...overrides,
  };
}

function renderizar() {
  return render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/7/etapas']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/etapas" element={<EtapasPage />} />
      </Routes>
    </MemoryRouter>,
  );
}

beforeEach(() => {
  [obtenerProyecto, listarEtapas, actualizarEtapas, navigate, swalFire].forEach((mock) => mock.mockReset());
  swalFire.mockResolvedValue({ isConfirmed: true });
  rolesActivos = ['TECNICO_URP'];
});

describe('EtapasPage', () => {
  it('guarda costo y fechas de todas las etapas en un solo PUT (RN04)', async () => {
    obtenerProyecto.mockResolvedValue({ data: { esProyectoEmergencia: false } });
    listarEtapas.mockResolvedValue({ data: [etapa({ nombreEtapa: 'PERFIL' }), etapa({ nombreEtapa: 'EJECUCION', costo: 5000 })] });
    actualizarEtapas.mockResolvedValue({
      data: [etapa({ nombreEtapa: 'PERFIL', costo: 1000, fechaInicio: '01/01/2026', fechaFin: '01/06/2026' }), etapa({ nombreEtapa: 'EJECUCION', costo: 5000 })],
    });

    renderizar();
    await screen.findByText('Perfil');

    const inputs = screen.getAllByRole('textbox');
    // Fila 0 = Perfil: costo, fechaInicio, fechaFin. Fila 1 = Ejecución: solo fechaInicio/fechaFin
    // (el costo no tiene input editable, RN05/RN11). RN04 exige fechas para ambas filas.
    fireEvent.change(inputs[0], { target: { value: '1000' } });
    fireEvent.change(inputs[1], { target: { value: '01/01/2026' } });
    fireEvent.change(inputs[2], { target: { value: '01/06/2026' } });
    fireEvent.change(inputs[3], { target: { value: '02/01/2026' } });
    fireEvent.change(inputs[4], { target: { value: '02/12/2026' } });

    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(actualizarEtapas).toHaveBeenCalledWith({
        idProyecto: 7,
        actualizarEtapasRequest: {
          etapas: [
            { nombreEtapa: 'PERFIL', costo: 1000, fechaInicio: '01/01/2026', fechaFin: '01/06/2026' },
            // El costo de EJECUCIÓN no tiene input editable (RN05/RN11): se reenvía tal cual se
            // cargó, y el servidor lo sobrescribe de todas formas.
            { nombreEtapa: 'EJECUCION', costo: 5000, fechaInicio: '02/01/2026', fechaFin: '02/12/2026' },
          ],
        },
      }),
    );
  });

  it('guarda aunque falten costo/fechas de una etapa (RN19, puramente visual)', async () => {
    obtenerProyecto.mockResolvedValue({ data: { esProyectoEmergencia: false } });
    listarEtapas.mockResolvedValue({ data: [etapa({ nombreEtapa: 'PERFIL' })] });
    actualizarEtapas.mockResolvedValue({ data: [etapa({ nombreEtapa: 'PERFIL' })] });

    renderizar();
    await screen.findByText('Perfil');

    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(actualizarEtapas).toHaveBeenCalledWith({
        idProyecto: 7,
        actualizarEtapasRequest: { etapas: [{ nombreEtapa: 'PERFIL', costo: 0, fechaInicio: '', fechaFin: '' }] },
      }),
    );
  });

  it('no permite guardar una fecha en un formato distinto de dd/mm/aaaa (RN04)', async () => {
    obtenerProyecto.mockResolvedValue({ data: { esProyectoEmergencia: false } });
    listarEtapas.mockResolvedValue({ data: [etapa({ nombreEtapa: 'PERFIL' })] });

    renderizar();
    await screen.findByText('Perfil');

    const inputs = screen.getAllByRole('textbox');
    fireEvent.change(inputs[1], { target: { value: '2026-01-01' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    expect(await screen.findByText('Formato inválido, use dd/mm/aaaa')).toBeInTheDocument();
    expect(actualizarEtapas).not.toHaveBeenCalled();
  });

  it('no permite editar una etapa bloqueada por modificación (RN13)', async () => {
    obtenerProyecto.mockResolvedValue({ data: { esProyectoEmergencia: false } });
    listarEtapas.mockResolvedValue({ data: [etapa({ nombreEtapa: 'PREFACTIBILIDAD', bloqueadaPorModificacion: true })] });

    renderizar();
    await screen.findByText('Prefactibilidad');

    expect(screen.getByText('Bloqueada por modificación')).toBeInTheDocument();
    screen.getAllByRole('textbox').forEach((input) => expect(input).toBeDisabled());
  });
});
