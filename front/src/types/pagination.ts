// Subconjunto de campos de Spring Data Page<T> realmente usados hoy por el front
// (ver CatalogosGeneralesService/TablasRangosService del front Java original).
export interface SpringPage<T> {
  content: T[];
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  totalElements?: number;
  numberOfElements?: number;
  empty?: boolean;
}
