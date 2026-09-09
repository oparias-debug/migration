import { CatlogosPreinversinApi, PreinversinBandejaPreinversinApi } from './generated/preinversion-bandeja';
import { createHttpClient } from './httpClient';

const http = createHttpClient('/back');
export const bandejaApi = new PreinversinBandejaPreinversinApi(undefined, undefined, http);
export const tecnicosPreApi = new CatlogosPreinversinApi(undefined, undefined, http);
export type { SolicitudActivaItem, SolicitudArchivadaItem, ConteoTecnicoPre, UsuarioResumen,
  TipoSolicitud } from './generated/preinversion-bandeja';
