import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { ComentarioSolicitud } from '../../../api/preinversionApi';

interface RevisionPreProps {
  comentarios: ComentarioSolicitud[];
  /** Sólo con el proyecto en OBSERVADO_DGICP_REGISTRO (RN 2.9). */
  puedeResponder: boolean;
  enviando: boolean;
  /** Error de `campo: "respuesta"` devuelto por el back en un 400. */
  errorRespuesta?: string;
  onEnviar: (respuesta: string) => void;
}

/**
 * Sección "Revisión PRE" de la pantalla "Nuevo registro"
 * (CU-PRE-01-responder-observaciones.feature).
 *
 * El contrato modela esto como UN solo hilo (`revisionPre`: lista de
 * `ComentarioSolicitud` con autor y fecha), no como dos campos separados
 * "comentario" y "respuesta": las observaciones del Técnico PRE y las respuestas
 * del Técnico URP son entradas del mismo historial, y se distinguen por el rol
 * de quien las escribió. Se presenta como conversación por eso.
 */
export function RevisionPre({ comentarios, puedeResponder, enviando, errorRespuesta, onEnviar }: RevisionPreProps) {
  const { t } = useTranslation();
  const [respuesta, setRespuesta] = useState('');

  const enviar = () => {
    onEnviar(respuesta);
    setRespuesta('');
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
    </section>
  );
}

