import { z } from 'zod';
import { TipoCampo } from '../../../api/administracionApi';

/**
 * Reglas del Flujo Principal del CU-ADM-01 (§6.4). Se validan aquí además de en
 * el servidor porque son de estructura: el usuario está construyendo la lista de
 * campos y necesita saber qué le falta mientras la arma, no al enviarla.
 *
 *   regla 17 · al menos un campo definido
 *   regla 2  · al menos un campo marcado como KEY
 *   regla 3  · nombres de campo sin repetir
 *
 * Las reglas 12 y 13 (vigencia → estado) NO se replican: las resuelve el
 * servidor, que es quien conoce la fecha actual, y devuelve el `estado` ya
 * calculado. Duplicarlas aquí sería adivinar.
 */
export const CAMPO_OBLIGATORIO = 'Campo obligatorio';

const campoSchema = z.object({
  nombre: z.string().trim().min(1, CAMPO_OBLIGATORIO),
  tipo: z.nativeEnum(TipoCampo),
  esKey: z.boolean(),
  /** Sólo para tipo ENUM: un valor por línea en la interfaz. */
  valoresEnum: z.string(),
});

export type CampoFormValues = z.infer<typeof campoSchema>;

export const CAMPO_DEFAULT: CampoFormValues = {
  nombre: '',
  tipo: TipoCampo.String,
  esKey: false,
  valoresEnum: '',
};

export const catalogoSchema = z
  .object({
    codigo: z.string().trim().min(1, CAMPO_OBLIGATORIO),
    nombre: z.string().trim().min(1, CAMPO_OBLIGATORIO),
    catalogoPadreCodigo: z.string(),
    fechaDesde: z.string(),
    fechaHasta: z.string(),
    campos: z.array(campoSchema).min(1, 'Defina al menos un campo'),
  })
  .superRefine((valores, ctx) => {
    // regla 2
    if (!valores.campos.some((c) => c.esKey)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ['campos'],
        message: 'Marque al menos un campo como clave (KEY)',
      });
    }
    // regla 3 — se señala la fila repetida, no el conjunto
    const vistos = new Map<string, number>();
    valores.campos.forEach((campo, indice) => {
      const nombre = campo.nombre.trim().toLowerCase();
      if (!nombre) return;
      if (vistos.has(nombre)) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ['campos', indice, 'nombre'],
          message: 'Ya hay un campo con ese nombre',
        });
      } else {
        vistos.set(nombre, indice);
      }
    });
    // Un ENUM sin valores no restringe nada.
    valores.campos.forEach((campo, indice) => {
      if (campo.tipo === TipoCampo.Enum && campo.valoresEnum.trim() === '') {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ['campos', indice, 'valoresEnum'],
          message: 'Indique los valores permitidos',
        });
      }
    });
  });

export type CatalogoFormValues = z.infer<typeof catalogoSchema>;

export const CATALOGO_FORM_DEFAULTS: CatalogoFormValues = {
  codigo: '',
  nombre: '',
  catalogoPadreCodigo: '',
  fechaDesde: '',
  fechaHasta: '',
  campos: [{ ...CAMPO_DEFAULT, esKey: true }],
};

/** Un valor por línea en el textarea → el array del contrato. */
export const lineasAValores = (texto: string): string[] =>
  texto
    .split('\n')
    .map((linea) => linea.trim())
    .filter((linea) => linea !== '');
