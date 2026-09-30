import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { RutaPreinversionPage } from './RutaPreinversionPage';

const obtenerProyecto = vi.fn();
const obtenerRutaPreinversion = vi.fn();
const generarRutaPreinversion = vi.fn();
const aceptarRutaPreinversion = vi.fn();
const modificarRutaPreinversion = vi.fn();
const navigate = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    preinversionApi: { obtenerProyecto: (...a: unknown[]) => obtenerProyecto(...a) },
    etapasApi: {
      obtenerRutaPreinversion: (...a: unknown[]) => obtenerRutaPreinversion(...a),
      generarRutaPreinversion: (...a: unknown[]) => generarRutaPreinversion(...a),
      aceptarRutaPreinversion: (...a: unknown[]) => aceptarRutaPreinversion(...a),
      modificarRutaPreinversion: (...a: unknown[]) => modificarRutaPreinversion(...a),
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

function proyecto(iniciativaInversion: string) {
  return { idProyecto: 7, iniciativaInversion, esProyectoEmergencia: false };
}

function ruta(overrides: Partial<{ criterios: unknown; etapasAceptadas: string[]; fueModificada: boolean }> = {}) {
  return { idProyecto: 7, criterios: null, etapasAceptadas: [], fueModificada: false, ...overrides };
}

function renderizar() {
  return render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/7/ruta-preinversion']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/ruta-preinversion" element={<RutaPreinversionPage />} />
      </Routes>
    </MemoryRouter>,
  );
}

beforeEach(() => {
  [obtenerProyecto, obtenerRutaPreinversion, generarRutaPreinversion, aceptarRutaPreinversion, modificarRutaPreinversion, navigate, swalFire].forEach(
    (mock) => mock.mockReset(),
  );
  swalFire.mockResolvedValue({ isConfirmed: true });
  rolesActivos = ['TECNICO_URP'];
});

describe('RutaPreinversionPage', () => {
  it('para Programa/Estudios Generales muestra la ruta fija sin pedir criterios (RN07/RN08)', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('ESTUDIO_GENERAL') });
    obtenerRutaPreinversion.mockResolvedValue({ data: ruta({ etapasAceptadas: ['PERFIL', 'EJECUCION'] }) });

    renderizar();

    expect(await screen.findByText(/sus etapas ya están definidas/i)).toBeInTheDocument();
    expect(screen.queryByLabelText('Tipo de capital')).not.toBeInTheDocument();
  });

  it('genera, acepta la ruta sugerida y navega a Registro de Etapas (RN01, FA-01)', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('PROYECTO') });
    obtenerRutaPreinversion.mockResolvedValue({ data: ruta() });
    generarRutaPreinversion.mockResolvedValue({
      data: {
        criterios: { tipoCapital: 'CAPITAL_FISICO', tamanioProyecto: 'GRANDE', complejidad: 'ALTA' },
        etapasSugeridas: ['PERFIL', 'PREFACTIBILIDAD', 'FACTIBILIDAD', 'DISENO', 'EJECUCION'],
      },
    });
    aceptarRutaPreinversion.mockResolvedValue({
      data: ruta({ etapasAceptadas: ['PERFIL', 'PREFACTIBILIDAD', 'FACTIBILIDAD', 'DISENO', 'EJECUCION'] }),
    });

    renderizar();

    fireEvent.change(await screen.findByLabelText('Tipo de capital*'), { target: { value: 'CAPITAL_FISICO' } });
    fireEvent.change(screen.getByLabelText('Tamaño del proyecto*'), { target: { value: 'GRANDE' } });
    fireEvent.change(screen.getByLabelText('Complejidad*'), { target: { value: 'ALTA' } });
    fireEvent.click(screen.getByRole('button', { name: 'Generar Ruta de Preinversión' }));

    await waitFor(() => expect(generarRutaPreinversion).toHaveBeenCalled());
    fireEvent.click(await screen.findByRole('button', { name: 'Aceptar' }));

    await waitFor(() => expect(aceptarRutaPreinversion).toHaveBeenCalled());
    await waitFor(() => expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/7/etapas'));
  });

  it('exige la justificación al modificar la ruta (RN03)', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('PROYECTO') });
    obtenerRutaPreinversion.mockResolvedValue({ data: ruta({ etapasAceptadas: ['PERFIL', 'EJECUCION'] }) });

    renderizar();

    fireEvent.click(await screen.findByRole('button', { name: 'Modificar' }));
    fireEvent.click(screen.getByRole('button', { name: 'Guardar modificación' }));

    expect(await screen.findByText('*Campo obligatorio')).toBeInTheDocument();
    expect(modificarRutaPreinversion).not.toHaveBeenCalled();
  });

  // Camino 1 de Rocío (22/09/2026): un proyecto de emergencia no lleva ruta.
  it('un proyecto de emergencia no pide criterios y lleva a su ficha', async () => {
    obtenerProyecto.mockResolvedValue({ data: { idProyecto: 7, iniciativaInversion: 'PROYECTO', esProyectoEmergencia: true } });
    obtenerRutaPreinversion.mockResolvedValue({ data: { etapasAceptadas: [], fueModificada: false } });
    renderizar();

    expect(await screen.findByText(/no lleva Ruta de Preinversión/)).toBeInTheDocument();
    expect(screen.queryByLabelText(/Tipo de capital/)).not.toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: 'Ir a la Ficha de emergencia' }));
    expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/7/ficha-emergencia');
  });
});

/**
 * La ruta ya aceptada es lo que se abre. Antes el formulario de criterios salía
 * siempre debajo de las etapas vigentes y parecía que la ruta generada se
 * hubiera perdido (observación del 21/09/2026: "me aparece nuevamente la
 * pantalla con criterios, en vez que mostrarme la ruta que ya había generado").
 */
describe('cuando la ruta ya está aceptada', () => {
  const conRutaAceptada = () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('PROYECTO') });
    obtenerRutaPreinversion.mockResolvedValue({
      data: ruta({ etapasAceptadas: ['PERFIL', 'PREFACTIBILIDAD', 'EJECUCION'], fueModificada: true }),
    });
  };

  it('se ven las etapas vigentes y no el formulario de criterios', async () => {
    conRutaAceptada();
    renderizar();

    expect(await screen.findByText('Perfil, Prefactibilidad, Ejecución')).toBeInTheDocument();
    expect(screen.queryByLabelText('Tipo de capital*')).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Generar Ruta de Preinversión' })).not.toBeInTheDocument();
  });

  it('se ofrece seguir al Registro de Etapas, modificar o volver a calificar', async () => {
    conRutaAceptada();
    renderizar();

    fireEvent.click(await screen.findByRole('button', { name: 'Ir al Registro de Etapas' }));
    expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/7/etapas');
  });

  it('volver a calificar los criterios es una decisión aparte, y se puede cancelar', async () => {
    conRutaAceptada();
    renderizar();

    fireEvent.click(await screen.findByRole('button', { name: 'Volver a calificar los criterios' }));
    expect(await screen.findByLabelText('Tipo de capital*')).toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: 'Cancelar' }));
    await waitFor(() => expect(screen.queryByLabelText('Tipo de capital*')).not.toBeInTheDocument());
    expect(screen.getByText('Perfil, Prefactibilidad, Ejecución')).toBeInTheDocument();
  });

  // El botón salía al formulario de solicitud de CUP, que es del proceso anterior,
  // y desde allí se acababa en la bandeja de solicitudes.
  it('"Regresar" sale a la opción de menú del proceso, no al formulario de CUP', async () => {
    conRutaAceptada();
    renderizar();

    fireEvent.click(await screen.findByRole('button', { name: 'Regresar' }));
    expect(navigate).toHaveBeenCalledWith('/preinversion/creacion-ruta');
  });
});

/**
 * Un programa no califica criterios, pero su ruta existe y llega del servidor:
 * antes sólo se veía el aviso y la pantalla no dejaba ver nada.
 */
describe('la ruta fija de un programa', () => {
  it('muestra las etapas además del aviso', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('PROGRAMA') });
    obtenerRutaPreinversion.mockResolvedValue({ data: ruta({ etapasAceptadas: ['PERFIL', 'EJECUCION'] }) });

    renderizar();

    expect(await screen.findByText('Perfil, Ejecución')).toBeInTheDocument();
    expect(screen.getByText(/sus etapas ya están definidas/i)).toBeInTheDocument();
    expect(screen.queryByLabelText('Tipo de capital*')).not.toBeInTheDocument();
  });
});
