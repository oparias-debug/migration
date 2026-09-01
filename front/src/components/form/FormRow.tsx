import type { ReactNode } from 'react';

interface FormRowProps {
  label: string;
  /**
   * `id` del control que describe esta etiqueta. Sin él, el <label> no queda
   * asociado a ningún campo: el lector de pantalla no anuncia el nombre y
   * pulsar la etiqueta no enfoca el control.
   *
   * Es opcional para no romper las filas que agrupan varios controles (un
   * grupo de radios, por ejemplo), donde no hay un único destino válido.
   */
  controlId?: string;
  required?: boolean;
  error?: string;
  /** Ocupa las dos columnas de la rejilla, como en el diseño aprobado. */
  ancho?: boolean;
  /** Texto gris junto a la etiqueta (p. ej. la ayuda de las medidas). */
  ayuda?: string;
  children: ReactNode;
}

/**
 * Campo del formulario, con el marcado del diseño aprobado en solodevs.net:
 * etiqueta encima del control, asterisco rojo si es obligatorio y el error
 * debajo. La rejilla de dos columnas la pone el contenedor `.fr`; un campo
 * marcado con `ancho` ocupa la fila entera.
 *
 * Sustituye a la fila de Bootstrap (col-md-2 / col-md-10) del front Java.
 */
export function FormRow({ label, controlId, required, error, ancho, ayuda, children }: FormRowProps) {
  return (
    <div className={`f${ancho ? ' w' : ''}`}>
      <label htmlFor={controlId}>
        {label}
        {required && <span className="req">*</span>}
        {ayuda && <span className="ayuda">{ayuda}</span>}
      </label>
      {children}
      {error && <span className="error">{error}</span>}
    </div>
  );
}
