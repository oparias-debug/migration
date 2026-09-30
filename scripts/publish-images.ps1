<#
.SYNOPSIS
    Buildea las imágenes Docker de backend-srv, admin-srv, api-gateway, front y keycloak, y las publica en
    GitHub Container Registry (ghcr.io) para compartirlas de forma privada con un tester
    que no tiene el repo ni el entorno de desarrollo levantado.

.DESCRIPTION
    Genera:
      ghcr.io/<Owner>/siip-back:<Tag>
      ghcr.io/<Owner>/siip-admin-srv:<Tag>
      ghcr.io/<Owner>/siip-api-gateway:<Tag>
      ghcr.io/<Owner>/siip-front:<Tag>
      ghcr.io/<Owner>/siip-keycloak:<Tag>

    "postgres" y "sonarqube" NO se publican acá: son imágenes públicas que el tester baja
    directo de Docker Hub con el compose de dist-tester/ (ver dist-tester/README.md).

    Requisito previo (una sola vez, con un Personal Access Token con scope "write:packages"):
      docker login ghcr.io -u <tu-usuario-de-github>

.PARAMETER Tag
    Tag a usar para las 5 imágenes (default: "latest").

.PARAMETER Owner
    Owner de ghcr.io (default: "david-magnaperita", dueño del repo en GitHub).

.PARAMETER SkipMavenBuild
    No corre "mvn clean package -DskipTests" antes de armar las imágenes de backend-srv/admin-srv/api-gateway.
    Usalo si ya tenés los .jar generados y solo querés reconstruir las imágenes Docker.

.PARAMETER SkipFrontBuild
    No corre "docker compose run --rm front-build" antes de armar la imagen de front.
    Usalo si front/dist/ ya está compilado y solo querés reconstruir la imagen Docker.

.PARAMETER SkipPush
    Buildea las imágenes pero no las sube (para probar el build en local antes de publicar).

.EXAMPLE
    .\scripts\publish-images.ps1
    Build + push de las 4 imágenes con tag "latest".

.EXAMPLE
    .\scripts\publish-images.ps1 -Tag 2026-09-05 -SkipMavenBuild
    Reusa los .jar ya compilados y publica con un tag fechado (además de "latest", quedan
    ambos disponibles para volver a una versión anterior si hace falta).
#>
[CmdletBinding()]
param(
    [string]$Tag = 'latest',
    [string]$Owner = 'david-magnaperita',
    [switch]$SkipMavenBuild,
    [switch]$SkipFrontBuild,
    [switch]$SkipPush
)

$repoRoot = Split-Path -Parent $PSScriptRoot
$registry = "ghcr.io/$Owner"
# Carpeta del módulo -> nombre de la imagen en GHCR. backend-srv conserva la imagen
# siip-back (el paquete al que ya tiene acceso el tester); renombrarla crearía un paquete nuevo.
$services = [ordered]@{ 'backend-srv' = 'back'; 'admin-srv' = 'admin-srv'; 'api-gateway' = 'api-gateway'; 'front' = 'front'; 'keycloak' = 'keycloak' }
$results = @()

function Invoke-Step {
    param(
        [string]$Name,
        [scriptblock]$Action
    )

    Write-Host ""
    Write-Host "=== $Name ===" -ForegroundColor Cyan
    & $Action
    $exitCode = $LASTEXITCODE
    $script:results += [pscustomobject]@{ Step = $Name; Success = ($exitCode -eq 0) }
    if ($exitCode -ne 0) {
        Write-Host "FALLO: $Name (exit code $exitCode)" -ForegroundColor Red
    }
}

Push-Location $repoRoot
try {
    # Los Dockerfile de backend-srv, admin-srv y api-gateway solo copian target/*.jar (no compilan
    # dentro de Docker), así que hace falta el jar ya generado antes del build de imagen.
    # No hay pom agregador en la raíz: cada módulo se compila por separado.
    if (-not $SkipMavenBuild) {
        foreach ($module in @('backend-srv', 'admin-srv', 'api-gateway')) {
            Push-Location (Join-Path $repoRoot $module)
            try {
                Invoke-Step -Name "$($module): mvn clean package -DskipTests" -Action { & mvn clean package -DskipTests }
            } finally {
                Pop-Location
            }
        }
    }

    # front/Dockerfile tampoco compila: empaqueta front/dist/ (en la entidad lo deja el
    # pipeline Tekton). Se compila con el servicio front-build de docker-compose.yml, así
    # que no hace falta Node ni Java en el host.
    if (-not $SkipFrontBuild) {
        Invoke-Step -Name 'front: docker compose run --rm front-build' -Action { & docker compose run --rm --build front-build }
    }

    foreach ($service in $services.Keys) {
        $image = "$registry/siip-$($services[$service]):$Tag"
        # El Dockerfile de backend-srv y admin-srv es el del ambiente de la entidad (imagen base en
        # el registry interno de MH); fuera de esa red se usa su Dockerfile.local.
        $dockerfile = if ($service -in @('backend-srv', 'admin-srv')) { "./$service/Dockerfile.local" } else { "./$service/Dockerfile" }
        Invoke-Step -Name "docker build $service -> $image" -Action { & docker build -t $image -f $dockerfile "./$service" }

        if (-not $SkipPush) {
            Invoke-Step -Name "docker push $image" -Action { & docker push $image }
        }
    }
} finally {
    Pop-Location
}

Write-Host ""
Write-Host "=== Resumen ===" -ForegroundColor Cyan
foreach ($result in $results) {
    $color = if ($result.Success) { 'Green' } else { 'Red' }
    $status = if ($result.Success) { 'OK' } else { 'FALLO' }
    Write-Host ("{0,-45} {1}" -f $result.Step, $status) -ForegroundColor $color
}

if ($results | Where-Object { -not $_.Success }) {
    exit 1
}
exit 0
