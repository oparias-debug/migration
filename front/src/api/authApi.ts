import axios from 'axios';
import type { LoginRequest, TokenResponse } from '../types/auth';

// Instancia separada de httpClient: login/refresh nunca deben llevar el
// interceptor de Authorization/retry-401 (evita recursión en el propio refresh).
const authAxios = axios.create({ baseURL: '/' });

export const authApi = {
  login: (credentials: LoginRequest) =>
    authAxios.post<TokenResponse>('/auth/login', credentials).then((res) => res.data),

  // AuthController.refreshToken espera el refresh token como string JSON crudo
  // en el body (@RequestBody String), NO como { refreshToken: "..." }.
  refresh: (refreshToken: string) =>
    authAxios
      .post<TokenResponse>('/auth/refresh', JSON.stringify(refreshToken), {
        headers: { 'Content-Type': 'application/json' },
      })
      .then((res) => res.data),
};
