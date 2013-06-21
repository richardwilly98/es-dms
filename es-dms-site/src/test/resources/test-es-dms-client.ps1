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

function setToken() {
    Write-Host "Cookie header: " $session.Cookies.GetCookieHeader($uri)
    foreach ($cookie in $session.Cookies.GetCookies($uri)) {
        Write-Host "Cookie: " $cookie -ForegroundColor Red
        if ($cookie.Name -eq "ES_DMS_TICKET") {
            Write-Host "Cookie: " $cookie.Name " - value: " $cookie.Value -ForegroundColor Cyan
            $headers.Add("ES_DMS_TICKET", $cookie.Value)
            break
        }
    }
}

function getUri([string] $path) {
    [System.Uri]$myuri = New-Object System.Uri ($uri.AbsoluteUri + $path)
    return $myuri
}
function login($user, $password) {
    Write-Host "*** login ***" -ForegroundColor Yellow
    $jsonCredential = @{username=$user;password=$password} | ConvertTo-Json
    #$jsonCredential
    $response = Invoke-RestMethod -Uri (getUri "/api/auth/login") -Method Post -Body $jsonCredential -ContentType "application/json" -Verbose -SessionVariable session
    #$response | get-member
    #$session
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

function upload([string] $filename, [string] $contentType = "text/plain") {
    #$body = @{}
    #$body.Add("name", $filename)
    #$body.Add("file", $(get-content $filename -raw))
    #$contents = $(Get-Content $filename -Encoding Unicode) #gc "my file path"
    #$contents = $(Get-Content $filename -Encoding utf8) #gc "my file path"
    $contents = $(Get-Content $filename -Encoding UTF8)
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
    $body | Out-File request.txt -Encoding utf8

    $h = $headers
    #$h.Add("Content-Length", $body.Length.ToString())
    #$h.Add("Content-Type", "multipart/form-data; boundary=boundary")
    #$response = Invoke-RestMethod -Uri (getUri ("/api/documents/upload")) -Method Post -ContentType "multipart/form-data" -InFile $filename -Verbose -Headers $headers
    #$response = Invoke-RestMethod -Uri (getUri ("/api/documents/upload")) -Method Post -ContentType "multipart/form-data" -Body $body -Verbose -Headers $headers
    $response = Invoke-RestMethod -Uri (getUri ("/api/documents/upload")) -Method Post -ContentType "multipart/form-data;boundary=boundary" -Body $body -Verbose -Headers $headers
    $response
}

cls
login "admin" "secret"
#search "vaadin"
#upload "sample.pdf" "application/pdf"
upload "test.txt"
search "FilterTcpDropDown"
#search "sample"
#logout