<#
.SYNOPSIS
    Compara los contratos OpenAPI y los .feature de front con los de backend-srv y admin-srv.

.DESCRIPTION
    front tiene copias de los .yaml (front/openapi/) y de los .feature (front/features/) que son
    de backend-srv o de admin-srv. Este script empareja cada copia con su original por nombre de
    archivo (la carpeta no importa) y dice si están iguales, distintas o si falta alguna.

    Antes de comparar normaliza el fin de línea (CRLF/LF) y los espacios al final de cada línea,
    para que un cambio de editor no aparezca como diferencia.

    Estados:
      IGUAL          misma versión en el servicio y en front.
      DISTINTO       el contenido difiere: hay que decidir cuál es la buena y copiarla.
      SOLO-FRONT     front lo tiene y ningún servicio: renombrado, borrado o propio del front.
      SOLO-SERVICIO  el servicio lo tiene y front no: puede ser normal si front no lo usa.
      DUPLICADO      backend-srv y admin-srv tienen un archivo con el mismo nombre.

    Termina con código 1 si hay algún DISTINTO o DUPLICADO; si no, con 0.

.PARAMETER Tipo
    Qué comparar: Contratos (.openapi.yaml), Features (.feature) o Todo (por defecto).

.PARAMETER SoloDiferencias
    No lista los archivos IGUAL.

.PARAMETER IncluirSoloServicio
    Lista también los SOLO-SERVICIO (por defecto solo se cuentan en el total: front/features es
    un subconjunto de los .feature de los servicios, así que suelen ser muchos y normales).

.PARAMETER Diff
    Muestra el diff (git diff --no-index) de cada archivo DISTINTO. "-" es el servicio, "+" es front.

.EXAMPLE
    .\scripts\comparar-contratos.ps1
    Resumen completo de contratos y features.

.EXAMPLE
    .\scripts\comparar-contratos.ps1 -Tipo Contratos -SoloDiferencias -Diff
    Solo los .yaml que no coinciden, con su diff.
#>
[CmdletBinding()]
param(
    [ValidateSet('Todo', 'Contratos', 'Features')]
    [string]$Tipo = 'Todo',
    [switch]$SoloDiferencias,
    [switch]$IncluirSoloServicio,
    [switch]$Diff
)

$ErrorActionPreference = 'Stop'
$raiz = Split-Path -Parent $PSScriptRoot

$grupos = @(
    @{
        Nombre    = 'Contratos OpenAPI'
        Clave     = 'Contratos'
        Filtro    = '*.openapi.yaml'
        Servicios = [ordered]@{
            'backend-srv' = 'backend-srv/src/main/resources/openapi'
            'admin-srv'   = 'admin-srv/src/main/resources/openapi'
        }
        Front     = 'front/openapi'
    },
    @{
        Nombre    = 'Features (BDD)'
        Clave     = 'Features'
        Filtro    = '*.feature'
        Servicios = [ordered]@{
            'backend-srv' = 'backend-srv/src/test/resources/features'
            'admin-srv'   = 'admin-srv/src/test/resources/features'
        }
        Front     = 'front/features'
    }
)

function Get-Archivos([string]$carpeta, [string]$filtro) {
    $ruta = Join-Path $raiz $carpeta
    if (-not (Test-Path $ruta)) {
        Write-Warning "No existe la carpeta $carpeta"
        return @()
    }
    return @(Get-ChildItem -Path $ruta -Filter $filtro -Recurse -File)
}

function Get-ContenidoNormalizado([string]$ruta) {
    $texto = [System.IO.File]::ReadAllText($ruta, [System.Text.Encoding]::UTF8)
    $lineas = $texto -replace "`r`n", "`n" -split "`n" | ForEach-Object { $_.TrimEnd() }
    return ($lineas -join "`n").TrimEnd()
}

function Get-RutaRelativa([string]$ruta) {
    return $ruta.Substring($raiz.Length + 1).Replace('\', '/')
}

$colores = @{ 'IGUAL' = 'Green'; 'DISTINTO' = 'Red'; 'DUPLICADO' = 'Red'; 'SOLO-FRONT' = 'Yellow'; 'SOLO-SERVICIO' = 'DarkYellow' }
$hayProblemas = $false

foreach ($grupo in $grupos) {
    if ($Tipo -ne 'Todo' -and $Tipo -ne $grupo.Clave) { continue }

    # Originales por nombre de archivo: nombre -> lista de @{ Servicio; Ruta }
    $originales = @{}
    foreach ($servicio in $grupo.Servicios.Keys) {
        foreach ($archivo in Get-Archivos $grupo.Servicios[$servicio] $grupo.Filtro) {
            if (-not $originales.ContainsKey($archivo.Name)) { $originales[$archivo.Name] = @() }
            $originales[$archivo.Name] += @{ Servicio = $servicio; Ruta = $archivo.FullName }
        }
    }
    $copias = @{}
    foreach ($archivo in Get-Archivos $grupo.Front $grupo.Filtro) { $copias[$archivo.Name] = $archivo.FullName }

    $filas = @()
    $nombres = @($originales.Keys) + @($copias.Keys) | Sort-Object -Unique
    foreach ($nombre in $nombres) {
        $enServicio = $originales[$nombre]
        $enFront = $copias[$nombre]
        $servicio = ''
        if ($enServicio) { $servicio = ($enServicio | ForEach-Object { $_.Servicio }) -join ', ' }

        if ($enServicio -and $enServicio.Count -gt 1) {
            $estado = 'DUPLICADO'
        } elseif ($enServicio -and $enFront) {
            $igual = (Get-ContenidoNormalizado $enServicio[0].Ruta) -ceq (Get-ContenidoNormalizado $enFront)
            if ($igual) { $estado = 'IGUAL' } else { $estado = 'DISTINTO' }
        } elseif ($enFront) {
            $estado = 'SOLO-FRONT'
        } else {
            $estado = 'SOLO-SERVICIO'
        }
        $filas += [pscustomobject]@{ Estado = $estado; Archivo = $nombre; Servicio = $servicio; Original = $enServicio; Copia = $enFront }
    }

    Write-Host ''
    Write-Host "== $($grupo.Nombre): front/ frente a $(@($grupo.Servicios.Keys) -join ' y ')" -ForegroundColor Cyan
    foreach ($fila in $filas) {
        if ($SoloDiferencias -and $fila.Estado -eq 'IGUAL') { continue }
        if (-not $IncluirSoloServicio -and $fila.Estado -eq 'SOLO-SERVICIO') { continue }
        $detalle = ''
        if ($fila.Servicio) { $detalle = "  ($($fila.Servicio))" }
        Write-Host ('  {0,-14} {1}{2}' -f $fila.Estado, $fila.Archivo, $detalle) -ForegroundColor $colores[$fila.Estado]

        if ($Diff -and $fila.Estado -eq 'DISTINTO') {
            $original = Get-RutaRelativa $fila.Original[0].Ruta
            $copia = Get-RutaRelativa $fila.Copia
            Push-Location $raiz
            try {
                git --no-pager diff --no-index --ignore-space-at-eol --stat --patch -- $original $copia | Out-Host
            } finally {
                Pop-Location
            }
        }
    }

    $resumen = $filas | Group-Object Estado | Sort-Object Name | ForEach-Object { "$($_.Name): $($_.Count)" }
    Write-Host "  Total $($filas.Count) -> $($resumen -join ' | ')"
    if (-not $IncluirSoloServicio -and ($filas | Where-Object { $_.Estado -eq 'SOLO-SERVICIO' })) {
        Write-Host '  (los SOLO-SERVICIO no se listan; usar -IncluirSoloServicio)' -ForegroundColor DarkGray
    }
    if ($filas | Where-Object { $_.Estado -in @('DISTINTO', 'DUPLICADO') }) { $hayProblemas = $true }
}

Write-Host ''
if ($hayProblemas) {
    Write-Host 'Hay copias desincronizadas. Definir cuál es la versión buena, copiarla y, si es un .yaml, correr "npm run generate:api" en front.' -ForegroundColor Red
    exit 1
}
Write-Host 'Las copias de front coinciden con sus originales.' -ForegroundColor Green
exit 0
