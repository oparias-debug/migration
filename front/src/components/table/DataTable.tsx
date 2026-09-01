import type { ReactNode } from 'react';

export interface Column<T> {
  header: string;
  render: (row: T) => ReactNode;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  rows: T[];
  emptyMessage: string;
  renderActions: (row: T) => ReactNode;
}

// Equivalente a fragments/tabla.html: tabla genérica con columnas configurables
// + columna de acciones (editar/eliminar).
export function DataTable<T>({ columns, rows, emptyMessage, renderActions }: DataTableProps<T>) {
  if (rows.length === 0) {
    return <div>{emptyMessage}</div>;
  }

  return (
    <div className="table-responsive">
      <table className="table table-striped table-hover align-middle">
        <thead>
          <tr>
            {columns.map((col) => (
              <th key={col.header} scope="col">
                {col.header}
              </th>
            ))}
            <th />
          </tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={index}>
              {columns.map((col) => (
                <td key={col.header}>{col.render(row)}</td>
              ))}
              <td>
                <div className="float-end text-nowrap">{renderActions(row)}</div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
