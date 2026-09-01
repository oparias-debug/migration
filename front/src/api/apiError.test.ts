import { AxiosError, AxiosHeaders } from 'axios';
import { describe, expect, it } from 'vitest';
import { CLAVE_MENSAJE, erroresPorCampo, mensajeDeError, toErrorApi } from './apiError';

/** Error de axios con respuesta del back, como el que ve el catch de una llamada real. */
function errorConRespuesta(status: number, data: unknown): AxiosError {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('Request failed', String(status), config, {}, {
    status,
    statusText: '',
    data,
    headers: {},
    config,
  });
}

/** Error de axios sin respuesta: el servidor nunca contestó. */
function errorSinRespuesta(): AxiosError {
  return new AxiosError('Network Error', AxiosError.ERR_NETWORK, { headers: new AxiosHeaders() });
}

describe('toErrorApi', () => {
  it('clasifica como "red" el fallo sin respuesta del servidor', () => {
    const error = toErrorApi(errorSinRespuesta());

    expect(error.clase).toBe('red');
    expect(error.estadoHttp).toBe(0);
    expect(error.mensaje).toBeNull();
    expect(error.detalleTecnico).toBe('Network Error');
  });

  it('clasifica el 400 como "validacion" y conserva detalles[] por campo', () => {
    const error = toErrorApi(
      errorConRespuesta(400, {
        codigo: 'VALIDACION',
        mensaje: 'Existen campos de catálogo con identificadores inválidos.',
        detalles: [
          { campo: 'nombre', mensaje: '*Campo obligatorio' },
          { campo: 'idSector', mensaje: 'El catálogo referenciado no existe.' },
        ],
      }),
    );

    expect(error.clase).toBe('validacion');
    expect(error.codigo).toBe('VALIDACION');
    expect(error.detalles).toHaveLength(2);
  });

  it('clasifica el 401 como "sesion" (el refresh de httpClient ya falló)', () => {
    expect(toErrorApi(errorConRespuesta(401, {})).clase).toBe('sesion');
  });

  it('clasifica el 403 como "permiso"', () => {
    expect(toErrorApi(errorConRespuesta(403, {})).clase).toBe('permiso');
  });

  it('clasifica el 404 como "inexistente"', () => {
    expect(toErrorApi(errorConRespuesta(404, {})).clase).toBe('inexistente');
  });

  it('clasifica el 409 como "conflicto" (RN 1.c / RN 2.2.b)', () => {
    const error = toErrorApi(
      errorConRespuesta(409, { mensaje: 'El proyecto no se encuentra en un estado que permita esta accion.' }),
    );

    expect(error.clase).toBe('conflicto');
    expect(error.mensaje).toBe('El proyecto no se encuentra en un estado que permita esta accion.');
  });

  it('cae a "servidor" ante un estado no contemplado, como el 500', () => {
    const error = toErrorApi(errorConRespuesta(500, ''));

    expect(error.clase).toBe('servidor');
    expect(error.estadoHttp).toBe(500);
    expect(error.detalles).toEqual([]);
  });

  it('cae a "servidor" si lo lanzado no es un error de axios', () => {
    const error = toErrorApi(new Error('boom'));

    expect(error.clase).toBe('servidor');
    expect(error.detalleTecnico).toBe('boom');
  });
});

describe('erroresPorCampo', () => {
  it('convierte detalles[] en un mapa campo -> mensaje para setError', () => {
    const error = toErrorApi(
      errorConRespuesta(400, {
        mensaje: 'Si el proyecto es de emergencia, el tipo de evento y el N. de DL son obligatorios.',
        detalles: [
          { campo: 'tipoEvento', mensaje: '*Campo obligatorio' },
          { campo: 'numeroDecretoLegislativo', mensaje: '*Campo obligatorio' },
        ],
      }),
    );

    expect(erroresPorCampo(error)).toEqual({
      tipoEvento: '*Campo obligatorio',
      numeroDecretoLegislativo: '*Campo obligatorio',
    });
  });
});

describe('mensajeDeError', () => {
  it('prefiere el mensaje del back y sólo cae a la clave i18n si no viene', () => {
    const traducir = (clave: string) => `[${clave}]`;

    const conMensaje = toErrorApi(errorConRespuesta(409, { mensaje: 'Ya tiene una solicitud de CUP registrada.' }));
    const sinMensaje = toErrorApi(errorConRespuesta(409, {}));

    expect(mensajeDeError(conMensaje, traducir)).toBe('Ya tiene una solicitud de CUP registrada.');
    expect(mensajeDeError(sinMensaje, traducir)).toBe(`[${CLAVE_MENSAJE.conflicto}]`);
  });
});
