import { useEffect, useState } from 'react';
import { preinversionApi, EstadoProyecto } from '../api/preinversionApi';

/**
 * Totales de proyectos por estado.
 *
 * No hay endpoint de contadores, pero `listarProyectos` devuelve
 * `paginacion.totalElementos`, así que se pide una página de tamaño 1 por cada
 * estado y se lee ese total: son datos reales del back, no cifras de ejemplo.
 */
export function useConteosProyecto(estados: readonly EstadoProyecto[]) {
  const [conteos, setConteos] = useState<Partial<Record<EstadoProyecto, number>>>({});
  const [cargando, setCargando] = useState(true);

  const clave = estados.join(',');
  useEffect(() => {
    let vigente = true;
    setCargando(true);
    Promise.all(
      estados.map((estado) =>
        preinversionApi
          .listarProyectos({ estado, pagina: 0, tamanio: 1 })
          .then((res) => [estado, res.data.paginacion.totalElementos] as const)
          // Un estado que falle no debe tumbar la pantalla entera: se omite.
          .catch(() => null),
      ),
    ).then((pares) => {
      if (!vigente) return;
      setConteos(Object.fromEntries(pares.filter(Boolean) as (readonly [EstadoProyecto, number])[]));
      setCargando(false);
    });
    return () => {
      vigente = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [clave]);

  return { conteos, cargando };
}
