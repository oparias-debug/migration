import { useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { registrosCatalogoApi, type CatalogRecordsResult, type Estado } from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import type { CampoEditable } from './CamposEditor';
import { coincide } from './busqueda';

const CLAVE = 'administracion.catalogos';
/**
 * El contrato no pagina los registros: `listarRegistros` devuelve la lista
 * entera. Se pagina y se busca aquí, sobre lo recibido. Antes la tabla pedía 20
 * y no había manera de llegar al resto.
 */
const POR_PAGINA = 20;

/** Un registro tal como lo usa la pantalla: su clave, sus valores por nombre y su estado. */
interface Fila {
  readonly llave: string;
  readonly valores: Record<string, string>;
  /** Los valores que no son la clave, en el orden de las columnas; sirven para reconocer el registro. */
  readonly otros: readonly string[];
  readonly estado: Estado;
}

/**
 * El listado llega como tabla —`fieldSet` con los nombres de columna y
 * `resultSet` con una fila de valores en ese mismo orden— y no como registros
 * con sus campos nombrados. Aquí se vuelve a armar el mapa nombre -> valor, que
 * es con lo que se rellena el formulario y se comparan las columnas.
 *
 * La clave no viene aparte: el servidor siempre mete el campo KEY en el
 * `fieldSet` (CU-ADM-01-13, "el Field Set es codPais, continente" al pedir sólo
 * "continente"), así que se saca del valor que ocupa esa posición. Si no se sabe
 * qué campo es la clave, se toma el primero, que es donde el contrato la pone
 * cuando no se piden campos (KEY + primer campo no KEY).
 */
const aFilas = (res: CatalogRecordsResult, campoClave?: string): Fila[] => {
  const columnas = res.fieldSet ?? [];
  // Si el campo clave no está entre las columnas, indexOf da -1 y se cae a la primera.
  const iClave = Math.max(campoClave ? columnas.indexOf(campoClave) : 0, 0);
  return (res.resultSet ?? [])
    .map((fila) => {
      const valores = fila.valores ?? [];
      return {
        llave: valores[iClave] ?? '',
        valores: Object.fromEntries(columnas.map((nombre, i) => [nombre, valores[i] ?? ''])),
        otros: columnas.flatMap((_, i) => (i === iClave ? [] : [valores[i] ?? ''])).filter(Boolean),
        estado: fila.estado,
      };
    })
    .filter((f) => Boolean(f.llave));
};

/**
 * Registros de un catálogo: los datos que después se eligen en las pantallas del
 * sistema. Las columnas y el formulario salen de los campos del propio catálogo;
 * al editar, los campos de la clave quedan fijos porque identifican el registro.
 * Como los catálogos, un registro no se elimina: sólo se inactiva.
 */
export function RegistrosCatalogo({
  codigo,
  campos,
  catalogoPadre,
}: {
  readonly codigo: string;
  readonly campos: readonly CampoEditable[];
  /** Código del catálogo del que cuelga éste, si cuelga de alguno. */
  readonly catalogoPadre?: string | null;
}) {
  const { t } = useTranslation();
  const [registros, setRegistros] = useState<Fila[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [valores, setValores] = useState<Record<string, string>>({});
  const [editando, setEditando] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [busqueda, setBusqueda] = useState('');
  /**
   * Registros del catálogo padre, para elegir de cuál cuelga el que se crea.
   * En una jerarquía región → departamento → distrito, al dar de alta un
   * distrito hay que decir a qué departamento pertenece; el contrato lo exige
   * (`registroPadre`, Reglas 8 y 23) y la pantalla no lo ofrecía.
   */
  const [registrosPadre, setRegistrosPadre] = useState<Fila[]>([]);
  const [padreElegido, setPadreElegido] = useState('');
  const [busquedaPadre, setBusquedaPadre] = useState('');
  const [pagina, setPagina] = useState(0);

  const nombres = campos.map((c) => c.nombre);
  const campoClave = campos.find((c) => c.clave)?.nombre;

  const cargar = useCallback(() => {
    registrosCatalogoApi
      .listarRegistros({ codigo, campos: campos.map((c) => c.nombre) })
      .then(({ data }) => {
        setRegistros(aFilas(data, campoClave));
        setError(null);
      })
      .catch((error_) => setError(mensajeDeError(toErrorApi(error_), t)));
    // `campos` cambia de identidad en cada render del padre; basta con sus nombres.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [codigo, nombres.join('|'), campoClave, t]);

  useEffect(() => {
    cargar();
  }, [cargar]);

  useEffect(() => {
    if (!catalogoPadre) {
      setRegistrosPadre([]);
      return;
    }
    // Sin pedir campos, el servidor devuelve la clave y el primer campo que no
    // es clave, que es justo lo que hace falta para reconocer el registro.
    registrosCatalogoApi
      .listarRegistros({ codigo: catalogoPadre })
      .then(({ data }) => setRegistrosPadre(aFilas(data)))
      .catch(() => setRegistrosPadre([]));
  }, [catalogoPadre]);

  const limpiar = () => {
    setValores({});
    setEditando(null);
    setPadreElegido('');
    setBusquedaPadre('');
  };

  /** Cómo se lee un registro del padre en la lista: su clave y lo que la acompaña. */
  const rotuloPadre = (r: Fila) => (r.otros.length > 0 ? `${r.llave} — ${r.otros.join(' · ')}` : r.llave);

  const guardar = async () => {
    if (campos.filter((c) => c.clave).some((c) => !(valores[c.nombre] ?? '').trim())) {
      await Swal.fire({ icon: 'error', text: t(`${CLAVE}.claveObligatoria`) });
      return;
    }
    if (!editando && catalogoPadre && !padreElegido) {
      await Swal.fire({ icon: 'error', text: t(`${CLAVE}.padreObligatorio`) });
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
          codigo,
          llave: editando,
          requestBody: sinClave,
        });
      } else {
        await registrosCatalogoApi.crearRegistro({
          codigo,
          registroCreacion: {
            valores,
            // Sólo al crear: el contrato no admite cambiar de padre al actualizar.
            ...(catalogoPadre ? { registroPadre: padreElegido } : {}),
          },
        });
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
  const filtrados = registros.filter((r) => coincide(busqueda, r.llave, ...Object.values(r.valores)));
  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / POR_PAGINA));
  const paginaVigente = Math.min(pagina, totalPaginas - 1);
  const enPagina = filtrados.slice(paginaVigente * POR_PAGINA, (paginaVigente + 1) * POR_PAGINA);
  const padresFiltrados = registrosPadre.filter((r) => coincide(busquedaPadre, r.llave, ...Object.values(r.valores)));

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

      {registros.length > 0 && filtrados.length === 0 && (
        <p className="nota">{t(`${CLAVE}.sinCoincidencias`, { busqueda })}</p>
      )}

      {filtrados.length > 0 && (
        <div className="tabla-cont tabla-editable">
          <table>
            <thead>
              {/* Ya no hay columna de registro padre: desde el 05/10/2026 ninguna
                  operación de lectura devuelve el `registroPadre` de un registro
                  (sólo lo traen las respuestas de crear, actualizar y cambiar
                  estado), así que no hay de dónde sacarlo para la tabla. Se sigue
                  eligiendo al crear, que es lo que el contrato sí admite. */}
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
                <tr key={r.llave}>
                  {nombres.map((n) => (
                    <td key={n}>{r.valores[n] || '—'}</td>
                  ))}
                  <td>{t(`${CLAVE}.estados.${r.estado}`)}</td>
                  <td>
                    <button
                      type="button"
                      className="btn secundario"
                      aria-label={t(`${CLAVE}.editarRegistro`, { clave: r.llave })}
                      onClick={() => {
                        setEditando(r.llave);
                        setValores(r.valores);
                      }}
                    >
                      {t('common.editar')}
                    </button>{' '}
                    <button
                      type="button"
                      className="btn secundario"
                      aria-label={t(`${CLAVE}.inactivarRegistro`, { clave: r.llave })}
                      disabled={r.estado === 'INACTIVE'}
                      onClick={() =>
                        accion(
                          registrosCatalogoApi.cambiarEstadoRegistro({
                            codigo,
                            llave: r.llave,
                            cambioEstado: { estado: 'INACTIVE' },
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
        {/* De qué registro del catálogo padre cuelga éste. Se puede elegir de la
            lista o acotarla escribiendo parte de lo que se busca. Al editar no
            se ofrece: el contrato no admite mudar un registro de padre. */}
        {catalogoPadre && !editando && (
          <div className="fr">
            <div className="f">
              <label htmlFor="buscar-padre">{t(`${CLAVE}.buscarPadre`)}</label>
              <input
                id="buscar-padre"
                type="search"
                value={busquedaPadre}
                placeholder={t(`${CLAVE}.buscarRegistroPista`)}
                onChange={(e) => setBusquedaPadre(e.target.value)}
              />
            </div>
            <div className="f">
              <label htmlFor="reg-padre">
                {t(`${CLAVE}.registroPadre`, { catalogo: catalogoPadre })}
                <span className="req">*</span>
              </label>
              <select id="reg-padre" value={padreElegido} onChange={(e) => setPadreElegido(e.target.value)}>
                <option value="">{t('common.seleccione')}</option>
                {padresFiltrados.map((r) => (
                  <option key={r.llave} value={r.llave}>
                    {rotuloPadre(r)}
                  </option>
                ))}
              </select>
              {registrosPadre.length > 0 && padresFiltrados.length === 0 && (
                <span className="ayuda">{t(`${CLAVE}.sinCoincidencias`, { busqueda: busquedaPadre })}</span>
              )}
              {registrosPadre.length === 0 && <span className="ayuda">{t(`${CLAVE}.padreSinRegistros`)}</span>}
            </div>
          </div>
        )}

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
