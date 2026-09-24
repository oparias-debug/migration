import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { avanceMetasApi, type EstudioFilaAvanceMetas, type Cuatrimestre } from '../../../api/preinversionApi';
import { ListadoPAP, ejecutar, numero, porcentaje, type ColumnaPAP } from './papComun';
import { PanelRevision } from './PanelRevision';

const CLAVE = 'preinversion.avanceMetas';

/** Color del semáforo del avance (EstadoAvanceMetas). */
const TONO: Record<string, string> = {
  EN_TIEMPO: 'e-ok',
  EN_RIESGO: 'e-aviso',
  RETRASADO: 'e-error',
};

/**
 * "Informe de avance cuatrimestral de metas del PAP" (CU-PRE-33, Anexo A.1).
 *
 * El equivalente físico de CU-PRE-32, con el mismo ciclo de observaciones y
 * respuesta entre la DGICP y la institución que CU-PRE-31, pero por
 * cuatrimestre.
 */
export function AvanceMetasPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const columnas: ColumnaPAP<EstudioFilaAvanceMetas>[] = [
    { clave: 'cup', etiqueta: `${CLAVE}.columnaCup`, valor: (f) => f.cup },
    { clave: 'proyecto', etiqueta: `${CLAVE}.columnaProyecto`, valor: (f) => f.nombreProyecto },
    { clave: 'etapa', etiqueta: `${CLAVE}.columnaEtapa`, valor: (f) => String(f.etapa ?? '—') },
    { clave: 'meta', etiqueta: `${CLAVE}.columnaMeta`, valor: (f) => numero(f.meta), alineado: 'derecha' },
    { clave: 'entregable', etiqueta: `${CLAVE}.columnaEntregable`, valor: (f) => f.entregable ?? '—' },
    {
      clave: 'programadoAnio',
      etiqueta: `${CLAVE}.columnaProgramadoAnio`,
      valor: (f) => porcentaje(f.programadoEnElAnio),
      alineado: 'derecha',
    },
    {
      clave: 'ejecutadoAnio',
      etiqueta: `${CLAVE}.columnaEjecutadoAnio`,
      valor: (f) => porcentaje(f.ejecutadoEnElAnio),
      alineado: 'derecha',
    },
    {
      clave: 'programadoCuatri',
      etiqueta: `${CLAVE}.columnaProgramadoCuatri`,
      valor: (f) => porcentaje(f.programadoDelCuatrimestre),
      alineado: 'derecha',
    },
    {
      clave: 'ejecutadoCuatri',
      etiqueta: `${CLAVE}.columnaEjecutadoCuatri`,
      valor: (f) => porcentaje(f.ejecutadoDelCuatrimestre),
      alineado: 'derecha',
    },
    {
      clave: 'totalEjecutado',
      etiqueta: `${CLAVE}.columnaTotalEjecutado`,
      valor: (f) => porcentaje(f.totalMetaEjecutada),
      alineado: 'derecha',
    },
    {
      clave: 'estado',
      etiqueta: `${CLAVE}.columnaEstado`,
      valor: (f) =>
        f.estado ? (
          <span className={`marca-estado ${TONO[f.estado] ?? 'e-info'}`}>
            {t(`${CLAVE}.estadosAvance.${f.estado}`, { defaultValue: f.estado })}
          </span>
        ) : (
          '—'
        ),
    },
  ];

  const cargar = useCallback(async (p: { anio: number; periodo: string; pagina: number; tamanio: number }) => {
    const { data } = await avanceMetasApi.obtenerAvanceMetasFisicasPAP({
      anio: p.anio,
      periodo: p.periodo as Cuatrimestre,
      pagina: p.pagina,
      tamanio: p.tamanio,
    });
    return {
      contenido: data.contenido ?? [],
      totalPaginas: data.paginacion?.totalPaginas ?? 0,
      totalElementos: data.paginacion?.totalElementos ?? 0,
      pagina: data.paginacion?.pagina ?? p.pagina,
      idUnidadEjecutora: data.idUnidadEjecutora,
    };
  }, []);

  return (
    <ListadoPAP<EstudioFilaAvanceMetas>
      clave={CLAVE}
      columnas={columnas}
      cargar={cargar}
      conBusqueda={false}
      conPeriodo
      alAbrir={(fila, contexto) =>
        navigate(`/programacion/pap/avance-metas/${encodeURIComponent(fila.cup)}`, { state: contexto })
      }
      acciones={({ anio, periodo, idUnidadEjecutora }) => (
        <button
          type="button"
          className="btn secundario"
          onClick={() =>
            void ejecutar(
              () =>
                avanceMetasApi.generarReporteAvanceMetas({
                  anio,
                  periodo: periodo as Cuatrimestre,
                  idUnidadEjecutora: idUnidadEjecutora as number,
                  formato: 'EXCEL',
                }),
              `${CLAVE}.reporteGenerado`,
              t,
            )
          }
        >
          {t('preinversion.pap.generarReporte')}
        </button>
      )}
      pie={({ anio, periodo, idUnidadEjecutora, recargar }) => {
        const contexto = { anio, periodo: periodo as Cuatrimestre, idUnidadEjecutora: idUnidadEjecutora as number };
        return (
          <PanelRevision
            clave={CLAVE}
            alCambiar={recargar}
            acciones={{
              guardarObservaciones: (observacionesDgicp) =>
                avanceMetasApi.registrarObservacionesAvanceDgicp({
                  registrarObservacionesAvanceDgicpRequest: { ...contexto, observacionesDgicp },
                }),
              enviarObservaciones: () =>
                avanceMetasApi.enviarObservacionesAvanceDgicp({
                  enviarObservacionesAvanceDgicpRequest: contexto,
                }),
              guardarRespuesta: (respuestaInstitucion) =>
                avanceMetasApi.registrarRespuestaInstitucionAvance({
                  registrarRespuestaInstitucionAvanceRequest: { ...contexto, respuestaInstitucion },
                }),
              enviarRespuesta: () =>
                avanceMetasApi.enviarRespuestaInstitucionAvance({
                  enviarObservacionesAvanceDgicpRequest: contexto,
                }),
              finalizar: () =>
                avanceMetasApi.finalizarRevisionAvance({ finalizarRevisionAvanceRequest: contexto }),
            }}
          />
        );
      }}
    />
  );
}
