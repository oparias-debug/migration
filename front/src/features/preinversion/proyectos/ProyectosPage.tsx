import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import { preinversionApi, EstadoProyecto } from '../../../api/preinversionApi';
import type { ProyectoListItem } from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { useAuth } from '../../../auth/useAuth';
import { DataTable, type Column } from '../../../components/table/DataTable';
import { Pagination } from '../../../components/table/Pagination';
import { formatEstado, formatIniciativa } from './proyectoLabels';

const TAMANIO_PAGINA = 20;

// Estados de CU-PRE-01; son los que puede devolver esta bandeja.
const ESTADOS_FILTRABLES = [
  EstadoProyecto.EnRegistro,
  EstadoProyecto.EnviadoDgicpRegistro,
  EstadoProyecto.ObservadoDgicpRegistro,
  EstadoProyecto.CupAsignado,
] as const;

// Color de la píldora de estado, como en el diseño aprobado.
const TONO_ESTADO: Partial<Record<string, string>> = {
  [EstadoProyecto.EnRegistro]: 'e-neutro',
  [EstadoProyecto.EnviadoDgicpRegistro]: 'e-info',
  [EstadoProyecto.ObservadoDgicpRegistro]: 'e-aviso',
  [EstadoProyecto.CupAsignado]: 'e-ok',
};

/**
 * Columnas y filtros del diseño que el contrato todavía no soporta.
 * ProyectoListItem sólo trae idProyecto, nombre, unidadEjecutora,
 * iniciativaInversion, fechaIngreso y estado; y listarProyectos sólo acepta
 * `estado` como filtro.
 */
const SIN_RESPALDO_LISTADO = [
  'Columna CUP',
  'Columna "Asignado a"',
  'Búsqueda por texto',
  'Filtro por tipo de proyecto',
  'Filtro por fecha desde',
];

/**
 * RN 4: un registro sólo puede eliminarse mientras nunca haya solicitado el CUP.
 * Solicitar el CUP lo saca de "En Registro" (CU-PRE-01-solicitar-cup.feature:
 * "el proyecto pasa al estado Enviado a DGICP (Registro)"), así que ese estado
 * es exactamente la condición de "nunca ha solicitado CUP".
 *
 * Es sólo para decidir si se pinta el botón: quien manda sobre la regla es el
 * servidor, que responde 409 y así se trata más abajo.
 */
const puedeEliminarse = (proyecto: ProyectoListItem): boolean =>
  proyecto.estado === EstadoProyecto.EnRegistro;

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
  // `estado` es el ÚNICO filtro del contrato. Los demás del diseño (texto,
  // tipo, fecha desde) no existen como parámetro de listarProyectos.
  const [estado, setEstado] = useState<EstadoProyecto | ''>('');
  const [totalElementos, setTotalElementos] = useState(0);

  const cargar = useCallback(
    async (paginaSolicitada: number) => {
      setCargando(true);
      setError(null);
      try {
        const { data } = await preinversionApi.listarProyectos({
          pagina: paginaSolicitada,
          tamanio: TAMANIO_PAGINA,
          ...(estado ? { estado } : {}),
        });
        setProyectos(data.contenido);
        setTotalPaginas(data.paginacion.totalPaginas);
        setTotalElementos(data.paginacion.totalElementos);
        setPagina(data.paginacion.pagina);
      } catch (fallo) {
        setProyectos([]);
        setTotalPaginas(0);
        setError(mensajeDeError(toErrorApi(fallo), t));
      } finally {
        setCargando(false);
      }
    },
    [t, estado],
  );

  useEffect(() => {
    cargar(0);
  }, [cargar]);

  // Eliminar un registro antes de la primera solicitud de CUP
  // (CU-PRE-01-eliminar-registro.feature).
  const eliminar = async (proyecto: ProyectoListItem) => {
    const { isConfirmed } = await Swal.fire({
      text: t('preinversion.registro.confirmarEliminar', { nombre: proyecto.nombre }),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: t('common.aceptar'),
      cancelButtonText: t('common.cancelar'),
    });
    if (!isConfirmed) return;

    try {
      await preinversionApi.eliminarProyecto({ idProyecto: proyecto.idProyecto });
      Swal.fire({ icon: 'success', text: t('preinversion.registro.eliminado') });
      // Se recarga la página actual: el registro "deja de aparecer" en la
      // bandeja, que es justo lo que pide el escenario.
      cargar(pagina);
    } catch (fallo) {
      const apiError = toErrorApi(fallo);
      // 'conflicto' es el 409 del contrato: RN 4, ya solicitó el CUP. El motivo
      // concreto importa más que el texto genérico de error.
      Swal.fire({
        icon: 'error',
        text:
          apiError.clase === 'conflicto'
            ? t('preinversion.registro.eliminarNoPermitido')
            : mensajeDeError(apiError, t),
      });
    }
  };

  const columns: Column<ProyectoListItem>[] = [
    {
      header: t('preinversion.registro.columnaNombre'),
      render: (proyecto) => (
        <button
          type="button"
          className="enlace-fila"
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
    {
      header: t('preinversion.registro.columnaEstado'),
      render: (proyecto) => (
        <span className={`marca-estado ${TONO_ESTADO[proyecto.estado] ?? ''}`}>{formatEstado(proyecto.estado)}</span>
      ),
    },
  ];

  return (
    <>
      {error && (
        <div className="aviso-error" role="alert">
          <span>{error}</span>
          <button type="button" className="btn neutro" onClick={() => cargar(pagina)}>
            {t('errores.reintentar')}
          </button>
        </div>
      )}

      <div className="tarjeta">
        <div className="filtros">
          <div className="campo crece">
            <label htmlFor="f-estado">{t('preinversion.registro.filtroEstado')}</label>
            <select
              id="f-estado"
              value={estado}
              onChange={(e) => setEstado(e.target.value as EstadoProyecto | '')}
            >
              <option value="">{t('preinversion.registro.filtroTodos')}</option>
              {ESTADOS_FILTRABLES.map((e) => (
                <option key={e} value={e}>
                  {formatEstado(e)}
                </option>
              ))}
            </select>
          </div>
          {hasRole('TECNICO_URP') && (
            <div className="campo">
              <button type="button" className="btn primario" onClick={() => navigate('/preinversion/proyectos/nuevo')}>
                {t('preinversion.registro.nuevoRegistro')}
              </button>
            </div>
          )}
        </div>

        {cargando ? (
          <p className="cargando">{t('common.cargando')}</p>
        ) : error ? null : (
          <DataTable
            columns={columns}
            rows={proyectos}
            emptyMessage={t('preinversion.registro.sinRegistros')}
            /* Sólo el Técnico URP elimina, y sólo lo que nunca solicitó CUP:
               "el botón de eliminar no está disponible para otros actores". */
            renderActions={(proyecto) =>
              hasRole('TECNICO_URP') && puedeEliminarse(proyecto) ? (
                <button type="button" className="btn secundario" onClick={() => eliminar(proyecto)}>
                  {t('preinversion.registro.accionEliminar')}
                </button>
              ) : null
            }
          />
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
            onPageChange={cargar}
          />
        )}
      </div>

      {/* Columnas y filtros que están en el diseño pero no en el contrato. Se
          listan en vez de dibujarlos vacíos. */}
      <div className="sin-respaldo">
        <b>{t('preinversion.registro.sinRespaldoListado')}</b>
        <ul>
          {SIN_RESPALDO_LISTADO.map((campo) => (
            <li key={campo}>{campo}</li>
          ))}
        </ul>
        <span>{t('preinversion.registro.sinRespaldoListadoNota')}</span>
      </div>
    </>
  );
}

