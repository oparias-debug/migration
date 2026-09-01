import { z } from 'zod';
import { IniciativaInversion } from '../../../api/preinversionApi';

const INICIATIVAS = [IniciativaInversion.Programa, IniciativaInversion.Proyecto, IniciativaInversion.EstudioGeneral] as const;

// Mensaje exacto del Esquema del escenario "Intentar guardar con un campo
// obligatorio incompleto" (CU-PRE-01-registrar-nuevo-proyecto.feature).
export const CAMPO_OBLIGATORIO = '*Campo obligatorio';

// Los 6 campos requeridos por ProyectoRequest en el OpenAPI (iniciativaInversion,
// nombre, montoEstimadoInversion, idSector, idEjeTematico, descripcionProyecto) son
// exactamente los 6 de la tabla de Ejemplos del escenario. El resto de campos son
// condicionales (medidas GRD/GRC/ACC, emergencia, planes) según ProyectoRequest.
export const proyectoFormSchema = z
  .object({
    iniciativaInversion: z.enum(INICIATIVAS, { required_error: CAMPO_OBLIGATORIO, invalid_type_error: CAMPO_OBLIGATORIO }),
    nombre: z.string().trim().min(1, CAMPO_OBLIGATORIO).max(250),
    // Se valida como string (no z.coerce.number) porque un campo vacío coercería a 0,
    // que es un monto "válido" y ocultaría el error de campo obligatorio.
    montoEstimadoInversion: z
      .string()
      .trim()
      .min(1, CAMPO_OBLIGATORIO)
      .refine((valor) => !Number.isNaN(Number(valor)) && Number(valor) >= 0, { message: CAMPO_OBLIGATORIO }),
    // idSector/idEjeTematico/idEjePlanGobierno/idPlanSectorialRegional son el value de un
    // <select> (siempre string en HTML, o '' si no hay opción elegida), convertidos a number
    // en formValuesToRequest.
    idSector: z.string().trim().min(1, CAMPO_OBLIGATORIO),
    idEjeTematico: z.string().trim().min(1, CAMPO_OBLIGATORIO),
    medidasGrd: z.array(z.string()).default([]),
    medidasGrc: z.array(z.string()).default([]),
    medidasAcc: z.array(z.string()).default([]),
    esProyectoEmergencia: z.boolean().default(false),
    tipoEvento: z.string().trim().default(''),
    numeroDecretoLegislativo: z.string().trim().default(''),
    idEjePlanGobierno: z.string().trim().default(''),
    idPlanSectorialRegional: z.string().trim().default(''),
    descripcionProyecto: z.string().trim().min(1, CAMPO_OBLIGATORIO).max(1000),
  })
  .superRefine((datos, ctx) => {
    // Regla condicional documentada en ProyectoRequest del OpenAPI: tipoEvento y
    // numeroDecretoLegislativo son obligatorios solo si esProyectoEmergencia = true.
    if (datos.esProyectoEmergencia) {
      if (!datos.tipoEvento) {
        ctx.addIssue({ code: z.ZodIssueCode.custom, message: CAMPO_OBLIGATORIO, path: ['tipoEvento'] });
      }
      if (!datos.numeroDecretoLegislativo) {
        ctx.addIssue({ code: z.ZodIssueCode.custom, message: CAMPO_OBLIGATORIO, path: ['numeroDecretoLegislativo'] });
      }
    }
  });

export type ProyectoFormValues = z.infer<typeof proyectoFormSchema>;

// iniciativaInversion arranca sin selección (a diferencia del resto de campos, que
// arrancan en '') para que el escenario "campo obligatorio incompleto" pueda
// ejercitar el radio-group sin selección previa.
type ProyectoFormDefaults = Omit<ProyectoFormValues, 'iniciativaInversion'> & {
  iniciativaInversion?: ProyectoFormValues['iniciativaInversion'];
};

export const PROYECTO_FORM_DEFAULTS: ProyectoFormDefaults = {
  iniciativaInversion: undefined,
  nombre: '',
  montoEstimadoInversion: '',
  idSector: '',
  idEjeTematico: '',
  medidasGrd: [],
  medidasGrc: [],
  medidasAcc: [],
  esProyectoEmergencia: false,
  tipoEvento: '',
  numeroDecretoLegislativo: '',
  idEjePlanGobierno: '',
  idPlanSectorialRegional: '',
  descripcionProyecto: '',
};
