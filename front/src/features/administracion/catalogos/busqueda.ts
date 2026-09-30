/**
 * Búsqueda por fracciones de palabra dentro de los catálogos.
 *
 * "Cund" tiene que encontrar "Cundinamarca" (indicación del 30/09/2026), así que
 * se busca por partes y no por coincidencia exacta. Se ignoran mayúsculas y
 * tildes: en estos catálogos se escribe tanto "JERÁRQUICO" como "JERARQUICO", y
 * quien busca no tiene por qué acertar con el acento.
 *
 * El contrato no tiene parámetro de búsqueda —ni `listarCatalogos` ni
 * `buscarListaRegistros` lo aceptan—, así que el filtro es de este lado, sobre
 * lo que se haya traído. Sirve para catálogos de cientos de registros; para los
 * de miles haría falta que el servidor buscara.
 */

/** Sin tildes, sin mayúsculas y sin espacios de sobra. */
export const normalizar = (texto: string) =>
  texto
    .normalize('NFD')
    .replaceAll(/\p{Diacritic}/gu, '')
    .toLowerCase()
    .trim();

/**
 * ¿Aparece lo buscado en alguno de estos textos? Con la búsqueda vacía no se
 * filtra nada, que es lo que se espera al borrar la caja.
 *
 * Cada palabra de la búsqueda tiene que aparecer en algún sitio, no
 * necesariamente en el mismo ni en ese orden: así "cund girardot" encuentra la
 * fila que tenga las dos cosas sin obligar a escribirlas como están guardadas.
 */
export function coincide(busqueda: string, ...textos: (string | null | undefined)[]): boolean {
  const partes = normalizar(busqueda).split(/\s+/).filter(Boolean);
  if (partes.length === 0) return true;
  const donde = textos.filter(Boolean).map((t) => normalizar(t as string));
  return partes.every((parte) => donde.some((t) => t.includes(parte)));
}
