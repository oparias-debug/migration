import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { preinversionApi } from '../../../api/preinversionApi';
import { useAuth } from '../../../auth/useAuth';
import { Pestanas } from '../../../components/Pestanas';
import { DescripcionTecnicaTab } from './DescripcionTecnicaTab';

/**
 * Capítulo 1.3.2.2 del árbol del sistema, "Estudio técnico": dos pestañas.
 *
 * Descripción técnica es CU-PRE-11, que ya tiene contrato y back. Localización
 * es CU-PRE-12, que todavía no tiene contrato publicado: se deja en su sitio,
 * en construcción, para que se vea el capítulo completo.
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
    <div className="tarjeta">
      <h2>{t('preinversion.estudioTecnico.titulo')}</h2>
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
        {pestana === 'localizacion' && <p className="lead">{t('placeholder.title')}</p>}
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
  );
}
