$wc = New-Object System.Net.WebClient
$wc.Headers.Add("Accept", "text/event-stream")
try {
    $result = $wc.DownloadString("http://localhost:9002/jsp/mcp/sse")
    Write-Host "OK: $result"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}
