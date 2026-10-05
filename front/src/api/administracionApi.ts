import { CatalogosApi, RegistrosApi } from './generated/administracion-catalogos-admin';
import { CalendariosConsultasApi, CalendariosGestinApi } from './generated/administracion-calendario';
import { createHttpClient } from './httpClient';

/**
 * Cliente de Administración.
 *
 * Dominio propio: sus OpenAPI y sus módulos generados son independientes de los
 * de preinversión, así que también lleva su propia instancia de axios. El motivo
 * de no reutilizar el `httpClient` genérico está explicado en preinversionApi.
 */
// CU-ADM-04 (Calendario) vive en admin-srv, detrás de /admin/** de api-gateway,
// que monta su contrato bajo /api/v1.
const adminSrvAxios = createHttpClient('/admin/api/v1');

// CU-ADM-01 vive en admin-srv, detrás de /admin/** de api-gateway, que monta su
// contrato bajo /api/v1. Ése es el destino por defecto y el de la entidad.
//
// Se deja configurar por entorno porque no todos tienen admin-srv detrás: el de
// vista previa sigue sirviendo los catálogos desde backend-srv en /back, y sin
// esto su despliegue pediría /admin/api/v1, que allí no existe y devuelve el
// index.html de la SPA en vez de datos. Mismo motivo que VITE_API_PROXY_TARGET
// en vite.config.ts: dónde vive el backend es cosa del entorno, no del código.
const CATALOGOS_BASE = import.meta.env.VITE_CATALOGOS_BASE ?? '/admin/api/v1';
const catalogosAxios = createHttpClient(CATALOGOS_BASE);

// CU-ADM-01 (Administración de Catálogos). El contrato vuelve a traer un tag por
// recurso, así que el generador deja dos clases: una para los catálogos y otra
// para sus registros. Se mantienen los dos nombres de siempre en las pantallas.
export const catalogosApi = new CatalogosApi(undefined, undefined, catalogosAxios);
export const registrosCatalogoApi = new RegistrosApi(undefined, undefined, catalogosAxios);

export { Estado, Calificador } from './generated/administracion-catalogos-admin';
export type {
  Catalogo,
  CatalogoResumen,
  CatalogoCreacion,
  CatalogoDescriptores,
  Campo,
  CampoDefinicion,
  ChildCatalogResult,
  CatalogRecordsResult,
  ResultRow,
  Registro,
  RegistroCreacion,
  Vigencia,
} from './generated/administracion-catalogos-admin';

// CU-ADM-04 (Gestión de Calendario). El generador lo partió en dos clases, una
// por tag: el mantenimiento del calendario y las consultas de cálculo (días
// hábiles, tipo de día...), que aquí sirven para comprobar lo cargado.
export const calendariosApi = new CalendariosGestinApi(undefined, undefined, adminSrvAxios);
export const consultasCalendarioApi = new CalendariosConsultasApi(undefined, undefined, adminSrvAxios);

export type { Calendario, CalendarioResumen, CalendarItem } from './generated/administracion-calendario';
