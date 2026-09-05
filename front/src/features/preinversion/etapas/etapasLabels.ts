import {
  ComplejidadProyecto,
  FuenteFinanciamiento,
  NombreEtapa,
  TamanioProyecto,
  TipoCapital,
} from '../../../api/preinversionApi';

// Traducción de los enums fijos de CU-PRE-3.5 (Anexos B.2, C.1, C.3, C.4) a las etiquetas de
// pantalla, mismo criterio que proyectoLabels.ts::formatEstado/formatIniciativa.

const NOMBRE_ETAPA_LABELS: Record<string, string> = {
  [NombreEtapa.Perfil]: 'Perfil',
  [NombreEtapa.Prefactibilidad]: 'Prefactibilidad',
  [NombreEtapa.Factibilidad]: 'Factibilidad',
  [NombreEtapa.Diseno]: 'Diseño',
  [NombreEtapa.Ejecucion]: 'Ejecución',
};

const TIPO_CAPITAL_LABELS: Record<string, string> = {
  [TipoCapital.CapitalFisico]: 'Capital físico',
  [TipoCapital.CapitalHumano]: 'Capital humano',
  [TipoCapital.CapitalInstitucional]: 'Capital institucional',
  [TipoCapital.OtrosCapitales]: 'Otros capitales',
};

const TAMANIO_PROYECTO_LABELS: Record<string, string> = {
  [TamanioProyecto.Pequenio]: 'Pequeño',
  [TamanioProyecto.Mediano]: 'Mediano',
  [TamanioProyecto.Grande]: 'Grande',
};

const COMPLEJIDAD_LABELS: Record<string, string> = {
  [ComplejidadProyecto.Baja]: 'Baja',
  [ComplejidadProyecto.Media]: 'Media',
  [ComplejidadProyecto.Alta]: 'Alta',
  [ComplejidadProyecto.TodasLasComplejidades]: 'Todas las complejidades',
};

// Etiquetas del Anexo B.1 (RQ-T-02); ver descripción de FuenteFinanciamiento en el OpenAPI
// generado para las siglas SIIP (S/F, FGEN, RPRO, P.Ext, P.Int, Don., Otros).
const FUENTE_FINANCIAMIENTO_LABELS: Record<string, string> = {
  [FuenteFinanciamiento.SinFinanciamiento]: 'Sin financiamiento',
  [FuenteFinanciamiento.FondoGeneral]: 'Fondo General',
  [FuenteFinanciamiento.RecursosPropios]: 'Recursos Propios',
  [FuenteFinanciamiento.PrestamosExternos]: 'Préstamos Externos',
  [FuenteFinanciamiento.PrestamosInternos]: 'Préstamos Internos',
  [FuenteFinanciamiento.Donaciones]: 'Donaciones',
  [FuenteFinanciamiento.Otros]: 'Otros',
};

export function formatNombreEtapa(nombre: string): string {
  return NOMBRE_ETAPA_LABELS[nombre] ?? nombre;
}

export function formatTipoCapital(tipo: string): string {
  return TIPO_CAPITAL_LABELS[tipo] ?? tipo;
}

export function formatTamanioProyecto(tamanio: string): string {
  return TAMANIO_PROYECTO_LABELS[tamanio] ?? tamanio;
}

export function formatComplejidad(complejidad: string): string {
  return COMPLEJIDAD_LABELS[complejidad] ?? complejidad;
}

export function formatFuenteFinanciamiento(fuente: string): string {
  return FUENTE_FINANCIAMIENTO_LABELS[fuente] ?? fuente;
}

// Orden fijo del Anexo B.2/NombreEtapa — se reutiliza para pintar filas/checkboxes en ese orden.
export const NOMBRE_ETAPA_OPCIONES = [
  NombreEtapa.Perfil,
  NombreEtapa.Prefactibilidad,
  NombreEtapa.Factibilidad,
  NombreEtapa.Diseno,
  NombreEtapa.Ejecucion,
] as const;

export const TIPO_CAPITAL_OPCIONES = [
  TipoCapital.CapitalFisico,
  TipoCapital.CapitalHumano,
  TipoCapital.CapitalInstitucional,
  TipoCapital.OtrosCapitales,
] as const;

export const TAMANIO_PROYECTO_OPCIONES = [TamanioProyecto.Pequenio, TamanioProyecto.Mediano, TamanioProyecto.Grande] as const;

export const COMPLEJIDAD_OPCIONES = [
  ComplejidadProyecto.Baja,
  ComplejidadProyecto.Media,
  ComplejidadProyecto.Alta,
  ComplejidadProyecto.TodasLasComplejidades,
] as const;

export const FUENTE_FINANCIAMIENTO_OPCIONES = [
  FuenteFinanciamiento.SinFinanciamiento,
  FuenteFinanciamiento.FondoGeneral,
  FuenteFinanciamiento.RecursosPropios,
  FuenteFinanciamiento.PrestamosExternos,
  FuenteFinanciamiento.PrestamosInternos,
  FuenteFinanciamiento.Donaciones,
  FuenteFinanciamiento.Otros,
] as const;

// Estados de CU-PRE-01 desde los cuales el CUP ya fue asignado y, por lo tanto, el proyecto
// puede continuar hacia CU-PRE-3.5 (Selección y Registro de Etapas). Todo lo anterior
// (EN_REGISTRO, ENVIADO_DGICP_REGISTRO, OBSERVADO_DGICP_REGISTRO) todavía no tiene CUP.
export const ESTADOS_SIN_CUP = ['EN_REGISTRO', 'ENVIADO_DGICP_REGISTRO', 'OBSERVADO_DGICP_REGISTRO'];
