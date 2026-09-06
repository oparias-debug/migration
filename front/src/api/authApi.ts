import axios from 'axios';
import type { LoginRequest, TokenResponse } from '../types/auth';

// Instancia separada de httpClient: login/refresh nunca deben llevar el
// interceptor de Authorization/retry-401 (evita recursión en el propio refresh).
const authAxios = axios.create({ baseURL: '/' });

export const authApi = {
  login: (credentials: LoginRequest) =>
    authAxios.post<TokenResponse>('/auth/login', credentials).then((res) => res.data),

  // AuthController.refreshToken lo recibe como @RequestBody String y lo pasa
  // TAL CUAL a Keycloak como refresh_token. Con JSON.stringify el cuerpo llega
  // entre comillas ("eyJ...") y Keycloak lo rechaza: el refresco devolvía 401
  // siempre, así que la sesión moría a los 5 minutos —lo que vive el
  // access_token— aunque el interceptor de 401 estuviera bien.
  // Se envía en texto plano, y con Content-Type: text/plain para que axios no
  // lo serialice a JSON por su cuenta.
  refresh: (refreshToken: string) =>
    authAxios
      .post<TokenResponse>('/auth/refresh', refreshToken, {
        headers: { 'Content-Type': 'text/plain' },
      })
      .then((res) => res.data),
};
