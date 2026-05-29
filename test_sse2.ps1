try {
    $uri = "http://localhost:9002/jsp/mcp/sse"
    $request = [System.Net.WebRequest]::Create($uri)
    $request.Method = "GET"
    $request.Accept = "text/event-stream"
    $request.Timeout = 5000
    $response = $request.GetResponse()
    $stream = $response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $buffer = New-Object System.Char[] 2000
    $charsRead = $reader.Read($buffer, 0, $buffer.Length)
    if ($charsRead -gt 0) {
        $content = New-Object System.String($buffer, 0, $charsRead)
        Write-Host "SSE Response: $content"
    } else {
        Write-Host "No data received"
    }
    $reader.Close()
    $response.Close()
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}
