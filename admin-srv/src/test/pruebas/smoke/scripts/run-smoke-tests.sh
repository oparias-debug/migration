#!/bin/bash
# ============================================================================
# Script para ejecutar smoke tests con Karate
# Lee las variables de entorno del pod (ConfigMap/Secret) y ejecuta Maven.
# ============================================================================

set -euo pipefail

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../../.." && pwd)"

ENVIRONMENT="${KARATE_ENV:-dev}"
TEST_SCOPE="${1:-all}"

echo -e "${YELLOW}=========================================${NC}"
echo -e "${YELLOW}  Smoke Tests con Karate${NC}"
echo -e "${YELLOW}=========================================${NC}"
echo "Ambiente: ${ENVIRONMENT}"
echo "Alcance: ${TEST_SCOPE}"
echo ""

# Validar variables obligatorias para ambientes no-dev
if [ "${ENVIRONMENT}" != "dev" ]; then
  for var in BASE_URL OAUTH2_TOKEN_URL OAUTH2_CLIENT_ID; do
    if [ -z "${!var:-}" ]; then
      echo -e "${RED}ERROR: Variable '${var}' es obligatoria para el ambiente '${ENVIRONMENT}'.${NC}"
      echo "       Debe estar definida en el ConfigMap o Secret del namespace."
      exit 1
    fi
  done
fi

echo "BASE_URL: ${BASE_URL:-http://localhost:8080}"
echo "JWT_TOKEN: ${JWT_TOKEN:+[CONFIGURED]}"
echo ""

# Determinar scope
case ${TEST_SCOPE} in
  all)         TEST_METHOD="testSmokeAll";          DESCRIPTION="todos los smoke tests" ;;
  critical)    TEST_METHOD="testSmokeCritical";     DESCRIPTION="tests criticos" ;;
  health)      TEST_METHOD="testSmokeHealth";       DESCRIPTION="health checks" ;;
  api)         TEST_METHOD="testSmokeApi";          DESCRIPTION="API endpoints" ;;
  dependencies) TEST_METHOD="testSmokeDependencies"; DESCRIPTION="dependencias" ;;
  performance) TEST_METHOD="testSmokePerformance";  DESCRIPTION="performance" ;;
  *)
    echo -e "${RED}ERROR: Scope invalido: ${TEST_SCOPE}${NC}"
    echo "Scopes validos: all, critical, health, api, dependencies, performance"
    exit 1
    ;;
esac

# Construir argumentos Maven
MAVEN_ARGS=(
  "-Psmoke-tests"
  "-Dtest=SmokeTestRunner#${TEST_METHOD}"
  "-Dkarate.env=${ENVIRONMENT}"
)

[ -n "${BASE_URL:-}" ]            && MAVEN_ARGS+=("-Dbase.url=${BASE_URL}")
[ -n "${OAUTH2_TOKEN_URL:-}" ]    && MAVEN_ARGS+=("-Doauth2.token.url=${OAUTH2_TOKEN_URL}")
[ -n "${OAUTH2_CLIENT_ID:-}" ]    && MAVEN_ARGS+=("-Doauth2.client.id=${OAUTH2_CLIENT_ID}")
[ -n "${OAUTH2_CLIENT_SECRET:-}" ] && MAVEN_ARGS+=("-Doauth2.client.secret=${OAUTH2_CLIENT_SECRET}")
[ -n "${JWT_TOKEN:-}" ]           && MAVEN_ARGS+=("-Djwt.token=${JWT_TOKEN}")
[ -n "${CONNECTION_TIMEOUT:-}" ]  && MAVEN_ARGS+=("-Dtimeout=${CONNECTION_TIMEOUT}")

echo -e "${BLUE}Ejecutando: ${DESCRIPTION}${NC}"
echo ""

cd "${PROJECT_ROOT}"
mvn test "${MAVEN_ARGS[@]}"

if [ $? -eq 0 ]; then
  echo ""
  echo -e "${GREEN}=========================================${NC}"
  echo -e "${GREEN}  Smoke tests pasaron exitosamente${NC}"
  echo -e "${GREEN}=========================================${NC}"
  echo "Reportes en: target/karate-reports/"
  exit 0
else
  echo ""
  echo -e "${RED}=========================================${NC}"
  echo -e "${RED}  Smoke tests fallaron${NC}"
  echo -e "${RED}=========================================${NC}"
  echo "Revisar: target/karate-reports/ y target/surefire-reports/"
  exit 1
fi
