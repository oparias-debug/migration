<#
.SYNOPSIS
    Corre las pruebas de backend-srv y api-gateway (Maven) y de front (Vitest) en un solo comando.

.DESCRIPTION
    Equivalente a correr a mano:
      mvn clean verify                  (dentro de backend-srv/ y dentro de api-gateway/)
      npm run generate:api && npm run test   (desde front/)
    "generate:api" corre siempre antes de "test": front/src/api/generated se versiona, pero
    puede quedar desincronizado de los .yaml; así los tests corren contra los contratos
    actuales (ver front/docs/desarrollo.md).

.PARAMETER SkipBack
    No corre las pruebas de backend-srv ni de api-gateway.

.PARAMETER SkipFront
    No corre las pruebas de front.

.PARAMETER NoClean
    Corre "mvn verify" en vez de "mvn clean verify". Más rápido, pero el build incremental
    de Maven a veces deja artefactos generados (MapStruct) corruptos entre corridas.

.PARAMETER Lint
    Además corre "npm run lint" en front.

.PARAMETER Build
    Además corre "npm run build" en front.

.EXAMPLE
    .\scripts\run-tests.ps1
    Corre backend-srv, api-gateway y front completos (con "mvn clean verify").

.EXAMPLE
    .\scripts\run-tests.ps1 -SkipBack -Lint -Build
    Solo front, agregando lint y build (el checklist de Definition of Done del CONTRIBUTING.md).
#>
[CmdletBinding()]
param(
    [switch]$SkipBack,
    [switch]$SkipFront,
    [switch]$NoClean,
    [switch]$Lint,
    [switch]$Build
)

$repoRoot = Split-Path -Parent $PSScriptRoot
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

if (-not $SkipBack) {
    # backend-srv y api-gateway son proyectos Maven independientes (cada uno con su propio repo en
    # la entidad), no hay pom agregador en la raíz: se corre mvn dentro de cada uno.
    $mvnArgs = @()
    if (-not $NoClean) { $mvnArgs += 'clean' }
    $mvnArgs += 'verify'
    foreach ($module in @('backend-srv', 'api-gateway')) {
        Push-Location (Join-Path $repoRoot $module)
        try {
            Invoke-Step -Name "$($module): mvn $($mvnArgs -join ' ')" -Action { & mvn @mvnArgs }
        } finally {
            Pop-Location
        }
    }
}

if (-not $SkipFront) {
    Push-Location (Join-Path $repoRoot 'front')
    try {
        Invoke-Step -Name 'Front: npm run generate:api' -Action { & npm run generate:api }
        if ($Lint) {
            Invoke-Step -Name 'Front: npm run lint' -Action { & npm run lint }
        }
        Invoke-Step -Name 'Front: npm run test' -Action { & npm run test }
        if ($Build) {
            Invoke-Step -Name 'Front: npm run build' -Action { & npm run build }
        }
    } finally {
        Pop-Location
    }
}

Write-Host ""
Write-Host "=== Resumen ===" -ForegroundColor Cyan
foreach ($result in $results) {
    $color = if ($result.Success) { 'Green' } else { 'Red' }
    $status = if ($result.Success) { 'OK' } else { 'FALLO' }
    Write-Host ("{0,-40} {1}" -f $result.Step, $status) -ForegroundColor $color
}

if ($results | Where-Object { -not $_.Success }) {
    exit 1
}
exit 0
