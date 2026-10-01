import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { registrosCatalogoApi, type CatalogRecordFieldValuesResponse } from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { coincide } from './busqueda';

const CLAVE = 'administracion.catalogos';
const POR_PAGINA = 10;

/** Los valores de un registro, de lista de {campo, valor} a mapa nombre -> valor. */
const aMapa = (valores: CatalogRecordFieldValuesResponse['values']): Record<string, string> =>
  Object.fromEntries((valores ?? []).map((v) => [v.field, v.valor]));

/**
 * Lista de selección de un catálogo: lo que ve cualquier pantalla que necesite
 * elegir uno de sus registros (indicación del 01/10/2026).
 *
 * Es la misma tabla con búsqueda de la administración de catálogos, pero para
 * consultar: sin la columna de estado, sin los botones de editar e inactivar, y
 * sólo con los registros activos. Se elige pulsando la fila.
 *
 * Quien la usa pasa el código del catálogo; las columnas salen del propio
 * registro, así que sirve igual para un catálogo de dos columnas que para uno
 * de seis.
 */
export function ListaSeleccion({
  codigo,
  elegido,
  alElegir,
  etiqueta,
}: {
  readonly codigo: string;
  /** Clave del registro ya elegido, para marcarlo. */
  readonly elegido?: string;
  readonly alElegir: (clave: string, valores: Record<string, string>) => void;
  /** Qué se está eligiendo, para que la tabla se anuncie con sentido. */
  readonly etiqueta?: string;
}) {
  const { t } = useTranslation();
  const [registros, setRegistros] = useState<CatalogRecordFieldValuesResponse[]>([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busqueda, setBusqueda] = useState('');
  const [pagina, setPagina] = useState(0);

  useEffect(() => {
    setCargando(true);
    registrosCatalogoApi
      .buscarListaRegistros({ code: codigo })
      .then(({ data }) => {
        // Sólo los activos: de un catálogo se elige entre lo vigente.
        setRegistros((data ?? []).filter((r) => Boolean(r.keyValue) && r.active !== 'INACTIVE'));
        setError(null);
      })
      .catch((error_) => setError(mensajeDeError(toErrorApi(error_), t)))
      .finally(() => setCargando(false));
  }, [codigo, t]);

  if (cargando) return <p className="nota">{t('common.cargando')}</p>;
  if (error) return <p className="aviso-error">{error}</p>;
  if (registros.length === 0) return <p className="nota">{t(`${CLAVE}.sinRegistrosActivos`)}</p>;

  // Las columnas salen del primer registro: son los campos que pidió el servidor.
  const columnas = (registros[0].values ?? []).map((v) => v.field);
  const filtrados = registros.filter((r) => coincide(busqueda, r.keyValue, ...(r.values ?? []).map((v) => v.valor)));
  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / POR_PAGINA));
  const paginaVigente = Math.min(pagina, totalPaginas - 1);
  const enPagina = filtrados.slice(paginaVigente * POR_PAGINA, (paginaVigente + 1) * POR_PAGINA);

  return (
    <div className="lista-seleccion">
      <div className="f buscador-tabla">
        <label htmlFor={`buscar-sel-${codigo}`}>{t(`${CLAVE}.buscarEnCatalogo`, { catalogo: codigo })}</label>
        <input
          id={`buscar-sel-${codigo}`}
          type="search"
          value={busqueda}
          placeholder={t(`${CLAVE}.buscarRegistroPista`)}
          onChange={(e) => {
            setBusqueda(e.target.value);
            setPagina(0);
          }}
        />
      </div>

      {filtrados.length === 0 ? (
        <p className="nota">{t(`${CLAVE}.sinCoincidencias`, { busqueda })}</p>
      ) : (
        <div className="tabla-cont">
          <table>
            <caption className="sr-only">{etiqueta ?? t(`${CLAVE}.buscarEnCatalogo`, { catalogo: codigo })}</caption>
            <thead>
              <tr>
                {columnas.map((c) => (
                  <th key={c}>{c}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {enPagina.map((r) => {
                const valores = aMapa(r.values);
                const esElegido = r.keyValue === elegido;
                return (
                  // Se elige pulsando la fila entera, no un botón aparte.
                  <tr
                    key={r.keyValue}
                    className={esElegido ? 'fila-elegible elegida' : 'fila-elegible'}
                    aria-selected={esElegido}
                    onClick={() => alElegir(r.keyValue, valores)}
                  >
                    {columnas.map((c, i) => (
                      <td key={c}>
                        {i === 0 ? (
                          <button type="button" className="enlace-fila" onClick={() => alElegir(r.keyValue, valores)}>
                            {valores[c] ?? '—'}
                          </button>
                        ) : (
                          (valores[c] ?? '—')
                        )}
                      </td>
                    ))}
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {totalPaginas > 1 && (
        <div className="acciones-form">
          <button type="button" className="btn neutro" disabled={paginaVigente === 0} onClick={() => setPagina((p) => p - 1)}>
            {t('common.previous')}
          </button>
          <span className="nota">{t(`${CLAVE}.pagina`, { actual: paginaVigente + 1, total: totalPaginas })}</span>
          <button
            type="button"
            className="btn neutro"
            disabled={paginaVigente >= totalPaginas - 1}
            onClick={() => setPagina((p) => p + 1)}
          >
            {t('common.next')}
          </button>
        </div>
      )}
    </div>
  );
}
