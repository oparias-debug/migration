import { z } from 'zod';
import { conSeparadorDeMiles, sinSeparadorDeMiles } from '../proyectos/proyectoFormSchema';

// Ningún campo (de fila ni la Justificación) es obligatorio a nivel de servidor en el PUT de
// "Guardar" (CU-PRE-05.openapi.yaml, guardarAlternativasSolucion): RN2-3/RN2-4 solo se evalúan en
// el POST de "Siguiente" (avanzarAAnalisisInteresados), con el mensaje literal que entrega el
// servidor. Por eso este schema no impone ninguna regla de mínimos: RN3-2 confirma que "Guardar"
// nunca rechaza el formulario, aunque sombree en rojo (retroalimentación de cliente) los campos
// vacíos — ver AlternativasSolucionPage.
const filaAlternativaSchema = z.object({
  nombreAlternativa: z.string(),
  montoAlternativa: z.string(),
  descripcionAlternativa: z.string(),
  seleccionada: z.boolean(),
});

export type FilaAlternativaFormValues = z.infer<typeof filaAlternativaSchema>;

export const FILA_ALTERNATIVA_DEFAULT: FilaAlternativaFormValues = {
  nombreAlternativa: '',
  montoAlternativa: '',
  descripcionAlternativa: '',
  seleccionada: false,
};

export const registroAlternativasSchema = z.object({
  alternativas: z.array(filaAlternativaSchema),
  justificacion: z.string(),
});

export type RegistroAlternativasFormValues = z.infer<typeof registroAlternativasSchema>;

export const REGISTRO_ALTERNATIVAS_FORM_DEFAULTS: RegistroAlternativasFormValues = {
  alternativas: [],
  justificacion: '',
};

/** RegistroAlternativasRequest.justificacion: maxLength 1000 (CU-PRE-05.openapi.yaml). */
export const JUSTIFICACION_MAXLENGTH = 1000;

export { conSeparadorDeMiles, sinSeparadorDeMiles };
