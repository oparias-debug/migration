import { aNumero } from '../analisis/analisisComun';
import type { NombreEtapa } from '../../../api/preinversionApi';

/** Rol que registra el PAP institucional (x-roles de CU-PRE-30 a CU-PRE-33). */
export const ROL_PAP = 'TECNICO_URP';

/** Las cinco etapas de la Ruta de Preinversión, para el desplegable de una fila nueva. */
export const ETAPAS: readonly string[] = ['PERFIL', 'PREFACTIBILIDAD', 'FACTIBILIDAD', 'DISENO', 'EJECUCION'];

/** Número con el que el contrato trabaja; vacío se manda como cero, no como nulo. */
export const aMonto = (texto: unknown): number => aNumero(texto) ?? 0;

/**
 * Las fichas financieras (CU-PRE-30 y CU-PRE-32) se editan como una sola tabla
 * de fuentes y se guardan agrupadas por etapa, que es como las pide el
 * contrato.
 */
export function agruparPorEtapa<F extends { etapa: string }, R>(
  filas: readonly F[],
  fuente: (fila: F) => R,
): { etapa: NombreEtapa; fuentes: R[] }[] {
  const etapas: { etapa: NombreEtapa; fuentes: R[] }[] = [];
  for (const fila of filas) {
    if (!fila.etapa) continue;
    let grupo = etapas.find((e) => e.etapa === fila.etapa);
    if (!grupo) {
      grupo = { etapa: fila.etapa as NombreEtapa, fuentes: [] };
      etapas.push(grupo);
    }
    grupo.fuentes.push(fuente(fila));
  }
  return etapas;
}
