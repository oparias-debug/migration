import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { bancoProyectosApi, type ProyectoBancoItem } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { Pagination } from '../../../components/table/Pagination';

const CLAVE = 'preinversion.banco';
const TAMANIO_PAGINA = 20;

const formatearMonto = (valor: number | null | undefined): string =>
  valor == null ? '—' : valor.toLocaleString('es-SV', { style: 'currency', currency: 'USD' });

/**
 * "Banco de Proyectos" (CU-PRE-29, Anexo A.1).
 *
 * El módulo transversal del menú, que hasta ahora mostraba "En construcción".
 * Es un listado de consulta: todos los proyectos con su etapa, su inversión
 * estimada y su estado, con búsqueda por CUP o nombre (RN02).
 *
 * La columna "Prioridad" y la ficha del proyecto vienen de casos de uso que
 * todavía no existen: la prioridad llega del contrato cuando el servidor la
 * trae, y el nombre no enlaza a ninguna parte hasta que exista esa ficha.
 */
export function BancoProyectosPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const [proyectos, setProyectos] = useState<ProyectoBancoItem[]>([]);
  const [busqueda, setBusqueda] = useState('');
  const [busquedaAplicada, setBusquedaAplicada] = useState('');
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargar = useCallback(
    async (paginaSolicitada: number) => {
      setCargando(true);
      setError(null);
      try {
        const { data } = await bancoProyectosApi.listarBancoProyectos({
          pagina: paginaSolicitada,
          tamanio: TAMANIO_PAGINA,
          ...(busquedaAplicada ? { busqueda: busquedaAplicada } : {}),
        });
        setProyectos(data.contenido ?? []);
        setTotalPaginas(data.paginacion?.totalPaginas ?? 0);
        setTotalElementos(data.paginacion?.totalElementos ?? 0);
        setPagina(data.paginacion?.pagina ?? paginaSolicitada);
      } catch (error_) {
        setProyectos([]);
        setTotalPaginas(0);
        setError(mensajeDeError(toErrorApi(error_), t));
      } finally {
        setCargando(false);
      }
    },
    [busquedaAplicada, t],
  );

  useEffect(() => {
    void cargar(0);
  }, [cargar]);

  return (
    <>
      {error && (
        <div className="aviso-error" role="alert">
          <span>{error}</span>
          <button type="button" className="btn neutro" onClick={() => void cargar(pagina)}>
            {t('errores.reintentar')}
          </button>
        </div>
      )}

      <div className="tarjeta">
        <div className="filtros">
          <div className="campo crece">
            <label htmlFor="banco-busqueda">{t(`${CLAVE}.busqueda`)}</label>
            <input
              id="banco-busqueda"
              type="search"
              placeholder={t(`${CLAVE}.busquedaAyuda`)}
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter') setBusquedaAplicada(busqueda.trim());
              }}
            />
          </div>
          <div className="campo">
            <button type="button" className="btn primario" onClick={() => setBusquedaAplicada(busqueda.trim())}>
              {t(`${CLAVE}.buscar`)}
            </button>
          </div>
        </div>

        {cargando && <p className="cargando">{t('common.cargando')}</p>}

        {!cargando && !error && (
          <div className="tabla-cont">
            <table>
              <thead>
                <tr>
                  <th>{t(`${CLAVE}.columnaCup`)}</th>
                  <th>{t(`${CLAVE}.columnaProyecto`)}</th>
                  <th>{t(`${CLAVE}.columnaEtapa`)}</th>
                  <th>{t(`${CLAVE}.columnaInversion`)}</th>
                  <th>{t(`${CLAVE}.columnaEstado`)}</th>
                  <th>{t(`${CLAVE}.columnaPrioridad`)}</th>
                </tr>
              </thead>
              <tbody>
                {proyectos.length === 0 && (
                  <tr>
                    <td className="vacio" colSpan={6}>
                      {t(`${CLAVE}.sinProyectos`)}
                    </td>
                  </tr>
                )}
                {proyectos.map((p) => (
                  <tr key={p.idProyecto}>
                    <td className="mono">{p.cup}</td>
                    <td>
                      <button
                        type="button"
                        className="enlace-fila"
                        onClick={() => navigate(`/preinversion/proyectos/${p.idProyecto}`)}
                      >
                        {p.nombreProyecto}
                      </button>
                    </td>
                    <td>{p.etapa}</td>
                    <td style={{ textAlign: 'right' }}>{formatearMonto(p.inversionEstimada)}</td>
                    <td>{p.estado}</td>
                    <td>{p.prioridad ?? '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {!error && !cargando && proyectos.length > 0 && (
          <p className="conteo-listado">
            {t('preinversion.registro.conteo', {
              desde: pagina * TAMANIO_PAGINA + 1,
              hasta: pagina * TAMANIO_PAGINA + proyectos.length,
              total: totalElementos,
            })}
          </p>
        )}

        {!error && (
          <Pagination
            currentPage={pagina}
            totalPages={totalPaginas}
            first={pagina <= 0}
            last={pagina >= totalPaginas - 1}
            onPageChange={(p) => void cargar(p)}
          />
        )}
      </div>
    </>
  );
}
