import { CatlogosPreinversinApi, PreinversinRegistroYSolicitudDeCUPApi } from './generated/preinversion';
import { PreinversinRevisinYEmisinDeCUPApi } from './generated/preinversion-revision-cup';
import { PreinversinIdentificacinApi } from './generated/preinversion-identificacion';
import { CatlogosSeleccinYRegistroDeEtapasApi, PreinversinSeleccinYRegistroDeEtapasApi } from './generated/preinversion-etapas';
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
// CU-PRE-01.5 (Revisión y Emisión de CUP): mismo recurso Proyecto y mismo basePath /back,
// pero tag distinto en el OpenAPI -> fragmento y módulo generado propios
// (generated/preinversion-revision-cup); comparte la instancia de axios de arriba.
export const revisionCupApi = new PreinversinRevisinYEmisinDeCUPApi(undefined, undefined, preinversionAxios);
// CU-PRE-04 (Identificación): mismo recurso Proyecto y mismo basePath /back, pero tag distinto
// en el OpenAPI -> fragmento y módulo generado propios (generated/preinversion-identificacion);
// comparte la instancia de axios de arriba.
export const identificacionApi = new PreinversinIdentificacinApi(undefined, undefined, preinversionAxios);
// CU-PRE-3.5 (Selección y Registro de Etapas): mismo recurso Proyecto y mismo basePath /back,
// fragmento y módulo generado propios (generated/preinversion-etapas). Los 3 endpoints de
// catálogo de este CU tienen su propio tag (ver nota en el YAML sobre el choque con
// CatlogosPreinversinApi de CU-PRE-01) y por eso el generador los separó en su propia clase.
export const etapasApi = new PreinversinSeleccinYRegistroDeEtapasApi(undefined, undefined, preinversionAxios);
export const catalogoEtapasApi = new CatlogosSeleccinYRegistroDeEtapasApi(undefined, undefined, preinversionAxios);

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
// Único tipo propio de este fragmento (el resto son los mismos schemas de CU-PRE-01,
// duplicados por el generador en su módulo — ver comentario de revisionCupApi arriba).
export type { DevolucionSolicitudRequest } from './generated/preinversion-revision-cup';
// CU-PRE-04: tipos propios de este fragmento. UnidadEjecutoraResumen no se reexporta de nuevo
// (mismo schema que CU-PRE-01, ya exportado arriba desde generated/preinversion).
export type { Identificacion, IdentificacionRequest, ArchivoAdjuntoResumen } from './generated/preinversion-identificacion';

// CU-PRE-3.5: tipos propios de este fragmento. IniciativaInversion/InstitucionResumen/
// UnidadEjecutoraResumen/SectorResumen/EjeTematicoResumen/etc. también quedaron duplicados en
// este módulo (mismo criterio que revisionCupApi) pero no hace falta re-exportarlos aparte: las
// páginas de este CU reciben esos objetos ya anidados dentro de FichaInformacionGeneral.
export type {
  RutaPreinversion,
  RutaPreinversionSugerida,
  CriteriosCalificacion,
  ModificarRutaPreinversionRequest,
  Etapa,
  EtapaRegistroRequest,
  ActualizarEtapasRequest,
  FichaInformacionGeneral,
  SeleccionCoEjecutorRequest,
  FichaEmergencia,
  FichaEmergenciaRequest,
  ProductoSeleccionado,
  ComponenteCosto,
  TipoCostoResumen,
  UbicacionGeografica,
  ProductoIndicador,
} from './generated/preinversion-etapas';
export { NombreEtapa, TipoCapital, TamanioProyecto, ComplejidadProyecto, FuenteFinanciamiento } from './generated/preinversion-etapas';
