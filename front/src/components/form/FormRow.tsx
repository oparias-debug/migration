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
  children: ReactNode;
}

// Equivalente a fragments/forms.html::inputRow + fieldErrors: fila con label
// (col-md-2) + control (col-md-10) + mensaje de error bajo el control.
export function FormRow({ label, controlId, required, error, children }: FormRowProps) {
  return (
    <div className="row mb-3">
      <label className="col-md-2 col-form-label" htmlFor={controlId}>
        {label}
        {required ? '*' : ''}
      </label>
      <div className="col-md-10">
        {children}
        {error && <div className="invalid-feedback d-block">{error}</div>}
      </div>
    </div>
  );
}

