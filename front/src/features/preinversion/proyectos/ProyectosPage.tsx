import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { preinversionApi } from '../../../api/preinversionApi';
import type { ProyectoListItem } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { DataTable, type Column } from '../../../components/table/DataTable';
import { Pagination } from '../../../components/table/Pagination';
import { formatEstado, formatIniciativa } from './proyectoLabels';

const TAMANIO_PAGINA = 20;

// Bandeja "Registro de Proyecto" (Antecedentes de CU-PRE-01-registrar-nuevo-proyecto.feature).
export function ProyectosPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();

  const [proyectos, setProyectos] = useState<ProyectoListItem[]>([]);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [cargando, setCargando] = useState(true);
  // Un fallo del listado no puede verse igual que un listado vacío: sin esto,
  // un 500 se renderiza como "no hay registros".
  const [error, setError] = useState<string | null>(null);

  const cargar = useCallback(
    async (paginaSolicitada: number) => {
      setCargando(true);
      setError(null);
      try {
        const { data } = await preinversionApi.listarProyectos({ pagina: paginaSolicitada, tamanio: TAMANIO_PAGINA });
        setProyectos(data.contenido);
        setTotalPaginas(data.paginacion.totalPaginas);
        setPagina(data.paginacion.pagina);
      } catch (fallo) {
        setProyectos([]);
        setTotalPaginas(0);
        setError(mensajeDeError(toErrorApi(fallo), t));
      } finally {
        setCargando(false);
      }
    },
    [t],
  );

  useEffect(() => {
    cargar(0);
  }, [cargar]);

  const columns: Column<ProyectoListItem>[] = [
    {
      header: t('preinversion.registro.columnaNombre'),
      render: (proyecto) => (
        <button
          type="button"
          className="btn btn-link p-0 align-baseline"
          onClick={() => navigate(`/preinversion/proyectos/${proyecto.idProyecto}`)}
        >
          {proyecto.nombre}
        </button>
      ),
    },
    { header: t('preinversion.registro.columnaUnidadEjecutora'), render: (proyecto) => proyecto.unidadEjecutora.nombre },
    { header: t('preinversion.registro.columnaIniciativa'), render: (proyecto) => formatIniciativa(proyecto.iniciativaInversion) },
    {
      header: t('preinversion.registro.columnaFechaIngreso'),
      render: (proyecto) => new Date(proyecto.fechaIngreso).toLocaleDateString(),
    },
    { header: t('preinversion.registro.columnaEstado'), render: (proyecto) => formatEstado(proyecto.estado) },
  ];

  return (
    <>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h1>{t('preinversion.registro.tituloBandeja')}</h1>
        {hasRole('TECNICO_URP') && (
          <button type="button" className="btn btn-primary" onClick={() => navigate('/preinversion/proyectos/nuevo')}>
            {t('preinversion.registro.nuevoRegistro')}
          </button>
        )}
      </div>

      {error && (
        <div className="alert alert-danger d-flex justify-content-between align-items-center" role="alert">
          <span>{error}</span>
          <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => cargar(pagina)}>
            {t('errores.reintentar')}
          </button>
        </div>
      )}

      {cargando ? (
        <p>{t('common.cargando')}</p>
      ) : error ? null : (
        <DataTable columns={columns} rows={proyectos} emptyMessage={t('preinversion.registro.sinRegistros')} renderActions={() => null} />
      )}

      {!error && (
      <Pagination
        currentPage={pagina}
        totalPages={totalPaginas}
        first={pagina <= 0}
        last={pagina >= totalPaginas - 1}
        onPageChange={cargar}
      />
      )}
    </>
  );
}

