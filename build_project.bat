@echo off
echo Setting JAVA_HOME for build...
set JAVA_HOME=C:\Program Files\Java\jdk-24
echo JAVA_HOME set to: %JAVA_HOME%

echo Running Gradle clean...
gradlew.bat clean

echo Running Gradle build...
gradlew.bat assembleDebug

echo Build completed!
pause
