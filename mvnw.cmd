@REM ----------------------------------------------------------------------------
@REM Maven Wrapper 启动脚本 (Windows)
@REM ----------------------------------------------------------------------------

@if "%DEBUG%"=="" @echo off
@REM 找到项目根目录
set "DIRNAME=%~dp0"
if "%DIRNAME%"=="" set DIRNAME=.
set "MAVEN_PROJECTBASEDIR=%DIRNAME%"
@REM 查找 maven-wrapper.jar
set "WRAPPER_JAR=%DIRNAME%.mvn\wrapper\maven-wrapper.jar"
if not exist "%WRAPPER_JAR%" (
    echo Maven Wrapper JAR not found: %WRAPPER_JAR%
    echo Please download it from https://repo.maven.apache.org/
    exit /b 1
)

@REM 启动 Maven Wrapper
@"%JAVA_HOME%\bin\java.exe" ^
    -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" ^
    -jar "%WRAPPER_JAR%" %*

@REM 如果 java 不在 JAVA_HOME 中，尝试直接使用 PATH 中的 java
if %ERRORLEVEL% neq 0 (
    java -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -jar "%WRAPPER_JAR%" %*
)
