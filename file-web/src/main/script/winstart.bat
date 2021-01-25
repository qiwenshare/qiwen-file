@echo off & setlocal enabledelayedexpansion
rem if need special jdk
rem JAVA_HOME="c:\Java\jdk1.8.0_191\"
rem PATH=%JAVA_HOME%\bin;%PATH%

rem enter app deploy path
cd ..\

set DEPLOY_PATH=%cd%
set CONF_DIR=%DEPLOY_PATH%/conf
set LIB_JARS=%DEPLOY_PATH%/lib/*
set LOG_PATH=%DEPLOY_PATH%/logs

set CLASSPATH=".;%CONF_DIR%;%LIB_JARS%"

set JAVA_VM=-D64 -server -Xmx512m -Xms512m  -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC 
set JAVA_OPTIONS=-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=8231

echo DEPLOY_PATH: %DEPLOY_PATH%
echo CLASSPATH=%CLASSPATH%
echo "Start App..."

java -version
java  %JAVA_VM% %JAVA_OPTIONS% -classpath %CLASSPATH% -Xdebug  -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8336 com.qiwenshare.file.FileApplication

goto end

:end
pause