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

// Tabla genérica con columnas configurables + columna de acciones. El estilo
// sale de base.css (thead th / tbody td), como en el diseño aprobado: la fila
// vacía va DENTRO de la tabla para no perder las cabeceras cuando no hay datos.
export function DataTable<T>({ columns, rows, emptyMessage, renderActions }: DataTableProps<T>) {
  return (
    <div className="tabla-cont">
      <table>
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
          {rows.length === 0 && (
            <tr>
              <td className="vacio" colSpan={columns.length + 1}>
                {emptyMessage}
              </td>
            </tr>
          )}
          {rows.map((row, index) => (
            <tr key={index}>
              {columns.map((col) => (
                <td key={col.header}>{col.render(row)}</td>
              ))}
              <td>
                {renderActions(row)}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
