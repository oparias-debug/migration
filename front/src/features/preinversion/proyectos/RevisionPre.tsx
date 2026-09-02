import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { ComentarioSolicitud } from '../../../api/preinversionApi';

interface RevisionPreProps {
  comentarios: ComentarioSolicitud[];
  /** Sólo con el proyecto en OBSERVADO_DGICP_REGISTRO (RN 2.9). Técnico URP responde. */
  puedeResponder: boolean;
  /**
   * Sólo con el proyecto en ENVIADO_DGICP_REGISTRO (CU-PRE-01.5-devolver.feature).
   * Técnico PRE devuelve la solicitud con una observación.
   */
  puedeDevolver?: boolean;
  enviando: boolean;
  /** Error de `campo: "respuesta"` devuelto por el back en un 400 (sólo aplica a Responder). */
  errorRespuesta?: string;
  onEnviar: (respuesta: string) => void;
  /** El comentario no es obligatorio (ver CU-PRE-01.5, sección Validaciones): puede llegar vacío. */
  onDevolver?: (comentario: string) => void;
}

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
}: RevisionPreProps) {
  const { t } = useTranslation();
  const [respuesta, setRespuesta] = useState('');
  const [comentario, setComentario] = useState('');

  const enviar = () => {
    onEnviar(respuesta);
    setRespuesta('');
  };

  const devolver = () => {
    onDevolver?.(comentario);
    setComentario('');
  };

  return (
    <section className="card mb-4" aria-labelledby="revision-pre-titulo">
      <div className="card-header">
        <h2 id="revision-pre-titulo" className="h6 mb-0">
          {t('preinversion.revisionPre.titulo')}
        </h2>
      </div>

      <div className="card-body">
        {comentarios.length === 0 ? (
          <p className="text-muted mb-0">{t('preinversion.revisionPre.sinComentarios')}</p>
        ) : (
          <ol className="list-unstyled mb-0">
            {comentarios.map((comentario) => {
              const esRespuestaDeLaUnidad = comentario.autor.rol === 'TECNICO_URP';
              return (
                <li
                  key={comentario.idComentario}
                  className={`border-start border-3 ps-3 mb-3 ${
                    esRespuestaDeLaUnidad ? 'border-secondary' : 'border-warning'
                  }`}
                >
                  <div className="d-flex justify-content-between align-items-baseline gap-2 flex-wrap">
                    <strong>{comentario.autor.nombreCompleto}</strong>
                    <small className="text-muted">
                      {comentario.autor.rol && <span className="me-2">{comentario.autor.rol}</span>}
                      <time dateTime={comentario.fechaComentario}>
                        {new Date(comentario.fechaComentario).toLocaleString()}
                      </time>
                    </small>
                  </div>
                  <p className="mb-0" style={{ whiteSpace: 'pre-wrap' }}>
                    {comentario.texto}
                  </p>
                </li>
              );
            })}
          </ol>
        )}
      </div>

      {puedeResponder && (
        <div className="card-footer bg-white">
          <label className="form-label" htmlFor="respuesta">
            {t('preinversion.revisionPre.campoRespuesta')}*
          </label>
          <textarea
            id="respuesta"
            className={`form-control${errorRespuesta ? ' is-invalid' : ''}`}
            rows={3}
            value={respuesta}
            onChange={(evento) => setRespuesta(evento.target.value)}
            disabled={enviando}
          />
          {errorRespuesta && <div className="invalid-feedback d-block">{errorRespuesta}</div>}
          <button type="button" className="btn btn-primary mt-2" onClick={enviar} disabled={enviando}>
            {t('preinversion.revisionPre.botonEnviar')}
          </button>
        </div>
      )}

      {puedeDevolver && (
        <div className="card-footer bg-white">
          <label className="form-label" htmlFor="comentarioPre">
            {t('preinversion.revisionPre.campoComentarios')}
          </label>
          <textarea
            id="comentarioPre"
            className="form-control"
            rows={3}
            value={comentario}
            onChange={(evento) => setComentario(evento.target.value)}
            disabled={enviando}
          />
          <button type="button" className="btn btn-warning mt-2" onClick={devolver} disabled={enviando}>
            {t('preinversion.revisionPre.botonDevolver')}
          </button>
        </div>
      )}
    </section>
  );
}

