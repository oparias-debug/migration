import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';

const consultarFichaViabilidad = vi.fn();
const guardarComentariosViabilidad = vi.fn();
const enviarComentariosViabilidad = vi.fn();
const emitirViabilidad = vi.fn();
const solicitarViabilidad = vi.fn();
const cargarDocumentoViabilidad = vi.fn();
const swalFire = vi.fn();
const navigate = vi.fn();

vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  viabilidadRevisionApi: {
    consultarFichaViabilidad: (...a: unknown[]) => consultarFichaViabilidad(...a),
    guardarComentariosViabilidad: (...a: unknown[]) => guardarComentariosViabilidad(...a),
    enviarComentariosViabilidad: (...a: unknown[]) => enviarComentariosViabilidad(...a),
    emitirViabilidad: (...a: unknown[]) => emitirViabilidad(...a),
  },
  viabilidadUrpApi: {
    solicitarViabilidad: (...a: unknown[]) => solicitarViabilidad(...a),
    cargarDocumentoViabilidad: (...a: unknown[]) => cargarDocumentoViabilidad(...a),
  },
}));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

const { ViabilidadPage } = await import('./ViabilidadPage');

const SIN_ACCIONES = {
  solicitarViabilidad: false,
  guardarComentarios: false,
  enviarComentarios: false,
  emitirViabilidad: false,
  irAElegibilidad: false,
};

const ficha = (acciones: Partial<typeof SIN_ACCIONES> = {}, extra: Record<string, unknown> = {}) => ({
  data: {
    proyectoId: 14,
    cup: '10002',
    nombreProyecto: 'Hospital Nacional de Santa Ana',
    estadoProyecto: 'Proyecto Formulado',
    objetivoGeneral: 'Ampliar la cobertura hospitalaria del occidente del país',
    descripcion: 'Construcción y equipamiento del hospital',
    productos: ['Sala de emergencias', 'Quirófanos'],
    poblacionObjetivo: 125000,
    inversionEstimada: 1250000,
    costoOperacion: 80000,
    costoMantenimiento: 45000,
    indicadoresEvaluacion: [
      { nombre: 'VAN', valor: 320000 },
      { nombre: 'TIR', valor: 14.5 },
    ],
    documentos: [],
    comentariosViabilizador: [],
    observacionesGeneralesJustificacion: null,
    accionesDisponibles: { ...SIN_ACCIONES, ...acciones },
    ...extra,
  },
});

const montar = () =>
  render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/14/viabilidad']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id/viabilidad" element={<ViabilidadPage />} />
      </Routes>
    </MemoryRouter>,
  );

beforeEach(() => {
  [guardarComentariosViabilidad, enviarComentariosViabilidad, emitirViabilidad, solicitarViabilidad,
    cargarDocumentoViabilidad, navigate].forEach((m) => m.mockReset().mockResolvedValue({ data: {} }));
  consultarFichaViabilidad.mockReset().mockResolvedValue(ficha());
  swalFire.mockReset().mockResolvedValue({ isConfirmed: true });
});

describe('Viabilidad (CU-PRE-24)', () => {
  it('reúne en una ficha lo registrado en los capítulos anteriores', async () => {
    montar();
    expect(await screen.findByText('Hospital Nacional de Santa Ana')).toBeInTheDocument();
    expect(screen.getByText('Ampliar la cobertura hospitalaria del occidente del país')).toBeInTheDocument();
    expect(screen.getByText('Sala de emergencias, Quirófanos')).toBeInTheDocument();
    expect(screen.getByText('125,000')).toBeInTheDocument();
    expect(screen.getByText('$1,250,000.00')).toBeInTheDocument();
    expect(screen.getByText('VAN: 320,000 · TIR: 14.5')).toBeInTheDocument();
  });

  // Lo que se puede hacer lo decide el servidor, no el rol leído en la pantalla.
  it('sin acciones disponibles la ficha es de sólo lectura', async () => {
    montar();
    await screen.findByText('Hospital Nacional de Santa Ana');
    for (const boton of ['Solicitar viabilidad', 'Emitir viabilidad', 'Enviar comentarios', 'Guardar']) {
      expect(screen.queryByRole('button', { name: boton })).not.toBeInTheDocument();
    }
    expect(screen.queryByLabelText(/^Comentario de/)).not.toBeInTheDocument();
  });

  it('quien revisa comenta campo por campo y lo guarda', async () => {
    consultarFichaViabilidad.mockResolvedValue(ficha({ guardarComentarios: true }));
    montar();
    await screen.findByText('Hospital Nacional de Santa Ana');

    fireEvent.change(screen.getByLabelText('Comentario de Objetivo general'), {
      target: { value: 'Precisar el alcance territorial' },
    });
    fireEvent.change(screen.getByLabelText('Observaciones generales / justificación'), {
      target: { value: 'Falta sustentar la demanda' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() =>
      expect(guardarComentariosViabilidad).toHaveBeenCalledWith({
        proyectoId: 14,
        guardarComentariosViabilidadRequest: {
          comentariosViabilizador: [{ campo: 'OBJETIVO_GENERAL', comentario: 'Precisar el alcance territorial' }],
          observacionesGeneralesJustificacion: 'Falta sustentar la demanda',
        },
      }),
    );
  });

  it('los comentarios ya guardados vuelven al abrir la ficha', async () => {
    consultarFichaViabilidad.mockResolvedValue(
      ficha({}, { comentariosViabilizador: [{ campo: 'DESCRIPCION', comentario: 'Ampliar el detalle técnico' }] }),
    );
    montar();
    expect(await screen.findByText('Ampliar el detalle técnico')).toBeInTheDocument();
  });

  it.each([
    ['Solicitar viabilidad', 'solicitarViabilidad', () => solicitarViabilidad],
    ['Enviar comentarios', 'enviarComentarios', () => enviarComentariosViabilidad],
    ['Emitir viabilidad', 'emitirViabilidad', () => emitirViabilidad],
  ])('%s se confirma antes de llamar al servidor', async (boton, accion, servicio) => {
    consultarFichaViabilidad.mockResolvedValue(ficha({ [accion]: true }));
    montar();
    await screen.findByText('Hospital Nacional de Santa Ana');

    fireEvent.click(screen.getByRole('button', { name: boton }));

    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'warning' })));
    await waitFor(() => expect(servicio()).toHaveBeenCalledWith({ proyectoId: 14 }));
  });

  it('si se cancela la confirmación no se llama al servidor', async () => {
    consultarFichaViabilidad.mockResolvedValue(ficha({ emitirViabilidad: true }));
    swalFire.mockResolvedValue({ isConfirmed: false });
    montar();
    await screen.findByText('Hospital Nacional de Santa Ana');

    fireEvent.click(screen.getByRole('button', { name: 'Emitir viabilidad' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalled());
    expect(emitirViabilidad).not.toHaveBeenCalled();
  });

  it('carga un documento con su tipo', async () => {
    consultarFichaViabilidad.mockResolvedValue(ficha({ solicitarViabilidad: true }));
    montar();
    await screen.findByText('Hospital Nacional de Santa Ana');

    const archivo = new File(['contenido'], 'documento-preinversion.pdf', { type: 'application/pdf' });
    fireEvent.change(screen.getByLabelText('Archivo'), { target: { files: [archivo] } });
    fireEvent.click(screen.getByRole('button', { name: 'Cargar documento' }));

    await waitFor(() =>
      expect(cargarDocumentoViabilidad).toHaveBeenCalledWith({
        proyectoId: 14,
        tipoDocumento: 'DOCUMENTO_PREINVERSION',
        archivo,
      }),
    );
  });

  it('sin archivo elegido no se llama al servidor', async () => {
    consultarFichaViabilidad.mockResolvedValue(ficha({ solicitarViabilidad: true }));
    montar();
    await screen.findByText('Hospital Nacional de Santa Ana');

    fireEvent.click(screen.getByRole('button', { name: 'Cargar documento' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'info' })));
    expect(cargarDocumentoViabilidad).not.toHaveBeenCalled();
  });

  it('lista los documentos cargados', async () => {
    consultarFichaViabilidad.mockResolvedValue(
      ficha({}, {
        documentos: [
          {
            documentoId: 1,
            tipoDocumento: 'DOCUMENTO_PREINVERSION',
            nombreArchivo: 'perfil-hospital.pdf',
            fechaCarga: '2026-09-26T10:00:00Z',
          },
        ],
      }),
    );
    montar();
    expect(await screen.findByText('perfil-hospital.pdf')).toBeInTheDocument();
    expect(screen.getByText('Documento de Preinversión')).toBeInTheDocument();
  });

  it('emitida la viabilidad, se ofrece continuar a Elegibilidad', async () => {
    consultarFichaViabilidad.mockResolvedValue(ficha({ irAElegibilidad: true }));
    montar();
    await screen.findByText('Hospital Nacional de Santa Ana');

    fireEvent.click(screen.getByRole('button', { name: 'Continuar a Elegibilidad' }));

    expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos/14/elegibilidad');
  });

  it('un fallo al cargar la ficha se ve como error', async () => {
    consultarFichaViabilidad.mockRejectedValue(new Error('falla'));
    montar();
    expect(await screen.findByRole('alert')).toBeInTheDocument();
  });

  it('si el servidor rechaza una acción, se avisa y la ficha no se pierde', async () => {
    consultarFichaViabilidad.mockResolvedValue(ficha({ emitirViabilidad: true }));
    emitirViabilidad.mockRejectedValue(new Error('falla'));
    montar();
    await screen.findByText('Hospital Nacional de Santa Ana');

    fireEvent.click(screen.getByRole('button', { name: 'Emitir viabilidad' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'error' })));
    expect(screen.getByText('Hospital Nacional de Santa Ana')).toBeInTheDocument();
  });
});
