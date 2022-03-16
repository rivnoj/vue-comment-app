@echo off
set address=3
set command=R

if "%~1" equ "" (
	echo Porta serial não informada
	java -jar jar/mttr-simulator-1.0-SNAPSHOT.jar
	goto :DONE
)
	
java -jar jar/mttr-simulator-1.0-SNAPSHOT.jar %1 %address% %command%

:DONE
@echo on