import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { bandejaApi, catalogoBandejaApi, TipoSolicitud } from '../../../api/preinversionApi';
import type {
  ConteoTecnicoPre, SolicitudActivaItem, SolicitudArchivadaItem, UsuarioResumen,
} from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { Pagination } from '../../../components/table/Pagination';
import { formatTipoSolicitud, tonoEstado, TIPOS_SOLICITUD, ROL_COORDINADOR } from './bandejaLabels';

const TAMANIO_PAGINA = 20;

/**
 * CU-PRE-02 · Bandeja Preinversión.
 *
 * Las solicitudes de CUP y de Opinión Técnica llegan aquí para que el
 * Coordinador PRE las asigne a un Técnico PRE. Columnas según el escenario
 * "Consultar la tabla de solicitudes activas" (Anexo A.1); el pie muestra el
 * conteo por técnico separado por tipo (RN04).
 *
 * Asignar y archivar son sólo del Coordinador PRE (x-roles del contrato); un
 * Técnico PRE ve la tabla pero no esas acciones.
 */
export function BandejaPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const puedeGestionar = hasRole(ROL_COORDINADOR);

  const [vista, setVista] = useState<'activas' | 'archivadas'>('activas');
  const [tipo, setTipo] = useState<TipoSolicitud | ''>('');
  const [pagina, setPagina] = useState(0);
  const [activas, setActivas] = useState<SolicitudActivaItem[]>([]);
  const [archivadas, setArchivadas] = useState<SolicitudArchivadaItem[]>([]);
  const [conteo, setConteo] = useState<ConteoTecnicoPre[]>([]);
  const [tecnicos, setTecnicos] = useState<UsuarioResumen[]>([]);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editando, setEditando] = useState<number | null>(null);
  const [seleccion, setSeleccion] = useState('');

  const cargar = useCallback(
    async (paginaSolicitada: number) => {
      setCargando(true);
      setError(null);
      try {
        const parametros = { pagina: paginaSolicitada, tamanio: TAMANIO_PAGINA, ...(tipo ? { tipoSolicitud: tipo } : {}) };
        if (vista === 'activas') {
          const { data } = await bandejaApi.listarSolicitudesActivas(parametros);
          setActivas(data.contenido);
          setConteo(data.conteoPorTecnico);
          setTotalPaginas(data.paginacion.totalPaginas);
          setTotalElementos(data.paginacion.totalElementos);
          setPagina(data.paginacion.pagina);
        } else {
          const { data } = await bandejaApi.listarSolicitudesArchivadas(parametros);
          setArchivadas(data.contenido);
          setTotalPaginas(data.paginacion.totalPaginas);
          setTotalElementos(data.paginacion.totalElementos);
          setPagina(data.paginacion.pagina);
        }
      } catch (fallo) {
        setActivas([]); setArchivadas([]); setConteo([]); setTotalPaginas(0);
        setError(mensajeDeError(toErrorApi(fallo), t));
      } finally {
        setCargando(false);
      }
    },
    [t, tipo, vista],
  );

  useEffect(() => { cargar(0); }, [cargar]);

  // El catálogo de técnicos sólo lo necesita quien puede asignar.
  useEffect(() => {
    if (!puedeGestionar) return;
    catalogoBandejaApi.listarTecnicosPre().then(({ data }) => setTecnicos(data)).catch(() => setTecnicos([]));
  }, [puedeGestionar]);

  const cambiarFiltro = <T,>(fn: (v: T) => void) => (v: T) => { fn(v); setPagina(0); };
  const fecha = (iso: string) => new Date(iso).toLocaleDateString();

  const guardarAsignacion = async (idSolicitud: number) => {
    try {
      await bandejaApi.asignarTecnicoPre({ idSolicitud, asignacionTecnicoPreRequest: { idTecnicoAsignado: Number(seleccion) } });
      setEditando(null); setSeleccion('');
      await cargar(pagina);
    } catch (fallo) {
      Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(fallo), t) });
    }
  };

  const archivar = async (s: SolicitudActivaItem) => {
    // El texto difiere entre el paso 3.4 del CU y el mockup del Anexo A.3; se
    // usa el del mockup y queda anotado en el .feature como pendiente.
    const { isConfirmed } = await Swal.fire({
      text: t('preinversion.bandeja.confirmarArchivar'),
      icon: 'warning', showCancelButton: true,
      confirmButtonText: t('common.aceptar'), cancelButtonText: t('common.cancelar'),
    });
    if (!isConfirmed) return;
    try {
      await bandejaApi.archivarSolicitud({ idSolicitud: s.idSolicitud });
      await cargar(pagina);
    } catch (fallo) {
      Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(fallo), t) });
    }
  };

  const filas = vista === 'activas' ? activas : archivadas;

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
          <div className="campo">
            <label htmlFor="vista">{t('preinversion.bandeja.campoVista')}</label>
            <select id="vista" value={vista} onChange={(e) => cambiarFiltro(setVista)(e.target.value as 'activas' | 'archivadas')}>
              <option value="activas">{t('preinversion.bandeja.vistaActivas')}</option>
              <option value="archivadas">{t('preinversion.bandeja.vistaArchivadas')}</option>
            </select>
          </div>
          <div className="campo">
            <label htmlFor="tipo">{t('preinversion.bandeja.columnaTipo')}</label>
            <select id="tipo" value={tipo} onChange={(e) => cambiarFiltro(setTipo)(e.target.value as TipoSolicitud | '')}>
              <option value="">{t('preinversion.registro.filtroTodos')}</option>
              {TIPOS_SOLICITUD.map((x) => <option key={x} value={x}>{formatTipoSolicitud(x)}</option>)}
            </select>
          </div>
          <div className="campo crece" />
        </div>

        <div className="tabla-cont">
          <table>
            <thead>
              <tr>
                <th>{t('preinversion.bandeja.columnaUnidadEjecutora')}</th>
                <th>{t('preinversion.bandeja.columnaTipo')}</th>
                <th>{t('preinversion.bandeja.columnaCup')}</th>
                <th>{t('preinversion.bandeja.columnaProyecto')}</th>
                <th>{t('preinversion.bandeja.columnaFechaSolicitud')}</th>
                <th>{vista === 'activas' ? t('preinversion.bandeja.columnaEstado') : t('preinversion.bandeja.columnaFechaArchivo')}</th>
                {vista === 'activas' && <th>{t('preinversion.bandeja.columnaAsignadoA')}</th>}
                {vista === 'activas' && puedeGestionar && <th />}
              </tr>
            </thead>
            <tbody>
              {!cargando && filas.length === 0 && (
                <tr><td className="vacio" colSpan={8}>{t('preinversion.bandeja.sinRegistros')}</td></tr>
              )}
              {filas.map((s) => {
                const activa = vista === 'activas';
                const item = s as SolicitudActivaItem;
                return (
                  <tr key={s.idSolicitud}>
                    <td>{s.unidadEjecutora.nombre}</td>
                    <td>{formatTipoSolicitud(s.tipoSolicitud)}</td>
                    <td className="mono">{s.cup ?? '—'}</td>
                    <td><b>{s.nombreProyecto}</b></td>
                    <td>{fecha(s.fechaSolicitud)}</td>
                    <td>
                      {activa
                        ? <span className={`marca-estado ${tonoEstado(item.estado)}`}>{item.estado}</span>
                        : fecha((s as SolicitudArchivadaItem).fechaArchivo)}
                    </td>
                    {activa && (
                      <td>
                        {editando === s.idSolicitud ? (
                          <select value={seleccion} onChange={(e) => setSeleccion(e.target.value)}>
                            <option value="">{t('common.seleccione')}</option>
                            {tecnicos.map((x) => <option key={x.idUsuario} value={x.idUsuario}>{x.nombreCompleto}</option>)}
                          </select>
                        ) : (
                          item.asignadoA?.nombreCompleto ?? t('preinversion.bandeja.sinAsignar')
                        )}
                      </td>
                    )}
                    {activa && puedeGestionar && (
                      <td style={{ whiteSpace: 'nowrap' }}>
                        {editando === s.idSolicitud ? (
                          <>
                            <button type="button" className="enlace-fila" disabled={!seleccion} onClick={() => guardarAsignacion(s.idSolicitud)}>
                              {t('preinversion.registro.botonGuardar')}
                            </button>
                            {' · '}
                            <button type="button" className="enlace-fila" onClick={() => { setEditando(null); setSeleccion(''); }}>
                              {t('common.cancelar')}
                            </button>
                          </>
                        ) : (
                          <>
                            <button type="button" className="enlace-fila"
                              onClick={() => { setEditando(s.idSolicitud); setSeleccion(item.asignadoA ? String(item.asignadoA.idUsuario) : ''); }}>
                              {t('preinversion.bandeja.botonAsignar')}
                            </button>
                            {' · '}
                            <button type="button" className="enlace-fila" onClick={() => navigate(`/preinversion/proyectos/${s.idProyecto}`)}>
                              {t('preinversion.bandeja.botonAbrir')}
                            </button>
                            {' · '}
                            <button type="button" className="enlace-fila" onClick={() => archivar(item)}>
                              {t('preinversion.bandeja.botonArchivar')}
                            </button>
                          </>
                        )}
                      </td>
                    )}
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>

        {!error && !cargando && filas.length > 0 && (
          <p className="conteo-listado">
            {t('preinversion.registro.conteo', {
              desde: pagina * TAMANIO_PAGINA + 1,
              hasta: pagina * TAMANIO_PAGINA + filas.length,
              total: totalElementos,
            })}
          </p>
        )}
        {!error && (
          <Pagination currentPage={pagina} totalPages={totalPaginas}
            first={pagina <= 0} last={pagina >= totalPaginas - 1} onPageChange={cargar} />
        )}
      </div>

      {vista === 'activas' && conteo.length > 0 && (
        <>
          <h2 className="seccion">{t('preinversion.bandeja.tituloConteo')}</h2>
          <p className="nota">{t('preinversion.bandeja.notaConteo')}</p>
          <div className="tarjeta">
            <div className="tabla-cont">
              <table>
                <thead>
                  <tr>
                    <th>{t('preinversion.bandeja.columnaTecnico')}</th>
                    <th style={{ textAlign: 'right' }}>CUP</th>
                    <th style={{ textAlign: 'right' }}>{formatTipoSolicitud(TipoSolicitud.OpinionTecnica)}</th>
                    <th style={{ textAlign: 'right' }}>{t('preinversion.bandeja.columnaTotal')}</th>
                  </tr>
                </thead>
                <tbody>
                  {conteo.map((c) => (
                    <tr key={c.tecnico.idUsuario}>
                      <td>{c.tecnico.nombreCompleto}</td>
                      <td style={{ textAlign: 'right' }}>{c.cantidadCup}</td>
                      <td style={{ textAlign: 'right' }}>{c.cantidadOpinionTecnica}</td>
                      <td style={{ textAlign: 'right' }}><b>{c.cantidadCup + c.cantidadOpinionTecnica}</b></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}
    </>
  );
}
