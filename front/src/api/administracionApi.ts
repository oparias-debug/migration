import { CatlogosApi, RegistrosApi } from './generated/administracion-catalogos-admin';
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
