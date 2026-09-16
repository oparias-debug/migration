import { useTranslation } from 'react-i18next';

export interface Pestana<C extends string> {
  readonly clave: C;
  /** Clave de i18n del rótulo. */
  readonly texto: string;
}

/**
 * Barra de pestañas de una pantalla. Marca con un punto rojo las que tienen
 * campos pendientes: si no, el borde rojo de un campo que está en otra pestaña
 * no se vería.
 *
 * Quien la usa decide si los paneles se ocultan o se desmontan; aquí sólo se
 * gobierna la barra.
 */
export function Pestanas<C extends string>({
  pestanas,
  activa,
  onCambiar,
  pendientes,
  etiqueta,
}: {
  readonly pestanas: readonly Pestana<C>[];
  readonly activa: C;
  readonly onCambiar: (clave: C) => void;
  /** Claves con campos pendientes. */
  readonly pendientes?: Partial<Record<C, boolean>>;
  /** Clave de i18n del nombre de la barra, para lectores de pantalla. */
  readonly etiqueta: string;
}) {
  const { t } = useTranslation();
  return (
    <div className="pestanas" role="tablist" aria-label={t(etiqueta)}>
      {pestanas.map((p) => {
        const pendiente = pendientes?.[p.clave] ?? false;
        return (
          <button
            key={p.clave}
            type="button"
            role="tab"
            id={`pestana-${p.clave}`}
            aria-controls={`panel-${p.clave}`}
            aria-selected={p.clave === activa}
            className={`pestana${p.clave === activa ? ' activa' : ''}${pendiente ? ' con-pendientes' : ''}`}
            onClick={() => onCambiar(p.clave)}
          >
            {t(p.texto)}
            {/* El punto rojo lo pinta el CSS (.con-pendientes); esto es para lectores de pantalla. */}
            {pendiente && <span className="sr-only">{` (${t('pestanas.pendiente')})`}</span>}
          </button>
        );
      })}
    </div>
  );
}
