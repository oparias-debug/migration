export interface LoginRequest {
  username: string;
  password: string;
}

// Contrato real de api-gateway AuthController: snake_case vía @JsonProperty en TokenResponse.
export interface TokenResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
}

export interface DecodedAccessToken {
  realm_access?: { roles?: string[] };
  preferred_username?: string;
  exp: number;
  [key: string]: unknown;
}
