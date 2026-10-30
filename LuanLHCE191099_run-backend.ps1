$ErrorActionPreference = 'Stop'
Push-Location (Join-Path $PSScriptRoot 'backend')
try {
    if (-not (Test-Path -LiteralPath 'application-local.properties')) {
        throw 'Copy application-local.properties.example to application-local.properties and fill in the SQL connection first.'
    }
    & .\mvnw.cmd spring-boot:run
    exit $LASTEXITCODE
} finally { Pop-Location }
