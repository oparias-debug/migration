import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { DiagnosticoPage } from './DiagnosticoPage';

const obtenerMatrizInteresados = vi.fn();
const guardarMatrizInteresados = vi.fn();
const obtenerProyecto = vi.fn();
const swalFire = vi.fn();
const navigate = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    preinversionApi: { obtenerProyecto: (...a: unknown[]) => obtenerProyecto(...a) },
    interesadosApi: {
      obtenerMatrizInteresados: (...a: unknown[]) => obtenerMatrizInteresados(...a),
      guardarMatrizInteresados: (...a: unknown[]) => guardarMatrizInteresados(...a),
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

const INTERESADO = {
  nombreInteresado: 'Alcaldía de Santa Ana',
  tipo: 'COOPERANTE',
  nivelInfluencia: 'ALTO',
  nivelInteres: 'ALTO',
  estrategiaGestion: 'Convenio de cooperación',
};

const montar = () =>
  render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/7/diagnostico']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/diagnostico" element={<DiagnosticoPage />} />
      </Routes>
    </MemoryRouter>,
  );

const escribirFila = (numero: number, valores: Partial<typeof INTERESADO>) => {
  const etiquetas: Record<keyof typeof INTERESADO, string> = {
    nombreInteresado: `Nombre del interesado ${numero}`,
    tipo: `Tipo del interesado ${numero}`,
    nivelInfluencia: `Nivel de influencia del interesado ${numero}`,
    nivelInteres: `Nivel de interés del interesado ${numero}`,
    estrategiaGestion: `Estrategia de gestión del interesado ${numero}`,
  };
  for (const [campo, valor] of Object.entries(valores)) {
    fireEvent.change(screen.getByLabelText(etiquetas[campo as keyof typeof INTERESADO]), { target: { value: valor } });
  }
};

describe('DiagnosticoPage · CU-PRE-06 Gestión de interesados', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['TECNICO_URP'];
    obtenerProyecto.mockResolvedValue({ data: { nombre: 'Hospital de Santa Ana', cup: '10001' } });
    obtenerMatrizInteresados.mockResolvedValue({ data: { idProyecto: 7, interesados: [] } });
    swalFire.mockResolvedValue({ isConfirmed: true });
  });

  it('muestra el proyecto y la pestaña del capítulo', async () => {
    montar();
    expect(await screen.findByRole('tab', { name: 'Gestión de interesados' })).toHaveAttribute('aria-selected', 'true');
    expect(await screen.findByText('Hospital de Santa Ana')).toBeInTheDocument();
    expect(screen.getByText(/CUP 10001/)).toBeInTheDocument();
  });

  it('empieza con una fila en blanco cuando no hay nada registrado', async () => {
    montar();
    expect(await screen.findByLabelText('Nombre del interesado 1')).toHaveValue('');
  });

  it('carga los interesados ya registrados', async () => {
    obtenerMatrizInteresados.mockResolvedValue({ data: { idProyecto: 7, interesados: [INTERESADO] } });
    montar();
    expect(await screen.findByLabelText('Nombre del interesado 1')).toHaveValue('Alcaldía de Santa Ana');
    expect(screen.getByLabelText('Tipo del interesado 1')).toHaveValue('COOPERANTE');
    expect(screen.getByLabelText('Nivel de influencia del interesado 1')).toHaveValue('ALTO');
  });

  it('agrega y elimina filas', async () => {
    montar();
    await screen.findByLabelText('Nombre del interesado 1');
    fireEvent.click(screen.getByRole('button', { name: 'Agregar interesado' }));
    await waitFor(() => expect(screen.getByLabelText('Nombre del interesado 2')).toBeInTheDocument());

    fireEvent.click(screen.getByRole('button', { name: 'Eliminar el interesado 2' }));
    await waitFor(() => expect(screen.queryByLabelText('Nombre del interesado 2')).not.toBeInTheDocument());
  });

  it('guarda la matriz completa y no manda las filas en blanco', async () => {
    guardarMatrizInteresados.mockResolvedValue({ data: { idProyecto: 7, interesados: [INTERESADO] } });
    montar();
    await screen.findByLabelText('Nombre del interesado 1');
    escribirFila(1, INTERESADO);
    fireEvent.click(screen.getByRole('button', { name: 'Agregar interesado' }));
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(guardarMatrizInteresados).toHaveBeenCalledWith({
        idProyecto: 7,
        matrizInteresadosRequest: { interesados: [INTERESADO] },
      }),
    );
    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'success' })));
  });

  // RN06: los campos pendientes se sombrean en rojo al intentar guardar.
  it('marca en rojo lo que falta', async () => {
    guardarMatrizInteresados.mockResolvedValue({ data: { idProyecto: 7, interesados: [] } });
    montar();
    const nombre = await screen.findByLabelText('Nombre del interesado 1');
    expect(nombre).not.toHaveClass('malo');

    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    await waitFor(() => expect(screen.getByLabelText('Nombre del interesado 1')).toHaveClass('malo'));
    expect(screen.getByLabelText('Tipo del interesado 1')).toHaveClass('malo');
  });

  // RN05: se puede repetir un interesado si difiere en alguna columna; no si las cuatro coinciden.
  it('no guarda si dos filas coinciden en las cuatro columnas', async () => {
    montar();
    await screen.findByLabelText('Nombre del interesado 1');
    escribirFila(1, INTERESADO);
    fireEvent.click(screen.getByRole('button', { name: 'Agregar interesado' }));
    await screen.findByLabelText('Nombre del interesado 2');
    escribirFila(2, { ...INTERESADO, estrategiaGestion: 'Otra estrategia' });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'error' })));
    expect(guardarMatrizInteresados).not.toHaveBeenCalled();
  });

  it('sí guarda cuando difieren en alguna columna', async () => {
    guardarMatrizInteresados.mockResolvedValue({ data: { idProyecto: 7, interesados: [] } });
    montar();
    await screen.findByLabelText('Nombre del interesado 1');
    escribirFila(1, INTERESADO);
    fireEvent.click(screen.getByRole('button', { name: 'Agregar interesado' }));
    await screen.findByLabelText('Nombre del interesado 2');
    escribirFila(2, { ...INTERESADO, nivelInteres: 'BAJO' });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(guardarMatrizInteresados).toHaveBeenCalled());
    expect(guardarMatrizInteresados.mock.calls[0][0].matrizInteresadosRequest.interesados).toHaveLength(2);
  });

  it('el Técnico PRE consulta pero no edita', async () => {
    rolesActivos = ['TECNICO_PRE'];
    obtenerMatrizInteresados.mockResolvedValue({ data: { idProyecto: 7, interesados: [INTERESADO] } });
    montar();
    expect(await screen.findByLabelText('Nombre del interesado 1')).toHaveAttribute('readonly');
    expect(screen.getByLabelText('Tipo del interesado 1')).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Agregar interesado' })).not.toBeInTheDocument();
  });

  it('si la carga falla, lo dice y no rompe la pantalla', async () => {
    obtenerMatrizInteresados.mockRejectedValue(new Error('403'));
    montar();
    expect(await screen.findByRole('tab', { name: 'Gestión de interesados' })).toBeInTheDocument();
    await waitFor(() => expect(screen.queryByLabelText('Nombre del interesado 1')).not.toBeInTheDocument());
  });

  it('Regresar vuelve a Alternativas de solución', async () => {
    montar();
    await screen.findByLabelText('Nombre del interesado 1');
    fireEvent.click(screen.getByRole('button', { name: 'Regresar' }));
    expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/7/alternativas-solucion');
  });
});
