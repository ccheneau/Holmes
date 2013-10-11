#define MyAppName "Holmes UPnP Media Server"
#define MyAppVersion "0.5.1"
#define MyAppPublisher "Cedric Cheneau"
#define MyAppURL "http://ccheneau.github.io/Holmes/"
#define MyAppExeName "startup.bat"

[Setup]
AppId={{338670D7-052E-4682-829C-33329C828C34}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
LicenseFile=target\holmes-{#MyAppVersion}\holmes-{#MyAppVersion}\license.txt
OutputBaseFilename=holmes-{#MyAppVersion}-setup
OutputDir=target
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "french"; MessagesFile: "compiler:Languages\French.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "target\holmes-{#MyAppVersion}\holmes-{#MyAppVersion}\bin\startup.bat"; DestDir: "{app}\bin"; Flags: ignoreversion
Source: "target\holmes-{#MyAppVersion}\holmes-{#MyAppVersion}\holmes.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "target\holmes-{#MyAppVersion}\holmes-{#MyAppVersion}\license.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "target\holmes-{#MyAppVersion}\holmes-{#MyAppVersion}\Readme.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "target\holmes-{#MyAppVersion}\holmes-{#MyAppVersion}\ui\*"; DestDir: "{app}\ui"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "target\holmes-{#MyAppVersion}\holmes-{#MyAppVersion}\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "target\holmes-{#MyAppVersion}\holmes-{#MyAppVersion}\conf\*"; DestDir: "{app}\conf"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "target\holmes-{#MyAppVersion}\holmes-{#MyAppVersion}\resources\*"; DestDir: "{app}\resources"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\bin\{#MyAppExeName}"; IconFilename: "{app}\holmes.ico"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\bin\{#MyAppExeName}"; Tasks: desktopicon; IconFilename: "{app}\holmes.ico"

[Run]
Filename: "{app}\bin\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: shellexec postinstall skipifsilent

