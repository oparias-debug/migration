import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { AxiosError, AxiosHeaders } from 'axios';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { FichaEmergenciaPage } from './FichaEmergenciaPage';

const obtenerFichaEmergencia = vi.fn();
const registrarFichaEmergencia = vi.fn();
const listarProductosIndicadores = vi.fn();
const listarTiposCosto = vi.fn();
const listarUbicacionesGeograficas = vi.fn();
const navigate = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    etapasApi: {
      obtenerFichaEmergencia: (...a: unknown[]) => obtenerFichaEmergencia(...a),
      registrarFichaEmergencia: (...a: unknown[]) => registrarFichaEmergencia(...a),
    },
    catalogoEtapasApi: {
      listarProductosIndicadores: (...a: unknown[]) => listarProductosIndicadores(...a),
      listarTiposCosto: (...a: unknown[]) => listarTiposCosto(...a),
      listarUbicacionesGeograficas: (...a: unknown[]) => listarUbicacionesGeograficas(...a),
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
    cup: 'CUP-2026-001',
    nombreProyecto: 'Reconstrucción de puente rural',
    etapaActual: 'PERFIL',
    etapaFutura: 'EJECUCION',
    planteamientoProblema: null,
    objetivoGeneral: null,
    descripcionProyecto: null,
    productos: [],
    departamento: null,
    distrito: null,
    latitud: null,
    longitud: null,
    direccionEspecifica: null,
    poblacionObjetivo: null,
    inversionEstimada: null,
    componentesCosto: [],
    fuentesFinanciamiento: [],
    fuenteRecursos: null,
    ...overrides,
  };
}

function error400(detalles: { campo: string; mensaje: string }[]) {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('Request failed', '400', config, {}, {
    status: 400,
    statusText: '',
    data: { codigo: 'VALIDACION', mensaje: 'Existen campos sin diligenciar', detalles },
    headers: {},
    config,
  });
}

function renderizar() {
  return render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/7/ficha-emergencia']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/ficha-emergencia" element={<FichaEmergenciaPage />} />
      </Routes>
    </MemoryRouter>,
  );
}

beforeEach(() => {
  [
    obtenerFichaEmergencia,
    registrarFichaEmergencia,
    listarProductosIndicadores,
    listarTiposCosto,
    listarUbicacionesGeograficas,
    navigate,
    swalFire,
  ].forEach((mock) => mock.mockReset());
  obtenerFichaEmergencia.mockResolvedValue({ data: ficha() });
  listarProductosIndicadores.mockResolvedValue({ data: [{ codigoProducto: 'PROD-1', producto: 'Kits de alimentos' }] });
  listarTiposCosto.mockResolvedValue({ data: [] });
  listarUbicacionesGeograficas.mockResolvedValue({ data: [{ distrito: 'San Salvador', departamento: 'San Salvador', region: 'Central' }] });
  swalFire.mockResolvedValue({ isConfirmed: true });
  rolesActivos = ['TECNICO_URP'];
});

describe('FichaEmergenciaPage', () => {
  it('registra la ficha con los campos obligatorios diligenciados (FA-05)', async () => {
    registrarFichaEmergencia.mockResolvedValue({ data: ficha({ planteamientoProblema: 'Deslizamientos.' }) });

    renderizar();
    await screen.findByText('CUP-2026-001');

    fireEvent.change(screen.getByLabelText('Planteamiento del problema*'), { target: { value: 'Deslizamientos de tierra.' } });
    fireEvent.click(screen.getByLabelText('Kits de alimentos'));
    fireEvent.change(screen.getByLabelText('Distrito*'), { target: { value: 'San Salvador' } });
    fireEvent.change(screen.getByLabelText('Población objetivo*'), { target: { value: '5,000 familias' } });

    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(registrarFichaEmergencia).toHaveBeenCalled());
    const solicitud = registrarFichaEmergencia.mock.calls[0][0];
    expect(solicitud.idProyecto).toBe(7);
    expect(solicitud.fichaEmergenciaRequest.planteamientoProblema).toBe('Deslizamientos de tierra.');
    expect(solicitud.fichaEmergenciaRequest.productos).toEqual([{ codigoProducto: 'PROD-1' }]);
    expect(solicitud.fichaEmergenciaRequest.distrito).toBe('San Salvador');
    await waitFor(() => expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/7/etapas'));
  });

  it('marca los campos que el back devuelve en un 400 "Existen campos sin diligenciar"', async () => {
    registrarFichaEmergencia.mockRejectedValue(
      error400([
        { campo: 'planteamientoProblema', mensaje: '*Campo obligatorio' },
        { campo: 'poblacionObjetivo', mensaje: '*Campo obligatorio' },
      ]),
    );

    renderizar();
    await screen.findByText('CUP-2026-001');

    fireEvent.change(screen.getByLabelText('Planteamiento del problema*'), { target: { value: 'x' } });
    fireEvent.click(screen.getByLabelText('Kits de alimentos'));
    fireEvent.change(screen.getByLabelText('Distrito*'), { target: { value: 'San Salvador' } });
    fireEvent.change(screen.getByLabelText('Población objetivo*'), { target: { value: 'x' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(screen.getAllByText('*Campo obligatorio')).toHaveLength(2));
    expect(swalFire).not.toHaveBeenCalled();
  });
});
