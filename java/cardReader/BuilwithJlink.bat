@echo off
echo ============================================
echo   Building CardReader Portable App
echo ============================================
echo.

set APP_NAME=CardReader
set MAIN_CLASS=org.akash.Main

REM ------------------------------------------------
REM Step 1: Build with Maven
REM ------------------------------------------------
echo [1/4] Building with Maven...
call mvn clean package
if errorlevel 1 (
    echo ERROR: Maven build failed!
    pause
    exit /b 1
)

REM ------------------------------------------------
REM Step 2: Create custom JRE with jlink
REM ------------------------------------------------
echo.
echo [2/4] Creating custom JRE...
if exist "custom-runtime" rmdir /s /q "custom-runtime"

jlink ^
  --module-path "%JAVA_HOME%\jmods" ^
  --add-modules java.base,java.logging,java.smartcardio,jdk.crypto.ec ^
  --output custom-runtime ^
  --strip-debug ^
  --compress 2 ^
  --no-header-files ^
  --no-man-pages

if errorlevel 1 (
    echo ERROR: jlink failed!
    pause
    exit /b 1
)

REM ------------------------------------------------
REM Step 3: Portable structure
REM ------------------------------------------------
echo.
echo [3/4] Creating portable structure...
if exist "portable-app" rmdir /s /q "portable-app"

mkdir "portable-app\%APP_NAME%"
mkdir "portable-app\%APP_NAME%\jre"

xcopy "custom-runtime" "portable-app\%APP_NAME%\jre\" /E /I /Q >nul
copy "target\cardReader-1.0-SNAPSHOT.jar" "portable-app\%APP_NAME%\" >nul

REM ------------------------------------------------
REM Step 4: Launcher
REM ------------------------------------------------
echo.
echo [4/4] Creating launcher...

(
echo @echo off
echo cd /d "%%~dp0"
echo echo Starting %APP_NAME%...
echo jre\bin\java.exe -cp cardReader-1.0-SNAPSHOT.jar %MAIN_CLASS%
echo pause
) > "portable-app\%APP_NAME%\%APP_NAME%.bat"

REM Cleanup
rmdir /s /q "custom-runtime"

echo.
echo ============================================
echo   BUILD COMPLETE
echo ============================================
echo.
echo Location: portable-app\%APP_NAME%\
echo.
pause
