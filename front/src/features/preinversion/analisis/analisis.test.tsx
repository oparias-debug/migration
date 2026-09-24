import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';

const obtenerAnalisisAmbiental = vi.fn();
const guardarAnalisisAmbiental = vi.fn();
const obtenerAnalisisRiesgo = vi.fn();
const guardarAnalisisRiesgo = vi.fn();
const avanzarAAnalisisLegal = vi.fn();
const obtenerAnalisisLegal = vi.fn();
const guardarAnalisisLegal = vi.fn();
const swalFire = vi.fn();
const navigate = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  analisisAmbientalApi: {
    obtenerAnalisisAmbiental: (...a: unknown[]) => obtenerAnalisisAmbiental(...a),
    guardarAnalisisAmbiental: (...a: unknown[]) => guardarAnalisisAmbiental(...a),
  },
  analisisRiesgoApi: {
    obtenerAnalisisRiesgo: (...a: unknown[]) => obtenerAnalisisRiesgo(...a),
    guardarAnalisisRiesgo: (...a: unknown[]) => guardarAnalisisRiesgo(...a),
    avanzarAAnalisisLegal: (...a: unknown[]) => avanzarAAnalisisLegal(...a),
  },
  analisisLegalApi: {
    obtenerAnalisisLegal: (...a: unknown[]) => obtenerAnalisisLegal(...a),
    guardarAnalisisLegal: (...a: unknown[]) => guardarAnalisisLegal(...a),
  },
}));

let rolesActivos: string[] = ['TECNICO_URP'];
vi.mock('../../../auth/useAuth', () => ({ useAuth: () => ({ hasRole: (r: string) => rolesActivos.includes(r) }) }));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

const { AnalisisAmbientalPage } = await import('./AnalisisAmbientalPage');
const { AnalisisRiesgoPage } = await import('./AnalisisRiesgoPage');
const { AnalisisLegalPage } = await import('./AnalisisLegalPage');

const montar = (ruta: string, Pantalla: () => JSX.Element) =>
  render(
    <MemoryRouter initialEntries={[`/preinversion/proyectos/7/${ruta}`]}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/:paso" element={<Pantalla />} />
      </Routes>
    </MemoryRouter>,
  );

beforeEach(() => {
  [obtenerAnalisisAmbiental, guardarAnalisisAmbiental, obtenerAnalisisRiesgo, guardarAnalisisRiesgo,
    avanzarAAnalisisLegal, obtenerAnalisisLegal, guardarAnalisisLegal, swalFire, navigate].forEach((m) => m.mockReset());
  rolesActivos = ['TECNICO_URP'];
  swalFire.mockResolvedValue({ isConfirmed: true });
});

describe('Análisis Ambiental (CU-PRE-14)', () => {
  it('muestra la matriz con su total y agrega una fila', async () => {
    obtenerAnalisisAmbiental.mockResolvedValue({
      data: {
        idProyecto: 7,
        tieneImpactosAmbientales: true,
        filas: [{ medio: 'FISICO_AGUA', impacto: 'Turbidez del río', costoMedidaGestion: 1500 }],
        totalCostoMedidasGestion: 1500,
      },
    });
    montar('analisis-ambiental', AnalisisAmbientalPage);

    expect(await screen.findByDisplayValue('Turbidez del río')).toBeInTheDocument();
    expect(screen.getByText(/1,500/)).toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: 'Agregar fila' }));
    expect(screen.getAllByLabelText(/Descripción del impacto/)).toHaveLength(2);
  });

  // RN04: con la respuesta en "No", la tabla no se despliega.
  it('con "No" no despliega la tabla', async () => {
    obtenerAnalisisAmbiental.mockResolvedValue({
      data: { idProyecto: 7, tieneImpactosAmbientales: false, filas: [] },
    });
    montar('analisis-ambiental', AnalisisAmbientalPage);

    await screen.findByText('¿El proyecto tiene impactos ambientales?');
    expect(screen.queryByRole('button', { name: 'Agregar fila' })).not.toBeInTheDocument();
  });

  it('guarda la matriz entera, con el costo como número', async () => {
    obtenerAnalisisAmbiental.mockResolvedValue({
      data: { idProyecto: 7, tieneImpactosAmbientales: true, filas: [{ impacto: 'Polvo', costoMedidaGestion: null }] },
    });
    guardarAnalisisAmbiental.mockResolvedValue({ data: { idProyecto: 7, filas: [], totalCostoMedidasGestion: 2000 } });
    montar('analisis-ambiental', AnalisisAmbientalPage);

    fireEvent.change(await screen.findByLabelText(/Costo de la medida/), { target: { value: '2,000' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(guardarAnalisisAmbiental).toHaveBeenCalled());
    expect(guardarAnalisisAmbiental.mock.calls[0][0].analisisAmbientalRequest.filas[0].costoMedidaGestion).toBe(2000);
  });
});

describe('Análisis de Riesgos (CU-PRE-15)', () => {
  it('muestra la calificación que calcula el servidor, no la inventa', async () => {
    obtenerAnalisisRiesgo.mockResolvedValue({
      data: {
        idProyecto: 7,
        tieneRiesgosDesastres: true,
        filas: [{ descripcionRiesgo: 'Inundación', probabilidad: 'PROBABLE', impactoRiesgo: 'EXTREMO', calificacionRiesgo: 'ALTO' }],
        totalAccionesMitigacion: 0,
      },
    });
    montar('analisis-riesgo', AnalisisRiesgoPage);

    expect(await screen.findByDisplayValue('Inundación')).toBeInTheDocument();
    // "Alto" también es una opción del desplegable de impacto: se mira la celda
    // de la calificación, que es la que el servidor calcula.
    expect(document.querySelector('.marca-estado')?.textContent).toBe('Alto');
  });

  // RN06: avanzar exige la acción de mitigación de los riesgos altos, y eso lo
  // valida el servidor: la pantalla muestra su mensaje y no navega.
  it('si el servidor rechaza avanzar, lo dice y no cambia de pantalla', async () => {
    obtenerAnalisisRiesgo.mockResolvedValue({ data: { idProyecto: 7, filas: [] } });
    avanzarAAnalisisLegal.mockRejectedValue(new Error('falla'));
    montar('analisis-riesgo', AnalisisRiesgoPage);

    fireEvent.click(await screen.findByRole('button', { name: /Siguiente/ }));

    await waitFor(() => expect(swalFire).toHaveBeenCalled());
    expect(navigate).not.toHaveBeenCalled();
  });

  it('al avanzar pasa a Análisis Legal', async () => {
    obtenerAnalisisRiesgo.mockResolvedValue({ data: { idProyecto: 7, filas: [] } });
    avanzarAAnalisisLegal.mockResolvedValue({ data: {} });
    montar('analisis-riesgo', AnalisisRiesgoPage);

    fireEvent.click(await screen.findByRole('button', { name: /Siguiente/ }));
    await waitFor(() => expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/7/analisis-legal'));
  });
});

describe('Análisis Legal (CU-PRE-16)', () => {
  it('guarda las filas y muestra el total del servidor', async () => {
    obtenerAnalisisLegal.mockResolvedValue({
      data: { idProyecto: 7, requiereAnalisisLegal: true, filas: [{ entregable: 'Escritura' }], totalCostoEntregables: 0 },
    });
    guardarAnalisisLegal.mockResolvedValue({
      data: { idProyecto: 7, requiereAnalisisLegal: true, filas: [{ entregable: 'Escritura', costoEntregable: 900 }], totalCostoEntregables: 900 },
    });
    montar('analisis-legal', AnalisisLegalPage);

    fireEvent.change(await screen.findByLabelText(/Costo del entregable/), { target: { value: '900' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(guardarAnalisisLegal).toHaveBeenCalled());
    expect(await screen.findByText(/900/)).toBeInTheDocument();
  });

  it('un actor que no registra no ve las acciones de edición', async () => {
    rolesActivos = ['TECNICO_PRE'];
    obtenerAnalisisLegal.mockResolvedValue({ data: { idProyecto: 7, requiereAnalisisLegal: true, filas: [] } });
    montar('analisis-legal', AnalisisLegalPage);

    await screen.findByText('Análisis Legal');
    expect(screen.queryByRole('button', { name: 'Agregar fila' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
  });
});
