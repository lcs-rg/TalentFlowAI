@echo off
setlocal
set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9
set MVNW_REPOURL=https://repo.maven.apache.org/maven2

if not defined JAVA_HOME (
    echo JAVA_HOME not set.
    exit /b 1
)

if not exist "%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9\bin\mvn.cmd" (
    echo Downloading Maven...
    powershell -Command "Invoke-WebRequest -Uri '%MVNW_REPOURL%/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip' -OutFile '%TEMP%\maven.zip'"
    powershell -Command "Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force"
    del "%TEMP%\maven.zip"
)

"%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9\bin\mvn.cmd" %*
