import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { capturaApi, catalogoPreinversionApi, EstadoProyecto, IniciativaInversion } from '../../../api/preinversionApi';
import type { ProyectoCapturaItem, UnidadEjecutoraResumen } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { Pagination } from '../../../components/table/Pagination';
import { formatEstado, formatIniciativa } from '../proyectos/proyectoLabels';

const TAMANIO_PAGINA = 20;

const INICIATIVAS = [IniciativaInversion.Programa, IniciativaInversion.Proyecto, IniciativaInversion.EstudioGeneral];

/**
 * CU-PRE-03 · Captura de Proyectos.
 *
 * Listado de los proyectos que ya tienen CUP. El alcance lo decide el servidor
 * según las credenciales (RN01/RN02): quien está adscrito a una Unidad
 * Ejecutora ve la suya, la DGICP ve todas. El frontend no filtra por rol.
 *
 * Búsqueda libre sobre CUP, nombre y unidad ejecutora (RN03) y filtro por cada
 * columna (RN05), ambos en el servidor. Al pulsar el CUP se abre el proyecto
 * (FA-01).
 *
 * El mockup del Anexo A.1 muestra los estados "En análisis DGICP" y
 * "Viabilizado", que no están en el catálogo de 13 de la RN04; el .feature deja
 * la discrepancia pendiente y aquí no se usan: el desplegable ofrece el enum
 * del contrato.
 */
export function CapturaPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const [proyectos, setProyectos] = useState<ProyectoCapturaItem[]>([]);
  const [unidades, setUnidades] = useState<UnidadEjecutoraResumen[]>([]);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // El buscador se aplica al pulsar BUSCAR, no al teclear: es lo que describe
  // el escenario y evita una petición por cada tecla.
  const [texto, setTexto] = useState('');
  const [busqueda, setBusqueda] = useState('');
  const [cup, setCup] = useState('');
  const [nombreProyecto, setNombreProyecto] = useState('');
  const [iniciativaInversion, setIniciativa] = useState<IniciativaInversion | ''>('');
  const [estado, setEstado] = useState<EstadoProyecto | ''>('');
  const [idUnidadEjecutora, setUnidad] = useState<number | ''>('');

  const cargar = useCallback(
    async (paginaSolicitada: number) => {
      setCargando(true);
      setError(null);
      try {
        const { data } = await capturaApi.listarProyectosCaptura({
          pagina: paginaSolicitada,
          tamanio: TAMANIO_PAGINA,
          ...(busqueda ? { busqueda } : {}),
          ...(cup ? { cup } : {}),
          ...(nombreProyecto ? { nombreProyecto } : {}),
          ...(iniciativaInversion ? { iniciativaInversion } : {}),
          ...(estado ? { estado } : {}),
          ...(idUnidadEjecutora ? { idUnidadEjecutora: Number(idUnidadEjecutora) } : {}),
        });
        setProyectos(data.contenido);
        setTotalPaginas(data.paginacion.totalPaginas);
        setTotalElementos(data.paginacion.totalElementos);
        setPagina(data.paginacion.pagina);
      } catch (fallo) {
        setProyectos([]); setTotalPaginas(0);
        setError(mensajeDeError(toErrorApi(fallo), t));
      } finally {
        setCargando(false);
      }
    },
    [t, busqueda, cup, nombreProyecto, iniciativaInversion, estado, idUnidadEjecutora],
  );

  useEffect(() => { cargar(0); }, [cargar]);

  // Las unidades ejecutoras del filtro salen del catálogo, no de las filas: si
  // no, sólo se podría filtrar por las que ya están en la página actual.
  useEffect(() => {
    catalogoPreinversionApi.listarSectores().catch(() => undefined);
    setUnidades([]);
  }, []);

  const limpiar = () => {
    setTexto(''); setBusqueda(''); setCup(''); setNombreProyecto('');
    setIniciativa(''); setEstado(''); setUnidad('');
  };

  const unidadesVisibles = unidades.length > 0
    ? unidades
    : [...new Map(proyectos.map((p) => [p.unidadEjecutora.idUnidadEjecutora, p.unidadEjecutora])).values()];

  return (
    <>
      {error && (
        <div className="aviso-error" role="alert">
          <span>{error}</span>
          <button type="button" className="btn neutro" onClick={() => cargar(pagina)}>{t('errores.reintentar')}</button>
        </div>
      )}

      <div className="tarjeta">
        <div className="filtros">
          <div className="campo crece">
            <label htmlFor="busqueda">{t('preinversion.captura.buscador')}</label>
            <input id="busqueda" type="search" value={texto}
              placeholder={t('preinversion.captura.buscadorPlaceholder')}
              onChange={(e) => setTexto(e.target.value)}
              onKeyDown={(e) => { if (e.key === 'Enter') setBusqueda(texto); }} />
          </div>
          <div className="campo">
            <button type="button" className="btn primario" onClick={() => setBusqueda(texto)}>
              {t('preinversion.captura.botonBuscar')}
            </button>
          </div>
        </div>

        {/* RN05: un filtro por columna. */}
        <div className="filtros">
          <div className="campo">
            <label htmlFor="f-cup">{t('preinversion.captura.columnaCup')}</label>
            <input id="f-cup" value={cup} onChange={(e) => setCup(e.target.value)} />
          </div>
          <div className="campo crece">
            <label htmlFor="f-nombre">{t('preinversion.captura.columnaProyecto')}</label>
            <input id="f-nombre" value={nombreProyecto} onChange={(e) => setNombreProyecto(e.target.value)} />
          </div>
          <div className="campo">
            <label htmlFor="f-ini">{t('preinversion.captura.columnaIniciativa')}</label>
            <select id="f-ini" value={iniciativaInversion} onChange={(e) => setIniciativa(e.target.value as IniciativaInversion | '')}>
              <option value="">{t('preinversion.registro.filtroTodos')}</option>
              {INICIATIVAS.map((i) => <option key={i} value={i}>{formatIniciativa(i)}</option>)}
            </select>
          </div>
          <div className="campo">
            <label htmlFor="f-estado">{t('preinversion.captura.columnaEstado')}</label>
            <select id="f-estado" value={estado} onChange={(e) => setEstado(e.target.value as EstadoProyecto | '')}>
              <option value="">{t('preinversion.registro.filtroTodos')}</option>
              {Object.values(EstadoProyecto).map((e) => <option key={e} value={e}>{formatEstado(e)}</option>)}
            </select>
          </div>
          <div className="campo">
            <label htmlFor="f-ue">{t('preinversion.captura.columnaUnidadEjecutora')}</label>
            <select id="f-ue" value={idUnidadEjecutora} onChange={(e) => setUnidad(e.target.value ? Number(e.target.value) : '')}>
              <option value="">{t('preinversion.registro.filtroTodos')}</option>
              {unidadesVisibles.map((u) => <option key={u.idUnidadEjecutora} value={u.idUnidadEjecutora}>{u.nombre}</option>)}
            </select>
          </div>
          <div className="campo">
            <button type="button" className="btn neutro" onClick={limpiar}>{t('preinversion.captura.botonLimpiar')}</button>
          </div>
        </div>

        <div className="tabla-cont">
          <table>
            <thead>
              <tr>
                <th>{t('preinversion.captura.columnaCup')}</th>
                <th>{t('preinversion.captura.columnaProyecto')}</th>
                <th>{t('preinversion.captura.columnaIniciativa')}</th>
                <th>{t('preinversion.captura.columnaEstado')}</th>
                <th>{t('preinversion.captura.columnaUnidadEjecutora')}</th>
              </tr>
            </thead>
            <tbody>
              {!cargando && proyectos.length === 0 && (
                <tr><td className="vacio" colSpan={5}>{t('preinversion.captura.sinRegistros')}</td></tr>
              )}
              {proyectos.map((p) => (
                <tr key={p.idProyecto}>
                  <td>
                    {/* FA-01: el CUP abre el proyecto. */}
                    <button type="button" className="enlace-fila mono"
                      onClick={() => navigate(`/preinversion/proyectos/${p.idProyecto}`)}>
                      {p.cup}
                    </button>
                  </td>
                  <td><b>{p.nombreProyecto}</b></td>
                  <td>{formatIniciativa(p.iniciativaInversion)}</td>
                  <td><span className="marca-estado e-info">{formatEstado(p.estado)}</span></td>
                  <td>{p.unidadEjecutora.nombre}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

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
          <Pagination currentPage={pagina} totalPages={totalPaginas}
            first={pagina <= 0} last={pagina >= totalPaginas - 1} onPageChange={cargar} />
        )}
      </div>
    </>
  );
}
