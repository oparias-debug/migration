import { z } from 'zod';

/**
 * Reglas del "Detalle de Macroactividad" (Anexo A.2), que son las únicas que
 * el CU pide validar en el cliente:
 *
 *   "no permite guardar, ya que el campo es obligatorio"            → nombre
 *   "se requiere al menos un campo diligenciado por fila"           → costos
 *
 * Los totales (RN04, RN07) y los precios ajustados (F1 paso 1.2) los calcula el
 * servidor y llegan ya resueltos; aquí se muestran mientras se escribe sólo
 * como anticipo, sin sustituir al cálculo del servidor.
 */
export const NOMBRE_OBLIGATORIO = 'El nombre de la macroactividad es obligatorio';
export const AL_MENOS_UN_PERIODO = 'Registre al menos un período en esta fila';

const filaInsumoSchema = z.object({
  tipoInsumo: z.string(),
  nombre: z.string(),
  factorCorreccion: z.number(),
  /** Un texto por período; vacío = sin dato, que el contrato admite como null. */
  costos: z.array(z.string()),
});

export type FilaInsumoFormValues = z.infer<typeof filaInsumoSchema>;

export const macroactividadSchema = z
  .object({
    nombreMacroactividad: z.string().trim().min(1, NOMBRE_OBLIGATORIO),
    insumos: z.array(filaInsumoSchema),
  })
  .superRefine((valores, ctx) => {
    // El CU pide "al menos un campo de Período n" por fila, pero la tabla se
    // pinta con el catálogo de insumos completo (RN05): exigirlo fila a fila
    // obligaría a rellenar insumos que no aplican, y contradiría esa regla. Se
    // interpreta como lo que tiene sentido: al menos una fila con algún costo.
    const hayAlgunCosto = valores.insumos.some((fila) => fila.costos.some((c) => c.trim() !== ''));
    if (!hayAlgunCosto) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, path: ['insumos'], message: AL_MENOS_UN_PERIODO });
    }
  });

export type MacroactividadFormValues = z.infer<typeof macroactividadSchema>;

/** "$1,750,427.58" en la pantalla; number en el contrato. */
export const aNumero = (texto: string): number | null => {
  const limpio = texto.replace(/[^0-9.-]/g, '');
  if (limpio === '') return null;
  const n = Number(limpio);
  return Number.isFinite(n) ? n : null;
};

export const formatearMonto = (valor: number | null | undefined): string =>
  valor == null ? '—' : valor.toLocaleString('es-SV', { style: 'currency', currency: 'USD' });

/**
 * Anticipo del Total Período a precio de mercado (RN04): suma de los insumos de
 * ese período. El servidor recalcula y su resultado es el que manda.
 */
export const totalPeriodoMercado = (insumos: FilaInsumoFormValues[], periodo: number): number =>
  insumos.reduce((suma, fila) => suma + (aNumero(fila.costos[periodo] ?? '') ?? 0), 0);

/**
 * Anticipo del Total Período a precios ajustados (F1 paso 1.2):
 * (Insumo 1 × FC) + (Insumo 2 × FC) + … Cada insumo lleva su propio Factor.
 */
export const totalPeriodoAjustado = (insumos: FilaInsumoFormValues[], periodo: number): number =>
  insumos.reduce(
    (suma, fila) => suma + (aNumero(fila.costos[periodo] ?? '') ?? 0) * (fila.factorCorreccion ?? 1),
    0,
  );
