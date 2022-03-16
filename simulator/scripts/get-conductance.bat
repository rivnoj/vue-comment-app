@echo off

if "%~1" equ "" (
	echo Porta serial não informada
	java -jar jar/mttr-simulator-1.0-SNAPSHOT.jar
	goto :DONE
)
	
cmd /c mttr-calculate-conductance.bat %1
timeout 6 /nobreak
cmd /c mttr-read-telemetry-data.bat %1

:DONE
@echo on