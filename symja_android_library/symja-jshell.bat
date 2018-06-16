REM change directory to symja installation folder
REM cd /symja
REM run symja Console
SET "JAVA_HOME=C:\Program Files\Java\jdk-10.0.1"

"%JAVA_HOME%\bin\jshell" --class-path "/temp/symja/lib/*" --startup start-symja.jsh