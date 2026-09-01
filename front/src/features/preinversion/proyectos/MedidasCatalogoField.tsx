import { useEffect, useState } from 'react';
import { catalogoPreinversionApi } from '../../../api/preinversionApi';
import type { MedidaCatalogo, TipoMedidaCatalogo } from '../../../api/preinversionApi';

interface MedidasCatalogoFieldProps {
  tipo: TipoMedidaCatalogo;
  value: string[];
  onChange: (value: string[]) => void;
  disabled?: boolean;
}

// Lista de checkboxes para medidasGrd/medidasGrc/medidasAcc de ProyectoRequest,
// poblada desde /catalogos/medidas (Anexos C.1/C.1.5/C.2, mismo catálogo que
// consulta el botón "Ver descripción de categorías").
export function MedidasCatalogoField({ tipo, value, onChange, disabled }: MedidasCatalogoFieldProps) {
  const [opciones, setOpciones] = useState<MedidaCatalogo[]>([]);

  useEffect(() => {
    let activo = true;
    catalogoPreinversionApi.listarMedidasCatalogo({ tipo }).then((res) => {
      if (activo) setOpciones(res.data);
    });
    return () => {
      activo = false;
    };
  }, [tipo]);

  const toggle = (codigo: string) => {
    onChange(value.includes(codigo) ? value.filter((c) => c !== codigo) : [...value, codigo]);
  };

  return (
    <div>
      {opciones.map((opcion) => (
        <div className="form-check" key={opcion.codigo}>
          <input
            type="checkbox"
            className="form-check-input"
            id={`${tipo}-${opcion.codigo}`}
            checked={value.includes(opcion.codigo)}
            onChange={() => toggle(opcion.codigo)}
            disabled={disabled}
          />
          <label className="form-check-label" htmlFor={`${tipo}-${opcion.codigo}`}>
            {opcion.descripcion}
          </label>
        </div>
      ))}
    </div>
  );
}
