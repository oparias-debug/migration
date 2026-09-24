import { useTranslation } from 'react-i18next';

/** Una columna de la tabla: cómo se escribe y cómo se lee. */
export interface ColumnaAnalisis<F> {
  readonly clave: keyof F & string;
  /** Clave de i18n del encabezado. */
  readonly etiqueta: string;
  readonly tipo: 'texto' | 'numero' | 'select' | 'calculada';
  /** Valores del desplegable, para `tipo: 'select'`. */
  readonly opciones?: readonly string[];
  /** Cómo se muestra cada opción o el valor calculado. */
  readonly formato?: (valor: string) => string;
  /** Clase del valor calculado, para darle color. */
  readonly clase?: (valor: string) => string | undefined;
  readonly ancho?: string;
}

interface Props<F extends Record<string, unknown>> {
  readonly columnas: readonly ColumnaAnalisis<F>[];
  readonly filas: F[];
  readonly filaVacia: () => F;
  readonly onCambiar: (filas: F[]) => void;
  readonly puedeEditar: boolean;
  /** Si las filas se agregan y se quitan; en el PAP vienen dadas por la ruta del proyecto. */
  readonly permiteAgregar?: boolean;
  /** Clave de i18n del texto que se muestra cuando no hay filas. */
  readonly sinFilas: string;
}

/**
 * Tabla editable de los tres análisis del capítulo 1.3.2 —ambiental (CU-PRE-14),
 * de riesgos (CU-PRE-15) y legal (CU-PRE-16)—.
 *
 * Los tres casos de uso describen la misma mecánica: una tabla donde se agregan
 * y se quitan filas y que se guarda entera de una vez (el PUT reemplaza el
 * arreglo completo). Lo que cambia entre ellos son las columnas, así que se
 * declaran y esto las pinta; lo demás —agregar, quitar, escribir— es común.
 *
 * Las columnas `calculada` las resuelve el servidor y aquí sólo se muestran.
 */
export function TablaAnalisis<F extends Record<string, unknown>>({
  columnas,
  filas,
  filaVacia,
  onCambiar,
  puedeEditar,
  permiteAgregar = true,
  sinFilas,
}: Props<F>) {
  const { t } = useTranslation();

  const cambiar = (indice: number, clave: string, valor: unknown) => {
    onCambiar(filas.map((fila, i) => (i === indice ? { ...fila, [clave]: valor } : fila)));
  };

  return (
    <>
      <div className="tabla-cont tabla-editable">
        <table>
          <thead>
            <tr>
              {columnas.map((c) => (
                <th key={c.clave} style={c.ancho ? { width: c.ancho } : undefined}>
                  {t(c.etiqueta)}
                </th>
              ))}
              {puedeEditar && permiteAgregar && <th>{t('common.acciones')}</th>}
            </tr>
          </thead>
          <tbody>
            {filas.length === 0 && (
              <tr>
                <td className="vacio" colSpan={columnas.length + (puedeEditar && permiteAgregar ? 1 : 0)}>
                  {t(sinFilas)}
                </td>
              </tr>
            )}
            {filas.map((fila, indice) => (
              // El índice es la identidad real de la fila: el contrato no da
              // ninguna clave y el arreglo se manda entero.
              // eslint-disable-next-line react/no-array-index-key
              <tr key={indice}>
                {columnas.map((c) => {
                  const valor = String(fila[c.clave] ?? '');
                  if (c.tipo === 'calculada') {
                    // El distintivo de color es para lo que tiene estado (la calificación
                    // de un riesgo); un importe o un porcentaje calculado se lee mejor
                    // como texto, sin el punto de color de una marca sin tono.
                    const tono = c.clase?.(valor);
                    return (
                      <td key={c.clave}>
                        {!valor && '—'}
                        {valor && tono && <span className={`marca-estado ${tono}`}>{c.formato?.(valor) ?? valor}</span>}
                        {valor && !tono && (c.formato?.(valor) ?? valor)}
                      </td>
                    );
                  }
                  if (c.tipo === 'select') {
                    return (
                      <td key={c.clave}>
                        <select
                          aria-label={`${t(c.etiqueta)} ${indice + 1}`}
                          value={valor}
                          disabled={!puedeEditar}
                          onChange={(e) => cambiar(indice, c.clave, e.target.value || undefined)}
                        >
                          <option value="">{t('common.seleccione')}</option>
                          {(c.opciones ?? []).map((opcion) => (
                            <option key={opcion} value={opcion}>
                              {c.formato?.(opcion) ?? opcion}
                            </option>
                          ))}
                        </select>
                      </td>
                    );
                  }
                  return (
                    <td key={c.clave}>
                      <input
                        type="text"
                        inputMode={c.tipo === 'numero' ? 'decimal' : undefined}
                        aria-label={`${t(c.etiqueta)} ${indice + 1}`}
                        value={valor}
                        disabled={!puedeEditar}
                        onChange={(e) => cambiar(indice, c.clave, e.target.value)}
                      />
                    </td>
                  );
                })}
                {puedeEditar && permiteAgregar && (
                  <td>
                    <button
                      type="button"
                      className="btn neutro"
                      onClick={() => onCambiar(filas.filter((_, i) => i !== indice))}
                    >
                      {t('common.quitar')}
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {puedeEditar && permiteAgregar && (
        <div className="acciones-form">
          <button type="button" className="btn secundario" onClick={() => onCambiar([...filas, filaVacia()])}>
            {t('common.agregarFila')}
          </button>
        </div>
      )}
    </>
  );
}
