import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { preinversionApi } from '../../../api/preinversionApi';
import { useAuth } from '../../../auth/useAuth';
import { Pestanas } from '../../../components/Pestanas';
import { InteresadosTab } from './InteresadosTab';
import { PoblacionTab } from './PoblacionTab';
import { AreaInfluenciaTab } from './AreaInfluenciaTab';
import { MercadoTab } from './MercadoTab';

/**
 * Capítulo 1.3.2.1 del árbol del sistema, "Diagnóstico de la situación actual":
 * cuatro casos de uso que el cliente ve como pestañas de una misma pantalla
 * (CU-PRE-06 interesados, 07 población, 08 área de influencia, 09 mercado).
 *
 * Cada pestaña carga y guarda lo suyo contra su propio endpoint, así que se
 * montan sólo al abrirlas; no hay un Guardar común. Aquí sólo se listan las que
 * ya tienen pantalla.
 */
const PESTANAS = [
  { clave: 'interesados', texto: 'preinversion.diagnostico.pestana.interesados' },
  { clave: 'poblacion', texto: 'preinversion.diagnostico.pestana.poblacion' },
  { clave: 'areaInfluencia', texto: 'preinversion.diagnostico.pestana.areaInfluencia' },
  { clave: 'mercado', texto: 'preinversion.diagnostico.pestana.mercado' },
] as const;
type Clave = (typeof PESTANAS)[number]['clave'];

export function DiagnosticoPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);
  const [pestana, setPestana] = useState<Clave>('interesados');
  const [cabecera, setCabecera] = useState<{ nombre: string; cup: string | null } | null>(null);

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

        <Pestanas
          pestanas={PESTANAS}
          activa={pestana}
          onCambiar={setPestana}
          etiqueta="preinversion.diagnostico.pestanas"
        />

        <div role="tabpanel" id={`panel-${pestana}`}>
          {pestana === 'interesados' && <InteresadosTab idProyecto={idProyecto} puedeEditar={puedeEditar} />}
          {pestana === 'poblacion' && <PoblacionTab idProyecto={idProyecto} puedeEditar={puedeEditar} />}
          {pestana === 'areaInfluencia' && <AreaInfluenciaTab idProyecto={idProyecto} puedeEditar={puedeEditar} />}
          {pestana === 'mercado' && <MercadoTab idProyecto={idProyecto} puedeEditar={puedeEditar} />}
        </div>

        <div className="acciones-form">
          <button
            type="button"
            className="btn neutro"
            onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/alternativas-solucion`)}
          >
            {t('common.regresar')}
          </button>
        </div>
      </div>
    </div>
  );
}
