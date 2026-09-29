#!/bin/bash
# ============================================================================
# Script de ejecución de pruebas Karate
# ============================================================================
# Lee las variables de entorno del pod (inyectadas desde ConfigMap y Secrets
# del namespace de OpenShift) y ejecuta Maven con los valores asignados.
#
# Uso:
#   ./run-tests.sh integration    # Pruebas de integración
#   ./run-tests.sh smoke          # Pruebas de smoke
#   ./run-tests.sh stress         # Pruebas de stress
#   ./run-tests.sh all            # Todas las pruebas
#
# Variables de entorno requeridas (desde ConfigMap/Secret):
#   KARATE_ENV          - Ambiente: dev, qa, test, preprod, prod
#   BASE_URL            - URL base del servicio
#   OAUTH2_TOKEN_URL    - URL del endpoint de token OAuth2
#   OAUTH2_CLIENT_ID    - Client ID para obtener JWT
#   OAUTH2_CLIENT_SECRET - Client Secret (desde Secret)
#
# Variables opcionales:
#   STRESS_THREADS      - Hilos paralelos para stress (default: 10)
#   CONNECTION_TIMEOUT  - Timeout de conexión en ms (default: 30000)
#   JWT_TOKEN           - Token JWT pre-generado
# ============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../.." && pwd)"

# ============================================================================
# VALIDACIÓN DE VARIABLES OBLIGATORIAS
# ============================================================================

KARATE_ENV="${KARATE_ENV:-dev}"

if [ "${KARATE_ENV}" != "dev" ]; then
  REQUIRED_VARS=("BASE_URL" "OAUTH2_TOKEN_URL" "OAUTH2_CLIENT_ID")
  for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var:-}" ]; then
      echo "ERROR: Variable '${var}' es obligatoria para el ambiente '${KARATE_ENV}'."
      echo "       Debe estar definida en el ConfigMap o Secret del namespace."
      exit 1
    fi
  done
fi

# ============================================================================
# CONSTRUCCIÓN DE ARGUMENTOS MAVEN
# ============================================================================

MAVEN_ARGS=(
  "-Dkarate.env=${KARATE_ENV}"
)

# Variables del ConfigMap/Secret -> system properties de Maven
[ -n "${BASE_URL:-}" ]            && MAVEN_ARGS+=("-Dbase.url=${BASE_URL}")
[ -n "${OAUTH2_TOKEN_URL:-}" ]    && MAVEN_ARGS+=("-Doauth2.token.url=${OAUTH2_TOKEN_URL}")
[ -n "${OAUTH2_CLIENT_ID:-}" ]    && MAVEN_ARGS+=("-Doauth2.client.id=${OAUTH2_CLIENT_ID}")
[ -n "${OAUTH2_CLIENT_SECRET:-}" ] && MAVEN_ARGS+=("-Doauth2.client.secret=${OAUTH2_CLIENT_SECRET}")
[ -n "${JWT_TOKEN:-}" ]           && MAVEN_ARGS+=("-Djwt.token=${JWT_TOKEN}")
[ -n "${STRESS_THREADS:-}" ]      && MAVEN_ARGS+=("-Dstress.threads=${STRESS_THREADS}")
[ -n "${CONNECTION_TIMEOUT:-}" ]  && MAVEN_ARGS+=("-Dtimeout=${CONNECTION_TIMEOUT}")

# ============================================================================
# EJECUCIÓN
# ============================================================================

TEST_TYPE="${1:-all}"

echo "==================================="
echo "Ejecutando pruebas Karate"
echo "==================================="
echo "Ambiente:    ${KARATE_ENV}"
echo "Base URL:    ${BASE_URL:-http://localhost:8080}"
echo "Tipo:        ${TEST_TYPE}"
echo "Threads:     ${STRESS_THREADS:-10}"
echo "==================================="

cd "${PROJECT_ROOT}"

case "${TEST_TYPE}" in
  integration)
    echo ">> Ejecutando pruebas de integración..."
    mvn test -Pintegration-tests "${MAVEN_ARGS[@]}"
    ;;
  smoke)
    echo ">> Ejecutando smoke tests..."
    mvn test -Psmoke-tests "${MAVEN_ARGS[@]}"
    ;;
  stress)
    if [ "${KARATE_ENV}" = "prod" ]; then
      echo "ERROR: No se permiten pruebas de stress en producción."
      exit 1
    fi
    echo ">> Ejecutando pruebas de stress..."
    mvn test -Pstress-tests "${MAVEN_ARGS[@]}"
    ;;
  all)
    echo ">> Ejecutando pruebas de integración..."
    mvn test -Pintegration-tests "${MAVEN_ARGS[@]}"

    echo ">> Ejecutando smoke tests..."
    mvn test -Psmoke-tests "${MAVEN_ARGS[@]}"

    if [ "${KARATE_ENV}" != "prod" ]; then
      echo ">> Ejecutando pruebas de stress..."
      mvn test -Pstress-tests "${MAVEN_ARGS[@]}"
    else
      echo ">> Omitiendo stress tests en producción."
    fi
    ;;
  *)
    echo "Uso: $0 {integration|smoke|stress|all}"
    exit 1
    ;;
esac

echo "==================================="
echo "Pruebas finalizadas exitosamente"
echo "==================================="
