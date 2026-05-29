$body = '{"jsonrpc":"2.0","id":1,"method":"tools/list"}'
$wc = New-Object System.Net.WebClient
$wc.Headers.Add("Content-Type", "application/json")
try {
    $result = $wc.UploadString("http://localhost:9002/jsp/mcp/message", "POST", $body)
    Write-Host "OK: $result"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}
