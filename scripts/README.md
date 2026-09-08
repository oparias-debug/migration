# Publicar / actualizar las imágenes del tester

Esto es para vos (quien mantiene el ambiente), no para el tester — su instructivo está en
[dist-tester/README.md](../dist-tester/README.md).

## Qué hace

`publish-images.ps1` buildea y sube a GitHub Container Registry (privado) las 4 imágenes que
arma el proyecto: `back`, `api-gateway`, `front` y `keycloak`. `postgres` y `sonarqube` no se
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

Esto corre `mvn clean package -DskipTests`, buildea las 4 imágenes y las sube todas con tag
`latest` — el mismo tag que usa `dist-tester/.env.example` (`IMAGE_TAG=latest`), así que el
tester solo necesita `docker compose pull` para bajar lo nuevo, sin tocar nada de su lado.

Antes de correrlo, corré [run-tests.ps1](run-tests.ps1) (o al menos `mvn clean verify` /
`npm run test`) para no publicarle al tester una versión rota.

## Parámetros útiles

| Parámetro | Para qué sirve |
|---|---|
| `-SkipPush` | Buildea local sin subir nada — para probar que el build funciona antes de publicar de verdad. |
| `-SkipMavenBuild` | Reusa los `.jar` ya compilados (`back/target`, `api-gateway/target`) y solo rearma las imágenes Docker. Útil si ya corriste `mvn package` vos mismo. |
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
paquete `siip-back` / `siip-api-gateway` / `siip-front` / `siip-keycloak` → **Package settings**
y confirmá:

- Visibilidad: **Private**.
- **Manage Actions access** / invitá al tester (o a su cuenta de GitHub) con permiso de lectura,
  si el paquete no hereda ya el acceso del repo.

Sin esto, el tester va a ver "unauthorized" al hacer `docker compose pull` aunque su login a
ghcr.io esté bien.

## Problemas comunes

- **"unauthorized" al hacer push:** el login expiró o el token no tiene scope `write:packages`.
  Volvé a `docker login ghcr.io`.
- **El build de back/api-gateway falla con clases que no existen:** correlo con `mvn clean
  package` (no solo `package`) — hay un problema conocido de compilación incremental con el
  mapper de MapStruct que a veces deja archivos generados corruptos entre corridas sin `clean`.
- **El tester sigue viendo la versión vieja después de avisarle:** confirmá que el push
  realmente haya terminado (mirá el resumen final del script) y que el tester haya corrido
  `docker compose pull` antes de `docker compose up -d` — `up -d` solo no vuelve a bajar nada.
