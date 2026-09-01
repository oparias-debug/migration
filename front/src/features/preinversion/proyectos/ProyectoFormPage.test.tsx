import { beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { AxiosError, AxiosHeaders } from 'axios';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import type { ModelError } from '../../../api/generated/preinversion';
import i18n from '../../../i18n/i18n';

const obtenerProyecto = vi.fn();
const registrarProyecto = vi.fn();
const actualizarProyecto = vi.fn();
const solicitarCup = vi.fn();
const responderObservacionCup = vi.fn();
const listarSectores = vi.fn().mockResolvedValue({ data: [{ idSector: 1, nombre: 'Salud' }] });
const listarEjesTematicos = vi.fn().mockResolvedValue({ data: [{ idEjeTematico: 7, nombre: 'Infraestructura de salud' }] });
const listarCatalogo = vi.fn().mockResolvedValue({ data: [] });
const swalFire = vi.fn().mockResolvedValue({ isConfirmed: true });

vi.mock('../../../api/preinversionApi', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../../api/preinversionApi')>()),
  preinversionApi: {
    obtenerProyecto: (...a: unknown[]) => obtenerProyecto(...a),
    registrarProyecto: (...a: unknown[]) => registrarProyecto(...a),
    actualizarProyecto: (...a: unknown[]) => actualizarProyecto(...a),
    solicitarCup: (...a: unknown[]) => solicitarCup(...a),
    responderObservacionCup: (...a: unknown[]) => responderObservacionCup(...a),
  },
  catalogoPreinversionApi: {
    listarSectores: () => listarSectores(),
    listarEjesTematicos: () => listarEjesTematicos(),
    listarEjesPlanGobierno: () => listarCatalogo(),
    listarPlanesSectoriales: () => listarCatalogo(),
    listarMedidasCatalogo: () => listarCatalogo(),
  },
}));

vi.mock('../../../auth/useAuth', () => ({ useAuth: () => ({ hasRole: () => true }) }));
vi.mock('sweetalert2', () => ({ default: { fire: (...a: unknown[]) => swalFire(...a) } }));

const { ProyectoFormPage } = await import('./ProyectoFormPage');

function errorHttp(status: number, data?: ModelError): AxiosError {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('fallo', String(status), config, null, {
    status,
    statusText: '',
    headers: {},
    config,
    data,
  });
}

const montarNuevo = () =>
  render(
    <MemoryRouter initialEntries={['/preinversion/proyectos/nuevo']}>
      <Routes>
        <Route path="/preinversion/proyectos/nuevo" element={<ProyectoFormPage />} />
      </Routes>
    </MemoryRouter>,
  );

const montarEdicion = (id = '7') =>
  render(
    <MemoryRouter initialEntries={[`/preinversion/proyectos/${id}`]}>
      <Routes>
        <Route path="/preinversion/proyectos/:id" element={<ProyectoFormPage />} />
      </Routes>
    </MemoryRouter>,
  );

// Rellena los seis obligatorios de ProyectoRequest para que zod deje pasar el
// submit: lo que se prueba aquí es qué hace la pantalla con la respuesta del
// back, no la validación en cliente.
async function completarMinimos() {
  await screen.findByRole('option', { name: 'Salud' });
  fireEvent.click(screen.getByLabelText('Proyecto'));
  fireEvent.change(screen.getByLabelText(/Nombre del proyecto/i), { target: { value: 'Proyecto de prueba' } });
  fireEvent.change(screen.getByLabelText(/Inversión Estimada/i), { target: { value: '1000' } });
  fireEvent.change(screen.getByLabelText(/^Sector/i), { target: { value: '1' } });
  fireEvent.change(screen.getByLabelText(/Eje temático/i), { target: { value: '7' } });
  fireEvent.change(screen.getByLabelText(/Descripción del proyecto/i), { target: { value: 'Descripción de prueba' } });
}

describe('ProyectoFormPage', () => {
  beforeEach(() => {
    obtenerProyecto.mockReset();
    registrarProyecto.mockReset();
    actualizarProyecto.mockReset();
    solicitarCup.mockReset();
    responderObservacionCup.mockReset();
    swalFire.mockClear();
  });

  // El defecto que se corrige: los 400 del back traen detalles[].campo y se
  // descartaban en un modal genérico. El escenario "Solicitar el CUP con campos
  // incompletos" exige marcar cada campo con su mensaje del Anexo B.2.
  it('pinta sobre cada campo el mensaje que devuelve el back en un 400', async () => {
    registrarProyecto.mockRejectedValue(
      errorHttp(400, {
        codigo: 'VALIDACION',
        mensaje: 'Existen campos obligatorios sin completar.',
        timestamp: '2026-08-29T10:00:00Z',
        detalles: [
          { campo: 'nombre', mensaje: '*Campo obligatorio' },
          { campo: 'descripcionProyecto', mensaje: 'Máximo 1000 caracteres' },
        ],
      }),
    );

    montarNuevo();
    await completarMinimos();
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(screen.getByText('*Campo obligatorio')).toBeInTheDocument());
    expect(screen.getByText('Máximo 1000 caracteres')).toBeInTheDocument();
    // Los detalles ya se ven en los campos: no se duplica con un modal.
    expect(swalFire).not.toHaveBeenCalled();
  });

  it('un 409 explica que el estado no permite la acción, no "error al guardar"', async () => {
    registrarProyecto.mockRejectedValue(
      errorHttp(409, {
        codigo: 'ESTADO_NO_EDITABLE',
        mensaje: 'El proyecto está en ENVIADO_DGICP_REGISTRO y no admite edición.',
        timestamp: '2026-08-29T10:00:00Z',
      }),
    );

    montarNuevo();
    await completarMinimos();
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalled());
    expect(swalFire.mock.calls[0][0]).toMatchObject({
      icon: 'error',
      text: 'El proyecto está en ENVIADO_DGICP_REGISTRO y no admite edición.',
    });
  });

  it('un 403 avisa que el rol no tiene permiso', async () => {
    registrarProyecto.mockRejectedValue(errorHttp(403));

    montarNuevo();
    await completarMinimos();
    fireEvent.click(screen.getByRole('button', { name: 'Guardar' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalled());
    expect(swalFire.mock.calls[0][0]).toMatchObject({
      text: i18n.t('errores.permiso'),
    });
  });

  // ---------------------------------------------------------------------
  // CU-PRE-01-solicitar-cup.feature
  // ---------------------------------------------------------------------

  const proyectoEn = (estado: string) => ({
    data: {
      idProyecto: 7,
      cup: null,
      nombre: 'Equipamiento del hospital regional',
      iniciativaInversion: 'PROYECTO',
      montoEstimadoInversion: 1000,
      sector: { idSector: 1, nombre: 'Salud' },
      ejeTematico: { idEjeTematico: 7, nombre: 'Infraestructura de salud' },
      descripcionProyecto: 'Descripción',
      medidasGrd: [], medidasGrc: [], medidasAcc: [],
      esProyectoEmergencia: false,
      institucion: { idInstitucion: 1, nombre: 'MINSAL' },
      unidadEjecutora: { idUnidadEjecutora: 4501, nombre: 'MINSAL' },
      estado,
      fechaIngreso: '2026-08-18T09:00:00Z',
      revisionPre: [],
    },
  });

  it('camino feliz: solicita el CUP y vuelve al listado', async () => {
    obtenerProyecto.mockResolvedValue(proyectoEn('EN_REGISTRO'));
    solicitarCup.mockResolvedValue(proyectoEn('ENVIADO_DGICP_REGISTRO'));

    montarEdicion('7');
    fireEvent.click(await screen.findByRole('button', { name: 'Solicitar CUP' }));

    await waitFor(() => expect(solicitarCup).toHaveBeenCalledWith({ idProyecto: 7 }));
    expect(swalFire).toHaveBeenCalledWith(expect.objectContaining({ icon: 'success' }));
  });

  it('con campos incompletos: marca cada campo y no sale de la pantalla', async () => {
    obtenerProyecto.mockResolvedValue(proyectoEn('EN_REGISTRO'));
    solicitarCup.mockRejectedValue(
      errorHttp(400, {
        codigo: 'VALIDACION',
        mensaje: 'Existen inconsistencias.',
        timestamp: '2026-08-29T10:00:00Z',
        detalles: [{ campo: 'descripcionProyecto', mensaje: '*Campo obligatorio' }],
      }),
    );

    montarEdicion('7');
    fireEvent.click(await screen.findByRole('button', { name: 'Solicitar CUP' }));

    expect(await screen.findByText('*Campo obligatorio')).toBeInTheDocument();
    // Se cancela la acción y se sigue en "Nuevo registro".
    expect(screen.getByRole('button', { name: 'Solicitar CUP' })).toBeInTheDocument();
  });

  it('un 409 al solicitar explica que el estado no lo permite', async () => {
    obtenerProyecto.mockResolvedValue(proyectoEn('EN_REGISTRO'));
    solicitarCup.mockRejectedValue(
      errorHttp(409, { codigo: 'ESTADO', mensaje: 'No se puede solicitar el CUP desde este estado.', timestamp: '' }),
    );

    montarEdicion('7');
    fireEvent.click(await screen.findByRole('button', { name: 'Solicitar CUP' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalled());
    expect(swalFire.mock.calls.at(-1)?.[0]).toMatchObject({
      text: 'No se puede solicitar el CUP desde este estado.',
    });
  });

  // Escenario "Solo consulta mientras el proyecto está Enviado a DGICP".
  it('un proyecto enviado a DGICP es solo de consulta', async () => {
    obtenerProyecto.mockResolvedValue(proyectoEn('ENVIADO_DGICP_REGISTRO'));

    montarEdicion('7');

    expect(await screen.findByRole('status')).toHaveTextContent(
      i18n.t('preinversion.registro.soloConsulta', { estado: 'Enviado DGICP (Registro)' }),
    );
    expect(screen.queryByRole('button', { name: 'Solicitar CUP' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
    expect(screen.getByLabelText(/Nombre del proyecto/i)).toBeDisabled();
  });

  it('no deja solicitar el CUP con cambios sin guardar', async () => {
    obtenerProyecto.mockResolvedValue(proyectoEn('EN_REGISTRO'));

    montarEdicion('7');
    fireEvent.change(await screen.findByLabelText(/Nombre del proyecto/i), { target: { value: ' modificado' } });

    expect(screen.getByRole('button', { name: 'Solicitar CUP' })).toBeDisabled();
    expect(screen.getByText('Guarde los cambios antes de solicitar el CUP.')).toBeInTheDocument();
    expect(solicitarCup).not.toHaveBeenCalled();
  });

  // "el sistema sombrea en rojo el contorno de cada campo con inconsistencia
  //  y muestra en cada campo los mensajes descritos en el Anexo B.2".
  // Se comprueba campo por campo: seis de ellos no tenían dónde pintar el error
  // y el mensaje del back se perdía en silencio.
  it.each([
    'iniciativaInversion',
    'nombre',
    'montoEstimadoInversion',
    'idSector',
    'idEjeTematico',
    'medidasGrd',
    'medidasGrc',
    'medidasAcc',
    'esProyectoEmergencia',
    'idEjePlanGobierno',
    'idPlanSectorialRegional',
    'descripcionProyecto',
  ])('un 400 sobre "%s" se ve en pantalla', async (campo) => {
    obtenerProyecto.mockResolvedValue(proyectoEn('EN_REGISTRO'));
    solicitarCup.mockRejectedValue(
      errorHttp(400, {
        codigo: 'VALIDACION',
        mensaje: 'Existen inconsistencias.',
        timestamp: '',
        detalles: [{ campo, mensaje: `Mensaje del Anexo B.2 para ${campo}` }],
      }),
    );

    montarEdicion('7');
    fireEvent.click(await screen.findByRole('button', { name: 'Solicitar CUP' }));

    expect(await screen.findByText(`Mensaje del Anexo B.2 para ${campo}`)).toBeInTheDocument();
    // Si el mensaje ya está en su campo, no se duplica en un modal.
    expect(swalFire).not.toHaveBeenCalledWith(expect.objectContaining({ icon: 'error' }));
  });

  // tipoEvento y numeroDecretoLegislativo sólo existen en pantalla con
  // "Proyecto de emergencia" marcado, que es cuando el contrato los exige.
  it.each(['tipoEvento', 'numeroDecretoLegislativo'])(
    'un 400 sobre "%s" se ve en su campo cuando es proyecto de emergencia',
    async (campo) => {
      const p = proyectoEn('EN_REGISTRO');
      obtenerProyecto.mockResolvedValue({ data: { ...p.data, esProyectoEmergencia: true, tipoEvento: 'Sismo', numeroDecretoLegislativo: 'DL-1' } });
      solicitarCup.mockRejectedValue(
        errorHttp(400, {
          codigo: 'VALIDACION',
          mensaje: 'Existen inconsistencias.',
          timestamp: '',
          detalles: [{ campo, mensaje: `Mensaje del Anexo B.2 para ${campo}` }],
        }),
      );

      montarEdicion('7');
      fireEvent.click(await screen.findByRole('button', { name: 'Solicitar CUP' }));

      expect(await screen.findByText(`Mensaje del Anexo B.2 para ${campo}`)).toBeInTheDocument();
    },
  );

  // Y si llega un error sobre un campo que no está en pantalla, no se pierde:
  // se muestra arriba con su texto en lugar de desaparecer.
  it('un error sobre un campo oculto se muestra igualmente', async () => {
    obtenerProyecto.mockResolvedValue(proyectoEn('EN_REGISTRO'));
    solicitarCup.mockRejectedValue(
      errorHttp(400, {
        codigo: 'VALIDACION',
        mensaje: 'Existen inconsistencias.',
        timestamp: '',
        detalles: [{ campo: 'tipoEvento', mensaje: 'Debe indicar el tipo de evento' }],
      }),
    );

    montarEdicion('7');
    fireEvent.click(await screen.findByRole('button', { name: 'Solicitar CUP' }));

    await waitFor(() => expect(swalFire).toHaveBeenCalled());
    expect(swalFire.mock.calls.at(-1)?.[0]).toMatchObject({
      text: expect.stringContaining('Debe indicar el tipo de evento'),
    });
  });

  // ---------------------------------------------------------------------
  // CU-PRE-01-responder-observaciones.feature
  // ---------------------------------------------------------------------

  const observacionDelPre = {
    idComentario: 901,
    autor: { idUsuario: 301, nombreCompleto: 'Carlos Méndez', rol: 'TECNICO_PRE' },
    texto: 'Detallar el alcance por departamento.',
    fechaComentario: '2026-08-20T15:20:00Z',
  };

  const proyectoObservado = () => {
    const p = proyectoEn('OBSERVADO_DGICP_REGISTRO');
    return { data: { ...p.data, revisionPre: [observacionDelPre] } };
  };

  it('muestra los comentarios del Técnico PRE en la sección Revisión PRE', async () => {
    obtenerProyecto.mockResolvedValue(proyectoObservado());

    montarEdicion('7');

    expect(await screen.findByText('Detallar el alcance por departamento.')).toBeInTheDocument();
    expect(screen.getByText('Carlos Méndez')).toBeInTheDocument();
  });

  it('habilita el campo Respuesta sólo con el proyecto observado', async () => {
    obtenerProyecto.mockResolvedValue(proyectoEn('EN_REGISTRO'));
    montarEdicion('7');
    await screen.findByRole('heading', { name: 'Revisión PRE' });
    expect(screen.queryByLabelText(/Respuesta/i)).not.toBeInTheDocument();
  });

  it('camino feliz: responde la observación y actualiza el hilo', async () => {
    obtenerProyecto.mockResolvedValue(proyectoObservado());
    const respuestaDelUrp = {
      idComentario: 902,
      autor: { idUsuario: 101, nombreCompleto: 'Ana Beltrán', rol: 'TECNICO_URP' },
      texto: 'Se detalla el alcance por departamento.',
      fechaComentario: '2026-08-21T09:00:00Z',
    };
    responderObservacionCup.mockResolvedValue({
      data: { ...proyectoObservado().data, estado: 'ENVIADO_DGICP_REGISTRO', revisionPre: [observacionDelPre, respuestaDelUrp] },
    });

    montarEdicion('7');
    fireEvent.change(await screen.findByLabelText(/Respuesta/i), { target: { value: 'Se detalla el alcance por departamento.' } });
    fireEvent.click(screen.getByRole('button', { name: 'Enviar' }));

    await waitFor(() =>
      expect(responderObservacionCup).toHaveBeenCalledWith({
        idProyecto: 7,
        respuestaObservacionRequest: { respuesta: 'Se detalla el alcance por departamento.' },
      }),
    );
    expect(await screen.findByText('Se detalla el alcance por departamento.')).toBeInTheDocument();
  });

  it('guarda los cambios del formulario antes de enviar la respuesta', async () => {
    obtenerProyecto.mockResolvedValue(proyectoObservado());
    actualizarProyecto.mockResolvedValue(proyectoObservado());
    responderObservacionCup.mockResolvedValue(proyectoObservado());

    montarEdicion('7');
    fireEvent.change(await screen.findByLabelText(/Nombre del proyecto/i), { target: { value: ' corregido' } });
    fireEvent.change(screen.getByLabelText(/Respuesta/i), { target: { value: 'Corregido.' } });
    fireEvent.click(screen.getByRole('button', { name: 'Enviar' }));

    await waitFor(() => expect(responderObservacionCup).toHaveBeenCalled());
    expect(actualizarProyecto).toHaveBeenCalled();
    // El PUT va antes que el POST: el back guarda y luego recibe la respuesta.
    expect(actualizarProyecto.mock.invocationCallOrder[0]).toBeLessThan(
      responderObservacionCup.mock.invocationCallOrder[0],
    );
  });

  it('no envía una respuesta vacía y lo marca en el campo', async () => {
    obtenerProyecto.mockResolvedValue(proyectoObservado());

    montarEdicion('7');
    await screen.findByLabelText(/Respuesta/i);
    fireEvent.click(screen.getByRole('button', { name: 'Enviar' }));

    expect(await screen.findByText('*Campo obligatorio')).toBeInTheDocument();
    expect(responderObservacionCup).not.toHaveBeenCalled();
  });

  it('un 400 del back sobre el campo respuesta se muestra bajo el campo', async () => {
    obtenerProyecto.mockResolvedValue(proyectoObservado());
    responderObservacionCup.mockRejectedValue(
      errorHttp(400, {
        codigo: 'VALIDACION',
        mensaje: 'El campo Respuesta es obligatorio.',
        timestamp: '',
        detalles: [{ campo: 'respuesta', mensaje: 'No puede quedar vacío' }],
      }),
    );

    montarEdicion('7');
    fireEvent.change(await screen.findByLabelText(/Respuesta/i), { target: { value: 'algo' } });
    fireEvent.click(screen.getByRole('button', { name: 'Enviar' }));

    expect(await screen.findByText('No puede quedar vacío')).toBeInTheDocument();
    expect(swalFire).not.toHaveBeenCalled();
  });

  // El defecto que se corrige: obtenerProyecto sin .catch dejaba la pantalla
  // colgada en "Cargando..." para siempre.
  it('un 404 al abrir un proyecto muestra el error en vez de quedarse cargando', async () => {
    obtenerProyecto.mockRejectedValue(errorHttp(404));

    montarEdicion('999');

    const aviso = await screen.findByRole('alert');
    expect(aviso).toHaveTextContent(i18n.t('errores.inexistente'));
    expect(screen.queryByText('Cargando...')).not.toBeInTheDocument();
  });
});
