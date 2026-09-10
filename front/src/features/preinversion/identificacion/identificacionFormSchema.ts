import { z } from 'zod';

/**
 * Ningún campo es obligatorio a nivel de servidor: `IdentificacionRequest` no
 * declara `required` y el PUT acepta el formulario a medias, de modo que se
 * pueda guardar el avance e ir completando en varias sesiones.
 *
 * Lo que sí pide el CU es avisar de lo que falta: "el sistema sombrea en color
 * rojo los bordes de los campos pendientes de completar" (RNC-2). Eso es
 * retroalimentación de cliente y se resuelve en la pantalla, no rechazando el
 * guardado — mismo criterio que en Alternativas de Solución.
 *
 * Los `maxLength` sí vienen del contrato y se replican aquí para que el aviso
 * salga mientras se escribe y no al recibir un 400.
 */
export const ANTECEDENTES_MAXLENGTH = 3000;
export const PROBLEMA_CENTRAL_MAXLENGTH = 500;
export const OBJETIVO_GENERAL_MAXLENGTH = 500;
export const OBJETIVO_ESPECIFICO_MAXLENGTH = 500;

const objetivoEspecificoSchema = z.object({
  // El array del contrato es de strings; react-hook-form necesita objetos para
  // que useFieldArray pueda darle una `key` estable a cada fila.
  texto: z.string().max(OBJETIVO_ESPECIFICO_MAXLENGTH),
});

export type ObjetivoEspecificoFormValues = z.infer<typeof objetivoEspecificoSchema>;

export const OBJETIVO_ESPECIFICO_DEFAULT: ObjetivoEspecificoFormValues = { texto: '' };

export const identificacionSchema = z.object({
  antecedentes: z.string().max(ANTECEDENTES_MAXLENGTH),
  problemaCentral: z.string().max(PROBLEMA_CENTRAL_MAXLENGTH),
  objetivoGeneral: z.string().max(OBJETIVO_GENERAL_MAXLENGTH),
  objetivosEspecificos: z.array(objetivoEspecificoSchema),
});

export type IdentificacionFormValues = z.infer<typeof identificacionSchema>;

export const IDENTIFICACION_FORM_DEFAULTS: IdentificacionFormValues = {
  antecedentes: '',
  problemaCentral: '',
  objetivoGeneral: '',
  objetivosEspecificos: [],
};

/** Un campo "pendiente de completar" es el que quedó vacío (RNC-2). */
export const estaPendiente = (valor: string): boolean => valor.trim().length === 0;
