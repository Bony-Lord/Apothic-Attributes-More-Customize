@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

set "GRADLE_VERSION=8.10.2"
set "LOCAL_ROOT=%CD%\.gradle-local"
set "LOCAL_HOME=%LOCAL_ROOT%\gradle-%GRADLE_VERSION%"
set "LOCAL_GRADLE=%LOCAL_HOME%\bin\gradle.bat"
set "GRADLE_ZIP=%LOCAL_ROOT%\gradle-%GRADLE_VERSION%-bin.zip"
set "GRADLE_URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip"

echo Apothic Attributes: More Customize - NeoForge 1.21.1 build
echo.

echo [1/4] Checking Java 21...
set "JAVA_VERSION="
for /f "tokens=3" %%V in ('java -version 2^>^&1 ^| findstr /i "version"') do set "JAVA_VERSION=%%~V"
if not defined JAVA_VERSION (
    echo ERROR: Java was not found in PATH.
    echo Install a 64-bit Java 21 JDK and run this file again.
    exit /b 1
)
if not "!JAVA_VERSION:~0,3!"=="21." (
    echo ERROR: Java 21 is required, but Java !JAVA_VERSION! is active.
    echo Put a Java 21 JDK first in PATH and run this file again.
    exit /b 1
)
echo Found Java !JAVA_VERSION!.

echo [2/4] Checking Gradle...
where gradle >nul 2>nul
if not errorlevel 1 (
    echo Found Gradle in PATH. The system installation will be used.
    set "USE_SYSTEM_GRADLE=1"
    goto :build
)

if exist "%LOCAL_GRADLE%" (
    echo Found local Gradle %GRADLE_VERSION%.
    goto :build
)

echo Gradle was not found. Installing Gradle %GRADLE_VERSION% locally into:
echo %LOCAL_HOME%
if not exist "%LOCAL_ROOT%" mkdir "%LOCAL_ROOT%"

echo Downloading Gradle...
powershell.exe -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -UseBasicParsing -Uri '%GRADLE_URL%' -OutFile '%GRADLE_ZIP%'"
if errorlevel 1 (
    echo PowerShell download failed. Trying curl...
    curl.exe --fail --location --output "%GRADLE_ZIP%" "%GRADLE_URL%"
    if errorlevel 1 goto :download_error
)

echo Extracting Gradle...
powershell.exe -NoProfile -ExecutionPolicy Bypass -Command ^
    "Expand-Archive -LiteralPath '%GRADLE_ZIP%' -DestinationPath '%LOCAL_ROOT%' -Force"
if errorlevel 1 goto :extract_error

if not exist "%LOCAL_GRADLE%" goto :extract_error
del /q "%GRADLE_ZIP%" >nul 2>nul
echo Local Gradle installation completed.

:build
echo [3/4] Cleaning and building the project...
set "GRADLE_USER_HOME=%LOCAL_ROOT%\cache"
if defined USE_SYSTEM_GRADLE (
    call gradle --no-daemon clean build
) else (
    call "%LOCAL_GRADLE%" --no-daemon clean build
)
if errorlevel 1 goto :build_error

echo [4/4] Build completed successfully.
echo JAR files are located in:
echo %CD%\build\libs
exit /b 0

:download_error
echo ERROR: Gradle could not be downloaded from:
echo %GRADLE_URL%
echo Check the internet connection and try again.
exit /b 1

:extract_error
echo ERROR: The downloaded Gradle archive could not be extracted.
echo Delete the .gradle-local folder and run this file again.
exit /b 1

:build_error
echo ERROR: The Gradle build failed with exit code %errorlevel%.
exit /b %errorlevel%
