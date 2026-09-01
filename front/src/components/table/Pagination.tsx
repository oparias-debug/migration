import { useTranslation } from 'react-i18next';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  onPageChange: (page: number) => void;
}

/**
 * Paginación con el marcado del diseño aprobado: barra al pie de la tarjeta,
 * con "página X / N" en el centro en vez de una lista con un botón por página.
 *
 * El listado de proyectos puede tener cientos de páginas: un botón por página
 * desbordaría la barra. Con anterior/siguiente el ancho no depende del total.
 */
export function Pagination({ currentPage, totalPages, first, last, onPageChange }: PaginationProps) {
  const { t } = useTranslation();

  if (totalPages <= 0) return null;

  return (
    <div className="paginacion">
      <span className="info">
        {t('common.pagina')} {currentPage + 1} {t('common.de')} {totalPages}
      </span>
      <div className="botones">
        <button type="button" className="btn neutro" onClick={() => onPageChange(currentPage - 1)} disabled={first}>
          {t('common.previous')}
        </button>
        <span className="num">
          {currentPage + 1} / {totalPages}
        </span>
        <button type="button" className="btn neutro" onClick={() => onPageChange(currentPage + 1)} disabled={last}>
          {t('common.next')}
        </button>
      </div>
    </div>
  );
}
