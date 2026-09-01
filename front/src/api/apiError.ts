import axios from 'axios';
import type { ErrorDetalle, ModelError } from './generated/preinversion';

/**
 * Traducción de un error de axios al schema `Error` del contrato
 * (`codigo`, `mensaje`, `timestamp`, `detalles[]`).
 *
 * Existe porque cada código de estado del OpenAPI significa algo distinto para
 * el usuario y hoy todos terminaban en el mismo modal genérico:
 *
 * - 400 → hay campos inválidos y el back dice cuáles en `detalles[].campo`.
 *         El escenario "Solicitar el CUP con campos incompletos"
 *         (CU-PRE-01-solicitar-cup.feature) exige marcar cada campo y mostrar
 *         ahí el mensaje del Anexo B.2, no un aviso general.
 * - 401 → sesión vencida. Lo resuelve el interceptor de httpClient (refresh);
 *         si llega hasta acá es que el refresh también falló.
 * - 403 → el rol autenticado no puede ejecutar la operación (x-roles del yaml).
 * - 404 → el proyecto o la unidad ejecutora destino no existen.
 * - 409 → el estado del proyecto no admite la acción (RN 1.c / RN 2.2.b).
 *         No es culpa de lo que escribió el usuario.
 */

export type ClaseError = 'validacion' | 'sesion' | 'permiso' | 'inexistente' | 'conflicto' | 'servidor' | 'red';

export interface ErrorApi {
  clase: ClaseError;
  estadoHttp: number;
  /** `codigo` del schema Error; null si la respuesta no lo trae. */
  codigo: string | null;
  /**
   * `mensaje` del schema Error. Sólo se rellena con lo que manda el back, que
   * es texto de negocio en español (Anexo B.2). Nunca con `error.message` de
   * axios: eso es texto técnico en inglés y no se le muestra al usuario.
   */
  mensaje: string | null;
  /** Texto técnico del fallo, para diagnóstico. No se muestra en pantalla. */
  detalleTecnico: string | null;
  /** `detalles[]` del schema Error. Vacío salvo en 400. */
  detalles: ErrorDetalle[];
}

const CLASE_POR_ESTADO: Record<number, ClaseError> = {
  400: 'validacion',
  401: 'sesion',
  403: 'permiso',
  404: 'inexistente',
  409: 'conflicto',
};

/** Clave de i18next del aviso general que corresponde a cada clase. */
export const CLAVE_MENSAJE: Record<ClaseError, string> = {
  validacion: 'errores.validacion',
  sesion: 'errores.sesion',
  permiso: 'errores.permiso',
  inexistente: 'errores.inexistente',
  conflicto: 'errores.conflicto',
  servidor: 'errores.servidor',
  red: 'errores.red',
};

function esModelError(cuerpo: unknown): cuerpo is ModelError {
  return typeof cuerpo === 'object' && cuerpo !== null && 'mensaje' in cuerpo;
}

export function toErrorApi(error: unknown): ErrorApi {
  if (axios.isAxiosError(error)) {
    // Sin `response` el servidor no contestó: red caída, CORS o gateway abajo.
    if (!error.response) {
      return { clase: 'red', estadoHttp: 0, codigo: null, mensaje: null, detalleTecnico: error.message, detalles: [] };
    }

    const estadoHttp = error.response.status;
    const cuerpo: unknown = error.response.data;
    const modelError = esModelError(cuerpo) ? cuerpo : null;

    return {
      clase: CLASE_POR_ESTADO[estadoHttp] ?? 'servidor',
      estadoHttp,
      codigo: modelError?.codigo ?? null,
      mensaje: modelError?.mensaje ?? null,
      detalleTecnico: error.message,
      detalles: modelError?.detalles ?? [],
    };
  }

  return {
    clase: 'servidor',
    estadoHttp: 0,
    codigo: null,
    mensaje: null,
    detalleTecnico: error instanceof Error ? error.message : String(error),
    detalles: [],
  };
}

/**
 * `detalles[]` como `{ campo: mensaje }`, listo para volcarlo sobre el
 * formulario con `setError` de react-hook-form.
 */
export function erroresPorCampo(error: ErrorApi): Record<string, string> {
  return Object.fromEntries(error.detalles.map((detalle) => [detalle.campo, detalle.mensaje]));
}

/**
 * Texto a mostrar: se prefiere el `mensaje` que manda el back (viene del Anexo
 * B.2 y es más específico) y se cae a la clave genérica de i18next sólo si no
 * lo trae.
 */
export function mensajeDeError(error: ErrorApi, t: (clave: string) => string): string {
  return error.mensaje ?? t(CLAVE_MENSAJE[error.clase]);
}

