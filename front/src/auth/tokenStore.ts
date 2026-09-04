import type { DecodedAccessToken } from '../types/auth';
import { jwtDecode } from 'jwt-decode';

export interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  roles: string[];
  username: string | null;
}

const initialState: AuthState = { accessToken: null, refreshToken: null, roles: [], username: null };

/**
 * La sesión se guarda en localStorage, no sólo en memoria.
 *
 * Con el estado únicamente en una variable de módulo, cualquier recarga de
 * página —F5, un enlace pegado, un marcador— vaciaba la sesión y devolvía al
 * usuario al login aunque su token siguiera vigente.
 *
 * Es la solución pragmática, no la definitiva: guardar el token en
 * localStorage lo deja al alcance de cualquier script de la página. Lo correcto
 * es una cookie httpOnly con el refresh token, y eso depende del back
 * (api-gateway). Mientras tanto, esto evita el fallo que el usuario sufre en
 * cada recarga.
 */
const CLAVE = 'siip.auth';

function leerDelAlmacen(): AuthState {
  try {
    const guardado = localStorage.getItem(CLAVE);
    if (!guardado) return initialState;
    const datos = JSON.parse(guardado) as AuthState;
    // Un token vencido no sirve: se descarta para no arrancar con una sesión
    // muerta que fallaría en la primera petición.
    if (!datos.accessToken || tokenVencido(datos.accessToken)) return initialState;
    return datos;
  } catch {
    return initialState;
  }
}

function tokenVencido(accessToken: string): boolean {
  try {
    const { exp } = jwtDecode<DecodedAccessToken & { exp?: number }>(accessToken);
    return typeof exp === 'number' ? exp * 1000 <= Date.now() : false;
  } catch {
    return true;
  }
}

function escribirEnAlmacen(estado: AuthState): void {
  try {
    if (estado.accessToken) localStorage.setItem(CLAVE, JSON.stringify(estado));
    else localStorage.removeItem(CLAVE);
  } catch {
    /* almacenamiento bloqueado: la sesión sigue viva en memoria */
  }
}

let state: AuthState = leerDelAlmacen();
const listeners = new Set<() => void>();

export function getAuthState(): AuthState {
  return state;
}

export function setAuthState(next: AuthState): void {
  state = next;
  escribirEnAlmacen(next);
  listeners.forEach((listener) => listener());
}

export function clearAuthState(): void {
  setAuthState(initialState);
}

export function subscribeAuthState(listener: () => void): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

// Réplica de LoginController.asignarSession (front Java original): decodifica el
// access_token y extrae realm_access.roles.
export function stateFromTokens(accessToken: string, refreshToken: string): AuthState {
  const decoded = jwtDecode<DecodedAccessToken>(accessToken);
  return {
    accessToken,
    refreshToken,
    roles: decoded.realm_access?.roles ?? [],
    username: decoded.preferred_username ?? null,
  };
}
