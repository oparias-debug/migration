import { useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { registrosCatalogoApi, type CatalogRecord } from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import type { CampoEditable } from './CamposEditor';
import { coincide } from './busqueda';

const CLAVE = 'administracion.catalogos';
/**
 * Se traen de una vez todos los registros del catálogo. El contrato no tiene
 * parámetro de búsqueda, así que buscar por fracciones de palabra sólo puede
 * hacerse sobre lo que esté cargado; además la tabla mostraba sólo los primeros
 * 20 y no había manera de llegar al resto. Con esto se cubre un catálogo de
 * cientos de registros; si alguno pasa de este tope se avisa en pantalla.
 */
const TOPE_REGISTROS = 1000;
const POR_PAGINA = 20;

/**
 * Registros de un catálogo: los datos que después se eligen en las pantallas del
 * sistema. Las columnas y el formulario salen de los campos del propio catálogo;
 * al editar, los campos de la clave quedan fijos porque identifican el registro.
 * Como los catálogos, un registro no se elimina: sólo se inactiva.
 */
export function RegistrosCatalogo({ codigo, campos }: { readonly codigo: string; readonly campos: readonly CampoEditable[] }) {
  const { t } = useTranslation();
  const [registros, setRegistros] = useState<(CatalogRecord & { key: string })[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [valores, setValores] = useState<Record<string, string>>({});
  const [editando, setEditando] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [busqueda, setBusqueda] = useState('');
  const [pagina, setPagina] = useState(0);
  /** Cuántos hay en total, para avisar si no caben todos. */
  const [total, setTotal] = useState(0);

  const nombres = campos.map((c) => c.nombre);

  const cargar = useCallback(() => {
    registrosCatalogoApi
      .buscarListaRegistros({ code: codigo, fields: campos.map((c) => c.nombre), page: 0, size: TOPE_REGISTROS })
      .then(({ data }) => {
        setRegistros((data.content ?? []).filter((r): r is CatalogRecord & { key: string } => Boolean(r.key)));
        setTotal(data.totalElements ?? 0);
        setError(null);
      })
      .catch((error_) => setError(mensajeDeError(toErrorApi(error_), t)));
    // `campos` cambia de identidad en cada render del padre; basta con sus nombres.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [codigo, nombres.join('|'), t]);

  useEffect(() => {
    cargar();
  }, [cargar]);

  const limpiar = () => {
    setValores({});
    setEditando(null);
  };

  const guardar = async () => {
    if (campos.filter((c) => c.clave).some((c) => !(valores[c.nombre] ?? '').trim())) {
      await Swal.fire({ icon: 'error', text: t(`${CLAVE}.claveObligatoria`) });
      return;
    }
    setGuardando(true);
    try {
      if (editando) {
        // La clave va en la ruta y no se puede cambiar: el back rechaza la
        // actualización si el cuerpo trae un campo KEY, aunque sea el mismo valor.
        const claves = new Set(campos.filter((c) => c.clave).map((c) => c.nombre));
        const sinClave = Object.fromEntries(Object.entries(valores).filter(([n]) => !claves.has(n)));
        await registrosCatalogoApi.actualizarRegistro({
          code: codigo,
          key: editando,
          catalogRecordUpdateRequest: { values: sinClave },
        });
      } else {
        await registrosCatalogoApi.crearRegistroCatalogo({ code: codigo, catalogRecordCreateRequest: { values: valores } });
      }
      await Swal.fire({ icon: 'success', text: t(`${CLAVE}.registroGuardado`) });
      limpiar();
      cargar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  const accion = async (promesa: Promise<unknown>, exito: string) => {
    try {
      await promesa;
      await Swal.fire({ icon: 'success', text: t(exito) });
      cargar();
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    }
  };


  // La búsqueda mira la clave y todos los valores del registro, por fracciones
  // de palabra y sin distinguir tildes ni mayúsculas.
  const filtrados = registros.filter((r) => coincide(busqueda, r.key, ...Object.values(r.values ?? {})));
  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / POR_PAGINA));
  const paginaVigente = Math.min(pagina, totalPaginas - 1);
  const enPagina = filtrados.slice(paginaVigente * POR_PAGINA, (paginaVigente + 1) * POR_PAGINA);

  return (
    <section>
      <h2 className="seccion">{t(`${CLAVE}.registros`)}</h2>
      {error && <p className="aviso-error">{error}</p>}
      {!error && registros.length === 0 && <p className="nota">{t(`${CLAVE}.sinRegistros`)}</p>}

      {registros.length > 0 && (
        <div className="f buscador-tabla">
          <label htmlFor="buscar-registro">{t(`${CLAVE}.buscarRegistro`)}</label>
          <input
            id="buscar-registro"
            type="search"
            value={busqueda}
            placeholder={t(`${CLAVE}.buscarRegistroPista`)}
            onChange={(e) => {
              setBusqueda(e.target.value);
              setPagina(0);
            }}
          />
        </div>
      )}

      {/* El catálogo tiene más registros de los que caben de una vez: la
          búsqueda sólo mira los traídos, así que conviene decirlo. */}
      {total > registros.length && (
        <p className="nota">{t(`${CLAVE}.registrosParciales`, { traidos: registros.length, total })}</p>
      )}

      {registros.length > 0 && filtrados.length === 0 && (
        <p className="nota">{t(`${CLAVE}.sinCoincidencias`, { busqueda })}</p>
      )}

      {filtrados.length > 0 && (
        <div className="tabla-cont tabla-editable">
          <table>
            <thead>
              <tr>
                {nombres.map((n) => (
                  <th key={n}>{n}</th>
                ))}
                <th>{t(`${CLAVE}.estado`)}</th>
                <th>{t('common.acciones')}</th>
              </tr>
            </thead>
            <tbody>
              {enPagina.map((r) => (
                <tr key={r.key}>
                  {nombres.map((n) => (
                    <td key={n}>{r.values?.[n] ?? '—'}</td>
                  ))}
                  <td>{t(`${CLAVE}.estados.${r.active}`)}</td>
                  <td>
                    <button
                      type="button"
                      className="btn secundario"
                      aria-label={t(`${CLAVE}.editarRegistro`, { clave: r.key })}
                      onClick={() => {
                        setEditando(r.key);
                        setValores({ ...r.values });
                      }}
                    >
                      {t('common.editar')}
                    </button>{' '}
                    <button
                      type="button"
                      className="btn secundario"
                      aria-label={t(`${CLAVE}.inactivarRegistro`, { clave: r.key })}
                      disabled={r.active === 'INACTIVE'}
                      onClick={() =>
                        accion(
                          registrosCatalogoApi.inactivarRegistroCatalogo({
                            code: codigo,
                            key: r.key,
                            inactivationRequest: { active: 'INACTIVE' },
                          }),
                          `${CLAVE}.registroInactivado`,
                        )
                      }
                    >
                      {t(`${CLAVE}.inactivar`)}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {totalPaginas > 1 && (
        <div className="acciones-form">
          <button type="button" className="btn neutro" disabled={pagina === 0} onClick={() => setPagina((p) => p - 1)}>
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

      <fieldset className="registro-form">
        <legend>{editando ? t(`${CLAVE}.editandoRegistro`, { clave: editando }) : t(`${CLAVE}.nuevoRegistro`)}</legend>
        <div className="fr">
          {campos.map((c) => (
            <div className="f" key={c.nombre}>
              <label htmlFor={`reg-${c.nombre}`}>
                {c.nombre}
                {c.clave && <span className="req">*</span>}
              </label>
              <input
                id={`reg-${c.nombre}`}
                type="text"
                value={valores[c.nombre] ?? ''}
                // La clave identifica el registro: al editar no se cambia.
                readOnly={Boolean(editando) && c.clave}
                onChange={(e) => setValores((v) => ({ ...v, [c.nombre]: e.target.value }))}
              />
            </div>
          ))}
        </div>
        <div className="acciones-form">
          {editando && (
            <button type="button" className="btn neutro" onClick={limpiar}>
              {t('common.cancelar')}
            </button>
          )}
          <button type="button" className="btn primario" onClick={guardar} disabled={guardando}>
            {t(`${CLAVE}.guardarRegistro`)}
          </button>
        </div>
      </fieldset>
    </section>
  );
}
