[CmdletBinding(SupportsShouldProcess=$true)]

Param(
    [Parameter(Mandatory=$true)][string] $path,
    [Parameter(Mandatory=$false)][Alias("u")][string] $username = "admin",
    [Parameter(Mandatory=$false)][Alias("p")][string] $password = "secret",
    [Parameter(Mandatory=$false)][string] $baseuri = "https://localhost:8443"
)

Add-Type @"
    using System.Net;
    using System.Security.Cryptography.X509Certificates;
    public class TrustAllCertsPolicy : ICertificatePolicy {
        public bool CheckValidationResult(
            ServicePoint srvPoint, X509Certificate certificate,
            WebRequest request, int certificateProblem) {
            return true;
        }
    }
"@

Add-Type -AssemblyName System.Web

[System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy

$uri = New-Object System.Uri ($baseuri + "/es-dms-site")
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$cookieContainer = New-Object System.Net.CookieContainer
$session.Cookies = $cookieContainer
$headers = @{}
[string] $token = $null
$mimeTypes = @{};

function UrlEncode([string]$url) {
  [Web.Httputility]::UrlEncode($url)
}

function setToken() {
    Write-Debug "Cookie header: ($session.Cookies.GetCookieHeader($uri))"
    foreach ($cookie in $session.Cookies.GetCookies($uri)) {
        if ($cookie.Name -eq "ES_DMS_TICKET") {
            Write-Host "Cookie: " $cookie.Name " - value: " $cookie.Value -ForegroundColor Cyan
            $script:token = $cookie.Value
            $headers.Add("ES_DMS_TICKET", $cookie.Value)
            break
        }
    }
}

function getUri([string] $path) {
    [System.Uri]$localuri = New-Object System.Uri ($uri.AbsoluteUri + $path)
    return $localuri
}

function login([string] $user, [string] $password) {
    Write-Host "*** login wit $user ***" -ForegroundColor Yellow
    $jsonCredential = @{username=$user;password=$password} | ConvertTo-Json
    $response = Invoke-RestMethod -Uri (getUri "/api/auth/login") -Method Post -Body $jsonCredential -ContentType "application/json" -Verbose -SessionVariable session
    setToken
}

function logout() {
    Write-Host "*** logout ***" -ForegroundColor Yellow
    Invoke-RestMethod -Uri (getUri "/api/auth/logout") -Method Post -ContentType "application/json" -Verbose -WebSession $session
}

function search([string] $criteria) {
    Write-Host "*** search ***" -ForegroundColor Yellow
    $headers.GetEnumerator() | Sort-Object Name
    $response = Invoke-RestMethod -Uri (getUri ("/api/documents/search/" + $criteria)) -Method Get -Verbose -Headers $headers
    parseSearchResult $response
}

function parseSearchResult($response) {
    Write-Host "To Json $(ConvertTo-Json $response)" -ForegroundColor Gray
    foreach($x in $response) {
    $x.id
    }
}

function uploadOld([string] $filename, [string] $contentType = "text/plain") {
    Write-Host "*** upload ***" -ForegroundColor Yellow
    $contents = $(Get-Content $filename -encoding byte)
    $body = @"
--boundary
Content-Disposition: form-data; name="name"

$filename
--boundary
Content-Disposition: form-data; name="file"; filename="$filename"
Content-Type: $contentType

$contents
--boundary--
"@
    $body
    $body | Out-File request.txt -Encoding Default

    $h = $headers
    #$h.Add("Content-Length", $body.Length.ToString())
    #$h.Add("Content-Type", "multipart/form-data; boundary=boundary")
    #$response = Invoke-RestMethod -Uri (getUri ("/api/documents/upload")) -Method Post -ContentType "multipart/form-data" -InFile $filename -Verbose -Headers $headers
    #$response = Invoke-RestMethod -Uri (getUri ("/api/documents/upload")) -Method Post -ContentType "multipart/form-data" -Body $body -Verbose -Headers $headers
    $response = Invoke-RestMethod -Uri (getUri ("/api/documents/upload")) -Method Post -ContentType "multipart/form-data;boundary=boundary" -Body $body -Verbose -Headers $headers
    $response
}

function upload([string] $filename, [string] $contentType = "text/plain") {
    Write-Host "*** upload ***" -ForegroundColor Yellow
    #curl --request POST --header "ES_DMS_TICKET: d732c84f-cfa6-42a0-97af-eb4bfb25a187" --form "name=pippo" --form "file=@sample.pdf;type=application/pdf" --insecure https://localhost:8443/es-dms-site/api/documents/upload --include
    $uri = (getUri("/api/documents/upload")).AbsoluteUri
    #$command = "curl --request POST --header 'ES_DMS_TICKET: {0}' --form 'name={1}' --form 'file=@{1};type={2}' --insecure https://localhost:8443/es-dms-site/api/documents/upload --include" -f $token, $filename, $contentType
    $command = "curl --request POST --header 'ES_DMS_TICKET: {0}' --form 'name={1}' --form 'file=@{1};type={2}' --insecure {3} --include" -f $token, $filename, $contentType, $uri
    Write-Host "Execute command: $command" -ForegroundColor Yellow
    $response = Invoke-Expression $command
    Write-Host "curl response: $response" -ForegroundColor Cyan
}

function import([string] $path) {
    $files = Get-ChildItem -Path $path -Recurse | Where {!$_.PSIsContainer}
    foreach($file in $files) {
        importFile $file
    }
}

function importFile($file) {
#$file | Get-Member
    if (Test-Path $file.FullName) {
        $mimeType = Get-MimeType -extension $file.Extension 
        
        if ($mimeType -eq $null) {
            $mimeType = "text/plain"
            Write-Host "Mime type not found using: " $mimeType
        }

        Write-Host "Importing file: " $file.Name " - MIME: " $mimeType " - from location: " $file.FullName
        upload $file.FullName $mimeType
    }
}

function readMimeTypes([string] $mineTypesFile) {
    [xml]$file = Get-Content  $mineTypesFile
    
    Write-Host "MIME file: " $mineTypesFile

    foreach( $mimetype in $file.Worksheet.'row') {

        $Ext = $mimetype.'Ext';
        if ($Ext){
            try{
                $mimeTypes.Add($Ext, $mimetype.Data);
                write-host $Ext "-" $mimetype.Data; 
            }
            catch{
                write-host $_.Exception.Message
            }
        }
    }
}

function readTikaMimeTypes([string] $xmlFile) {
    Write-Host "MIME file: " $xmlFile

    [xml]$file = Get-Content  $xmlFile
    
    foreach( $mimetype in $file.'mime-info'.'mime-type') {
        if ($mimetype.glob -eq $null) {
            continue
        }
        foreach($ext in $mimetype.glob) {
            [string] $msg = "type {0} - extension {1}" -f $mimetype.type, $ext.pattern
            Write-Debug $msg
            try {
                $mimeTypes.Add($ext.pattern, $mimetype.type);
            }
            catch{
                Write-Warning $_.Exception.Message
            }
        }
    }
}

function Get-MimeType()
{
  param($extension = $null);
  $mimeType = $null;
  #write-host "Looking for extension: " $extension
  if ( $null -ne $extension )
  {
    #$drive = Get-PSDrive HKCR -ErrorAction SilentlyContinue;
    #if ( $null -eq $drive )
    #{
    #  $drive = New-PSDrive -Name HKCR -PSProvider Registry -Root HKEY_CLASSES_ROOT
    #}
    #$mimeType = (Get-ItemProperty HKCR:$extension)."Content Type";

    $mimeType = $mimeTypes.Get_Item($extension);
    #write-host $mimeType " found"
  }
  $mimeType;
}

cls
login $username $password
#search "vaadin"
#upload "test.pdf" "application/pdf"
#upload "test.txt"
#search "FilterTcpDropDown"
#search "Escalation"

readTikaMimeTypes "tika-mimetypes.xml"
#$mimefilepath = Resolve-Path "mimeTypes.xml";
#readMimeTypes $mimefilepath #"C:\Users\admin\Documents\GitHub\es-dms\es-dms-site\src\test\resources\mimeTypes.xml"
import $path
#logout

#cls
