@echo off
REM Copyright (C) 2012  Cedric Cheneau
REM 
REM This program is free software: you can redistribute it and/or modify
REM it under the terms of the GNU General Public License as published by
REM the Free Software Foundation, either version 3 of the License, or
REM (at your option) any later version.
REM 
REM This program is distributed in the hope that it will be useful,
REM but WITHOUT ANY WARRANTY; without even the implied warranty of
REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
REM GNU General Public License for more details.
REM 
REM You should have received a copy of the GNU General Public License
REM along with this program.  If not, see <http://www.gnu.org/licenses/>.

setlocal

rem Set library path
set bin_path=%~p0
call :resolve_path "%bin_path%.." home_path
set lib_path=%home_path%\lib

rem Set java args
set java_args=-Dnet.holmes.home="%home_path%" -Dfile.encoding=UTF-8

set java=javaw
if "%JAVA_HOME%" == "" call :find_java_home
if not "%JAVA_HOME%" == "" set java="%JAVA_HOME%\bin\javaw.exe"

start "Holmes" ""%java%"" -Xmx30m %java_args% -jar "%lib_path%\holmes-core-${project.version}.jar" 

endlocal

goto :EOF

:resolve_path
set %2=%~f1
goto :EOF

:find_java_home
for /F "skip=2 tokens=2*" %%A in ('reg query "HKLM\Software\JavaSoft\Java Runtime Environment" /v CurrentVersion') do set CurVer=%%B
for /F "skip=2 tokens=2*" %%A in ('reg query "HKLM\Software\JavaSoft\Java Runtime Environment\%CurVer%" /v JavaHome') do set JAVA_HOME=%%B
goto :EOF