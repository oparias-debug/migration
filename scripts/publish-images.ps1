<#
.SYNOPSIS
    Buildea las imágenes Docker de back, api-gateway, front y keycloak, y las publica en
    GitHub Container Registry (ghcr.io) para compartirlas de forma privada con un tester
    que no tiene el repo ni el entorno de desarrollo levantado.

.DESCRIPTION
    Genera:
      ghcr.io/<Owner>/siip-back:<Tag>
      ghcr.io/<Owner>/siip-api-gateway:<Tag>
      ghcr.io/<Owner>/siip-front:<Tag>
      ghcr.io/<Owner>/siip-keycloak:<Tag>

    "postgres" y "sonarqube" NO se publican acá: son imágenes públicas que el tester baja
    directo de Docker Hub con el compose de dist-tester/ (ver dist-tester/README.md).

    Requisito previo (una sola vez, con un Personal Access Token con scope "write:packages"):
      docker login ghcr.io -u <tu-usuario-de-github>

.PARAMETER Tag
    Tag a usar para las 4 imágenes (default: "latest").

.PARAMETER Owner
    Owner de ghcr.io (default: "david-magnaperita", dueño del repo en GitHub).

.PARAMETER SkipMavenBuild
    No corre "mvn clean package -DskipTests" antes de armar las imágenes de back/api-gateway.
    Usalo si ya tenés los .jar generados y solo querés reconstruir las imágenes Docker.

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
    [switch]$SkipPush
)

$repoRoot = Split-Path -Parent $PSScriptRoot
$registry = "ghcr.io/$Owner"
$services = @('back', 'api-gateway', 'front', 'keycloak')
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
    # back/Dockerfile y api-gateway/Dockerfile solo copian target/*.jar (no compilan
    # dentro de Docker), así que hace falta el jar ya generado antes del build de imagen.
    if (-not $SkipMavenBuild) {
        Invoke-Step -Name 'mvn clean package -DskipTests' -Action { & mvn clean package -DskipTests }
    }

    foreach ($service in $services) {
        $image = "$registry/siip-$($service):$Tag"
        Invoke-Step -Name "docker build $service -> $image" -Action { & docker build -t $image "./$service" }

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
