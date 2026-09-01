import { CatlogosPreinversinApi, PreinversinRegistroYSolicitudDeCUPApi } from './generated/preinversion';
import { createHttpClient } from './httpClient';

// El cliente generado solo usa el `basePath` que se le pasa en el constructor
// cuando la instancia de axios recibida NO trae baseURL configurado (ver
// createRequestFunction en generated/preinversion/common.ts: si
// axios.defaults.baseURL es truthy, ignora basePath y arma la URL con solo el
// path de la operación). Por eso se le da su propia instancia con
// baseURL: '/back' en vez de reusar httpClient (baseURL: '/'), que haría que
// las requests salieran a /proyectos en lugar de /back/proyectos.
// api-gateway reescribe /back/** -> back:8081/** (RewritePath, ver
// nginx.conf.template / application.yml de api-gateway).
const preinversionAxios = createHttpClient('/back');
export const preinversionApi = new PreinversinRegistroYSolicitudDeCUPApi(undefined, undefined, preinversionAxios);
// Catálogos seleccionables (sectores, ejes temáticos, ejes del Plan de Gobierno, planes
// sectoriales/regionales, medidas GRD/GRC/ACC): tag distinto en el OpenAPI, por eso el
// generador los separó en su propia clase de cliente.
export const catalogoPreinversionApi = new CatlogosPreinversinApi(undefined, undefined, preinversionAxios);

export type {
  Proyecto,
  ProyectoRequest,
  ProyectoListItem,
  ProyectoListResponse,
  PaginacionMetadata,
  MedidaCatalogo,
  SectorResumen,
  MacrosectorResumen,
  EjeTematicoResumen,
  EjePlanGobiernoResumen,
  PlanSectorialRegionalResumen,
  RespuestaObservacionRequest,
  CambioUnidadEjecutoraRequest,
  ComentarioSolicitud,
  UsuarioResumen,
  InstitucionResumen,
  UnidadEjecutoraResumen,
} from './generated/preinversion';
export { EstadoProyecto, IniciativaInversion, TipoMedidaCatalogo } from './generated/preinversion';
