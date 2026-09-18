import { CatlogosApi, RegistrosApi } from './generated/administracion-catalogos-admin';
import { ConsultasDeCalendarioApi, GestinDeCalendariosApi } from './generated/administracion-calendario';
import { createHttpClient } from './httpClient';

/**
 * Cliente de Administración (CU-ADM-01 · Administración de Catálogos).
 *
 * Dominio propio: su OpenAPI y su módulo generado son independientes de los de
 * preinversión, así que también lleva su propia instancia de axios. El motivo
 * de no reutilizar el `httpClient` genérico está explicado en preinversionApi.
 *
 * El generador partió el cliente en dos clases porque el contrato usa dos tags:
 * los catálogos (la definición) y sus registros (los datos).
 */
const administracionAxios = createHttpClient('/back');

export const catalogosApi = new CatlogosApi(undefined, undefined, administracionAxios);
export const registrosCatalogoApi = new RegistrosApi(undefined, undefined, administracionAxios);

// CU-ADM-04 (Gestión de Calendario). El generador lo partió en dos clases, una
// por tag: el mantenimiento del calendario y las consultas de cálculo, que el
// back usa para contar días hábiles y que aquí sirven para comprobar lo cargado.
export const calendariosApi = new GestinDeCalendariosApi(undefined, undefined, administracionAxios);
export const consultasCalendarioApi = new ConsultasDeCalendarioApi(undefined, undefined, administracionAxios);

export {
  TipoCampo,
  EstadoVigencia,
} from './generated/administracion-catalogos-admin';

export type {
  CampoDefinicion,
  CatalogoResponse,
  CatalogoHijoResponse,
  CrearCatalogoRequest,
  ActualizarCatalogoRequest,
  ExistenciaCatalogoResponse,
  RegistroResponse,
  RegistroValoresResponse,
  ListaRegistrosResponse,
  Vigencia,
} from './generated/administracion-catalogos-admin';
