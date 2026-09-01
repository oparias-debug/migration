import { createContext, useCallback, useSyncExternalStore, type ReactNode } from 'react';
import { authApi } from '../api/authApi';
import { clearAuthState, getAuthState, setAuthState, stateFromTokens, subscribeAuthState } from './tokenStore';
import type { AuthState } from './tokenStore';

interface AuthContextValue extends AuthState {
  isAuthenticated: boolean;
  hasRole: (role: string) => boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const state = useSyncExternalStore(subscribeAuthState, getAuthState);

  const login = useCallback(async (username: string, password: string) => {
    const tokenResponse = await authApi.login({ username, password });
    setAuthState(stateFromTokens(tokenResponse.access_token, tokenResponse.refresh_token));
  }, []);

  const logout = useCallback(() => {
    // No existe endpoint de logout/revoke en api-gateway; basta limpiar el estado local.
    clearAuthState();
  }, []);

  const hasRole = useCallback((role: string) => state.roles.includes(role), [state.roles]);

  const value: AuthContextValue = {
    ...state,
    isAuthenticated: state.accessToken !== null,
    hasRole,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
