import { z } from 'zod';
import { ComplejidadProyecto, NombreEtapa, TamanioProyecto, TipoCapital } from '../../../api/preinversionApi';
import { CAMPO_OBLIGATORIO, conSeparadorDeMiles, sinSeparadorDeMiles } from '../proyectos/proyectoFormSchema';

const TIPOS_CAPITAL = [
  TipoCapital.CapitalFisico,
  TipoCapital.CapitalHumano,
  TipoCapital.CapitalInstitucional,
  TipoCapital.OtrosCapitales,
] as const;
const TAMANIOS_PROYECTO = [TamanioProyecto.Pequenio, TamanioProyecto.Mediano, TamanioProyecto.Grande] as const;
const COMPLEJIDADES = [
  ComplejidadProyecto.Baja,
  ComplejidadProyecto.Media,
  ComplejidadProyecto.Alta,
  ComplejidadProyecto.TodasLasComplejidades,
] as const;
const NOMBRES_ETAPA = [
  NombreEtapa.Perfil,
  NombreEtapa.Prefactibilidad,
  NombreEtapa.Factibilidad,
  NombreEtapa.Diseno,
  NombreEtapa.Ejecucion,
] as const;

// RN01: los 3 criterios son obligatorios para "Generar Ruta de Preinversión"/"Aceptar".
export const criteriosCalificacionSchema = z.object({
  tipoCapital: z.enum(TIPOS_CAPITAL, { required_error: CAMPO_OBLIGATORIO, invalid_type_error: CAMPO_OBLIGATORIO }),
  tamanioProyecto: z.enum(TAMANIOS_PROYECTO, { required_error: CAMPO_OBLIGATORIO, invalid_type_error: CAMPO_OBLIGATORIO }),
  complejidad: z.enum(COMPLEJIDADES, { required_error: CAMPO_OBLIGATORIO, invalid_type_error: CAMPO_OBLIGATORIO }),
});

export type CriteriosCalificacionFormValues = z.infer<typeof criteriosCalificacionSchema>;

export const CRITERIOS_FORM_DEFAULTS: Partial<CriteriosCalificacionFormValues> = {
  tipoCapital: undefined,
  tamanioProyecto: undefined,
  complejidad: undefined,
};

// RN03: "Justifique Modificación" es obligatorio; se exige al menos una etapa seleccionada.
export const modificarRutaSchema = z.object({
  justificacion: z.string().trim().min(1, CAMPO_OBLIGATORIO),
  etapas: z.array(z.enum(NOMBRES_ETAPA)).min(1, CAMPO_OBLIGATORIO),
});

export type ModificarRutaFormValues = z.infer<typeof modificarRutaSchema>;

// RN19 (confirmado v1.3): costo/fechaInicio/fechaFin ya NO son obligatorios a nivel de guardado
// — es puramente visual (borde rojo), el servidor no rechaza el PUT por campos faltantes. RN04
// (confirmado v1.3): si se completa una fecha, sí debe cumplir el formato dd/mm/aaaa; a diferencia
// de RN19, este formato sí bloquea el guardado (ver CU-PRE-3.5.openapi.yaml).
export const FORMATO_FECHA_INVALIDO = 'Formato inválido, use dd/mm/aaaa';
const FECHA_DDMMAAAA = /^\d{2}\/\d{2}\/\d{4}$/;

const filaEtapaSchema = z.object({
  nombreEtapa: z.enum(NOMBRES_ETAPA),
  costo: z.string().trim(),
  fechaInicio: z.string().trim().refine((valor) => valor === '' || FECHA_DDMMAAAA.test(valor), FORMATO_FECHA_INVALIDO),
  fechaFin: z.string().trim().refine((valor) => valor === '' || FECHA_DDMMAAAA.test(valor), FORMATO_FECHA_INVALIDO),
});

export const actualizarEtapasSchema = z.object({
  etapas: z.array(filaEtapaSchema),
});

export type ActualizarEtapasFormValues = z.infer<typeof actualizarEtapasSchema>;

export { conSeparadorDeMiles, sinSeparadorDeMiles };

// FA-05: planteamientoProblema, productos, distrito y poblacionObjetivo son obligatorios.
// "Departamento" no es un campo propio del request: se valida de forma transitiva a través de
// distrito (ver CU-PRE-3.5.openapi.yaml, FichaEmergenciaRequest.distrito).
export const fichaEmergenciaSchema = z.object({
  planteamientoProblema: z.string().trim().min(1, CAMPO_OBLIGATORIO),
  objetivoGeneral: z.string().trim().default(''),
  descripcionProyecto: z.string().trim().default(''),
  productos: z.array(z.object({ codigoProducto: z.string() })).min(1, CAMPO_OBLIGATORIO),
  distrito: z.string().trim().min(1, CAMPO_OBLIGATORIO),
  latitud: z.string().trim().default(''),
  longitud: z.string().trim().default(''),
  direccionEspecifica: z.string().trim().default(''),
  poblacionObjetivo: z.string().trim().min(1, CAMPO_OBLIGATORIO),
  inversionEstimada: z.string().trim().default(''),
  componentesCosto: z.array(z.object({ tipoCosto: z.string(), costo: z.string() })).default([]),
  costosOperacion: z.string().trim().default(''),
  costosMantenimiento: z.string().trim().default(''),
  fuentesFinanciamiento: z.array(z.string()).default([]),
  fuenteRecursos: z.string().trim().default(''),
});

export type FichaEmergenciaFormValues = z.infer<typeof fichaEmergenciaSchema>;

export const FICHA_EMERGENCIA_FORM_DEFAULTS: FichaEmergenciaFormValues = {
  planteamientoProblema: '',
  objetivoGeneral: '',
  descripcionProyecto: '',
  productos: [],
  distrito: '',
  latitud: '',
  longitud: '',
  direccionEspecifica: '',
  poblacionObjetivo: '',
  inversionEstimada: '',
  componentesCosto: [],
  costosOperacion: '',
  costosMantenimiento: '',
  fuentesFinanciamiento: [],
  fuenteRecursos: '',
};
