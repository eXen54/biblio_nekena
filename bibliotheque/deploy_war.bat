@echo off
ECHO Starting WAR file deployment process...

:: Set project-specific variables
SET PROJECT_DIR=C:\Users\itu\Documents\Projet\s4\Web-Dyn\EXAM2\biblio_nekena\bibliotheque
SET TOMCAT_DIR=C:\Users\itu\Documents\Projet\s4\Web-Dyn\apache-tomcat-10.1.28
SET WAR_FILE=target\bibliotheque-0.0.1-SNAPSHOT.war
SET WEBAPPS_DIR=%TOMCAT_DIR%\webapps
SET CATALINA_HOME=%TOMCAT_DIR%

:: Step 1: Change to project directory
cd /d %PROJECT_DIR%
IF %ERRORLEVEL% NEQ 0 (
    ECHO Failed to change to project directory: %PROJECT_DIR%
    pause
    exit /b %ERRORLEVEL%
)

:: Step 2: Compile the project with tests skipped to ensure WAR file
ECHO Compiling the project (skipping tests)...
call mvn clean package -DskipTests
IF %ERRORLEVEL% NEQ 0 (
    ECHO Maven build failed!
    pause
    exit /b %ERRORLEVEL%
)

:: Step 3: Check if the .war file exists
IF NOT EXIST %WAR_FILE% (
    ECHO WAR file not found at %WAR_FILE%. Ensure pom.xml has <packaging>war</packaging>.
    pause
    exit /b 1
)

:: Step 4: Clean previous deployment (quietly)
ECHO Cleaning previous deployment...
del /Q %WEBAPPS_DIR%\bibliotheque-0.0.1-SNAPSHOT.war 2>nul
rmdir /S /Q %WEBAPPS_DIR%\bibliotheque-0.0.1-SNAPSHOT 2>nul

:: Step 5: Copy the .war file to Tomcat webapps directory
ECHO Deploying WAR file to Tomcat...
copy %WAR_FILE% %WEBAPPS_DIR%
IF %ERRORLEVEL% NEQ 0 (
    ECHO Failed to copy WAR file to %WEBAPPS_DIR%
    pause
    exit /b %ERRORLEVEL%
)

:: Step 6: Set CATALINA_HOME and start Tomcat
ECHO Setting CATALINA_HOME to %CATALINA_HOME%
set CATALINA_HOME=%TOMCAT_DIR%
ECHO Starting Tomcat...
call %TOMCAT_DIR%\bin\startup.bat
IF %ERRORLEVEL% NEQ 0 (
    ECHO Failed to start Tomcat! Check %TOMCAT_DIR%\logs\catalina.out for details.
    pause
    exit /b %ERRORLEVEL%
)

ECHO Deployment successful! Access the application at http://localhost:8080/bibliotheque-0.0.1-SNAPSHOT/
pause