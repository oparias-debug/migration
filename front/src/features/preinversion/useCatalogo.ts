import { useEffect, useState } from 'react';

// Carga una lista de catálogo (sectores, ejes temáticos, ejes del Plan de Gobierno, planes
// sectoriales/regionales) una sola vez al montar. Cada catálogo tiene su propio shape de
// id/nombre, así que este hook solo maneja el fetch; el componente decide cómo renderizarlo.
export function useCatalogo<T>(cargar: () => Promise<{ data: T[] }>): T[] {
  const [opciones, setOpciones] = useState<T[]>([]);

  useEffect(() => {
    let activo = true;
    cargar().then((res) => {
      if (activo) setOpciones(res.data);
    });
    return () => {
      activo = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return opciones;
}
