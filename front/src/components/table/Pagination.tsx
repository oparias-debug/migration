import { useTranslation } from 'react-i18next';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  onPageChange: (page: number) => void;
}

// Equivalente al <nav>/<ul class="pagination"> de fragments/tabla.html.
export function Pagination({ currentPage, totalPages, first, last, onPageChange }: PaginationProps) {
  const { t } = useTranslation();

  if (totalPages <= 0) return null;

  return (
    <nav aria-label="Pagination" className="d-flex justify-content-center" style={{ height: 70 }}>
      <ul className="pagination my-auto">
        <li className={first ? 'page-item disabled' : 'page-item'}>
          <button className="page-link" onClick={() => onPageChange(currentPage - 1)} disabled={first}>
            {t('common.previous')}
          </button>
        </li>
        {Array.from({ length: totalPages }, (_, i) => (
          <li key={i} className={currentPage === i ? 'page-item active' : 'page-item'}>
            <button className="page-link" onClick={() => onPageChange(i)}>
              {i + 1}
            </button>
          </li>
        ))}
        <li className={last ? 'page-item disabled' : 'page-item'}>
          <button className="page-link" onClick={() => onPageChange(currentPage + 1)} disabled={last}>
            {t('common.next')}
          </button>
        </li>
      </ul>
    </nav>
  );
}
