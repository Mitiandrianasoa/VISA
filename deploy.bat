@echo off
REM Script de déploiement complet : Backend Visa + Frontoffice Sprint

REM ========================================
REM CONFIGURATION
REM ========================================
REM Modifiez ces variables selon votre environnement

REM Chemin vers votre installation Tomcat
set TOMCAT_HOME=E:\Logiciel\apache-tomcat-10.1.34

REM ========================================
REM DEPLOIEMENT
REM ========================================

echo ========================================
echo Deploiement complet VISA
echo ========================================
echo.
echo Configuration:
echo - Tomcat: %TOMCAT_HOME%
echo.

REM ========================================
REM 1. Demarrage du Backend Visa
echo ========================================
echo.
echo [1/4] Demarrage du backend Visa (Spring Boot)...
cd visa\visa

echo Verification de la base de donnees PostgreSQL...
echo Assurez-vous que PostgreSQL est demarre sur localhost:5433
echo.

echo Demarrage de l'application Spring Boot...
start "Backend Visa" cmd /k "mvnw.cmd spring-boot:run"

echo Backend demarre dans une nouvelle fenetre
echo Attente de 10 secondes pour que le backend demarre...
timeout /t 10 /nobreak

cd ..\..

REM ========================================
REM 2. Compilation du Frontoffice Sprint
echo ========================================
echo.
echo [2/4] Compilation du frontoffice Sprint...
cd sprint

call mvn clean package

if %ERRORLEVEL% neq 0 (
    echo Erreur lors de la compilation Maven
    pause
    exit /b 1
)

echo Compilation terminee avec succes

REM ========================================
REM 3. Deploiement du Frontoffice sur Tomcat
echo ========================================
echo.
echo [3/4] Deploiement du frontoffice sur Tomcat...

if not exist "%TOMCAT_HOME%" (
    echo Le dossier Tomcat n'existe pas: %TOMCAT_HOME%
    echo Veuillez modifier TOMCAT_HOME dans ce script
    pause
    exit /b 1
)

echo Copie du WAR vers Tomcat...
copy /Y target\sprint-framework.war "%TOMCAT_HOME%\webapps\"

if %ERRORLEVEL% neq 0 (
    echo Erreur lors de la copie du WAR
    pause
    exit /b 1
)

cd ..

REM ========================================
REM 4. Instructions finales
echo ========================================
echo.
echo [4/4] Deploiement termine !
echo.
echo ========================================
echo RESUME
echo ========================================
echo.
echo Backend Visa  : Demarre sur http://localhost:8000
echo Frontoffice   : http://localhost:8080/sprint-framework/demande/nouveau
echo.
echo Pour demarrer Tomcat, executez: %TOMCAT_HOME%\bin\startup.bat
echo.
echo ========================================

pause
