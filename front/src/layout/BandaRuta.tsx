import { Fragment } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

/** Banda de ruta (breadcrumb). El último tramo es la pantalla actual. */
export interface Tramo {
  texto: string;
  ruta?: string;
  /** true = `texto` ya es el literal a mostrar, no una clave de i18n. */
  literal?: boolean;
}

export function BandaRuta({ tramos }: { tramos: Tramo[] }) {
  const { t } = useTranslation();
  return (
    <nav className="ruta" aria-label={t('ruta.migas')}>
      {tramos.map((tr, i) => {
        const ultimo = i === tramos.length - 1;
        const etiqueta = tr.literal ? tr.texto : t(tr.texto);
        return (
          <Fragment key={tr.texto + i}>
            {i > 0 && (
              <span className="sep" aria-hidden="true">
                ›
              </span>
            )}
            {tr.ruta && !ultimo ? (
              <Link to={tr.ruta}>{etiqueta}</Link>
            ) : (
              <span className={ultimo ? 'actual' : undefined} aria-current={ultimo ? 'page' : undefined}>
                {etiqueta}
              </span>
            )}
          </Fragment>
        );
      })}
    </nav>
  );
}
