import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { catalogosApi, type Catalog } from '../../../api/administracionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { FormRow } from '../../../components/form/FormRow';
import { CamposEditor, aCampoContrato, problemaDeCampos, type CampoEditable } from './CamposEditor';

/** Actor del CU-ADM-01: el back exige este rol en todas sus operaciones. */
export const ROL_ADMIN_CATALOGOS = 'ADMINISTRADOR_DE_CATALOGOS';
const CLAVE = 'administracion.catalogos';
const TAMANO_PAGINA = 10;

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
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [creando, setCreando] = useState(false);

  const cargar = useCallback(() => {
    setCargando(true);
    catalogosApi
      .listarCatalogos({ page: pagina, size: TAMANO_PAGINA })
      .then(({ data }) => {
        setCatalogos(data.content ?? []);
        setTotalPaginas(data.totalPages ?? 0);
        setErrorCarga(null);
      })
      .catch((error_) => setErrorCarga(mensajeDeError(toErrorApi(error_), t)))
      .finally(() => setCargando(false));
  }, [pagina, t]);

  const puedeAdministrar = hasRole(ROL_ADMIN_CATALOGOS);
  useEffect(() => {
    if (puedeAdministrar) cargar();
  }, [cargar, puedeAdministrar]);

  if (!puedeAdministrar) {
    return (
      <p className="aviso-error" role="alert">
        {t(`${CLAVE}.sinPermiso`)}
      </p>
    );
  }

  return (
    <div className="tarjeta">
      <h2>{t(`${CLAVE}.titulo`)}</h2>

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
        <div className="tabla-cont">
          <table>
            <thead>
              <tr>
                <th>{t(`${CLAVE}.codigo`)}</th>
                <th>{t(`${CLAVE}.nombre`)}</th>
                <th>{t(`${CLAVE}.estado`)}</th>
                <th>{t(`${CLAVE}.campos`)}</th>
                <th>{t('common.acciones')}</th>
              </tr>
            </thead>
            <tbody>
              {catalogos.map((c) => (
                <tr key={c.code}>
                  <td className="mono">{c.code}</td>
                  <td>{c.name}</td>
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
          <button type="button" className="btn neutro" disabled={pagina === 0} onClick={() => setPagina((p) => p - 1)}>
            {t('common.previous')}
          </button>
          <span className="nota">{t(`${CLAVE}.pagina`, { actual: pagina + 1, total: totalPaginas })}</span>
          <button
            type="button"
            className="btn neutro"
            disabled={pagina + 1 >= totalPaginas}
            onClick={() => setPagina((p) => p + 1)}
          >
            {t('common.next')}
          </button>
        </div>
      )}
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
      <h3>{t(`${CLAVE}.tituloCrear`)}</h3>
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

