import { TipoSolicitud } from '../../../api/preinversionApi';

// Etiquetas del catálogo de la RN07. El mockup del Anexo A.1 muestra además
// "En análisis", que no está en ese catálogo; el .feature lo deja pendiente de
// aclarar con negocio, así que aquí no se traduce ni se usa.
const TIPO_LABELS: Partial<Record<string, string>> = {
  [TipoSolicitud.Cup]: 'CUP',
  [TipoSolicitud.OpinionTecnica]: 'Opinión Técnica',
};

export function formatTipoSolicitud(tipo: string): string {
  return TIPO_LABELS[tipo] ?? tipo;
}

export const TIPOS_SOLICITUD = [TipoSolicitud.Cup, TipoSolicitud.OpinionTecnica];

/** Tono de la píldora de estado; los desconocidos caen en el neutro. */
export function tonoEstado(estado: string): string {
  if (/observ/i.test(estado)) return 'e-aviso';
  if (/enviad/i.test(estado)) return 'e-info';
  return 'e-neutro';
}

export const ROLES_BANDEJA = ['COORDINADOR_PRE', 'TECNICO_PRE'];
/** Asignar y archivar son sólo del Coordinador PRE (x-roles del contrato). */
export const ROL_COORDINADOR = 'COORDINADOR_PRE';
