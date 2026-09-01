import { describe, expect, it } from 'vitest';
import { AxiosError, AxiosHeaders } from 'axios';
import { erroresPorCampo, mensajeDeError, toErrorApi } from './apiError';
import type { ModelError } from './generated/preinversion';

function respuestaDe(status: number, data?: ModelError): AxiosError {
  const config = { headers: new AxiosHeaders() };
  return new AxiosError('fallo', String(status), config, null, {
    status,
    statusText: '',
    headers: {},
    config,
    data,
  });
}

const errorDeValidacion: ModelError = {
  codigo: 'VALIDACION',
  mensaje: 'Existen campos obligatorios sin completar.',
  timestamp: '2026-08-29T10:00:00Z',
  detalles: [
    { campo: 'nombre', mensaje: '*Campo obligatorio' },
    { campo: 'idSector', mensaje: '*Campo obligatorio' },
  ],
};

describe('toErrorApi', () => {
  it('clasifica cada código de estado del contrato', () => {
    expect(toErrorApi(respuestaDe(400)).clase).toBe('validacion');
    expect(toErrorApi(respuestaDe(401)).clase).toBe('sesion');
    expect(toErrorApi(respuestaDe(403)).clase).toBe('permiso');
    expect(toErrorApi(respuestaDe(404)).clase).toBe('inexistente');
    expect(toErrorApi(respuestaDe(409)).clase).toBe('conflicto');
    expect(toErrorApi(respuestaDe(500)).clase).toBe('servidor');
  });

  it('conserva codigo, mensaje y detalles del schema Error', () => {
    const resultado = toErrorApi(respuestaDe(400, errorDeValidacion));
    expect(resultado.codigo).toBe('VALIDACION');
    expect(resultado.mensaje).toBe('Existen campos obligatorios sin completar.');
    expect(resultado.detalles).toHaveLength(2);
  });

  it('no revienta cuando la respuesta de error no trae cuerpo', () => {
    const resultado = toErrorApi(respuestaDe(409));
    expect(resultado.codigo).toBeNull();
    expect(resultado.mensaje).toBeNull();
    expect(resultado.detalles).toEqual([]);
  });

  it('distingue un fallo de red de un error del servidor', () => {
    const sinRespuesta = new AxiosError('Network Error');
    expect(toErrorApi(sinRespuesta).clase).toBe('red');
    expect(toErrorApi(sinRespuesta).estadoHttp).toBe(0);
  });

  it('no expone el texto técnico de axios como mensaje al usuario', () => {
    const sinRespuesta = toErrorApi(new AxiosError('Network Error'));
    expect(sinRespuesta.mensaje).toBeNull();
    expect(sinRespuesta.detalleTecnico).toBe('Network Error');
  });

  it('tolera cualquier cosa que no sea un error de axios', () => {
    expect(toErrorApi(new Error('roto')).clase).toBe('servidor');
    expect(toErrorApi('roto').clase).toBe('servidor');
  });
});

describe('erroresPorCampo', () => {
  it('convierte detalles[] en un mapa campo → mensaje', () => {
    expect(erroresPorCampo(toErrorApi(respuestaDe(400, errorDeValidacion)))).toEqual({
      nombre: '*Campo obligatorio',
      idSector: '*Campo obligatorio',
    });
  });

  it('devuelve un mapa vacío si no hay detalles', () => {
    expect(erroresPorCampo(toErrorApi(respuestaDe(500)))).toEqual({});
  });
});

describe('mensajeDeError', () => {
  const t = (clave: string) => `[${clave}]`;

  it('prefiere el mensaje del back porque es el del Anexo B.2', () => {
    expect(mensajeDeError(toErrorApi(respuestaDe(400, errorDeValidacion)), t)).toBe(
      'Existen campos obligatorios sin completar.',
    );
  });

  it('cae a la clave de i18next cuando el back no manda mensaje', () => {
    expect(mensajeDeError(toErrorApi(respuestaDe(409)), t)).toBe('[errores.conflicto]');
    expect(mensajeDeError(toErrorApi(respuestaDe(403)), t)).toBe('[errores.permiso]');
  });
});
