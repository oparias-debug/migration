import { describe, expect, it } from 'vitest';
import type { CalendarItem } from '../../../api/administracionApi';
import { clasificar, cubre, mesesDelCalendario, semanasDelMes } from './diasDelCalendario';

const laboralDeMarzo: CalendarItem = {
  id: 1, tipoItem: 'LABORAL', codigo: 'HABILES', nombre: 'Días hábiles', estado: 'ACTIVO',
  recurrencia: { tipo: 'UNA_VEZ', fechaInicio: '2027-03-01', fechaFin: '2027-03-31' },
} as CalendarItem;

const finesDeSemana: CalendarItem = {
  id: 2, tipoItem: 'NO_LABORAL', codigo: 'FINDE', nombre: 'Fines de semana', estado: 'ACTIVO',
  recurrencia: {
    tipo: 'SEMANAL', fechaInicio: '2027-03-01', fechaFin: '2027-03-31',
    diasSemana: ['SATURDAY', 'SUNDAY'],
  },
} as CalendarItem;

const quincenas: CalendarItem = {
  id: 3, tipoItem: 'NO_LABORAL', codigo: 'QUINCENA', nombre: 'Quincenas', estado: 'ACTIVO',
  recurrencia: { tipo: 'MENSUAL', diasDelMes: [15, 30], meses: ['MARCH'] },
} as CalendarItem;

const excepcionLaboral: CalendarItem = {
  id: 4, tipoItem: 'EXCEPCION', fecha: '2027-03-06', tipo: 'DIA_LABORAL',
  descripcion: 'Jornada extraordinaria', estado: 'ACTIVO',
} as CalendarItem;

describe('cubre', () => {
  it('UNA_VEZ cubre el rango completo y nada fuera de él', () => {
    expect(cubre(laboralDeMarzo, '2027-03-01')).toBe(true);
    expect(cubre(laboralDeMarzo, '2027-03-31')).toBe(true);
    expect(cubre(laboralDeMarzo, '2027-04-01')).toBe(false);
  });

  it('SEMANAL sólo cubre los días de la semana indicados', () => {
    // 2027-03-06 es sábado y 2027-03-08 lunes.
    expect(cubre(finesDeSemana, '2027-03-06')).toBe(true);
    expect(cubre(finesDeSemana, '2027-03-08')).toBe(false);
  });

  it('MENSUAL cubre los días del mes indicados en los meses indicados', () => {
    expect(cubre(quincenas, '2027-03-15')).toBe(true);
    expect(cubre(quincenas, '2027-03-16')).toBe(false);
    expect(cubre(quincenas, '2027-04-15')).toBe(false);
  });
});

describe('clasificar', () => {
  it('un día sólo dentro de un período LABORAL es laboral', () => {
    expect(clasificar([laboralDeMarzo, finesDeSemana], '2027-03-08').tipo).toBe('LABORAL');
  });

  // RN02: en la intersección manda NO_LABORAL.
  it('un día cubierto por LABORAL y NO_LABORAL a la vez es no laboral', () => {
    expect(clasificar([laboralDeMarzo, finesDeSemana], '2027-03-06').tipo).toBe('NO_LABORAL');
  });

  // Sección 7.1: la excepción manda sobre los períodos.
  it('la excepción prevalece sobre los períodos', () => {
    const dia = clasificar([laboralDeMarzo, finesDeSemana, excepcionLaboral], '2027-03-06');
    expect(dia.tipo).toBe('LABORAL');
    expect(dia.cubren).toHaveLength(3);
  });

  // RN01/RN16: fuera de todo período no hay tipo; el servidor responde error.
  it('un día que no cae en ningún período se queda sin definir', () => {
    const dia = clasificar([laboralDeMarzo, finesDeSemana], '2027-04-05');
    expect(dia.tipo).toBeUndefined();
    expect(dia.cubren).toHaveLength(0);
  });
});

describe('rejilla', () => {
  it('el mes se dibuja en seis semanas de lunes a domingo', () => {
    const semanas = semanasDelMes(2027, 2); // marzo de 2027
    expect(semanas).toHaveLength(6);
    expect(semanas.every((s) => s.length === 7)).toBe(true);
    // 1 de marzo de 2027 es lunes: abre la primera semana.
    expect(semanas[0][0]).toBe('2027-03-01');
    expect(semanas[0][6]).toBe('2027-03-07');
  });

  it('los meses del calendario van del primero al último, ambos incluidos', () => {
    const meses = mesesDelCalendario('2027-03-15', '2027-10-31');
    expect(meses).toHaveLength(8);
    expect(meses[0]).toEqual({ anio: 2027, mes: 2 });
    expect(meses[7]).toEqual({ anio: 2027, mes: 9 });
  });

  it('un calendario a caballo entre dos años recorre los dos', () => {
    const meses = mesesDelCalendario('2027-11-01', '2028-01-31');
    expect(meses.map((m) => `${m.anio}-${m.mes}`)).toEqual(['2027-10', '2027-11', '2028-0']);
  });
});
