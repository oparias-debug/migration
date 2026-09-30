import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { catalogosApi, type Catalog } from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { coincide } from './busqueda';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import { CamposEditor, aCampoContrato, problemaDeCampos, type CampoEditable } from './CamposEditor';

/** Actor del CU-ADM-01: el back exige este rol en todas sus operaciones. */
export const ROL_ADMIN_CATALOGOS = 'ADMINISTRADOR_DE_CATALOGOS';
const CLAVE = 'administracion.catalogos';
/**
 * Se traen todos los catálogos de una vez y se pagina aquí. El contrato no tiene
 * parámetro de búsqueda en `listarCatalogos`, así que buscar por fracciones de
 * palabra sólo puede hacerse sobre lo cargado; un catálogo maestro son decenas
 * de entradas, no miles, así que caben.
 */
const TOPE_CATALOGOS = 500;
const POR_PAGINA = 10;

/**
 * Administración de Catálogos (CU-ADM-01): la lista de catálogos y el alta de uno
 * nuevo. Al abrir un catálogo se pasa a su ficha (CatalogoDetallePage), donde se
 * editan sus datos y se gestionan sus registros.
 */
export function CatalogosPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const [catalogos, setCatalogos] = useState<Catalog[]>([]);
  const [pagina, setPagina] = useState(0);
  const [busqueda, setBusqueda] = useState('');
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [creando, setCreando] = useState(false);

  const cargar = useCallback(() => {
    setCargando(true);
    catalogosApi
      .listarCatalogos({ page: 0, size: TOPE_CATALOGOS })
      .then(({ data }) => {
        setCatalogos(data.content ?? []);
        setErrorCarga(null);
      })
      .catch((error_) => setErrorCarga(mensajeDeError(toErrorApi(error_), t)))
      .finally(() => setCargando(false));
  }, [t]);

  const puedeAdministrar = hasRole(ROL_ADMIN_CATALOGOS);
  useEffect(() => {
    if (puedeAdministrar) cargar();
  }, [cargar, puedeAdministrar]);

  // La búsqueda mira el nombre y el código, por fracciones de palabra.
  const filtrados = catalogos.filter((c) => coincide(busqueda, c.name, c.code, c.parent));
  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / POR_PAGINA));
  const paginaVigente = Math.min(pagina, totalPaginas - 1);
  const enPagina = filtrados.slice(paginaVigente * POR_PAGINA, (paginaVigente + 1) * POR_PAGINA);

  if (!puedeAdministrar) {
    return (
      <p className="aviso-error" role="alert">
        {t(`${CLAVE}.sinPermiso`)}
      </p>
    );
  }

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t(`${CLAVE}.titulo`)}</span>
      </div>
      <div className="formbody">

        {creando ? (
          <NuevoCatalogo
            alCancelar={() => setCreando(false)}
            alCrear={(codigo) => navigate(`/catalogos-generales/${encodeURIComponent(codigo)}`)}
          />
        ) : (
          <div className="acciones-form">
            <button type="button" className="btn primario" onClick={() => setCreando(true)}>
              {t(`${CLAVE}.nuevo`)}
            </button>
          </div>
        )}

        {cargando && <p className="nota">{t('common.cargando')}</p>}
        {errorCarga && <p className="aviso-error">{errorCarga}</p>}
        {!cargando && !errorCarga && catalogos.length === 0 && <p className="nota">{t(`${CLAVE}.sinCatalogos`)}</p>}

        {catalogos.length > 0 && (
          <div className="f buscador-tabla">
            <label htmlFor="buscar-catalogo">{t(`${CLAVE}.buscarCatalogo`)}</label>
            <input
              id="buscar-catalogo"
              type="search"
              value={busqueda}
              placeholder={t(`${CLAVE}.buscarCatalogoPista`)}
              onChange={(e) => {
                setBusqueda(e.target.value);
                setPagina(0);
              }}
            />
          </div>
        )}

        {catalogos.length > 0 && filtrados.length === 0 && (
          <p className="nota">{t(`${CLAVE}.sinCoincidenciasCatalogo`, { busqueda })}</p>
        )}

        {filtrados.length > 0 && (
          <div className="tabla-cont">
            <table>
              <thead>
                <tr>
                  <th>{t(`${CLAVE}.codigo`)}</th>
                  <th>{t(`${CLAVE}.nombre`)}</th>
                  {/* De quién cuelga: sin esta columna la lista no dejaba ver
                      que un catálogo fuera hijo de otro. */}
                  <th>{t(`${CLAVE}.catalogoPadre`)}</th>
                  <th>{t(`${CLAVE}.estado`)}</th>
                  <th>{t(`${CLAVE}.campos`)}</th>
                  <th>{t('common.acciones')}</th>
                </tr>
              </thead>
              <tbody>
                {enPagina.map((c) => (
                  <tr key={c.code}>
                    <td className="mono">{c.code}</td>
                    <td>{c.name}</td>
                    <td className="mono">{c.parent ?? '—'}</td>
                    <td>{t(`${CLAVE}.estados.${c.active}`)}</td>
                    <td>{(c.fields ?? []).length}</td>
                    <td>
                      <button
                        type="button"
                        className="btn secundario"
                        aria-label={t(`${CLAVE}.abrirCatalogo`, { nombre: c.name })}
                        onClick={() => navigate(`/catalogos-generales/${encodeURIComponent(c.code ?? '')}`)}
                      >
                        {t(`${CLAVE}.abrir`)}
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
            <button type="button" className="btn neutro" disabled={paginaVigente === 0} onClick={() => setPagina((p) => p - 1)}>
              {t('common.previous')}
            </button>
            <span className="nota">{t(`${CLAVE}.pagina`, { actual: paginaVigente + 1, total: totalPaginas })}</span>
            <button
              type="button"
              className="btn neutro"
              disabled={paginaVigente + 1 >= totalPaginas}
              onClick={() => setPagina((p) => p + 1)}
            >
              {t('common.next')}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

/** Alta de un catálogo: sus datos y sus campos. */
function NuevoCatalogo({ alCancelar, alCrear }: { readonly alCancelar: () => void; readonly alCrear: (codigo: string) => void }) {
  const { t } = useTranslation();
  const [codigo, setCodigo] = useState('');
  const [nombre, setNombre] = useState('');
  const [padre, setPadre] = useState('');
  const [desde, setDesde] = useState('');
  const [hasta, setHasta] = useState('');
  const [campos, setCampos] = useState<CampoEditable[]>([{ nombre: '', clave: true }]);
  const [nombreEnUso, setNombreEnUso] = useState(false);
  const [guardando, setGuardando] = useState(false);

  // El CU pide avisar si el nombre ya existe antes de guardar.
  const verificarNombre = async () => {
    if (!nombre.trim()) return;
    try {
      const { data } = await catalogosApi.verificarExistenciaCatalogo({ name: nombre.trim() });
      setNombreEnUso(Boolean(data.exists));
    } catch {
      setNombreEnUso(false);
    }
  };

  const crear = async () => {
    const problema = !codigo.trim() || !nombre.trim() ? 'faltanDatos' : problemaDeCampos(campos);
    if (problema) {
      await Swal.fire({ icon: 'error', text: t(`${CLAVE}.problemas.${problema}`) });
      return;
    }
    setGuardando(true);
    try {
      await catalogosApi.crearCatalogo({
        catalogCreateRequest: {
          code: codigo.trim(),
          name: nombre.trim(),
          parent: padre.trim() || undefined,
          fromDate: desde || undefined,
          toDate: hasta || undefined,
          fields: aCampoContrato(campos),
        },
      });
      await Swal.fire({ icon: 'success', text: t(`${CLAVE}.creado`) });
      alCrear(codigo.trim());
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setGuardando(false);
    }
  };

  return (
    <section className="nuevo-catalogo">
      <h2 className="seccion">{t(`${CLAVE}.tituloCrear`)}</h2>
      <div className="fr">
        <FormRow label={t(`${CLAVE}.codigo`)} controlId="cat-codigo" required>
          <input id="cat-codigo" type="text" value={codigo} onChange={(e) => setCodigo(e.target.value)} />
        </FormRow>
        <FormRow
          label={t(`${CLAVE}.nombre`)}
          controlId="cat-nombre"
          required
          error={nombreEnUso ? t(`${CLAVE}.nombreEnUso`) : undefined}
        >
          <input
            id="cat-nombre"
            type="text"
            value={nombre}
            onChange={(e) => {
              setNombre(e.target.value);
              setNombreEnUso(false);
            }}
            onBlur={verificarNombre}
          />
        </FormRow>
        <FormRow label={t(`${CLAVE}.catalogoPadre`)} controlId="cat-padre">
          <input id="cat-padre" type="text" value={padre} onChange={(e) => setPadre(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.vigenciaDesde`)} controlId="cat-desde">
          <input id="cat-desde" type="date" value={desde} onChange={(e) => setDesde(e.target.value)} />
        </FormRow>
        <FormRow label={t(`${CLAVE}.vigenciaHasta`)} controlId="cat-hasta">
          <input id="cat-hasta" type="date" value={hasta} onChange={(e) => setHasta(e.target.value)} />
        </FormRow>
      </div>

      <CamposEditor campos={campos} alCambiar={setCampos} />

      <div className="acciones-form">
        <button type="button" className="btn neutro" onClick={alCancelar}>
          {t('common.cancelar')}
        </button>
        <button type="button" className="btn primario" onClick={crear} disabled={guardando}>
          {t(`${CLAVE}.crear`)}
        </button>
      </div>
    </section>
  );
}

