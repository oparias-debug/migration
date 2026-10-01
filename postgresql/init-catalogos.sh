#!/usr/bin/env bash
# Esquema de admin-srv y carga inicial de catálogos (CU-ADM-01) en la base local de docker-compose.
#
# Lo ejecuta el servicio admin-srv-init de docker-compose, del que depende admin-srv: corre antes
# de cada arranque de admin-srv y solo actúa la primera vez, cuando su esquema aún no tiene
# catálogos. En los arranques siguientes no toca nada. Para volver a cargar hay que vaciar la
# base (docker compose down -v) o borrar las tablas de admin-srv.
#
# La conexión viene de PGHOST, PGUSER, PGPASSWORD y PGDATABASE (las pone docker-compose).
#
# Pasos (admin-srv/sql se monta en /admin-srv-sql):
#   1. Crea las tablas de admin-srv si no existen (postgresql/esquema-admin-srv.sql, equivalente
#      local de los scripts Oracle V001..V003), en una sola transacción. admin-srv corre en local
#      con ddl-auto: validate.
#   2. Si catalogo ya tiene datos, termina: la carga ya se hizo.
#   3. Ejecuta tal cual, en orden (prefijo 00, 01, ...), los scripts de "inicializacion catalogos"
#      sobre el esquema de trabajo carga_catalogos.
#   4. Traslada esa carga a las tablas de admin-srv (postgresql/traslado-catalogos.sql, en una
#      transacción).
#   5. Borra el esquema de trabajo.
# Los scripts del paso 3 hacen sus propios COMMIT, así que la carga no es una sola transacción.
# Si algo falla, catalogo sigue vacío (el traslado es atómico) y el siguiente arranque la repite
# desde un carga_catalogos nuevo. ON_ERROR_STOP detiene todo en el primer error.

set -e

SQL=/admin-srv-sql
DIRECTORIO_CATALOGOS="$SQL/inicializacion catalogos"

ejecutar() {
    psql -v ON_ERROR_STOP=1 --quiet "$@"
}

consultar() {
    psql -v ON_ERROR_STOP=1 --quiet --tuples-only --no-align -c "$1"
}

if [ "$(consultar "SELECT to_regclass('catalogo') IS NULL")" = "t" ]; then
    echo "init-catalogos: creando el esquema de admin-srv"
    ejecutar --single-transaction -f "$SQL/postgresql/esquema-admin-srv.sql"
fi

# Unas tablas creadas por una versión anterior (por ejemplo, por Hibernate con ddl-auto: update)
# no se actualizan solas: se avisa en vez de fallar a mitad del traslado.
if [ "$(consultar "SELECT to_regclass('periodo_calendario') IS NULL OR NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema() AND table_name = 'catalogo'
          AND column_name = 'id_catalogo_padre')")" = "t" ]; then
    echo "init-catalogos: las tablas de admin-srv no coinciden con postgresql/esquema-admin-srv.sql" >&2
    echo "init-catalogos: son de una versión anterior; bórralas (o docker compose down -v) y vuelve a levantar admin-srv" >&2
    exit 1
fi

if [ "$(consultar "SELECT EXISTS (SELECT 1 FROM catalogo)")" = "t" ]; then
    echo "init-catalogos: los catálogos ya están cargados, no hay nada que hacer"
    exit 0
fi

ejecutar -c "DROP SCHEMA IF EXISTS carga_catalogos CASCADE" -c "CREATE SCHEMA carga_catalogos"
for script in "$DIRECTORIO_CATALOGOS"/*.sql; do
    [ -e "$script" ] || continue
    echo "init-catalogos: ejecutando $(basename "$script")"
    PGOPTIONS="-c search_path=carga_catalogos" ejecutar -f "$script" > /dev/null
done

echo "init-catalogos: trasladando la carga al esquema de admin-srv"
ejecutar -f "$SQL/postgresql/traslado-catalogos.sql"
ejecutar -c "DROP SCHEMA carga_catalogos CASCADE"
echo "init-catalogos: carga terminada"
