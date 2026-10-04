$ErrorActionPreference = 'Stop'
$eventId = [guid]::NewGuid().ToString()
$body = @{
  eventId = $eventId
  service = 'payment-service'
  environment = 'demo'
  level = 'ERROR'
  message = 'Smoke-test payment timeout'
  traceId = "trace-$eventId"
  metadata = @{ transactionId = 'tx-smoke' }
} | ConvertTo-Json
$accepted = Invoke-RestMethod -Method Post -Uri 'http://localhost:8081/api/v1/logs' -Headers @{'X-API-Key'='evl_dev_key'} -ContentType 'application/json' -Body $body
if ($accepted.status -ne 'accepted') { throw 'Ingestion did not accept the event' }
for ($attempt = 0; $attempt -lt 20; $attempt++) {
  Start-Sleep -Seconds 1
  $found = Invoke-RestMethod -Uri "http://localhost:8083/api/v1/search?traceId=trace-$eventId"
  if ($found.total -eq 1) { Write-Host "PASS: event $eventId traversed ingestion -> Kafka -> OpenSearch -> query"; exit 0 }
}
throw "Event $eventId was not searchable within 20 seconds"

