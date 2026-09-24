import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { IconoMascara } from '../../../components/Icono';

/** Los cuatro documentos del PAP, en el orden en que se trabajan durante el año. */
export const DOCUMENTOS_PAP = [
  { clave: 'programacion-financiera', caso: 'CU-PRE-30' },
  { clave: 'programacion-metas', caso: 'CU-PRE-31' },
  { clave: 'avance-financiero', caso: 'CU-PRE-32' },
  { clave: 'avance-metas', caso: 'CU-PRE-33' },
] as const;

/**
 * Entrada del PAP (Programa Anual de Preinversión).
 *
 * Primero se programa el año —lo financiero y las metas físicas— y después, cada
 * cuatrimestre, se informa el avance de cada uno. Los cuatro documentos son
 * pantallas distintas del mismo plan, así que la opción del menú lleva aquí y
 * desde aquí se elige.
 */
export function PapPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();

  return (
    <>
      <h2 className="seccion">{t('preinversion.pap.titulo')}</h2>
      <p className="nota">{t('preinversion.pap.intro')}</p>

      <div className="modulos">
        {DOCUMENTOS_PAP.map((documento, indice) => (
          <button
            key={documento.clave}
            type="button"
            className="modulo"
            onClick={() => navigate(`/programacion/pap/${documento.clave}`)}
          >
            <IconoMascara nombre="menu-programacion" tam={40} style={{ color: 'var(--preinv-txt)' }} />
            <h3 style={{ color: 'var(--preinv-txt)' }}>
              <span className="mono">2.2.{indice + 1}</span> {t(`preinversion.pap.documentos.${documento.clave}`)}
            </h3>
            <p>{t(`preinversion.pap.descripciones.${documento.clave}`)}</p>
            <div className="flecha" aria-hidden="true">
              →
            </div>
          </button>
        ))}
      </div>
    </>
  );
}
