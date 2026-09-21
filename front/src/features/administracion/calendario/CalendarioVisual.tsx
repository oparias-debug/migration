import { useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import type { Calendario } from '../../../api/administracionApi';
import { CLAVE_CALENDARIO as CLAVE } from './CalendarioPage';
import { clasificar, enRango, mesesDelCalendario, semanasDelMes, type TipoDeDia } from './diasDelCalendario';

const DIAS_CABECERA = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'] as const;
const MESES = [
  'JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE',
  'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER',
] as const;

/** Clase de color de cada día. La letra la pone el propio botón: el color solo no basta. */
const claseDia = (tipo: TipoDeDia) => {
  if (tipo === 'LABORAL') return 'es-laboral';
  if (tipo === 'NO_LABORAL') return 'es-no-laboral';
  return 'sin-definir';
};

interface CalendarioVisualProps {
  readonly calendario: Calendario;
  /** Mes mostrado, como índice dentro de los meses que abarca el calendario. */
  readonly indiceMes: number;
  readonly alCambiarMes: (indice: number) => void;
  readonly diaElegido: string | null;
  readonly alElegirDia: (fecha: string) => void;
}

/**
 * El calendario en forma de calendario: la rejilla del mes con cada día en
 * verde (laboral), rojo (no laboral) o en blanco si todavía no está definido,
 * que es como lo planteó el cliente para CU-ADM-04.
 *
 * Los días fuera del rango del calendario se pintan apagados y no se pueden
 * elegir: no pertenecen a esta definición.
 */
export function CalendarioVisual({ calendario, indiceMes, alCambiarMes, diaElegido, alElegirDia }: CalendarioVisualProps) {
  const { t } = useTranslation();
  const meses = useMemo(
    () => mesesDelCalendario(calendario.fechaInicio, calendario.fechaFin),
    [calendario.fechaInicio, calendario.fechaFin],
  );
  const mesActual = meses[Math.min(indiceMes, meses.length - 1)] ?? { anio: 0, mes: 0 };
  const semanas = useMemo(() => semanasDelMes(mesActual.anio, mesActual.mes), [mesActual.anio, mesActual.mes]);
  const items = calendario.items ?? [];

  return (
    <div className="cal-visual">
      <div className="cal-barra">
        <button
          type="button"
          className="btn neutro"
          disabled={indiceMes <= 0}
          onClick={() => alCambiarMes(indiceMes - 1)}
          aria-label={t(`${CLAVE}.mesAnterior`)}
        >
          ‹
        </button>
        <output className="cal-mes">
          {t(`${CLAVE}.mesesNombre.${MESES[mesActual.mes]}`)} {mesActual.anio}
        </output>
        <button
          type="button"
          className="btn neutro"
          disabled={indiceMes >= meses.length - 1}
          onClick={() => alCambiarMes(indiceMes + 1)}
          aria-label={t(`${CLAVE}.mesSiguiente`)}
        >
          ›
        </button>
      </div>

      <table className="cal-rejilla">
        <caption className="visualmente-oculto">
          {t(`${CLAVE}.tituloRejilla`, { calendario: calendario.nombre })}
        </caption>
        <thead>
          <tr>
            {DIAS_CABECERA.map((dia) => (
              <th key={dia} scope="col" abbr={t(`${CLAVE}.dias.${dia}`)}>
                {t(`${CLAVE}.dias.${dia}`).slice(0, 3)}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {semanas.map((semana) => (
            // Una semana que no toca el mes (la sexta, casi siempre) no se pinta.
            <tr
              key={semana[0]}
              className={semana.some((f) => Number(f.slice(5, 7)) - 1 === mesActual.mes) ? undefined : 'fuera-del-mes'}
            >
              {semana.map((fecha) => {
                const delMes = Number(fecha.slice(5, 7)) - 1 === mesActual.mes;
                const dentro = enRango(fecha, calendario.fechaInicio, calendario.fechaFin);
                const { tipo, cubren } = clasificar(items, fecha);
                const numero = Number(fecha.slice(8, 10));
                if (!delMes || !dentro) {
                  return (
                    <td key={fecha} className="fuera">
                      <span aria-hidden="true">{numero}</span>
                    </td>
                  );
                }
                const conExcepcion = cubren.some((i) => i.tipoItem === 'EXCEPCION');
                return (
                  <td key={fecha} className={claseDia(tipo)}>
                    <button
                      type="button"
                      className={fecha === diaElegido ? 'dia elegido' : 'dia'}
                      aria-pressed={fecha === diaElegido}
                      onClick={() => alElegirDia(fecha)}
                      aria-label={t(`${CLAVE}.diaEtiqueta`, {
                        fecha,
                        tipo: t(`${CLAVE}.tiposDia.${tipo ?? 'SIN_DEFINIR'}`),
                      })}
                    >
                      <span className="num">{numero}</span>
                      {/* La inicial acompaña al color: en una impresión en blanco y
                          negro, o para quien no distingue verde de rojo, el color solo
                          no dice nada. */}
                      <span className="marca" aria-hidden="true">
                        {tipo === 'LABORAL' ? 'L' : ''}
                        {tipo === 'NO_LABORAL' ? 'N' : ''}
                        {conExcepcion ? '*' : ''}
                      </span>
                    </button>
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>

      <ul className="cal-leyenda">
        <li><span className="muestra es-laboral" aria-hidden="true" />{t(`${CLAVE}.tiposDiaTitulo.LABORAL`)}</li>
        <li><span className="muestra es-no-laboral" aria-hidden="true" />{t(`${CLAVE}.tiposDiaTitulo.NO_LABORAL`)}</li>
        <li><span className="muestra sin-definir" aria-hidden="true" />{t(`${CLAVE}.tiposDiaTitulo.SIN_DEFINIR`)}</li>
        <li><span className="muestra" aria-hidden="true">*</span>{t(`${CLAVE}.conExcepcion`)}</li>
      </ul>
    </div>
  );
}
