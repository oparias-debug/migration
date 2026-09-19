import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { preinversionApi } from '../../../api/preinversionApi';
import { useAuth } from '../../../auth/useAuth';
import { Pestanas } from '../../../components/Pestanas';
import { DescripcionTecnicaTab } from './DescripcionTecnicaTab';
import { LocalizacionTab } from './LocalizacionTab';

/**
 * Capítulo 1.3.2.2 del árbol del sistema, "Estudio técnico": dos pestañas.
 *
 * Descripción técnica es CU-PRE-11 y Localización es CU-PRE-12; las dos con
 * contrato y back propios, cada una con su guardado.
 */
const PESTANAS = [
  { clave: 'descripcion', texto: 'preinversion.estudioTecnico.pestana.descripcion' },
  { clave: 'localizacion', texto: 'preinversion.estudioTecnico.pestana.localizacion' },
] as const;
type Clave = (typeof PESTANAS)[number]['clave'];

export function EstudioTecnicoPage() {
  const { t } = useTranslation();
  const { hasRole } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const idProyecto = Number(id);
  const [pestana, setPestana] = useState<Clave>('descripcion');
  const [cabecera, setCabecera] = useState<{ nombre: string; cup: string | null } | null>(null);

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
        <span>{t('preinversion.estudioTecnico.titulo')}</span>
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
          etiqueta="preinversion.estudioTecnico.pestanas"
        />

        <div role="tabpanel" id={`panel-${pestana}`}>
          {pestana === 'descripcion' && <DescripcionTecnicaTab idProyecto={idProyecto} puedeEditar={puedeEditar} />}
          {pestana === 'localizacion' && <LocalizacionTab idProyecto={idProyecto} puedeEditar={puedeEditar} />}
        </div>

        <div className="acciones-form">
          <button
            type="button"
            className="btn neutro"
            onClick={() => navigate(`/preinversion/proyectos/${idProyecto}/diagnostico`)}
          >
            {t('common.regresar')}
          </button>
        </div>
      </div>
    </div>
  );
}
