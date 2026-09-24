import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { ComentarioSolicitud } from '../../../api/preinversionApi';

interface RevisionPreProps {
  readonly comentarios: ComentarioSolicitud[];
  /** Sólo con el proyecto en OBSERVADO_DGICP_REGISTRO (RN 2.9). Técnico URP responde. */
  readonly puedeResponder: boolean;
  /**
   * Sólo con el proyecto en ENVIADO_DGICP_REGISTRO (CU-PRE-01.5-devolver.feature).
   * Técnico PRE devuelve la solicitud con una observación.
   */
  readonly puedeDevolver?: boolean;
  readonly enviando: boolean;
  /** Error de `campo: "respuesta"` devuelto por el back en un 400 (sólo aplica a Responder). */
  readonly errorRespuesta?: string;
  readonly onEnviar: (respuesta: string) => void;
  /**
   * El comentario no es obligatorio (ver CU-PRE-01.5, sección Validaciones):
   * puede llegar vacío. Devuelve `true` si la solicitud se devolvió de verdad;
   * si el actor canceló la confirmación, `false`, y el texto se conserva.
   */
  readonly onDevolver?: (comentario: string) => Promise<boolean>;
  /**
   * Guardar los cambios del formulario sin enviar la respuesta. Es el mismo
   * "Guardar" del pie de la pantalla, repetido aquí porque es donde está el
   * usuario cuando responde observaciones (Rocío, pruebas del 21/09/2026).
   */
  readonly onGuardar?: () => void;
  /** Identifica el borrador de la observación del Técnico PRE en este equipo. */
  readonly idProyecto?: number;
}

/** Clave del borrador local de la observación (ver `guardarBorrador`). */
const claveBorrador = (idProyecto?: number) => `siip.observacionPre.${idProyecto ?? 'sin-id'}`;

/**
 * Sección "Revisión PRE" de la pantalla "Nuevo registro"
 * (CU-PRE-01-responder-observaciones.feature, CU-PRE-01.5-devolver.feature).
 *
 * El contrato modela esto como UN solo hilo (`revisionPre`: lista de
 * `ComentarioSolicitud` con autor y fecha), no como dos campos separados
 * "comentario" y "respuesta": las observaciones del Técnico PRE y las respuestas
 * del Técnico URP son entradas del mismo historial, y se distinguen por el rol
 * de quien las escribió. Se presenta como conversación por eso. `puedeResponder`
 * (Técnico URP) y `puedeDevolver` (Técnico PRE) son mutuamente excluyentes en la
 * práctica: dependen de estados del proyecto distintos (OBSERVADO_DGICP_REGISTRO
 * vs. ENVIADO_DGICP_REGISTRO).
 */
export function RevisionPre({
  comentarios,
  puedeResponder,
  puedeDevolver,
  enviando,
  errorRespuesta,
  onEnviar,
  onDevolver,
  onGuardar,
  idProyecto,
}: RevisionPreProps) {
  const { t } = useTranslation();
  const [respuesta, setRespuesta] = useState('');
  const [comentario, setComentario] = useState('');
  const [borradorGuardado, setBorradorGuardado] = useState(false);

  // El contrato de CU-PRE-01.5 sólo tiene "devolver": no hay endpoint para
  // dejar una observación a medias en el servidor. Hasta que lo haya, el
  // borrador se conserva en este equipo, que es lo que evita perder el texto
  // al salir de la pantalla; el mensaje dice exactamente eso.
  useEffect(() => {
    if (!puedeDevolver) return;
    try {
      setComentario(localStorage.getItem(claveBorrador(idProyecto)) ?? '');
    } catch {
      /* Sin almacenamiento local (modo privado): se empieza en blanco. */
    }
  }, [puedeDevolver, idProyecto]);

  /**
   * Se devuelve sólo después de guardar, aunque la observación esté vacía
   * (Rocío, 24/09/2026: "¿lo pertinente no sería que primero se guarde y luego
   * se devuelva?").
   */
  const sinGuardar = !borradorGuardado;

  const guardarBorrador = () => {
    try {
      localStorage.setItem(claveBorrador(idProyecto), comentario);
      setBorradorGuardado(true);
    } catch {
      setBorradorGuardado(false);
    }
  };

  const enviar = () => {
    onEnviar(respuesta);
    setRespuesta('');
  };

  const devolver = async () => {
    // Si el actor cancela la confirmación, lo redactado se queda donde está:
    // borrarlo le costaba volver a escribir la observación entera
    // (Rocío, 24/09/2026).
    const devuelta = await onDevolver?.(comentario);
    if (!devuelta) return;
    setComentario('');
    setBorradorGuardado(false);
    try {
      localStorage.removeItem(claveBorrador(idProyecto));
    } catch {
      /* Nada que limpiar si no hay almacenamiento local. */
    }
  };

  return (
    <section className="revision-pre" aria-labelledby="revision-pre-titulo">
      <div className="rp-cabecera">
        <h2 id="revision-pre-titulo">{t('preinversion.revisionPre.titulo')}</h2>
      </div>

      <div className="rp-cuerpo">
        {comentarios.length === 0 ? (
          <p className="rp-vacio">{t('preinversion.revisionPre.sinComentarios')}</p>
        ) : (
          <ol className="rp-hilo">
            {comentarios.map((comentario) => {
              const esRespuestaDeLaUnidad = comentario.autor.rol === 'TECNICO_URP';
              return (
                <li
                  key={comentario.idComentario}
                  className={esRespuestaDeLaUnidad ? 'rp-mensaje de-urp' : 'rp-mensaje de-pre'}
                >
                  <div className="rp-quien">
                    <strong>{comentario.autor.nombreCompleto}</strong>
                    <small>
                      {comentario.autor.rol && <span>{comentario.autor.rol}</span>}
                      <time dateTime={comentario.fechaComentario}>
                        {new Date(comentario.fechaComentario).toLocaleString()}
                      </time>
                    </small>
                  </div>
                  <p className="rp-texto">
                    {comentario.texto}
                  </p>
                </li>
              );
            })}
          </ol>
        )}
      </div>

      {puedeResponder && (
        <div className="rp-pie">
          <label htmlFor="respuesta">{t('preinversion.revisionPre.campoRespuesta')}*</label>
          <textarea
            id="respuesta"
            className={errorRespuesta ? 'malo' : undefined}
            rows={3}
            value={respuesta}
            onChange={(evento) => setRespuesta(evento.target.value)}
            disabled={enviando}
          />
          {errorRespuesta && <span className="error">{errorRespuesta}</span>}
          <div className="rp-acciones">
            {onGuardar && (
              <button type="button" className="btn neutro" onClick={onGuardar} disabled={enviando}>
                {t('preinversion.registro.botonGuardar')}
              </button>
            )}
            <button type="button" className="btn primario" onClick={enviar} disabled={enviando}>
              {t('preinversion.revisionPre.botonEnviar')}
            </button>
          </div>
        </div>
      )}

      {puedeDevolver && (
        <div className="rp-pie">
          <label htmlFor="comentarioPre">{t('preinversion.revisionPre.campoComentarios')}</label>
          <textarea
            id="comentarioPre"
            rows={3}
            value={comentario}
            onChange={(evento) => { setComentario(evento.target.value); setBorradorGuardado(false); }}
            disabled={enviando}
          />
          {borradorGuardado && <output className="rp-aviso">{t('preinversion.revisionPre.borradorGuardado')}</output>}
          {/* Con la observación escrita y sin guardar, devolver la mandaría a
              medias: primero se guarda (Rocío, 24/09/2026). Sin observación —que
              el CU admite— se puede devolver directamente. */}
          {sinGuardar && <p className="rp-aviso">{t('preinversion.revisionPre.guardeAntesDeDevolver')}</p>}
          <div className="rp-acciones">
            <button type="button" className="btn neutro" onClick={guardarBorrador} disabled={enviando}>
              {t('preinversion.registro.botonGuardar')}
            </button>
            <button
              type="button"
              className="btn secundario"
              onClick={() => void devolver()}
              disabled={enviando || sinGuardar}
              title={sinGuardar ? t('preinversion.revisionPre.guardeAntesDeDevolver') : undefined}
            >
              {t('preinversion.revisionPre.botonDevolver')}
            </button>
          </div>
        </div>
      )}
    </section>
  );
}

