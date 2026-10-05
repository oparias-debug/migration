import { catalogosApi } from '../../../api/administracionApi';

/** Un eslabón de la jerarquía: lo justo para pintar la miga de pan. */
export interface Eslabon {
  readonly codigo: string;
  readonly nombre: string;
}

/**
 * Tope de saltos hacia arriba. La jerarquía real es de tres niveles
 * (región → departamento → distrito); con veinte sobra y nunca se recorre una
 * cadena absurda si los datos vinieran mal.
 */
const MAXIMO = 20;

/**
 * Los catálogos por encima de uno, del más alto al padre directo.
 *
 * El padre se escribe a mano en la ficha, así que nada impide dejar un ciclo
 * (A padre de B y B padre de A). Se lleva la cuenta de los ya visitados: sin eso
 * la miga de pan se quedaría dando vueltas y colgaría la pantalla.
 *
 * Si un eslabón no se puede leer, se devuelve lo que se haya podido reunir: una
 * miga de pan incompleta sigue sirviendo, y no vale la pena perder la pantalla
 * por ella.
 */
export async function cadenaDeAncestros(codigoPadre: string | null | undefined): Promise<Eslabon[]> {
  const ancestros: Eslabon[] = [];
  const vistos = new Set<string>();
  let actual = codigoPadre?.trim() || null;

  while (actual && !vistos.has(actual) && ancestros.length < MAXIMO) {
    vistos.add(actual);
    try {
      const { data } = await catalogosApi.consultarCatalogo({ codigo: actual });
      ancestros.push({ codigo: data.codigo ?? actual, nombre: data.nombre ?? actual });
      actual = data.padre?.trim() || null;
    } catch {
      break;
    }
  }
  return ancestros.reverse();
}
