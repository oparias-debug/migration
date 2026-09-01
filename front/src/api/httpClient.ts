import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios';
import { authApi } from './authApi';
import { clearAuthState, getAuthState, setAuthState, stateFromTokens } from '../auth/tokenStore';

interface RetryableConfig extends AxiosRequestConfig {
  _retry?: boolean;
}

// Fábrica en vez de una única instancia: los clientes generados por
// openapi-generator (ver preinversionApi.ts) solo respetan su propio basePath
// cuando la instancia de axios que reciben NO trae baseURL seteado (ver
// createRequestFunction en generated/*/common.ts) — así que cada API con un
// basePath distinto necesita su propia instancia, pero todas deben compartir
// el mismo interceptor de Authorization/retry-401.
export function createHttpClient(baseURL: string): AxiosInstance {
  const instance = axios.create({ baseURL });

  instance.interceptors.request.use((config) => {
    const { accessToken } = getAuthState();
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  });

  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config as RetryableConfig | undefined;
      const { refreshToken } = getAuthState();

      if (error.response?.status === 401 && originalRequest && !originalRequest._retry && refreshToken) {
        originalRequest._retry = true;
        try {
          const tokenResponse = await authApi.refresh(refreshToken);
          setAuthState(stateFromTokens(tokenResponse.access_token, tokenResponse.refresh_token));
          originalRequest.headers = {
            ...originalRequest.headers,
            Authorization: `Bearer ${tokenResponse.access_token}`,
          };
          return instance(originalRequest);
        } catch (refreshError) {
          clearAuthState();
          window.location.href = '/login';
          return Promise.reject(refreshError);
        }
      }

      return Promise.reject(error);
    },
  );

  return instance;
}

// Rutas relativas (/auth/*, /back/*): en dev las resuelve el proxy de Vite,
// en producción el reverse-proxy de Nginx — mismo comportamiento same-origin
// en ambos entornos, sin necesidad de CORS en api-gateway.
const httpClient = createHttpClient('/');

export default httpClient;
