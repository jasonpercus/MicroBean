@echo off

for /f "delims=" %%A in ('powershell -NoProfile -ExecutionPolicy Bypass -Command "Add-Type -AssemblyName PresentationFramework; & '%~dp0OptionPane.ps1'"') do (
    set valeur=%%A
)

if "%valeur%"=="" (
    echo Aucune version saisie. Arret du script.
    exit /b 1
)

echo Valeur saisie : %valeur%

mvn versions:set -DnewVersion=%valeur% -DgenerateBackupPoms=false
mvn clean verify