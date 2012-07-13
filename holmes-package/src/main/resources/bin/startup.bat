@echo off
setlocal

rem Set library path
set bin_path=%~p0
call :resolve_path "%bin_path%.." home_path
set lib_path=%home_path%\lib

rem Set java args
set java_args=-Dnet.holmes.home="%home_path%" -Dfile.encoding=UTF-8

set java=javaw
if not "%JAVA_HOME%" == "" set java="%JAVA_HOME%\bin\javaw.exe"

start "Holmes" %java% -Xmx30m %java_args% -jar %lib_path%\holmes-core-${project.version}.jar 

endlocal

goto :EOF

:resolve_path
set %2=%~f1
goto :EOF

