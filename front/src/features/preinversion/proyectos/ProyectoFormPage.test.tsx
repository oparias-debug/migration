import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { AxiosError, AxiosHeaders } from 'axios';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import '../../../i18n/i18n';
import { ProyectoFormPage } from './ProyectoFormPage';

const obtenerProyecto = vi.fn();
const actualizarProyecto = vi.fn();
const solicitarCup = vi.fn();
const responderObservacionCup = vi.fn();
const navigate = vi.fn();
const confirmDialog = vi.fn();
const swalFire = vi.fn();

// Se conservan los enums reales (proyectoLabels y proyectoFormSchema los importan
// de este mismo módulo); sólo se sustituyen los clientes que salen a la red.
vi.mock('../../../api/preinversionApi', async (importOriginal) => {
  const actual = await importOriginal<typeof import('../../../api/preinversionApi')>();
  const listaVacia = () => Promise.resolve({ data: [] });
  return {
    ...actual,
    preinversionApi: {
      obtenerProyecto: (...a: unknown[]) => obtenerProyecto(...a),
      actualizarProyecto: (...a: unknown[]) => actualizarProyecto(...a),
      solicitarCup: (...a: unknown[]) => solicitarCup(...a),
      responderObservacionCup: (...a: unknown[]) => responderObservacionCup(...a),
      registrarProyecto: vi.fn(),
    },
    catalogoPreinversionApi: {
      listarSectores: listaVacia,
      listarEjesTematicos: listaVacia,
      listarEjesPlanGobierno: listaVacia,
      listarPlanesSectoriales: listaVacia,
      listarMedidasCatalogo: listaVacia,
    },
  };
});

vi.mock('../../../auth/useAuth', () => ({ useAuth: () => ({ hasRole: () => true }) }));
vi.mock('../../../components/ConfirmDialog', () => ({ confirmDialog: (...a: unknown[]) => confirmDialog(...a) }));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return { ...actual, useNavigate: () => navigate };
});

function proyecto(estado: string, revisionPre: unknown[] = []) {
  return {
    idProyecto: 7,
    nombre: 'Construcción de puente sobre el río Lempa',
    iniciativaInversion: 'PROYECTO',
    montoEstimadoInversion: 150000,
    institucion: { idInstitucion: 1, nombre: 'Ministerio de Obras Públicas' },
    unidadEjecutora: { idUnidadEjecutora: 1, nombre: 'Unidad de Inversión Pública' },
    sector: { idSector: 3, nombre: 'Transporte', macrosector: { idMacrosector: 1, nombre: 'Infraestructura' } },
    ejeTematico: { idEjeTematico: 2, nombre: 'Conectividad' },
    medidasGrd: [],
    medidasGrc: [],
    medidasAcc: [],
    esProyectoEmergencia: false,
    descripcionProyecto: 'Puente de dos carriles sobre el río Lempa.',
    estado,
    fechaIngreso: '2026-02-10T09:00:00',
    revisionPre,
  };
}

function error400(detalles: { campo: string; mensaje: string }[]) {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('Request failed', '400', config, {}, {
    status: 400,
    statusText: '',
    data: { codigo: 'VALIDACION', mensaje: 'Existen campos con datos inválidos.', detalles },
    headers: {},
    config,
  });
}

function error(status: number, mensaje: string) {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('Request failed', String(status), config, {}, {
    status,
    statusText: '',
    data: { mensaje },
    headers: {},
    config,
  });
}

function renderizar() {
  return render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/7']}>
      <Routes>
        <Route path="/preinversion/proyectos/:id" element={<ProyectoFormPage />} />
      </Routes>
    </MemoryRouter>,
  );
}

beforeEach(() => {
  [obtenerProyecto, actualizarProyecto, solicitarCup, responderObservacionCup, navigate, confirmDialog, swalFire].forEach(
    (mock) => mock.mockReset(),
  );
  confirmDialog.mockResolvedValue(true);
  swalFire.mockResolvedValue({ isConfirmed: true });
});

describe('ProyectoFormPage — solicitar CUP', () => {
  it('confirma, llama al back y regresa a la bandeja (SF-1.2 / FA-1)', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('EN_REGISTRO') });
    solicitarCup.mockResolvedValue({ data: proyecto('ENVIADO_DGICP_REGISTRO') });

    renderizar();
    fireEvent.click(await screen.findByRole('button', { name: 'Solicitar CUP' }));

    await waitFor(() => expect(solicitarCup).toHaveBeenCalledWith({ idProyecto: 7 }));
    expect(confirmDialog).toHaveBeenCalled();
    await waitFor(() => expect(navigate).toHaveBeenCalledWith('/preinversion/proyectos'));
  });

  it('no llama al back si el usuario cancela la confirmación', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('EN_REGISTRO') });
    confirmDialog.mockResolvedValue(false);

    renderizar();
    fireEvent.click(await screen.findByRole('button', { name: 'Solicitar CUP' }));

    await waitFor(() => expect(confirmDialog).toHaveBeenCalled());
    expect(solicitarCup).not.toHaveBeenCalled();
  });

  it('marca cada campo devuelto en detalles[] de un 400, sin abrir el modal genérico', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('EN_REGISTRO') });
    solicitarCup.mockRejectedValue(
      error400([
        { campo: 'nombre', mensaje: '*Campo obligatorio' },
        { campo: 'descripcionProyecto', mensaje: '*Campo obligatorio' },
      ]),
    );

    renderizar();
    fireEvent.click(await screen.findByRole('button', { name: 'Solicitar CUP' }));

    // El escenario "Solicitar el CUP con campos incompletos" pide el mensaje
    // sobre el campo, no un aviso general.
    await waitFor(() => expect(screen.getAllByText('*Campo obligatorio')).toHaveLength(2));
    expect(swalFire).not.toHaveBeenCalled();
  });

  it('muestra en el modal el detalle de un campo que no está en pantalla', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('EN_REGISTRO') });
    // tipoEvento sólo se renderiza con "Proyecto de emergencia" marcado: marcarlo
    // no lo haría visible, así que el mensaje tiene que salir arriba.
    solicitarCup.mockRejectedValue(error400([{ campo: 'tipoEvento', mensaje: '*Campo obligatorio' }]));

    renderizar();
    fireEvent.click(await screen.findByRole('button', { name: 'Solicitar CUP' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalled());
    expect(swalFire.mock.calls[0][0].text).toContain('*Campo obligatorio');
  });
});

describe('ProyectoFormPage — Revisión PRE', () => {
  const OBSERVACION = {
    idComentario: 1,
    autor: { idUsuario: 9, nombreCompleto: 'Ana Pérez', rol: 'TECNICO_PRE' },
    texto: 'Falta detallar el alcance del componente 2.',
    fechaComentario: '2026-02-11T10:00:00',
  };

  it('muestra el hilo y permite responder con el proyecto observado (RN 2.9)', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('OBSERVADO_DGICP_REGISTRO', [OBSERVACION]) });
    responderObservacionCup.mockResolvedValue({ data: proyecto('ENVIADO_DGICP_REGISTRO', [OBSERVACION]) });

    renderizar();

    expect(await screen.findByText('Falta detallar el alcance del componente 2.')).toBeInTheDocument();
    fireEvent.change(screen.getByLabelText(/Respuesta/), { target: { value: 'Se amplió el alcance.' } });
    fireEvent.click(screen.getByRole('button', { name: 'Enviar' }));

    await waitFor(() =>
      expect(responderObservacionCup).toHaveBeenCalledWith({
        idProyecto: 7,
        respuestaObservacionRequest: { respuesta: 'Se amplió el alcance.' },
      }),
    );
  });

  it('exige la respuesta antes de llamar al back', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('OBSERVADO_DGICP_REGISTRO', [OBSERVACION]) });

    renderizar();
    fireEvent.click(await screen.findByRole('button', { name: 'Enviar' }));

    expect(await screen.findByText('*Campo obligatorio')).toBeInTheDocument();
    expect(responderObservacionCup).not.toHaveBeenCalled();
  });

  it('no ofrece responder mientras el proyecto sigue enviado a DGICP', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('ENVIADO_DGICP_REGISTRO', [OBSERVACION]) });

    renderizar();

    expect(await screen.findByText('Revisión PRE')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Enviar' })).not.toBeInTheDocument();
  });
});

describe('ProyectoFormPage — carga', () => {
  it('muestra el error y deja salir si el proyecto no existe (404)', async () => {
    obtenerProyecto.mockRejectedValue(error(404, 'El proyecto 7 no existe.'));

    renderizar();

    expect(await screen.findByRole('alert')).toHaveTextContent('El proyecto 7 no existe.');
    // Sin catch la pantalla quedaba colgada en "Cargando...".
    expect(screen.queryByText('Cargando...')).not.toBeInTheDocument();
  });
});

describe('ProyectoFormPage — formulario', () => {
  it('guarda los cambios del formulario con PUT /proyectos/{id} (SF-2)', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('EN_REGISTRO') });
    actualizarProyecto.mockResolvedValue({ data: proyecto('EN_REGISTRO') });

    renderizar();

    fireEvent.change(await screen.findByLabelText('Nombre del proyecto*'), {
      target: { value: 'Puente sobre el río Lempa — fase II' },
    });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(actualizarProyecto).toHaveBeenCalled());
    expect(actualizarProyecto.mock.calls[0][0].proyectoRequest.nombre).toBe('Puente sobre el río Lempa — fase II');
  });

  it('bloquea el guardado y marca el campo vacío sin llamar al back', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('EN_REGISTRO') });

    renderizar();

    fireEvent.change(await screen.findByLabelText('Nombre del proyecto*'), { target: { value: '' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    expect(await screen.findByText('*Campo obligatorio')).toBeInTheDocument();
    expect(actualizarProyecto).not.toHaveBeenCalled();
  });

  it('revela tipo de evento y N° de DL sólo al marcar "Proyecto de emergencia"', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('EN_REGISTRO') });

    renderizar();

    expect(screen.queryByLabelText('Tipo de evento*')).not.toBeInTheDocument();
    fireEvent.click(await screen.findByLabelText('Proyecto de emergencia'));

    expect(await screen.findByLabelText('Tipo de evento*')).toBeInTheDocument();
    expect(screen.getByLabelText('N° de DL*')).toBeInTheDocument();
  });

  it('deja la pantalla en sólo consulta mientras está enviado a DGICP', async () => {
    obtenerProyecto.mockResolvedValue({ data: proyecto('ENVIADO_DGICP_REGISTRO') });

    renderizar();

    // Sin este aviso el usuario sólo ve una pantalla muerta.
    expect(await screen.findByRole('status')).toHaveTextContent('Enviado DGICP (Registro)');
    expect(screen.getByLabelText('Nombre del proyecto*')).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
  });
});
