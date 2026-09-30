import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { rutaAnterior } from './pasosProyecto';
import { pasoAplica, useAplicaContenido } from './contenidoIniciativa';

interface Props {
  readonly idProyecto: number;
  /** Paso del árbol en el que está la pantalla. */
  readonly paso: string;
}

/**
 * "Regresar" de un capítulo: lleva al capítulo anterior del árbol, saltando los
 * que no se formulan en esta iniciativa. Desde el primero se vuelve a la Ruta de
 * Preinversión.
 *
 * Los nueve capítulos lo repetían con su propio onClick, y cada uno tenía que
 * acordarse de saltar lo que la barra no muestra; con un solo botón no pueden
 * discrepar.
 */
export function BotonRegresar({ idProyecto, paso }: Props) {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const aplica = useAplicaContenido();

  return (
    <button
      type="button"
      className="btn neutro"
      onClick={() => navigate(rutaAnterior(idProyecto, paso, (p) => pasoAplica(p, aplica)))}
    >
      {t('common.regresar')}
    </button>
  );
}
