import { CatalogApi, CatalogRecordApi } from './generated/administracion-catalogos-admin';
import { CalendariosConsultasApi, CalendariosGestinApi } from './generated/administracion-calendario';
import { createHttpClient } from './httpClient';

/**
 * Cliente de Administración.
 *
 * Dominio propio: sus OpenAPI y sus módulos generados son independientes de los
 * de preinversión, así que también lleva su propia instancia de axios. El motivo
 * de no reutilizar el `httpClient` genérico está explicado en preinversionApi.
 */
const administracionAxios = createHttpClient('/back');

// CU-ADM-01 (Administración de Catálogos). El generador lo partió en dos clases,
// una por tag del contrato: los catálogos (su definición) y sus registros (los datos).
export const catalogosApi = new CatalogApi(undefined, undefined, administracionAxios);
export const registrosCatalogoApi = new CatalogRecordApi(undefined, undefined, administracionAxios);

export { ActiveStatus, FieldQualifier } from './generated/administracion-catalogos-admin';
export type { Catalog, CatalogField, CatalogRecord } from './generated/administracion-catalogos-admin';

// CU-ADM-04 (Gestión de Calendario). El generador lo partió en dos clases, una
// por tag: el mantenimiento del calendario y las consultas de cálculo, que el
// back usa para contar días hábiles y que aquí sirven para comprobar lo cargado.
export const calendariosApi = new CalendariosGestinApi(undefined, undefined, administracionAxios);
export const consultasCalendarioApi = new CalendariosConsultasApi(undefined, undefined, administracionAxios);

export type { Calendario, CalendarioResumen, CalendarItem } from './generated/administracion-calendario';
