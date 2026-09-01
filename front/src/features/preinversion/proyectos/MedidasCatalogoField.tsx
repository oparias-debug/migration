import { useEffect, useState } from 'react';
import { catalogoPreinversionApi } from '../../../api/preinversionApi';
import type { MedidaCatalogo, TipoMedidaCatalogo } from '../../../api/preinversionApi';

interface MedidasCatalogoFieldProps {
  tipo: TipoMedidaCatalogo;
  /** Título de la categoría; rotula la casilla que marca todas sus opciones. */
  label: string;
  value: string[];
  onChange: (value: string[]) => void;
  disabled?: boolean;
}

/**
 * Categoría de medidas (GRD/GRC/ACC) con sus opciones, para medidasGrd,
 * medidasGrc y medidasAcc de ProyectoRequest. Las opciones vienen de
 * /catalogos/medidas (Anexos C.1/C.1.5/C.2), el mismo catálogo que consulta el
 * botón "Ver descripción de categorías".
 *
 * Comportamiento pedido por el cliente el 31/08/2026: marcar la categoría
 * selecciona todas sus opciones, y desde ahí se pueden desmarcar una a una.
 *
 * El estado de la categoría es DERIVADO de `value`, no una bandera aparte: así
 * un proyecto que ya trae medidas guardadas aparece con su categoría marcada
 * sin ningún efecto adicional, y no existe el estado imposible "categoría
 * marcada con cero medidas".
 */
export function MedidasCatalogoField({ tipo, label, value, onChange, disabled }: MedidasCatalogoFieldProps) {
  const [opciones, setOpciones] = useState<MedidaCatalogo[]>([]);

  useEffect(() => {
    let vigente = true;
    catalogoPreinversionApi.listarMedidasCatalogo({ tipo }).then((res) => {
      if (vigente) setOpciones(res.data);
    });
    return () => {
      vigente = false;
    };
  }, [tipo]);

  const activa = value.length > 0;
  const completa = opciones.length > 0 && value.length === opciones.length;
  const parcial = activa && !completa;

  // Desde "vacía" o "parcial" se marcan todas; desde "completa" se limpian.
  const alternarCategoria = () => onChange(completa ? [] : opciones.map((o) => o.codigo));

  const alternarOpcion = (codigo: string) =>
    onChange(value.includes(codigo) ? value.filter((c) => c !== codigo) : [...value, codigo]);

  return (
    <div>
      <div className="form-check">
        <input
          type="checkbox"
          className="form-check-input"
          id={`categoria-${tipo}`}
          checked={completa}
          // `indeterminate` no es un atributo HTML: sólo se fija sobre el nodo.
          ref={(el) => {
            if (el) el.indeterminate = parcial;
          }}
          aria-checked={parcial ? 'mixed' : completa}
          onChange={alternarCategoria}
          disabled={disabled || opciones.length === 0}
        />
        <label className="form-check-label fw-semibold" htmlFor={`categoria-${tipo}`}>
          {label}
        </label>
      </div>

      {/* Sangría con guía vertical: el cliente pidió que se vea de un vistazo
          que las opciones dependen de la categoría. */}
      <div className="ms-3 ps-3 border-start mt-1">
      {opciones.map((opcion) => (
        <div className="form-check" key={opcion.codigo}>
          <input
            type="checkbox"
            className="form-check-input"
            id={`${tipo}-${opcion.codigo}`}
            checked={value.includes(opcion.codigo)}
              onChange={() => alternarOpcion(opcion.codigo)}
              disabled={disabled || !activa}
          />
          <label className="form-check-label" htmlFor={`${tipo}-${opcion.codigo}`}>
            {opcion.descripcion}
          </label>
        </div>
      ))}
    </div>
    </div>
  );
}
