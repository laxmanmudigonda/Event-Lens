$ErrorActionPreference = 'Stop'
$eventId = [guid]::NewGuid().ToString()
$body = @{eventId=$eventId;service='order-service';environment='test';level='INFO';message='Idempotency test'} | ConvertTo-Json
1..2 | ForEach-Object { Invoke-RestMethod -Method Post -Uri 'http://localhost:8081/api/v1/logs' -Headers @{'X-API-Key'='evl_dev_key'} -ContentType 'application/json' -Body $body | Out-Null }
Start-Sleep -Seconds 4
$result = Invoke-RestMethod -Uri "http://localhost:8083/api/v1/search?q=Idempotency%20test&service=order-service"
if (@($result.events | Where-Object eventId -eq $eventId).Count -ne 1) { throw 'Duplicate document was created' }
Write-Host 'PASS: duplicate delivery overwrote the same OpenSearch document ID'

