import type { DecodedAccessToken } from '../types/auth';
import { jwtDecode } from 'jwt-decode';

export interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  roles: string[];
  username: string | null;
}

const initialState: AuthState = { accessToken: null, refreshToken: null, roles: [], username: null };

let state: AuthState = initialState;
const listeners = new Set<() => void>();

export function getAuthState(): AuthState {
  return state;
}

export function setAuthState(next: AuthState): void {
  state = next;
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
