/// <reference types="vite/client" />

interface ImportMetaEnv {
  /**
   * Dónde vive CU-ADM-01 (catálogos). Por defecto /admin/api/v1, que es admin-srv
   * detrás del api-gateway. El entorno de vista previa todavía los sirve desde
   * backend-srv y lo apunta a /back.
   */
  readonly VITE_CATALOGOS_BASE?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
