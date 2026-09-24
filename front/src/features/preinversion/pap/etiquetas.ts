import type { TFunction } from 'i18next';
import { formatNombreEtapa } from '../etapas/etapasLabels';

/**
 * El PAP devuelve los catálogos como códigos (`PRESTAMOS_EXTERNOS`,
 * `ESTUDIO_DE_PERFIL`), no como etiquetas. En pantalla se muestran con el
 * mismo texto que ya usan las demás pantallas: las etapas con el formateador
 * de la Ruta de Preinversión y las fuentes con los rótulos del presupuesto
 * (CU-PRE-17), para que el mismo código no se lea distinto según la pantalla.
 */
export const etiquetaEtapa = (valor: string | null | undefined): string => (valor ? formatNombreEtapa(valor) : '—');

export const etiquetaFuente = (t: TFunction, valor: string | null | undefined): string =>
  valor ? t(`preinversion.presupuesto.fuente.${valor}`, { defaultValue: valor }) : '—';

export const etiquetaEntregable = (t: TFunction, valor: string | null | undefined): string =>
  valor ? t(`preinversion.pap.entregables.${valor}`, { defaultValue: valor }) : '—';
