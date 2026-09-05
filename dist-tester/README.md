# SIIP — Guía para levantar el ambiente de pruebas

Este paquete levanta el sistema completo (front + api-gateway + back + Keycloak + PostgreSQL)
usando imágenes Docker ya construidas — no hace falta el código fuente, Java, Node ni Maven,
solo Docker Desktop.

## 1. Requisitos

- Instalar [Docker Desktop para Mac](https://www.docker.com/products/docker-desktop/). Elegí
  la versión según tu chip (menú  arriba a la izquierda → "Acerca de este Mac"):
  **Apple Silicon** para M1/M2/M3/M4, o **Intel Chip** para Mac más viejas.
  - Si tu Mac es Apple Silicon: las imágenes que te van a compartir están armadas para
    `amd64` (Intel), así que Docker las va a correr emuladas con Rosetta 2. Funciona, pero
    arranca un poco más lento — es normal, no es un error.
- Abrí Docker Desktop al menos una vez (queda un ícono de ballena 🐳 en la barra de menú)
  antes de usar la Terminal — si no, los comandos `docker` van a fallar.
- Una cuenta de GitHub con acceso al repositorio (te la da quien te envió esto) y un
  **Personal Access Token (classic)** con el scope `read:packages`, para poder descargar las
  imágenes privadas. Se genera en GitHub → Settings → Developer settings → Personal access
  tokens → Tokens (classic) → Generate new token.

## 2. Login a GitHub Container Registry (una sola vez)

Abrí la app **Terminal** (Launchpad → Terminal, o Cmd+Espacio y escribís "Terminal") y corré
(reemplazando `<tu-usuario>` y `<tu-token>`):

```bash
docker login ghcr.io -u <tu-usuario-de-github> -p <tu-personal-access-token>
```

## 3. Levantar el stack

Entrá a esta carpeta (`dist-tester/`) con `cd` y corré:

```bash
cp .env.example .env
docker compose pull
docker compose up -d
```

La primera vez tarda unos minutos en bajar las imágenes. Cuando termine:

- **Front (la aplicación):** http://localhost
- **api-gateway:** http://localhost:8080
- **Keycloak (admin):** http://localhost:8085 (usuario `admin` / clave `admin`)

Para ver logs: `docker compose logs -f back` (o `front`, `api-gateway`, `keycloak`).
Para apagar todo: `docker compose down` (agregá `-v` si además querés borrar los datos de
la base y arrancar de cero la próxima vez).

## 4. Usuarios de prueba

Ya vienen creados en Keycloak (no hace falta registrarse). Usá el que corresponda al rol que
estás probando:

| Usuario | Contraseña | Rol |
|---|---|---|
| tecnico.urp | tecnicoUrp123 | Técnico URP |
| tecnico.pre | tecnicoPre123 | Técnico PRE |
| tecnico.pro | tecnicoPro123 | Técnico PRO |
| tecnico.symp | tecnicoSymp123 | Técnico SYMP |
| tecnico.ual | tecnicoUal123 | Técnico UAL |
| coordinador.pre | coordinadorPre123 | Coordinador PRE |
| coordinador.pro | coordinadorPro123 | Coordinador PRO |
| coordinador.symp | coordinadorSymp123 | Coordinador SYMP |
| jefe.dgi | jefeDgi123 | Jefe DGI |
| subjefe.dgi | subjefeDgi123 | Subjefe DGI |
| viabilizador | viabilizador123 | Viabilizador |
| admin | admin123 | Administrador del sistema |
| user | user123 | Usuario interno (sin rol de negocio) |

Ver [GLOSSARY.md](https://github.com/david-magnaperita/siip/blob/main/GLOSSARY.md) del repo
si algún rol o término de la aplicación no es claro.

## 5. Cuando haya una versión nueva para probar

Te van a avisar que hay imágenes nuevas. Alcanza con (desde `dist-tester/`):

```bash
docker compose pull
docker compose up -d
```

Esto reemplaza los contenedores con la versión nueva sin tocar los datos ya guardados en
Postgres (a menos que haya un cambio de esquema — en ese caso te lo van a avisar aparte).

## 6. Problemas comunes

- **"unauthorized" al hacer `docker compose pull`:** el login del paso 2 expiró o el token no
  tiene el scope `read:packages`. Volvé a generar el token y a loguearte.
- **El front carga pero el login falla:** puede ser que Keycloak todavía esté arrancando
  (tarda ~30-60s la primera vez). Esperá y refrescá, o revisá `docker compose logs keycloak`.
- **Puerto ocupado (80, 8080, 8085 o 5432):** si ya tenés algo corriendo en esos puertos,
  parenlo o avisá para ajustar los puertos en `docker-compose.yml`. En Mac, el puerto 5000 lo
  suele ocupar "AirPlay Receiver" (no lo usamos acá, pero si alguna vez cambian el mapeo de
  puertos y pisa el 5000, se desactiva en Preferencias del Sistema → General → AirDrop y
  Handoff).
- **"zsh: command not found: docker":** abrí Docker Desktop primero (tiene que quedar el
  ícono de la ballena 🐳 arriba en la barra de menú) y volvé a intentar.
- **Mac con Apple Silicon (M1/M2/M3/M4) y algo arranca muy lento o se cuelga:** es la
  emulación de `amd64` con Rosetta 2. Si es un problema recurrente, avisá para armar también
  una versión `arm64` de las imágenes.
