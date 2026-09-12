import { PreinversinRegistroYSolicitudDeCUPApi } from './generated/preinversion';
import { PreinversinRevisinYEmisinDeCUPApi } from './generated/preinversion-revision-cup';
import { PreinversinIdentificacinApi } from './generated/preinversion-identificacion';
import { PreinversinBandejaPreinversinApi } from './generated/preinversion-bandeja';
import { PreinversinCapturaDeProyectosApi } from './generated/preinversion-captura';
import { PreinversinSeleccinYRegistroDeEtapasApi } from './generated/preinversion-etapas';
import { PreinversinAlternativasDeSolucinApi } from './generated/preinversion-alternativas';
import { PreinversinPresupuestoDeInversinApi } from './generated/preinversion-presupuesto';
import {
  CatlogosRegistroDeProyectosApi,
  CatlogosPresupuestoApi,
  CatlogosBandejaPreinversinApi,
  CatlogosSeleccinYRegistroDeEtapasApi,
} from './generated/administracion-catalogos';
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
export const catalogoPreinversionApi = new CatlogosRegistroDeProyectosApi(undefined, undefined, preinversionAxios);
// CU-PRE-01.5 (Revisión y Emisión de CUP): mismo recurso Proyecto y mismo basePath /back,
// pero tag distinto en el OpenAPI -> fragmento y módulo generado propios
// (generated/preinversion-revision-cup); comparte la instancia de axios de arriba.
export const revisionCupApi = new PreinversinRevisinYEmisinDeCUPApi(undefined, undefined, preinversionAxios);
// CU-PRE-04 (Identificación): mismo recurso Proyecto y mismo basePath /back, pero tag distinto
// en el OpenAPI -> fragmento y módulo generado propios (generated/preinversion-identificacion);
// comparte la instancia de axios de arriba.
export const identificacionApi = new PreinversinIdentificacinApi(undefined, undefined, preinversionAxios);
// CU-PRE-3.5 (Selección y Registro de Etapas): mismo recurso Proyecto y mismo basePath /back,
// fragmento y módulo generado propios (generated/preinversion-etapas). Sus 4 endpoints de
// catálogo tienen tag propio y viven ahora en CU-ADM-02-catalogos.openapi.yaml (dominio
// "administracion"), junto con los del resto de CUs — ver catalogoEtapasApi más abajo.
// CU-PRE-02 (Bandeja Preinversión): mismo basePath /back, tag propio -> módulo
// generado propio (generated/preinversion-bandeja). Su fragmento reutiliza por
// $ref los schemas de CU-PRE-01 (UnidadEjecutoraResumen, EstadoProyecto, Error),
// así que los dos YAML tienen que vivir en la misma carpeta para generar.
export const bandejaApi = new PreinversinBandejaPreinversinApi(undefined, undefined, preinversionAxios);
// El catálogo "Nombres Técnicos PRE" (Anexo C, RN04) vive en CU-ADM-02-catalogos.openapi.yaml
// (dominio "administracion"), junto con el resto de catálogos.
export const catalogoBandejaApi = new CatlogosBandejaPreinversinApi(undefined, undefined, preinversionAxios);
// CU-PRE-03 (Captura de Proyectos): mismo basePath /back, tag propio -> módulo
// generado propio (generated/preinversion-captura).
export const capturaApi = new PreinversinCapturaDeProyectosApi(undefined, undefined, preinversionAxios);
export const etapasApi = new PreinversinSeleccinYRegistroDeEtapasApi(undefined, undefined, preinversionAxios);
export const catalogoEtapasApi = new CatlogosSeleccinYRegistroDeEtapasApi(undefined, undefined, preinversionAxios);
// CU-PRE-05 (Alternativas de Solución): mismo recurso Proyecto y mismo basePath /back, pero tag
// distinto en el OpenAPI -> fragmento y módulo generado propios (generated/preinversion-alternativas);
// comparte la instancia de axios de arriba.
export const alternativasSolucionApi = new PreinversinAlternativasDeSolucinApi(undefined, undefined, preinversionAxios);

export type {
  Proyecto,
  ProyectoRequest,
  ProyectoListItem,
  ProyectoListResponse,
  PaginacionMetadata,
  RespuestaObservacionRequest,
  CambioUnidadEjecutoraRequest,
  ComentarioSolicitud,
  UsuarioResumen,
  InstitucionResumen,
  UnidadEjecutoraResumen,
} from './generated/preinversion';
export { EstadoProyecto, IniciativaInversion } from './generated/preinversion';
// Catálogos de CU-PRE-01/02/3.5 (sectores, ejes, medidas, tipos de costo, ubicaciones,
// productos e indicadores): reorganizados a un tag/spec propio de "administracion"
// (CU-ADM-02-catalogos.openapi.yaml), separado del CU que los consume.
export type {
  MedidaCatalogo,
  SectorResumen,
  MacrosectorResumen,
  EjeTematicoResumen,
  EjePlanGobiernoResumen,
  PlanSectorialRegionalResumen,
  TipoCostoResumen,
  UbicacionGeografica,
  ProductoIndicador,
} from './generated/administracion-catalogos';
export { TipoMedidaCatalogo } from './generated/administracion-catalogos';
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
} from './generated/preinversion-etapas';
export { NombreEtapa, TipoCapital, TamanioProyecto, ComplejidadProyecto, FuenteFinanciamiento } from './generated/preinversion-etapas';

// CU-PRE-02: tipos propios de este fragmento.
export type {
  SolicitudActivaItem,
  SolicitudArchivadaItem,
  SolicitudesActivasResponse,
  SolicitudesArchivadasResponse,
  ConteoTecnicoPre,
  AsignacionTecnicoPreRequest,
} from './generated/preinversion-bandeja';
export { TipoSolicitud } from './generated/preinversion-bandeja';

// CU-PRE-05: tipos propios de este fragmento.
export type { RegistroAlternativas, RegistroAlternativasRequest, AlternativaSolucionRequest } from './generated/preinversion-alternativas';

// CU-PRE-03: tipos propios de este fragmento.
export type { ProyectoCapturaItem, ProyectosCapturaResponse } from './generated/preinversion-captura';

// CU-PRE-17 (Presupuesto de Inversión): mismo recurso Proyecto y mismo basePath
// /back, pero fragmento y módulo generado propios. El catálogo de tipos de
// insumo va aparte porque el contrato le da su propio tag.
export const presupuestoApi = new PreinversinPresupuestoDeInversinApi(undefined, undefined, preinversionAxios);
export const catalogoInsumosApi = new CatlogosPresupuestoApi(undefined, undefined, preinversionAxios);
// FuenteFinanciamiento no se reexporta desde aquí: CU-PRE-3.5 y CU-PRE-17
// declaran el mismo enum con los mismos siete valores (comprobado), así que se
// reutiliza el que ya salía de preinversion-etapas y se evita un alias que
// haría pensar que son cosas distintas.
export type {
  Presupuesto,
  ProductoPresupuesto,
  Macroactividad,
  MacroactividadInsumo,
  MacroactividadRequest,
  MontoPorPeriodo,
  ComponentePresupuesto,
  InsumoTipoResumen,
  FuentesFinanciamientoRequest,
} from './generated/preinversion-presupuesto';
