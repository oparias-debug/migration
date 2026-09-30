import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Swal from 'sweetalert2';
import {
  viabilidadRevisionApi,
  viabilidadUrpApi,
  TipoDocumentoViabilidad,
  type ComentarioCampoViabilidad,
  type FichaViabilidadResponse,
} from '../../../api/preinversionApi';
import { mensajeDeError, toErrorApi } from '../../../api/apiError';
import { FichaViabilidad } from './FichaViabilidad';
import { rutaAnterior } from '../pasos/pasosProyecto';

const CLAVE = 'preinversion.viabilidad';

const TIPOS = [TipoDocumentoViabilidad.DocumentoPreinversion, TipoDocumentoViabilidad.OtroDocumento] as const;

/**
 * "Viabilidad" (CU-PRE-24, Anexo A.1), paso 1.5.1 del proyecto.
 *
 * Reúne en una sola ficha lo ya registrado en los capítulos anteriores para que
 * se decida sobre el proyecto: el Técnico URP adjunta el Documento de
 * Preinversión y solicita la viabilidad; quien revisa comenta campo por campo,
 * devuelve con observaciones o emite la viabilidad.
 *
 * Qué botones se ofrecen no se deduce del rol en la pantalla: el servidor los
 * resuelve en `accionesDisponibles` según el rol y el estado de la gestión, y
 * aquí se respetan. Así no hay dos reglas que puedan discrepar.
 */
export function ViabilidadPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const proyectoId = Number(id);
  const archivoRef = useRef<HTMLInputElement>(null);

  const [ficha, setFicha] = useState<FichaViabilidadResponse | null>(null);
  const [comentarios, setComentarios] = useState<Record<string, string>>({});
  const [observaciones, setObservaciones] = useState('');
  const [tipoDocumento, setTipoDocumento] = useState<string>(TipoDocumentoViabilidad.DocumentoPreinversion);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState<string | null>(null);
  const [ocupado, setOcupado] = useState(false);

  const volcar = (datos: FichaViabilidadResponse) => {
    setFicha(datos);
    setComentarios(
      Object.fromEntries((datos.comentariosViabilizador ?? []).map((c) => [c.campo, c.comentario])),
    );
    setObservaciones(datos.observacionesGeneralesJustificacion ?? '');
  };

  const cargar = useCallback(async () => {
    const { data } = await viabilidadRevisionApi.consultarFichaViabilidad({ proyectoId });
    volcar(data);
  }, [proyectoId]);

  useEffect(() => {
    if (!proyectoId) return;
    cargar()
      .catch((error_) => setErrorCarga(mensajeDeError(toErrorApi(error_), t)))
      .finally(() => setCargando(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [proyectoId]);

  const acciones = ficha?.accionesDisponibles;

  /** Ejecuta una acción del servidor, avisa del resultado y recarga la ficha. */
  const ejecutar = async (accion: () => Promise<unknown>, mensajeExito: string) => {
    setOcupado(true);
    try {
      await accion();
      await cargar();
      await Swal.fire({ icon: 'success', text: t(mensajeExito) });
    } catch (error_) {
      await Swal.fire({ icon: 'error', text: mensajeDeError(toErrorApi(error_), t) });
    } finally {
      setOcupado(false);
    }
  };

  const cuerpoComentarios = () => ({
    comentariosViabilizador: Object.entries(comentarios)
      .filter(([, texto]) => texto.trim() !== '')
      .map(([campo, comentario]) => ({ campo, comentario })) as ComentarioCampoViabilidad[],
    observacionesGeneralesJustificacion: observaciones.trim() || undefined,
  });

  const subirDocumento = async () => {
    const archivo = archivoRef.current?.files?.[0];
    if (!archivo) {
      await Swal.fire({ icon: 'info', text: t(`${CLAVE}.elijaArchivo`) });
      return;
    }
    await ejecutar(
      () =>
        viabilidadUrpApi.cargarDocumentoViabilidad({
          proyectoId,
          tipoDocumento: tipoDocumento as TipoDocumentoViabilidad,
          archivo,
        }),
      `${CLAVE}.documentoCargado`,
    );
    if (archivoRef.current) archivoRef.current.value = '';
  };

  const confirmarY = async (pregunta: string, accion: () => Promise<unknown>, exito: string) => {
    const respuesta = await Swal.fire({
      icon: 'warning',
      text: t(pregunta),
      showCancelButton: true,
      confirmButtonText: t('common.aceptar'),
      cancelButtonText: t('common.cancelar'),
    });
    if (respuesta.isConfirmed) await ejecutar(accion, exito);
  };

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t(`${CLAVE}.titulo`)}</span>
        {ficha && (
          <span className="mono">
            · {ficha.cup} · {ficha.estadoProyecto}
          </span>
        )}
      </div>
      <div className="formbody">
        {errorCarga && (
          <div className="aviso-error" role="alert">
            {errorCarga}
          </div>
        )}
        {cargando && <p className="cargando">{t('common.cargando')}</p>}

        {!cargando && !errorCarga && ficha && (
          <>
            <p className="nota">{ficha.nombreProyecto}</p>

            <FichaViabilidad
              ficha={ficha}
              comentarios={comentarios}
              puedeComentar={Boolean(acciones?.guardarComentarios)}
              onComentar={(campo, texto) => setComentarios((antes) => ({ ...antes, [campo]: texto }))}
            />

            <section>
              <h2 className="seccion">{t(`${CLAVE}.documentos`)}</h2>
              <div className="tabla-cont">
                <table className="tabla-datos">
                  <thead>
                    <tr>
                      <th>{t(`${CLAVE}.columnaTipo`)}</th>
                      <th>{t(`${CLAVE}.columnaArchivo`)}</th>
                      <th>{t(`${CLAVE}.columnaFecha`)}</th>
                    </tr>
                  </thead>
                  <tbody>
                    {(ficha.documentos ?? []).length === 0 && (
                      <tr>
                        <td className="vacio" colSpan={3}>
                          {t(`${CLAVE}.sinDocumentos`)}
                        </td>
                      </tr>
                    )}
                    {(ficha.documentos ?? []).map((d) => (
                      <tr key={d.documentoId}>
                        <td>{t(`${CLAVE}.tipos.${d.tipoDocumento}`)}</td>
                        <td>{d.nombreArchivo}</td>
                        <td>{new Date(d.fechaCarga).toLocaleDateString('es-SV')}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Cargar documentos es del Técnico URP; se ofrece cuando puede solicitar. */}
              {acciones?.solicitarViabilidad !== undefined && acciones.solicitarViabilidad && (
                <div className="filtros">
                  <div className="campo">
                    <label htmlFor="via-tipo">{t(`${CLAVE}.columnaTipo`)}</label>
                    <select id="via-tipo" value={tipoDocumento} onChange={(e) => setTipoDocumento(e.target.value)}>
                      {TIPOS.map((tipo) => (
                        <option key={tipo} value={tipo}>
                          {t(`${CLAVE}.tipos.${tipo}`)}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="campo crece">
                    <label htmlFor="via-archivo">{t(`${CLAVE}.archivo`)}</label>
                    <input id="via-archivo" type="file" ref={archivoRef} />
                  </div>
                  <div className="campo">
                    <button type="button" className="btn secundario" disabled={ocupado} onClick={() => void subirDocumento()}>
                      {t(`${CLAVE}.cargarDocumento`)}
                    </button>
                  </div>
                </div>
              )}
            </section>

            <section>
              <h2 className="seccion">{t(`${CLAVE}.observaciones`)}</h2>
              <div className="campo">
                <label htmlFor="via-observaciones">{t(`${CLAVE}.observacionesJustificacion`)}</label>
                <textarea
                  id="via-observaciones"
                  rows={3}
                  value={observaciones}
                  disabled={!acciones?.guardarComentarios}
                  onChange={(e) => setObservaciones(e.target.value)}
                />
              </div>
            </section>
          </>
        )}

        <div className="acciones-form">
          <button
            type="button"
            className="btn neutro"
            onClick={() => navigate(rutaAnterior(proyectoId, 'viabilidad'))}
          >
            {t('preinversion.registro.botonRegresar')}
          </button>

          {acciones?.guardarComentarios && (
            <button
              type="button"
              className="btn neutro"
              disabled={ocupado}
              onClick={() =>
                void ejecutar(
                  () =>
                    viabilidadRevisionApi.guardarComentariosViabilidad({
                      proyectoId,
                      guardarComentariosViabilidadRequest: cuerpoComentarios(),
                    }),
                  `${CLAVE}.comentariosGuardados`,
                )
              }
            >
              {t('preinversion.registro.botonGuardar')}
            </button>
          )}

          {acciones?.solicitarViabilidad && (
            <button
              type="button"
              className="btn primario"
              disabled={ocupado}
              onClick={() =>
                void confirmarY(
                  `${CLAVE}.confirmarSolicitar`,
                  () => viabilidadUrpApi.solicitarViabilidad({ proyectoId }),
                  `${CLAVE}.solicitada`,
                )
              }
            >
              {t(`${CLAVE}.solicitar`)}
            </button>
          )}

          {acciones?.enviarComentarios && (
            <button
              type="button"
              className="btn secundario"
              disabled={ocupado}
              onClick={() =>
                void confirmarY(
                  `${CLAVE}.confirmarEnviar`,
                  () => viabilidadRevisionApi.enviarComentariosViabilidad({ proyectoId }),
                  `${CLAVE}.comentariosEnviados`,
                )
              }
            >
              {t(`${CLAVE}.enviarComentarios`)}
            </button>
          )}

          {acciones?.emitirViabilidad && (
            <button
              type="button"
              className="btn primario"
              disabled={ocupado}
              onClick={() =>
                void confirmarY(
                  `${CLAVE}.confirmarEmitir`,
                  () => viabilidadRevisionApi.emitirViabilidad({ proyectoId }),
                  `${CLAVE}.emitida`,
                )
              }
            >
              {t(`${CLAVE}.emitir`)}
            </button>
          )}

          {acciones?.irAElegibilidad && (
            <button
              type="button"
              className="btn primario"
              onClick={() => navigate(`/preinversion/proyectos/${proyectoId}/elegibilidad`)}
            >
              {t(`${CLAVE}.irAElegibilidad`)}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
