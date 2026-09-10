import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { IdentificacionPage } from './IdentificacionPage';

const obtenerIdentificacion = vi.fn();
const guardarIdentificacion = vi.fn();
const cargarArbolProblemas = vi.fn();
const descargarArbolProblemas = vi.fn();
const eliminarArbolProblemas = vi.fn();
const cargarArbolObjetivos = vi.fn();
const navigate = vi.fn();
const swalFire = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  return {
    ...actual,
    identificacionApi: {
      obtenerIdentificacion: (...a: unknown[]) => obtenerIdentificacion(...a),
      guardarIdentificacion: (...a: unknown[]) => guardarIdentificacion(...a),
      cargarArbolProblemas: (...a: unknown[]) => cargarArbolProblemas(...a),
      descargarArbolProblemas: (...a: unknown[]) => descargarArbolProblemas(...a),
      eliminarArbolProblemas: (...a: unknown[]) => eliminarArbolProblemas(...a),
      cargarArbolObjetivos: (...a: unknown[]) => cargarArbolObjetivos(...a),
      descargarArbolObjetivos: vi.fn(),
      eliminarArbolObjetivos: vi.fn(),
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

const IDENTIFICACION_VACIA = {
  idProyecto: 7,
  unidadEjecutora: { idUnidadEjecutora: 4501, nombre: 'MINSAL' },
  nombreProyecto: 'Equipamiento del hospital de Santa Ana',
  cup: 'CUP-2026-0007',
  objetivosEspecificos: [],
};

const montar = () =>
  render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/7/identificacion']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/identificacion" element={<IdentificacionPage />} />
      </Routes>
    </MemoryRouter>,
  );

describe('IdentificacionPage · CU-PRE-04', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    rolesActivos = ['TECNICO_URP'];
    swalFire.mockResolvedValue({ isConfirmed: true });
    obtenerIdentificacion.mockResolvedValue({ data: IDENTIFICACION_VACIA });
  });

  // Antecedentes: "el sistema muestra los campos no editables Unidad Ejecutora,
  // Nombre del proyecto y CUP".
  it('muestra la cabecera que asigna el servidor, y no deja editarla', async () => {
    montar();
    const ue = await screen.findByDisplayValue('MINSAL');
    expect(ue).toHaveAttribute('readonly');
    expect(screen.getByDisplayValue('CUP-2026-0007')).toHaveAttribute('readonly');
    expect(screen.getByDisplayValue('Equipamiento del hospital de Santa Ana')).toHaveAttribute('readonly');
  });

  it('guarda lo registrado y avisa (camino feliz)', async () => {
    guardarIdentificacion.mockResolvedValue({ data: { ...IDENTIFICACION_VACIA, antecedentes: 'Un antecedente' } });
    montar();
    await screen.findByDisplayValue('MINSAL');

    fireEvent.change(screen.getByLabelText('Antecedentes'), { target: { value: 'Un antecedente' } });
    fireEvent.change(screen.getByLabelText('Problema central'), { target: { value: 'El problema' } });
    fireEvent.change(screen.getByLabelText('Objetivo general'), { target: { value: 'El objetivo' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(guardarIdentificacion).toHaveBeenCalledWith(
        expect.objectContaining({
          idProyecto: 7,
          identificacionRequest: expect.objectContaining({
            antecedentes: 'Un antecedente',
            problemaCentral: 'El problema',
            objetivoGeneral: 'El objetivo',
          }),
        }),
      ),
    );
    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'success' })));
  });

  // RNC-2: "sombrea en color rojo los bordes de los campos pendientes".
  it('marca en rojo los campos pendientes al guardar incompleto', async () => {
    guardarIdentificacion.mockResolvedValue({ data: IDENTIFICACION_VACIA });
    montar();
    await screen.findByDisplayValue('MINSAL');

    expect(screen.getByLabelText('Antecedentes')).not.toHaveClass('malo');
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(screen.getByLabelText('Antecedentes')).toHaveClass('malo'));
    expect(screen.getByLabelText('Problema central')).toHaveClass('malo');
    expect(screen.getByLabelText('Objetivo general')).toHaveClass('malo');
  });

  it('respeta el límite de caracteres de cada campo', async () => {
    montar();
    await screen.findByDisplayValue('MINSAL');
    expect(screen.getByLabelText('Antecedentes')).toHaveAttribute('maxlength', '3000');
    expect(screen.getByLabelText('Problema central')).toHaveAttribute('maxlength', '500');
    expect(screen.getByLabelText('Objetivo general')).toHaveAttribute('maxlength', '500');
    expect(screen.getByLabelText('Objetivo específico 1')).toHaveAttribute('maxlength', '500');
  });

  it('agrega y elimina filas de objetivo específico', async () => {
    montar();
    await screen.findByDisplayValue('MINSAL');
    expect(screen.getAllByLabelText(/^Objetivo específico/)).toHaveLength(1);

    fireEvent.click(screen.getByRole('button', { name: 'Agregar objetivo específico' }));
    await waitFor(() => expect(screen.getAllByLabelText(/^Objetivo específico/)).toHaveLength(2));

    fireEvent.click(screen.getByRole('button', { name: 'Eliminar el objetivo específico 2' }));
    await waitFor(() => expect(screen.getAllByLabelText(/^Objetivo específico/)).toHaveLength(1));
  });

  it('no manda al servidor las filas de objetivo que quedaron vacías', async () => {
    guardarIdentificacion.mockResolvedValue({ data: IDENTIFICACION_VACIA });
    montar();
    await screen.findByDisplayValue('MINSAL');

    fireEvent.change(screen.getByLabelText('Objetivo específico 1'), { target: { value: 'Objetivo uno' } });
    fireEvent.click(screen.getByRole('button', { name: 'Agregar objetivo específico' }));
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(guardarIdentificacion).toHaveBeenCalledWith(
        expect.objectContaining({
          identificacionRequest: expect.objectContaining({ objetivosEspecificos: ['Objetivo uno'] }),
        }),
      ),
    );
  });

  describe('árboles en PDF', () => {
    it('carga un archivo y lo deja disponible para descargar', async () => {
      cargarArbolProblemas.mockResolvedValue({
        data: { nombreArchivo: 'arbol.pdf', fechaCarga: '2026-09-10T10:00:00Z' },
      });
      montar();
      await screen.findByDisplayValue('MINSAL');

      const entrada = screen.getByTestId('archivo-problemas');
      const archivo = new File(['%PDF-1.4'], 'arbol.pdf', { type: 'application/pdf' });
      fireEvent.change(entrada, { target: { files: [archivo] } });

      await waitFor(() => expect(cargarArbolProblemas).toHaveBeenCalledWith({ idProyecto: 7, archivo }));
      expect(await screen.findByRole('button', { name: 'arbol.pdf' })).toBeInTheDocument();
    });

    it('muestra el archivo que ya venía cargado', async () => {
      obtenerIdentificacion.mockResolvedValue({
        data: {
          ...IDENTIFICACION_VACIA,
          archivoArbolProblemas: { nombreArchivo: 'previo.pdf', fechaCarga: '2026-09-01T10:00:00Z' },
        },
      });
      montar();
      expect(await screen.findByRole('button', { name: 'previo.pdf' })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: 'Reemplazar' })).toBeInTheDocument();
    });

    it('elimina el archivo tras confirmar', async () => {
      obtenerIdentificacion.mockResolvedValue({
        data: {
          ...IDENTIFICACION_VACIA,
          archivoArbolProblemas: { nombreArchivo: 'previo.pdf', fechaCarga: '2026-09-01T10:00:00Z' },
        },
      });
      eliminarArbolProblemas.mockResolvedValue({});
      montar();
      await screen.findByRole('button', { name: 'previo.pdf' });

      fireEvent.click(screen.getByRole('button', { name: 'Eliminar' }));

      await waitFor(() => expect(eliminarArbolProblemas).toHaveBeenCalledWith({ idProyecto: 7 }));
      await waitFor(() => expect(screen.queryByRole('button', { name: 'previo.pdf' })).not.toBeInTheDocument());
    });

    it('no elimina nada si se cancela la confirmación', async () => {
      obtenerIdentificacion.mockResolvedValue({
        data: {
          ...IDENTIFICACION_VACIA,
          archivoArbolProblemas: { nombreArchivo: 'previo.pdf', fechaCarga: '2026-09-01T10:00:00Z' },
        },
      });
      swalFire.mockResolvedValue({ isConfirmed: false });
      montar();
      await screen.findByRole('button', { name: 'previo.pdf' });

      fireEvent.click(screen.getByRole('button', { name: 'Eliminar' }));

      await waitFor(() => expect(swalFire).toHaveBeenCalled());
      expect(eliminarArbolProblemas).not.toHaveBeenCalled();
    });
  });

  // "Consultar la información en modo solo lectura" (CU-PRE-04-consultar-descargar).
  describe('otros actores', () => {
    it('el Técnico PRE consulta pero no edita ni guarda', async () => {
      rolesActivos = ['TECNICO_PRE'];
      obtenerIdentificacion.mockResolvedValue({
        data: { ...IDENTIFICACION_VACIA, antecedentes: 'Ya guardado', objetivosEspecificos: ['Uno'] },
      });
      montar();

      const antecedentes = await screen.findByLabelText('Antecedentes');
      expect(antecedentes).toHaveAttribute('readonly');
      expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
      expect(screen.queryByRole('button', { name: 'Agregar objetivo específico' })).not.toBeInTheDocument();
    });

    it('el Técnico PRE puede descargar un adjunto', async () => {
      rolesActivos = ['TECNICO_PRE'];
      obtenerIdentificacion.mockResolvedValue({
        data: {
          ...IDENTIFICACION_VACIA,
          archivoArbolProblemas: { nombreArchivo: 'previo.pdf', fechaCarga: '2026-09-01T10:00:00Z' },
        },
      });
      montar();
      expect(await screen.findByRole('button', { name: 'previo.pdf' })).toBeInTheDocument();
      expect(screen.queryByRole('button', { name: 'Eliminar' })).not.toBeInTheDocument();
    });
  });

  it('el botón Siguiente lleva a Alternativas de Solución (CU-PRE-05)', async () => {
    montar();
    await screen.findByDisplayValue('MINSAL');
    fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
    expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/7/alternativas-solucion');
  });
});
