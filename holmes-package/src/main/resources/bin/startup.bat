@echo off
setlocal

set bin_path=%~p0
call :resolve_path "%bin_path%.." home_path
set lib_path=%home_path%\lib

rem Set java args
set java_args=-Dnet.holmes.home="%home_path%" -Dfile.encoding=UTF-8
rem echo JAVA_ARGS: %java_args%

rem Set classpath
set class_path=
for /F %%a in ('dir /b %lib_path%\*.jar') do call :set_class_path %%a

set class_path="%class_path:~1%"
rem echo CLASS_PATH: %class_path%

"%JAVA_HOME%\bin\java.exe" -classpath %class_path% %java_args% net.holmes.core.MediaServerImpl

goto :EOF

:resolve_path
set %2=%~f1
goto :EOF

:set_class_path
set class_path=%class_path%;%lib_path%\%1
goto :EOF
