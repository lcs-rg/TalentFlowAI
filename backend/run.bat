@echo off
set JAVA_HOME=C:\Users\lucas\jdk-21.0.6+7
set PATH=C:\Users\lucas\jdk-21.0.6+7\bin;%PATH%
echo Usando: %JAVA_HOME%\bin\java
"%JAVA_HOME%\bin\java" -version 2>&1
echo.
cd /d "D:\Curriculo\Projetos currículo\TalentFlowAI\backend"
set M2_HOME=C:\Users\lucas\apache-maven-3.9.9
set PATH=%JAVA_HOME%\bin;%M2_HOME%\bin;%PATH%
"%JAVA_HOME%\bin\java" -classpath "%M2_HOME%\boot\plexus-classworlds-2.6.0.jar" -Dclassworlds.conf="%M2_HOME%\bin\m2.conf" -Dmaven.home="%M2_HOME%" -Dmaven.multiModuleProjectDirectory="%CD%" org.codehaus.plexus.classworlds.launcher.Launcher spring-boot:run -DskipTests
