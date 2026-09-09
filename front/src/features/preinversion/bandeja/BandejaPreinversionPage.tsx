import { useCallback, useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../../auth/useAuth';
import { bandejaApi, tecnicosPreApi } from '../../../api/bandejaPreinversionApi';
import type { SolicitudActivaItem, SolicitudArchivadaItem, ConteoTecnicoPre, UsuarioResumen, TipoSolicitud } from '../../../api/bandejaPreinversionApi';
import { confirmDialog } from '../../../components/ConfirmDialog';
import { Pagination } from '../../../components/table/Pagination';
import './bandeja.css';

type Fila = SolicitudActivaItem | SolicitudArchivadaItem;
export const rutaCaso = (solicitud: Pick<Fila, 'tipoSolicitud' | 'idProyecto'>) => solicitud.tipoSolicitud === 'CUP'
  ? `/preinversion/proyectos/${solicitud.idProyecto}`
  : `/preinversion/opinion-tecnica/${solicitud.idProyecto}`;
const fecha = (value: string) => new Date(value).toLocaleDateString('es-SV', { timeZone: 'America/El_Salvador' });

export function BandejaPreinversionPage({ archivadas = false }: { archivadas?: boolean }) {
  const { hasRole } = useAuth();
  const coordinador = hasRole('COORDINADOR_PRE');
  const permitido = coordinador || (!archivadas && hasRole('TECNICO_PRE'));
  const [filas, setFilas] = useState<Fila[]>([]);
  const [tecnicos, setTecnicos] = useState<UsuarioResumen[]>([]);
  const [conteos, setConteos] = useState<ConteoTecnicoPre[]>([]);
  const [tipo, setTipo] = useState<TipoSolicitud | ''>('');
  const [pagina, setPagina] = useState(0);
  const [total, setTotal] = useState(0);
  const [seleccion, setSeleccion] = useState<Record<number, string>>({});
  const [cargando, setCargando] = useState(false);
  const [ocupado, setOcupado] = useState(false);
  const [error, setError] = useState('');
  const [aviso, setAviso] = useState('');
  const revision = useRef(0);
  const accionActiva = useRef(false);

  const cargar = useCallback(async () => {
    if (!permitido) return;
    const version = ++revision.current;
    setCargando(true);
    setError('');
    try {
      const params = { tipoSolicitud: tipo || undefined, pagina, tamanio: 20 };
      const response = archivadas ? await bandejaApi.listarSolicitudesArchivadas(params)
        : await bandejaApi.listarSolicitudesActivas(params);
      if (version !== revision.current) return;
      setFilas(response.data.contenido);
      setTotal(response.data.paginacion.totalPaginas);
      setConteos('conteoPorTecnico' in response.data ? response.data.conteoPorTecnico : []);
      setSeleccion({});
      if (!response.data.contenido.length && pagina > 0) setPagina(pagina - 1);
    } catch {
      if (version === revision.current) {
        setError('No se pudieron cargar las solicitudes. Intente nuevamente.');
        setFilas([]);
        setConteos([]);
        setTotal(0);
      }
    } finally {
      if (version === revision.current) setCargando(false);
    }
  }, [archivadas, pagina, permitido, tipo]);

  useEffect(() => { void cargar(); return () => { revision.current++; }; }, [cargar]);
  useEffect(() => {
    if (!coordinador || archivadas) return;
    let vigente = true;
    tecnicosPreApi.listarTecnicosPre().then(({ data }) => { if (vigente) setTecnicos(data); })
      .catch(() => { if (vigente) setError('No se pudo cargar el catálogo de Técnicos PRE.'); });
    return () => { vigente = false; };
  }, [coordinador, archivadas]);

  async function actuar(s: SolicitudActivaItem, archivo: boolean) {
    if (accionActiva.current) return;
    accionActiva.current = true;
    setOcupado(true);
    setAviso('');
    try {
      const confirmado = await confirmDialog(archivo ? '¿Está seguro de archivar esta solicitud?'
        : '¿Está seguro de asignar esta solicitud?', { confirmButtonText: 'Aceptar', cancelButtonText: 'Cancelar' });
      if (!confirmado) {
        setSeleccion(prev => { const next = { ...prev }; delete next[s.idSolicitud]; return next; });
        return;
      }
      if (archivo) await bandejaApi.archivarSolicitud({ idSolicitud: s.idSolicitud });
      else await bandejaApi.asignarTecnicoPre({ idSolicitud: s.idSolicitud,
        asignacionTecnicoPreRequest: { idTecnicoAsignado: Number(seleccion[s.idSolicitud]) } });
      setAviso(archivo ? 'Solicitud archivada.' : 'Asignación guardada.');
      await cargar();
    } catch {
      setError('No se pudo completar la operación. Compruebe sus permisos e intente nuevamente.');
    } finally { accionActiva.current = false; setOcupado(false); }
  }

  if (!permitido) return <div role="alert">No tiene permiso para consultar esta bandeja.</div>;

  return <section aria-labelledby="bandeja-titulo">
    <h1 id="bandeja-titulo">{archivadas ? 'Reporte de solicitudes Preinversión archivadas' : 'Bandeja Preinversión'}</h1>
    {coordinador && <nav className="mb-3" aria-label="Bandeja">
      <Link to="/preinversion/bandeja">Solicitudes Activas</Link>{' · '}
      <Link to="/preinversion/bandeja/archivadas">Solicitudes archivadas</Link>
    </nav>}
    {!archivadas && <h2>Solicitudes Activas</h2>}
    <label htmlFor="tipo-solicitud">Tipo de solicitud</label>
    <select id="tipo-solicitud" className="form-select mb-3" value={tipo} disabled={ocupado}
      onChange={e => { setTipo(e.target.value as TipoSolicitud | ''); setPagina(0); }}>
      <option value="">Todas</option><option value="CUP">CUP</option><option value="OPINION_TECNICA">Opinión Técnica</option>
    </select>
    {error && <div role="alert" className="alert alert-danger">{error} <button onClick={() => void cargar()}>Reintentar</button></div>}
    {aviso && <div role="status" className="alert alert-success">{aviso}</div>}
    {cargando ? <p role="status">Cargando solicitudes…</p> : <div className="table-responsive">
      <table className="table table-striped align-middle bandeja-pre">
        <thead><tr>{['Unidad Ejecutora', 'Tipo de Solicitud', 'CUP', 'Nombre del Proyecto', 'Fecha de Solicitud',
          archivadas ? 'Estado de la solicitud' : 'Estado', archivadas ? 'Fecha de Archivo' : 'Asignado a']
          .map(c => <th key={c} scope="col">{c}</th>)}</tr></thead>
        <tbody>{filas.map(s => {
          const activa = 'estado' in s ? s : undefined;
          return <tr key={s.idSolicitud}>
            <td>{s.unidadEjecutora.nombre}</td><td>{s.tipoSolicitud === 'CUP' ? 'CUP' : 'Opinión Técnica'}</td>
            <td>{s.cup ?? ''}</td><td className="nombre-solicitud">
              {coordinador && activa && <button className="btn btn-outline-secondary btn-sm archivar me-2" disabled={ocupado}
                aria-label={`Archivar ${s.nombreProyecto}`} onClick={() => void actuar(activa, true)}>Archivar</button>}
              {!coordinador && activa ? <Link to={rutaCaso(s)}>{s.nombreProyecto}</Link> : s.nombreProyecto}
            </td><td>{fecha(s.fechaSolicitud)}</td>
            <td>{activa ? (activa.estado === 'OBSERVADO_DGICP_REGISTRO' ? 'Observado DGICP' : 'Enviado a DGICP') : 'Archivado'}</td>
            <td>{activa ? coordinador ? <div className="d-flex gap-2">
              <select aria-label={`Asignado a ${s.nombreProyecto}`} className="form-select" disabled={ocupado}
                value={seleccion[s.idSolicitud] ?? String(activa.asignadoA?.idUsuario ?? '')}
                onChange={e => setSeleccion(prev => ({ ...prev, [s.idSolicitud]: e.target.value }))}>
                <option value="">Sin asignar</option>
                {activa.asignadoA && !tecnicos.some(t => t.idUsuario === activa.asignadoA?.idUsuario) &&
                  <option value={activa.asignadoA.idUsuario}>{activa.asignadoA.nombreCompleto}</option>}
                {tecnicos.map(t => <option key={t.idUsuario} value={t.idUsuario}>{t.nombreCompleto}</option>)}
              </select>
              <button className="btn btn-primary" disabled={ocupado || !seleccion[s.idSolicitud] ||
                seleccion[s.idSolicitud] === String(activa.asignadoA?.idUsuario ?? '')}
                onClick={() => void actuar(activa, false)}>Guardar</button>
            </div> : activa.asignadoA?.nombreCompleto ?? 'Sin asignar'
              : 'fechaArchivo' in s ? fecha(s.fechaArchivo) : ''}</td>
          </tr>;
        })}</tbody>
      </table>{!filas.length && !error && <p>No hay solicitudes para mostrar.</p>}
    </div>}
    {!cargando && <Pagination currentPage={pagina} totalPages={total} first={pagina === 0} last={pagina >= total - 1}
      onPageChange={p => { if (!ocupado) setPagina(p); }} />}
    {coordinador && !archivadas && <table className="table" aria-label="Conteo de casos por Técnico PRE">
      <caption>Casos activos asignados por Técnico PRE (todos los tipos)</caption>
      <thead><tr><th scope="col">Tipo de solicitud</th>{conteos.map(c => <th scope="col" key={c.tecnico.idUsuario}>{c.tecnico.nombreCompleto}</th>)}</tr></thead>
      <tbody><tr><th scope="row">CUP</th>{conteos.map(c => <td key={c.tecnico.idUsuario}>{c.cantidadCup}</td>)}</tr>
        <tr><th scope="row">Opinión Técnica</th>{conteos.map(c => <td key={c.tecnico.idUsuario}>{c.cantidadOpinionTecnica}</td>)}</tr></tbody>
    </table>}
  </section>;
}
