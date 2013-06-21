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
[System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy

$baseuri = "https://localhost:8443"
$uri = New-Object System.Uri ($baseuri + "/es-dms-site")
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$cookieContainer = New-Object System.Net.CookieContainer
$session.Cookies = $cookieContainer
$headers = @{}
[string] $token = $null

function setToken() {
    Write-Debug "Cookie header: ($session.Cookies.GetCookieHeader($uri))"
    foreach ($cookie in $session.Cookies.GetCookies($uri)) {
        if ($cookie.Name -eq "ES_DMS_TICKET") {
            Write-Host "Cookie: " $cookie.Name " - value: " $cookie.Value -ForegroundColor Cyan
            $script:token = $cookie.Value
            Write-Host "Token: $token" -ForegroundColor Red
            $headers.Add("ES_DMS_TICKET", $cookie.Value)
            break
        }
    }
}

function getUri([string] $path) {
    [System.Uri]$localuri = New-Object System.Uri ($uri.AbsoluteUri + $path)
    return $localuri
}

function login([string] $user = "admin", [string] $password = "secret") {
    Write-Host "*** login ***" -ForegroundColor Yellow
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
    Write-Host "Token: $token" -ForegroundColor Red
#curl --request POST --header "ES_DMS_TICKET: d732c84f-cfa6-42a0-97af-eb4bfb25a187" --form "name=pippo" --form "file=@sample.pdf;type=application/pdf" --insecure https://localhost:8443/es-dms-site/api/documents/upload --include
    $command = "curl --request POST --header 'ES_DMS_TICKET: {0}' --form 'name={1}' --form 'file=@{1};type={2}' --insecure https://localhost:8443/es-dms-site/api/documents/upload --include" -f $token, $filename, $contentType
    Write-Host "Execute command: $command" -ForegroundColor Yellow
    $response = Invoke-Expression $command
    Write-Host "Found response: $response" -ForegroundColor Cyan
}

function import([string] $path) {
    #Get-ChildItem -Path V:\Myfolder -Filter CopyForbuild.bat -Recurse
    $files = Get-ChildItem -Path $path -Recurse | Where {!$_.PSIsContainer}# | Select-Object FullName
    foreach($file in $files) {
        importFile $file
    }
}

function importFile($file) {
#$file | Get-Member
    if (Test-Path $file.FullName) {
        $mimeType = Get-MimeType -extension $file.Extension 
        Write-Host "Import file extension " $file.Extension " - " $mimeType
        if ($mimeType -ne $null) {
            upload $file.FullName $mimeType
        }
    }
}

function Get-MimeType()
{
  param($extension = $null);
  $mimeType = $null;
  if ( $null -ne $extension )
  {
    $drive = Get-PSDrive HKCR -ErrorAction SilentlyContinue;
    if ( $null -eq $drive )
    {
      $drive = New-PSDrive -Name HKCR -PSProvider Registry -Root HKEY_CLASSES_ROOT
    }
    $mimeType = (Get-ItemProperty HKCR:$extension)."Content Type";
  }
  $mimeType;
}

cls
login "admin" "secret"
#search "vaadin"
#upload "test.pdf" "application/pdf"
#upload "test.txt"
#search "FilterTcpDropDown"
#search "Escalation"
import "D:\Users\Richard\Documents\OpenText"
#logout