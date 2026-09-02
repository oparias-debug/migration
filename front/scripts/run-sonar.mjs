#!/usr/bin/env node
// Wrapper de `npm run sonar`: inyecta sonar.login (además de SONAR_TOKEN/sonar.token) vía
// SONARQUBE_SCANNER_PARAMS. Necesario mientras el servidor local sea SonarQube 9.9.x (LTS
// Community, ver docker-compose.yml): ese servidor solo acepta autenticación Basic con el
// token como usuario (sonar.login); el auth Bearer que @sonar/scan arma por defecto a partir
// de SONAR_TOKEN (sonar.token) le devuelve "Not authorized". Si el día de mañana se sube la
// versión del servidor a una que soporte Bearer (10.x+), este wrapper deja de hacer falta y
// "sonar": "npm run test:coverage && npx --yes @sonar/scan" alcanza solo.
// No usamos sintaxis de shell (`VAR=x cmd`) para que el script funcione igual en bash y en
// PowerShell/cmd (Windows) sin depender de una dependencia extra tipo cross-env.
import { spawnSync } from 'node:child_process';

const token = process.env.SONAR_TOKEN;
if (!token) {
  console.error('Falta SONAR_TOKEN en el entorno (ver .env en la raíz del repo / REFERENCE.md).');
  process.exit(1);
}

const scannerParams = JSON.stringify({ 'sonar.login': token });

const result = spawnSync('npx', ['--yes', '@sonar/scan'], {
  stdio: 'inherit',
  shell: process.platform === 'win32',
  env: { ...process.env, SONARQUBE_SCANNER_PARAMS: scannerParams },
});

process.exit(result.status ?? 1);
