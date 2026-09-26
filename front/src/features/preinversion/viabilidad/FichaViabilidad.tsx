import { useTranslation } from 'react-i18next';
import { CampoFichaViabilidad, type FichaViabilidadResponse } from '../../../api/preinversionApi';
import { formatearMonto } from '../presupuesto/presupuestoFormSchema';

const CLAVE = 'preinversion.viabilidad';

interface Props {
  readonly ficha: FichaViabilidadResponse;
  /** Comentario por campo en edición; sólo lo escribe quien revisa. */
  readonly comentarios: Record<string, string>;
  readonly puedeComentar: boolean;
  readonly onComentar: (campo: string, texto: string) => void;
}

/** Las diez filas del Anexo A.1, en el orden del documento. */
const FILAS: { campo: string; valor: (f: FichaViabilidadResponse) => string }[] = [
  { campo: CampoFichaViabilidad.ObjetivoGeneral, valor: (f) => f.objetivoGeneral ?? '—' },
  { campo: CampoFichaViabilidad.Descripcion, valor: (f) => f.descripcion ?? '—' },
  { campo: CampoFichaViabilidad.Productos, valor: (f) => (f.productos ?? []).join(', ') || '—' },
  {
    campo: CampoFichaViabilidad.PoblacionObjetivo,
    valor: (f) => (f.poblacionObjetivo == null ? '—' : f.poblacionObjetivo.toLocaleString('es-SV')),
  },
  { campo: CampoFichaViabilidad.InversionEstimada, valor: (f) => formatearMonto(f.inversionEstimada ?? null) },
  { campo: CampoFichaViabilidad.CostoOperacion, valor: (f) => formatearMonto(f.costoOperacion ?? null) },
  { campo: CampoFichaViabilidad.CostoMantenimiento, valor: (f) => formatearMonto(f.costoMantenimiento ?? null) },
];

/**
 * Ficha del proyecto que se somete a viabilidad (Anexo A.1).
 *
 * Todo lo que muestra viene de los capítulos ya registrados —identificación,
 * descripción técnica, población, presupuesto, indicadores—, así que aquí es
 * sólo lectura. Lo único que se escribe es el comentario de cada fila, y sólo
 * cuando quien entra está revisando.
 */
export function FichaViabilidad({ ficha, comentarios, puedeComentar, onComentar }: Props) {
  const { t } = useTranslation();
  const indicadores = ficha.indicadoresEvaluacion ?? [];

  return (
    <div className="tabla-cont">
      <table className="tabla-datos">
        <thead>
          <tr>
            <th style={{ width: '22%' }}>{t(`${CLAVE}.columnaCampo`)}</th>
            <th>{t(`${CLAVE}.columnaValor`)}</th>
            <th style={{ width: '28%' }}>{t(`${CLAVE}.columnaComentario`)}</th>
          </tr>
        </thead>
        <tbody>
          {FILAS.map(({ campo, valor }) => (
            <tr key={campo}>
              <th scope="row">{t(`${CLAVE}.campos.${campo}`)}</th>
              <td>{valor(ficha)}</td>
              <td>
                {puedeComentar ? (
                  <input
                    type="text"
                    aria-label={t(`${CLAVE}.comentarioDe`, { campo: t(`${CLAVE}.campos.${campo}`) })}
                    value={comentarios[campo] ?? ''}
                    onChange={(e) => onComentar(campo, e.target.value)}
                  />
                ) : (
                  comentarios[campo] || '—'
                )}
              </td>
            </tr>
          ))}
          <tr>
            <th scope="row">{t(`${CLAVE}.campos.${CampoFichaViabilidad.IndicadoresEvaluacion}`)}</th>
            <td>
              {indicadores.length === 0
                ? '—'
                : indicadores
                    .map((i) => `${i.nombre}: ${i.valor == null ? '—' : i.valor.toLocaleString('es-SV')}`)
                    .join(' · ')}
            </td>
            <td>
              {puedeComentar ? (
                <input
                  type="text"
                  aria-label={t(`${CLAVE}.comentarioDe`, {
                    campo: t(`${CLAVE}.campos.${CampoFichaViabilidad.IndicadoresEvaluacion}`),
                  })}
                  value={comentarios[CampoFichaViabilidad.IndicadoresEvaluacion] ?? ''}
                  onChange={(e) => onComentar(CampoFichaViabilidad.IndicadoresEvaluacion, e.target.value)}
                />
              ) : (
                comentarios[CampoFichaViabilidad.IndicadoresEvaluacion] || '—'
              )}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}
