import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { preinversionApi } from '../../../api/preinversionApi';
import { useAuth } from '../../../auth/useAuth';
import { Pestanas } from '../../../components/Pestanas';
import { InteresadosTab } from './InteresadosTab';
import { PoblacionTab } from './PoblacionTab';
import { AreaInfluenciaTab } from './AreaInfluenciaTab';
import { MercadoTab } from './MercadoTab';

import { BotonSiguiente } from '../pasos/BotonSiguiente';
import { BotonRegresar } from '../pasos/BotonRegresar';
import { useAplicaContenido } from '../pasos/contenidoIniciativa';

/**
 * Capítulo 1.3.2.1 del árbol del sistema, "Diagnóstico de la situación actual":
 * cuatro casos de uso que el cliente ve como pestañas de una misma pantalla
 * (CU-PRE-06 interesados, 07 población, 08 área de influencia, 09 mercado).
 *
 * Cada pestaña carga y guarda lo suyo contra su propio endpoint, así que se
 * montan sólo al abrirlas; no hay un Guardar común. Aquí sólo se listan las que
 * ya tienen pantalla.
 *
 * Las cuatro son cuatro contenidos distintos del Anexo F y no aplican a la vez:
 * de un programa o un estudio general sólo se formula el Análisis de la
 * Población, así que las otras tres no se muestran. Por eso cada pestaña lleva
 * su caso de uso.
 */
const PESTANAS = [
  { clave: 'interesados', texto: 'preinversion.diagnostico.pestana.interesados', cu: 'CU-PRE-06' },
  { clave: 'poblacion', texto: 'preinversion.diagnostico.pestana.poblacion', cu: 'CU-PRE-07' },
  { clave: 'areaInfluencia', texto: 'preinversion.diagnostico.pestana.areaInfluencia', cu: 'CU-PRE-08' },
  { clave: 'mercado', texto: 'preinversion.diagnostico.pestana.mercado', cu: 'CU-PRE-09' },
] as const;
type Clave = (typeof PESTANAS)[number]['clave'];

export function DiagnosticoPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);
  const [pestana, setPestana] = useState<Clave | null>(null);
  const [cabecera, setCabecera] = useState<{ nombre: string; cup: string | null } | null>(null);
  const aplica = useAplicaContenido();
  const pestanas = PESTANAS.filter((p) => aplica(p.cu));
  // La pestaña abierta es la primera que aplique. Se elige al pintar y no en el
  // estado inicial porque el anexo llega después del primer pintado: hasta
  // entonces la primera es Interesados, que en un programa no se formula.
  const visible = pestanas.some((p) => p.clave === pestana) ? pestana : (pestanas[0]?.clave ?? null);

  // Sólo el Técnico URP edita; el Técnico PRE consulta (x-roles de los contratos).
  const puedeEditar = hasRole('TECNICO_URP');

  useEffect(() => {
    if (!idProyecto) return;
    preinversionApi
      .obtenerProyecto({ idProyecto })
      .then(({ data }) => setCabecera({ nombre: data.nombre, cup: data.cup ?? null }))
      .catch(() => {
        // La cabecera es contexto: si no carga, las pestañas funcionan igual.
      });
  }, [idProyecto]);

  return (
    <div className="formcard">
      <div className="formhead">
        <span>{t('preinversion.diagnostico.titulo')}</span>
      </div>
      <div className="formbody">
        {cabecera && (
          <p className="nota">
            <b>{cabecera.nombre}</b>
            {cabecera.cup && <span className="mono"> · CUP {cabecera.cup}</span>}
          </p>
        )}

        {visible && (
          <Pestanas
            pestanas={pestanas}
            activa={visible}
            onCambiar={setPestana}
            etiqueta="preinversion.diagnostico.pestanas"
          />
        )}

        <div role="tabpanel" id={`panel-${visible ?? 'ninguna'}`}>
          {visible === 'interesados' && <InteresadosTab idProyecto={idProyecto} puedeEditar={puedeEditar} />}
          {visible === 'poblacion' && <PoblacionTab idProyecto={idProyecto} puedeEditar={puedeEditar} />}
          {visible === 'areaInfluencia' && <AreaInfluenciaTab idProyecto={idProyecto} puedeEditar={puedeEditar} />}
          {visible === 'mercado' && <MercadoTab idProyecto={idProyecto} puedeEditar={puedeEditar} />}
        </div>

        <div className="acciones-form">
          <BotonRegresar idProyecto={idProyecto} paso="diagnostico" />
          <BotonSiguiente idProyecto={idProyecto} paso="diagnostico" />
        </div>
      </div>
    </div>
  );
}
