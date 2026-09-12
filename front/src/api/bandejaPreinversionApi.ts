import { PreinversinBandejaPreinversinApi } from './generated/preinversion-bandeja';
import { CatlogosBandejaPreinversinApi } from './generated/administracion-catalogos';
import { createHttpClient } from './httpClient';

const http = createHttpClient('/back');
export const bandejaApi = new PreinversinBandejaPreinversinApi(undefined, undefined, http);
export const tecnicosPreApi = new CatlogosBandejaPreinversinApi(undefined, undefined, http);
export type { SolicitudActivaItem, SolicitudArchivadaItem, ConteoTecnicoPre, UsuarioResumen,
  TipoSolicitud } from './generated/preinversion-bandeja';
