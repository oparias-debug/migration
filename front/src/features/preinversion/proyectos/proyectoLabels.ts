import { EstadoProyecto, IniciativaInversion } from '../../../api/preinversionApi';

// Traducción de los códigos de enum del contrato (EstadoProyecto/IniciativaInversion)
// a las etiquetas que usa el CU-PRE-01 (Antecedentes/Anexo A.1-A.2). Solo se cubren
// los estados que CU-PRE-01/CU-PRE-01.5 realmente transicionan; el resto (ciclo de
// vida posterior del proyecto) cae al fallback del propio código.
const ESTADO_LABELS: Partial<Record<string, string>> = {
  [EstadoProyecto.EnRegistro]: 'En Elaboración',
  [EstadoProyecto.EnviadoDgicpRegistro]: 'Enviado DGICP (Registro)',
  [EstadoProyecto.ObservadoDgicpRegistro]: 'Observado DGICP (Registro)',
  [EstadoProyecto.CupAsignado]: 'CUP Asignado',
};

const INICIATIVA_LABELS: Record<string, string> = {
  [IniciativaInversion.Programa]: 'Programa',
  [IniciativaInversion.Proyecto]: 'Proyecto',
  [IniciativaInversion.EstudioGeneral]: 'Estudios Generales',
};

export function formatEstado(estado: string): string {
  return ESTADO_LABELS[estado] ?? estado;
}

export function formatIniciativa(iniciativa: string): string {
  return INICIATIVA_LABELS[iniciativa] ?? iniciativa;
}

// Estados desde los cuales el Técnico URP puede seguir editando la pantalla
// "Nuevo registro" (RN 1.c, RN 2.2.b — mismo guard que actualizarProyecto en back).
export const ESTADOS_EDITABLES: string[] = [EstadoProyecto.EnRegistro, EstadoProyecto.ObservadoDgicpRegistro];

// Roles de Keycloak (realm_access.roles del JWT, ver keycloak/realm-export.json) habilitados
// para "Registro de Proyecto", según los x-roles documentados para GET /proyectos en
// CU-01.openapi.yaml (Técnico URP, Técnico PRE, Administrador del Sistema, Usuarios
// Internos/Externos). Se usa para ocultar el enlace del sidebar a quien no tiene acceso —
// listar() en back no restringe por rol, pero un actor sin fila en USUARIO (ver
// DevDataSeeder) igual recibiría 401 al entrar, así que ocultarlo evita ese callejón sin
// salida en la UI.
export const ROLES_CON_ACCESO_REGISTRO_PROYECTO = [
  'TECNICO_URP',
  'TECNICO_PRE',
  'ADMINISTRADOR_DEL_SISTEMA',
  'USUARIOS_INTERNOS',
];
