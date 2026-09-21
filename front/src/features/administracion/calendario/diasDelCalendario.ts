import type { CalendarItem } from '../../../api/administracionApi';

/**
 * Clasificación de los días de un calendario (CU-ADM-04, RN01/RN02 y sección 7.1).
 *
 * El contrato ofrece `consultarTipoDia`, pero pintar un mes entero son 30 ó 31
 * llamadas, y un año son 365: la rejilla se colorea aquí, con la misma
 * definición que ya viene en `recuperarDefinicionCalendario`. Las consultas del
 * servidor siguen siendo la fuente de verdad —la pantalla las ofrece aparte—;
 * esto es la vista.
 *
 * Las fechas se tratan como cadenas `YYYY-MM-DD` y se comparan como tales: son
 * ordenables lexicográficamente y no arrastran huso horario, que es justo lo
 * que rompe un `new Date('2026-08-01')` en El Salvador (UTC-6).
 */
export type TipoDeDia = 'LABORAL' | 'NO_LABORAL' | undefined;

const DIAS_SEMANA = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'] as const;
const MESES = [
  'JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE',
  'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER',
] as const;

/** `YYYY-MM-DD` → Date local (sin desplazamiento de huso). */
export function aFecha(iso: string): Date {
  const [anio, mes, dia] = iso.split('-').map(Number);
  return new Date(anio, mes - 1, dia);
}

export function aIso(fecha: Date): string {
  const mes = String(fecha.getMonth() + 1).padStart(2, '0');
  const dia = String(fecha.getDate()).padStart(2, '0');
  return `${fecha.getFullYear()}-${mes}-${dia}`;
}

export const enRango = (fecha: string, desde: string, hasta: string) => fecha >= desde && fecha <= hasta;

/** Si el ítem cubre esa fecha, según su recurrencia (UNA_VEZ, SEMANAL o MENSUAL). */
export function cubre(item: CalendarItem, fecha: string): boolean {
  if (item.tipoItem === 'EXCEPCION') return item.fecha === fecha;
  const recurrencia = item.recurrencia;
  if (recurrencia.tipo === 'SEMANAL') {
    return (
      enRango(fecha, recurrencia.fechaInicio, recurrencia.fechaFin) &&
      recurrencia.diasSemana.includes(DIAS_SEMANA[aFecha(fecha).getDay()])
    );
  }
  // MENSUAL no lleva fechas propias: lo acota el rango del calendario.
  if (recurrencia.tipo === 'MENSUAL') {
    return (
      recurrencia.meses.includes(MESES[aFecha(fecha).getMonth()]) &&
      recurrencia.diasDelMes.includes(aFecha(fecha).getDate())
    );
  }
  return enRango(fecha, recurrencia.fechaInicio, recurrencia.fechaFin);
}

export interface DiaClasificado {
  readonly fecha: string;
  readonly tipo: TipoDeDia;
  /** Ítems que cubren la fecha, para explicar en pantalla por qué es de ese tipo. */
  readonly cubren: CalendarItem[];
}

/**
 * Tipo de un día. Manda la excepción (sección 7.1); si no la hay, un día
 * cubierto a la vez por un período LABORAL y uno NO_LABORAL es NO_LABORAL
 * (RN02); un día que no cae en ningún período se queda sin definir, que es el
 * caso que las consultas del servidor responden con error (RN01/RN16).
 */
export function clasificar(items: readonly CalendarItem[], fecha: string): DiaClasificado {
  const cubren = items.filter((item) => cubre(item, fecha));
  const excepcion = cubren.find((item) => item.tipoItem === 'EXCEPCION');
  if (excepcion && excepcion.tipoItem === 'EXCEPCION') {
    return { fecha, tipo: excepcion.tipo === 'DIA_NO_LABORAL' ? 'NO_LABORAL' : 'LABORAL', cubren };
  }
  if (cubren.some((item) => item.tipoItem === 'NO_LABORAL')) return { fecha, tipo: 'NO_LABORAL', cubren };
  if (cubren.some((item) => item.tipoItem === 'LABORAL')) return { fecha, tipo: 'LABORAL', cubren };
  return { fecha, tipo: undefined, cubren };
}

/**
 * Las seis semanas de la rejilla de un mes, de lunes a domingo. Se devuelven
 * las fechas completas, incluidas las de los meses vecinos que rellenan la
 * primera y la última semana: la pantalla las pinta apagadas.
 */
export function semanasDelMes(anio: number, mes: number): string[][] {
  const primero = new Date(anio, mes, 1);
  // getDay() es 0 para domingo; la rejilla empieza en lunes.
  const desplazamiento = (primero.getDay() + 6) % 7;
  const inicio = new Date(anio, mes, 1 - desplazamiento);
  const semanas: string[][] = [];
  for (let semana = 0; semana < 6; semana += 1) {
    const dias: string[] = [];
    for (let dia = 0; dia < 7; dia += 1) {
      dias.push(aIso(new Date(inicio.getFullYear(), inicio.getMonth(), inicio.getDate() + semana * 7 + dia)));
    }
    semanas.push(dias);
  }
  return semanas;
}

/** Meses que abarca el calendario, como pares [año, mes] listos para la rejilla. */
export function mesesDelCalendario(fechaInicio: string, fechaFin: string): { anio: number; mes: number }[] {
  const meses: { anio: number; mes: number }[] = [];
  const fin = aFecha(fechaFin);
  const cursor = aFecha(fechaInicio);
  cursor.setDate(1);
  while (cursor.getFullYear() < fin.getFullYear() || (cursor.getFullYear() === fin.getFullYear() && cursor.getMonth() <= fin.getMonth())) {
    meses.push({ anio: cursor.getFullYear(), mes: cursor.getMonth() });
    cursor.setMonth(cursor.getMonth() + 1);
  }
  return meses;
}
