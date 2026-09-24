import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { programacionMetasApi, type EstudioFilaMetasFisicas } from '../../../api/preinversionApi';
import { ListadoPAP, ejecutar, numero, porcentaje, type ColumnaPAP } from './papComun';
import { PanelRevision } from './PanelRevision';

const CLAVE = 'preinversion.programacionMetas';

/**
 * "Programación por Meta Física Cuatrimestral del PAP" (CU-PRE-31, Anexo A.1).
 *
 * Hermana de CU-PRE-30: misma pantalla y mismo año, pero en porcentaje de meta
 * en lugar de monto. El envío a revisión de la DGICP es uno solo para los dos
 * documentos y vive aquí (decisión funcional v1.2 del contrato).
 */
export function ProgramacionMetasPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const columnas: ColumnaPAP<EstudioFilaMetasFisicas>[] = [
    { clave: 'cup', etiqueta: `${CLAVE}.columnaCup`, valor: (f) => f.cup },
    { clave: 'proyecto', etiqueta: `${CLAVE}.columnaProyecto`, valor: (f) => f.nombreProyecto },
    { clave: 'etapa', etiqueta: `${CLAVE}.columnaEtapa`, valor: (f) => String(f.etapa ?? '—') },
    { clave: 'meta', etiqueta: `${CLAVE}.columnaMeta`, valor: (f) => numero(f.metaTotal), alineado: 'derecha' },
    { clave: 'entregable', etiqueta: `${CLAVE}.columnaEntregable`, valor: (f) => String(f.entregable ?? '—') },
    {
      clave: 'previo',
      etiqueta: `${CLAVE}.columnaEjecutadoPrevio`,
      valor: (f) => porcentaje(f.ejecutadoAniosAnteriores),
      alineado: 'derecha',
    },
    { clave: 'total', etiqueta: `${CLAVE}.columnaTotalAnio`, valor: (f) => porcentaje(f.totalAnio), alineado: 'derecha' },
    {
      clave: 'posteriores',
      etiqueta: `${CLAVE}.columnaPosteriores`,
      valor: (f) => porcentaje(f.aniosPosteriores),
      alineado: 'derecha',
    },
    // RN-C: sólo lo reciben los actores internos de la DGICP; para el Técnico URP llega nulo.
    { clave: 'comentarios', etiqueta: `${CLAVE}.columnaComentarios`, valor: (f) => f.comentariosReporteDgicp ?? '—' },
  ];

  const cargar = useCallback(async (p: { anio: number; pagina: number; tamanio: number }) => {
    const { data } = await programacionMetasApi.obtenerProgramacionMetasFisicasPAP(p);
    return {
      contenido: data.contenido ?? [],
      totalPaginas: data.paginacion?.totalPaginas ?? 0,
      totalElementos: data.paginacion?.totalElementos ?? 0,
      pagina: data.paginacion?.pagina ?? p.pagina,
      idUnidadEjecutora: data.idUnidadEjecutora,
    };
  }, []);

  return (
    <ListadoPAP<EstudioFilaMetasFisicas>
      clave={CLAVE}
      columnas={columnas}
      cargar={cargar}
      conBusqueda={false}
      alAbrir={(fila, contexto) =>
        navigate(`/programacion/pap/programacion-metas/${encodeURIComponent(fila.cup)}`, { state: contexto })
      }
      acciones={({ anio, idUnidadEjecutora, recargar }) => (
        <>
          <button
            type="button"
            className="btn secundario"
            onClick={() =>
              void ejecutar(
                () =>
                  programacionMetasApi.generarReporteMetasFisicas({
                    anio,
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
          {/* Único envío a revisión del PAP institucional: cubre CU-PRE-30 y CU-PRE-31. */}
          <button
            type="button"
            className="btn primario"
            onClick={() =>
              void ejecutar(
                () =>
                  programacionMetasApi.enviarProgramacionARevisionDgicp({
                    enviarProgramacionARevisionDgicpRequest: {
                      anio,
                      idUnidadEjecutora: idUnidadEjecutora as number,
                    },
                  }),
                `${CLAVE}.enviadoARevision`,
                t,
                recargar,
              )
            }
          >
            {t('preinversion.pap.enviarRevision')}
          </button>
          <button
            type="button"
            className="btn neutro"
            onClick={() =>
              void ejecutar(
                () =>
                  programacionMetasApi.habilitarModificacionesMetasFueraPlazo({
                    enviarProgramacionARevisionDgicpRequest: {
                      anio,
                      idUnidadEjecutora: idUnidadEjecutora as number,
                    },
                  }),
                `${CLAVE}.plazoHabilitado`,
                t,
                recargar,
              )
            }
          >
            {t('preinversion.pap.habilitarPlazo')}
          </button>
        </>
      )}
      pie={({ anio, idUnidadEjecutora, recargar }) => (
        <PanelRevision
          clave={CLAVE}
          alCambiar={recargar}
          acciones={{
            guardarObservaciones: (observacionesDgicp) =>
              programacionMetasApi.registrarObservacionesDgicp({
                registrarObservacionesDgicpRequest: {
                  anio,
                  idUnidadEjecutora: idUnidadEjecutora as number,
                  observacionesDgicp,
                },
              }),
            enviarObservaciones: () =>
              programacionMetasApi.enviarObservacionesDgicp({
                enviarProgramacionARevisionDgicpRequest: { anio, idUnidadEjecutora: idUnidadEjecutora as number },
              }),
            guardarRespuesta: (respuestaInstitucion) =>
              programacionMetasApi.registrarRespuestaInstitucion({
                registrarRespuestaInstitucionRequest: {
                  anio,
                  idUnidadEjecutora: idUnidadEjecutora as number,
                  respuestaInstitucion,
                },
              }),
            enviarRespuesta: () =>
              programacionMetasApi.enviarRespuestaInstitucion({
                enviarProgramacionARevisionDgicpRequest: { anio, idUnidadEjecutora: idUnidadEjecutora as number },
              }),
            finalizar: () =>
              programacionMetasApi.finalizarRevision({
                finalizarRevisionRequest: { anio, idUnidadEjecutora: idUnidadEjecutora as number },
              }),
          }}
        />
      )}
    />
  );
}
