import { useTranslation } from 'react-i18next';

/** Un campo del catálogo tal como se edita en pantalla; la posición es su orden en la lista. */
export interface CampoEditable {
  nombre: string;
  clave: boolean;
}

export const CAMPO_VACIO: CampoEditable = { nombre: '', clave: false };

/**
 * Qué impide guardar los campos, o null si están bien. El contrato define el
 * campo como nombre + calificador (KEY o FIELD) + posición; la clave identifica
 * cada registro, así que hace falta al menos una.
 */
export function problemaDeCampos(campos: readonly CampoEditable[]): string | null {
  const nombres = campos.map((c) => c.nombre.trim());
  if (nombres.length === 0) return 'sinCampos';
  if (nombres.includes('')) return 'campoSinNombre';
  if (new Set(nombres.map((n) => n.toLowerCase())).size !== nombres.length) return 'camposRepetidos';
  if (!campos.some((c) => c.clave)) return 'sinClave';
  return null;
}

export const aCampoContrato = (campos: readonly CampoEditable[]) =>
  campos.map((c, i) => ({ name: c.nombre.trim(), qualifier: (c.clave ? 'KEY' : 'FIELD') as 'KEY' | 'FIELD', position: i + 1 }));

/** Lista editable de campos: nombre y si forma parte de la clave. */
export function CamposEditor({
  campos,
  alCambiar,
  soloLectura = false,
}: {
  readonly campos: readonly CampoEditable[];
  readonly alCambiar: (campos: CampoEditable[]) => void;
  readonly soloLectura?: boolean;
}) {
  const { t } = useTranslation();
  const clave = 'administracion.catalogos';
  const cambiar = (i: number, cambio: Partial<CampoEditable>) =>
    alCambiar(campos.map((c, j) => (j === i ? { ...c, ...cambio } : c)));

  return (
    <fieldset className="campos-catalogo">
      <legend>{t(`${clave}.campos`)}</legend>
      <div className="tabla-cont tabla-editable">
        <table>
          <thead>
            <tr>
              <th>{t(`${clave}.posicion`)}</th>
              <th>{t(`${clave}.campoNombre`)}</th>
              <th>{t(`${clave}.campoClave`)}</th>
              {!soloLectura && <th>{t('common.acciones')}</th>}
            </tr>
          </thead>
          <tbody>
            {campos.map((c, i) => (
              // Las filas no tienen identidad propia más allá de su posición.
              // eslint-disable-next-line react/no-array-index-key
              <tr key={i}>
                <td className="mono">{i + 1}</td>
                <td>
                  <input
                    type="text"
                    aria-label={t(`${clave}.campoNombreNumero`, { numero: i + 1 })}
                    value={c.nombre}
                    readOnly={soloLectura}
                    onChange={(e) => cambiar(i, { nombre: e.target.value })}
                  />
                </td>
                <td>
                  <input
                    type="checkbox"
                    aria-label={t(`${clave}.campoClaveNumero`, { numero: i + 1 })}
                    checked={c.clave}
                    disabled={soloLectura}
                    onChange={(e) => cambiar(i, { clave: e.target.checked })}
                  />
                </td>
                {!soloLectura && (
                  <td>
                    <button
                      type="button"
                      className="btn neutro"
                      aria-label={t(`${clave}.eliminarCampo`, { numero: i + 1 })}
                      onClick={() => alCambiar(campos.filter((_, j) => j !== i))}
                    >
                      {t('common.eliminar')}
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {!soloLectura && (
        <button type="button" className="btn secundario" onClick={() => alCambiar([...campos, { ...CAMPO_VACIO }])}>
          {t(`${clave}.agregarCampo`)}
        </button>
      )}
    </fieldset>
  );
}
