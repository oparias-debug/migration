import { beforeEach, describe, expect, it } from 'vitest';
import { clearAuthState, setAuthState, stateFromTokens } from './tokenStore';

function fakeJwt(payload: Record<string, unknown>): string {
  const header = Buffer.from(JSON.stringify({ alg: 'none', typ: 'JWT' })).toString('base64url');
  const body = Buffer.from(JSON.stringify(payload)).toString('base64url');
  return `${header}.${body}.signature`;
}

describe('stateFromTokens', () => {
  it('decodifica roles y username del access_token (equivalente a LoginController.asignarSession)', () => {
    const accessToken = fakeJwt({
      realm_access: { roles: ['ADMIN', 'USER'] },
      preferred_username: 'jdoe',
      exp: 9999999999,
    });

    const state = stateFromTokens(accessToken, 'refresh-token-abc');

    expect(state.accessToken).toBe(accessToken);
    expect(state.refreshToken).toBe('refresh-token-abc');
    expect(state.roles).toEqual(['ADMIN', 'USER']);
    expect(state.username).toBe('jdoe');
  });

  it('devuelve roles vacíos si el token no trae realm_access', () => {
    const accessToken = fakeJwt({ preferred_username: 'sinroles', exp: 9999999999 });

    const state = stateFromTokens(accessToken, 'refresh-token-xyz');

    expect(state.roles).toEqual([]);
    expect(state.username).toBe('sinroles');
  });
});

describe('persistencia de la sesión', () => {
  beforeEach(() => {
    localStorage.clear();
    clearAuthState();
  });

  // El fallo que se corrige: con el estado sólo en memoria, cualquier recarga
  // devolvía al login aunque el token siguiera vigente.
  it('guarda la sesión para que sobreviva a una recarga', () => {
    const vigente = fakeJwt({ preferred_username: 'ana', realm_access: { roles: ['TECNICO_URP'] }, exp: 4102444800 });
    setAuthState(stateFromTokens(vigente, 'refresh'));

    expect(localStorage.getItem('siip.auth')).toContain(vigente);
  });

  it('al cerrar sesión no queda nada guardado', () => {
    const vigente = fakeJwt({ preferred_username: 'ana', realm_access: { roles: [] }, exp: 4102444800 });
    setAuthState(stateFromTokens(vigente, 'refresh'));
    clearAuthState();

    expect(localStorage.getItem('siip.auth')).toBeNull();
  });
});
