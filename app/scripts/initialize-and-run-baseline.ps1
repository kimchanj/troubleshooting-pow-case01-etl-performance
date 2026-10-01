$ErrorActionPreference = 'Stop'

$appDirectory = Split-Path -Parent $PSScriptRoot
$sqlPlusCommand = Get-Command 'sqlplus.exe' -ErrorAction SilentlyContinue
if ($null -ne $sqlPlusCommand) {
    $sqlPlus = $sqlPlusCommand.Source
} else {
    $oracleHome = (Get-ItemProperty 'HKLM:\SOFTWARE\ORACLE\KEY_OraDB23Home1').ORACLE_HOME
    $sqlPlus = Join-Path $oracleHome 'bin\sqlplus.exe'
}
if (-not (Test-Path -LiteralPath $sqlPlus)) {
    throw 'sqlplus.exe was not found in PATH or the configured Oracle home.'
}
$securePassword = Read-Host 'Enter the current ETL_LAB password' -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

try {
    $plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)

    function Invoke-EtlSqlScript([string]$scriptPath) {
        $commands = @(
            'SET ECHO OFF'
            'SET VERIFY OFF'
            "CONNECT ETL_LAB/`"$plainPassword`"@//localhost:1521/FREEPDB1"
            "@$scriptPath"
        )
        $commands | & $sqlPlus -s -L /NOLOG
        if ($LASTEXITCODE -ne 0) {
            throw "SQL*Plus failed while running $scriptPath"
        }
    }

    Invoke-EtlSqlScript (Join-Path $appDirectory 'db\schema.sql')
    Invoke-EtlSqlScript (Join-Path $appDirectory 'db\seed-reference.sql')

    $env:ETL_DB_USERNAME = 'ETL_LAB'
    $env:ETL_DB_PASSWORD = $plainPassword
    $env:ETL_INPUT_FILE = Join-Path $appDirectory 'fixtures\baseline-shipments.csv'

    Push-Location $appDirectory
    try {
        & .\mvnw.cmd spring-boot:run
        if ($LASTEXITCODE -ne 0) {
            throw 'Baseline Spring Boot run failed.'
        }
    } finally {
        Pop-Location
    }

    Write-Host 'STEP 2 local schema, seed, and baseline import completed.'
} finally {
    Remove-Item Env:ETL_DB_USERNAME -ErrorAction SilentlyContinue
    Remove-Item Env:ETL_DB_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:ETL_INPUT_FILE -ErrorAction SilentlyContinue
    if ($null -ne $passwordPointer) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    }
    $plainPassword = $null
    $securePassword.Dispose()
}
