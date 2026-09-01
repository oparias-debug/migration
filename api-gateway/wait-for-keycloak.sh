#!/bin/sh

# Usa KEYCLOAK_INTERNAL_URL o un valor por defecto si no está definida
KEYCLOAK_URL="${KEYCLOAK_INTERNAL_URL:-http://keycloak:8080}"

echo "Esperando a que Keycloak esté listo en $KEYCLOAK_URL..."

until curl -sf "$KEYCLOAK_URL/health/ready"; do
  sleep 5
done

echo "Keycloak está listo. Iniciando API Gateway..."
exec java -jar app.jar
