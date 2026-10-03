# Scripts

| Script | Para qué sirve |
|---|---|
| [run-tests.ps1](run-tests.ps1) | Corre las pruebas de backend-srv, api-gateway y front en un solo comando. |
| [comparar-contratos.ps1](comparar-contratos.ps1) | Dice si las copias de contratos y `.feature` de front coinciden con las de backend-srv y admin-srv. |
| [publish-images.ps1](publish-images.ps1) | Publica las imágenes del tester (ver abajo). |

## Comparar contratos y features entre servicios y front

front guarda copias de los `.openapi.yaml` (`front/openapi/`) y de los `.feature`
(`front/features/`), cuyos originales viven en `backend-srv` o `admin-srv`. El script empareja
cada copia con su original por nombre de archivo y la clasifica:

| Estado | Qué significa |
|---|---|
| `IGUAL` | Misma versión en el servicio y en front. |
| `DISTINTO` | El contenido difiere: definir cuál es la buena, copiarla y, si es un `.yaml`, correr `npm run generate:api` en front. |
| `SOLO-FRONT` | Front lo tiene y ningún servicio: se renombró, se borró o es propio del front. |
| `SOLO-SERVICIO` | El servicio lo tiene y front no. Suele ser normal: por defecto solo se cuenta. |
| `DUPLICADO` | backend-srv y admin-srv tienen un archivo con el mismo nombre. |

Las diferencias de fin de línea y de espacios al final de las líneas se ignoran. Termina con
código 1 si hay algún `DISTINTO` o `DUPLICADO`, así que sirve también como chequeo antes de un
commit.

```powershell
# Resumen, sin los archivos iguales
.\scripts\comparar-contratos.ps1 -SoloDiferencias

# Solo los contratos, con el diff de cada uno ("-" es el servicio, "+" es front)
.\scripts\comparar-contratos.ps1 -Tipo Contratos -SoloDiferencias -Diff

# Listar también lo que front no tiene
.\scripts\comparar-contratos.ps1 -Tipo Features -IncluirSoloServicio
```

# Publicar / actualizar las imágenes del tester

Esto es para vos (quien mantiene el ambiente), no para el tester — su instructivo está en
[dist-tester/README.md](../dist-tester/README.md).

## Qué hace

`publish-images.ps1` buildea y sube a GitHub Container Registry (privado) las 5 imágenes que
arma el proyecto: `backend-srv`, `admin-srv`, `api-gateway`, `front` y `keycloak`. `postgres` y `sonarqube` no se
tocan acá — son imágenes públicas que el tester baja directo de Docker Hub.

## 1. Login a ghcr.io (una sola vez por máquina)

Necesitás un Personal Access Token (classic) de GitHub con scope `write:packages`
(GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)).

```powershell
docker login ghcr.io -u <tu-usuario-de-github> -p <tu-personal-access-token>

```

## 2. Publicar una actualización

Desde la raíz del repo:

```powershell
.\scripts\publish-images.ps1
```

Esto corre `mvn clean package -DskipTests` dentro de `backend-srv/`, `admin-srv/` y `api-gateway/`, compila el front con `docker compose run --rm front-build` (`-SkipFrontBuild` lo salta), buildea las 5 imágenes y las sube todas con tag
`latest` — el mismo tag que usa `dist-tester/.env.example` (`IMAGE_TAG=latest`), así que el
tester solo necesita `docker compose pull` para bajar lo nuevo, sin tocar nada de su lado.

Antes de correrlo, corré [run-tests.ps1](run-tests.ps1) (o al menos `mvn clean verify` /
`npm run test`) para no publicarle al tester una versión rota.

## Parámetros útiles

| Parámetro | Para qué sirve |
|---|---|
| `-SkipPush` | Buildea local sin subir nada — para probar que el build funciona antes de publicar de verdad. |
| `-SkipMavenBuild` | Reusa los `.jar` ya compilados (`backend-srv/target`, `admin-srv/target`, `api-gateway/target`) y solo rearma las imágenes Docker. Útil si ya corriste `mvn package` vos mismo. |
| `-Tag <valor>` | Publica con un tag específico en vez de `latest` (ver "Versionar" abajo). |
| `-Owner <valor>` | Cambia el owner de ghcr.io (default `david-magnaperita`). Casi nunca hace falta tocarlo. |

Ejemplos:

```powershell
# Probar el build sin publicar nada
.\scripts\publish-images.ps1 -SkipPush

# Ya corriste "mvn package" a mano, solo reconstruir/subir las imágenes Docker
.\scripts\publish-images.ps1 -SkipMavenBuild
```

## Versionar (opcional)

`latest` alcanza para el día a día (el tester siempre prueba lo último). Si en algún momento
necesitás que el tester pueda volver a una versión anterior puntual, publicá además con un tag
fechado — no reemplaza a `latest`, queda como una versión adicional:

```powershell
.\scripts\publish-images.ps1 -Tag 2026-09-05
```

Para que el tester use ese tag en particular, tiene que cambiar `IMAGE_TAG=2026-09-05` en su
`.env` (dentro de `dist-tester/`) y volver a correr `docker compose pull && docker compose up -d`.

## Primera vez / visibilidad de los paquetes

La primera vez que hagas push de cada imagen, entrá a GitHub → tu perfil → **Packages** → cada
paquete `siip-back` / `siip-admin-srv` / `siip-api-gateway` / `siip-front` / `siip-keycloak` →
**Package settings** y confirmá (`siip-admin-srv` es nuevo: el tester todavía no tiene acceso):

- Visibilidad: **Private**.
- **Manage Actions access** / invitá al tester (o a su cuenta de GitHub) con permiso de lectura,
  si el paquete no hereda ya el acceso del repo.

Sin esto, el tester va a ver "unauthorized" al hacer `docker compose pull` aunque su login a
ghcr.io esté bien.

## Problemas comunes

- **"unauthorized" al hacer push:** el login expiró o el token no tiene scope `write:packages`.
  Volvé a `docker login ghcr.io`.
- **El build de backend-srv/api-gateway falla con clases que no existen:** correlo con `mvn clean
  package` (no solo `package`) — hay un problema conocido de compilación incremental con el
  mapper de MapStruct que a veces deja archivos generados corruptos entre corridas sin `clean`.
- **El tester sigue viendo la versión vieja después de avisarle:** confirmá que el push
  realmente haya terminado (mirá el resumen final del script) y que el tester haya corrido
  `docker compose pull` antes de `docker compose up -d` — `up -d` solo no vuelve a bajar nada.
