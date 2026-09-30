import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { rutaSiguiente } from './pasosProyecto';

interface Props {
  readonly idProyecto: number;
  /** Paso del árbol en el que está la pantalla. */
  readonly paso: string;
  /** Qué hacer antes de avanzar, si el capítulo lo necesita. */
  readonly antesDeAvanzar?: () => Promise<boolean>;
  readonly deshabilitado?: boolean;
}

/**
 * "Siguiente" de un capítulo: lleva al siguiente que ya tenga pantalla, en el
 * orden del árbol.
 *
 * El rótulo no nombra su destino. Antes cada capítulo anunciaba adónde iba, así
 * que el mismo botón se llamaba distinto en cada pantalla y el nombre quedaba
 * desactualizado en cuanto se intercalaba un capítulo. En el último capítulo con
 * pantalla no se dibuja, en vez de ofrecer un camino que no existe.
 */
export function BotonSiguiente({ idProyecto, paso, antesDeAvanzar, deshabilitado }: Props) {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const destino = rutaSiguiente(idProyecto, paso);

  if (!destino) return null;

  const avanzar = async () => {
    if (antesDeAvanzar && !(await antesDeAvanzar())) return;
    navigate(destino);
  };

  return (
    <button type="button" className="btn primario" disabled={deshabilitado} onClick={() => void avanzar()}>
      {t('common.siguiente')}
    </button>
  );
}
