import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { FichaInformacionGeneralPage } from './FichaInformacionGeneralPage';

const obtenerFichaInformacionGeneral = vi.fn();
const seleccionarCoEjecutor = vi.fn();
const navigate = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    etapasApi: {
      obtenerFichaInformacionGeneral: (...a: unknown[]) => obtenerFichaInformacionGeneral(...a),
      seleccionarCoEjecutor: (...a: unknown[]) => seleccionarCoEjecutor(...a),
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

function ficha(overrides: Partial<Record<string, unknown>> = {}) {
  return {
    idProyecto: 7,
    institucion: { idInstitucion: 1, nombre: 'Ministerio de Obras Públicas' },
    unidadEjecutora: { idUnidadEjecutora: 1, nombre: 'Unidad de Inversión Pública' },
    coEjecutor: null,
    iniciativaInversion: 'PROYECTO',
    nombreProyecto: 'Construcción de puente sobre el río Lempa',
    montoEstimadoInversion: 150000,
    montoAjustadoEjecucion: null,
    sector: { idSector: 3, nombre: 'Transporte', macrosector: { idMacrosector: 1, nombre: 'Infraestructura' } },
    ejeTematico: { idEjeTematico: 2, nombre: 'Conectividad' },
    esProyectoGrdGrcAcc: false,
    esProyectoEmergencia: false,
    descripcionProyecto: 'Puente de dos carriles sobre el río Lempa.',
    ...overrides,
  };
}

function renderizar() {
  return render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/7/ficha-informacion-general']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/ficha-informacion-general" element={<FichaInformacionGeneralPage />} />
      </Routes>
    </MemoryRouter>,
  );
}

beforeEach(() => {
  [obtenerFichaInformacionGeneral, seleccionarCoEjecutor, navigate, swalFire].forEach((mock) => mock.mockReset());
  swalFire.mockResolvedValue({ isConfirmed: true });
  rolesActivos = ['TECNICO_URP'];
});

describe('FichaInformacionGeneralPage', () => {
  it('muestra la ficha de solo lectura (RN14)', async () => {
    obtenerFichaInformacionGeneral.mockResolvedValue({ data: ficha() });

    renderizar();

    expect(await screen.findByText('Construcción de puente sobre el río Lempa')).toBeInTheDocument();
    expect(screen.queryByText('Monto de inversión (ajustado en Ejecución)')).not.toBeInTheDocument();
  });

  it('muestra el monto ajustado solo cuando no es nulo (RN17)', async () => {
    obtenerFichaInformacionGeneral.mockResolvedValue({ data: ficha({ montoAjustadoEjecucion: 175000 }) });

    renderizar();

    expect(await screen.findByText('Monto de inversión (ajustado en Ejecución)')).toBeInTheDocument();
    expect(screen.getByText('175,000')).toBeInTheDocument();
  });

  it('no muestra la sección de Co-ejecutor a un Técnico URP (RN16)', async () => {
    obtenerFichaInformacionGeneral.mockResolvedValue({ data: ficha() });

    renderizar();

    await screen.findByText('Construcción de puente sobre el río Lempa');
    expect(screen.queryByText('Selección de Co-ejecutor')).not.toBeInTheDocument();
  });

  it('permite a un Coordinador SYMP asignar el Co-ejecutor por ID (RN16)', async () => {
    rolesActivos = ['COORDINADOR_SYMP'];
    obtenerFichaInformacionGeneral.mockResolvedValue({ data: ficha() });
    seleccionarCoEjecutor.mockResolvedValue({
      data: ficha({ coEjecutor: { idUnidadEjecutora: 9, nombre: 'Unidad Co-ejecutora' } }),
    });

    renderizar();

    fireEvent.change(await screen.findByLabelText('ID de Unidad Ejecutora Co-ejecutora'), { target: { value: '9' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(seleccionarCoEjecutor).toHaveBeenCalledWith({ idProyecto: 7, seleccionCoEjecutorRequest: { idUnidadEjecutoraCoEjecutora: 9 } }),
    );
  });
});
