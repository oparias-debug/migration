import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../../auth/useAuth';
import { ejecutar } from './papComun';

/** Las cinco acciones del ciclo de revisión, iguales en CU-PRE-31 y CU-PRE-33. */
export interface AccionesRevision {
  readonly guardarObservaciones: (texto: string) => Promise<unknown>;
  readonly enviarObservaciones: () => Promise<unknown>;
  readonly guardarRespuesta: (texto: string) => Promise<unknown>;
  readonly enviarRespuesta: () => Promise<unknown>;
  readonly finalizar: () => Promise<unknown>;
}

interface PanelProps {
  readonly clave: string;
  readonly acciones: AccionesRevision;
  readonly alCambiar: () => void;
}

const ROLES_DGICP = ['TECNICO_PRE', 'COORDINADOR_PRE'];

/**
 * Ciclo de revisión del PAP entre la DGICP y la institución (SF-3 de CU-PRE-31,
 * SF-2 de CU-PRE-33).
 *
 * La DGICP escribe las observaciones y las envía; la institución responde y
 * envía su respuesta; la DGICP finaliza la revisión. RN-A.c reparte quién
 * edita qué: las observaciones sólo el Técnico o Coordinador PRE, la respuesta
 * sólo el Técnico URP; cada quien ve el texto del otro en sólo lectura.
 *
 * El contrato no expone un GET del recurso de revisión —sólo lo devuelven las
 * propias acciones—, así que el panel arranca vacío y se queda con lo último
 * que respondió el servidor. Pendiente con Cristian.
 */
export function PanelRevision({ clave, acciones, alCambiar }: PanelProps) {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const esDgicp = ROLES_DGICP.some(hasRole);
  const esInstitucion = hasRole('TECNICO_URP');

  const [observaciones, setObservaciones] = useState('');
  const [respuesta, setRespuesta] = useState('');

  if (!esDgicp && !esInstitucion) return null;

  return (
    <div className="formcard">
      <div className="formhead">{t('preinversion.pap.revision.titulo')}</div>
      <div className="formbody">
        {esDgicp && (
          <div className="campo">
            <label htmlFor="pap-observaciones">{t('preinversion.pap.revision.observaciones')}</label>
            <textarea
              id="pap-observaciones"
              rows={3}
              value={observaciones}
              onChange={(e) => setObservaciones(e.target.value)}
            />
            <div className="acciones-form">
              <button
                type="button"
                className="btn neutro"
                disabled={!observaciones.trim()}
                onClick={() =>
                  void ejecutar(
                    () => acciones.guardarObservaciones(observaciones.trim()),
                    `${clave}.observacionesGuardadas`,
                    t,
                  )
                }
              >
                {t('common.guardar')}
              </button>
              <button
                type="button"
                className="btn primario"
                onClick={() =>
                  void ejecutar(acciones.enviarObservaciones, `${clave}.observacionesEnviadas`, t, alCambiar)
                }
              >
                {t('preinversion.pap.revision.enviarObservaciones')}
              </button>
            </div>
          </div>
        )}

        {esInstitucion && (
          <div className="campo">
            <label htmlFor="pap-respuesta">{t('preinversion.pap.revision.respuesta')}</label>
            <textarea id="pap-respuesta" rows={3} value={respuesta} onChange={(e) => setRespuesta(e.target.value)} />
            <div className="acciones-form">
              <button
                type="button"
                className="btn neutro"
                disabled={!respuesta.trim()}
                onClick={() =>
                  void ejecutar(() => acciones.guardarRespuesta(respuesta.trim()), `${clave}.respuestaGuardada`, t)
                }
              >
                {t('common.guardar')}
              </button>
              <button
                type="button"
                className="btn primario"
                onClick={() => void ejecutar(acciones.enviarRespuesta, `${clave}.respuestaEnviada`, t, alCambiar)}
              >
                {t('preinversion.pap.revision.enviarRespuesta')}
              </button>
            </div>
          </div>
        )}

        {esDgicp && (
          <div className="acciones-form">
            <button
              type="button"
              className="btn primario"
              onClick={() => void ejecutar(acciones.finalizar, `${clave}.revisionFinalizada`, t, alCambiar)}
            >
              {t('preinversion.pap.revision.finalizar')}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
