REM change directory to symja installation folder
REM cd /temp/symja
REM run start-symja.bat
SET "JAVA_HOME=C:\Program Files\Java\jdk-11.0.12"

"%JAVA_HOME%\bin\jshell" --class-path "/temp/symja/lib/*" --startup start-symja.jsh