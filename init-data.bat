@echo off
echo ================================
echo Insertion des donnees de test
echo ================================
echo.
echo Assurez-vous que PostgreSQL est demarre sur localhost:5433
echo.

set PGPASSWORD=etu003146

psql -h localhost -p 5433 -U etu003146 -d visa -f base\data-visa.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Donnees inserees avec succes !
) else (
    echo.
    echo Erreur lors de l'insertion des donnees.
)

pause
