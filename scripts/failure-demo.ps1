$ErrorActionPreference='Stop'
Write-Host 'Stopping OpenSearch to force bounded retries and dead-lettering...'
$eventId=$null
try {
  docker compose stop opensearch
  $body=@{service='payment-service';environment='failure-demo';level='ERROR';message='Index should fail and enter DLT'}|ConvertTo-Json
  $accepted=Invoke-RestMethod -Method Post -Uri 'http://localhost:8081/api/v1/logs' -Headers @{'X-API-Key'='evl_dev_key'} -ContentType 'application/json' -Body $body
  $eventId=$accepted.eventId
  Start-Sleep -Seconds 12
}
finally {
  docker compose start opensearch
}

for($attempt=0;$attempt -lt 30;$attempt++){
  $health=docker inspect --format='{{.State.Health.Status}}' eventlens-opensearch-1
  if($health -eq 'healthy'){break}
  Start-Sleep -Seconds 3
}
if($health -ne 'healthy'){throw "OpenSearch did not recover; status=$health"}

$dead=@(Invoke-RestMethod -Uri 'http://localhost:8083/api/v1/dead-letters')
if(@($dead|Where-Object {$_.originalEvent.eventId -eq $eventId}).Count -lt 1){throw "No DLT record found for $eventId"}
Invoke-RestMethod -Method Post -Uri "http://localhost:8083/api/v1/dead-letters/$eventId/replay"
Start-Sleep -Seconds 6
$result=Invoke-RestMethod -Uri 'http://localhost:8083/api/v1/search?environment=failure-demo'
if(@($result.events|Where-Object eventId -eq $eventId).Count -ne 1){throw "Replay did not restore $eventId"}
Write-Host "PASS: bounded retries, DLT, recovery, and replay verified for $eventId"
