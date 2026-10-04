param([int]$Rate=100,[int]$Duration=60,[int]$Consumers=1)
$ErrorActionPreference='Stop'
docker compose up -d --scale processing-service=$Consumers
mvn -q -pl load-generator -am package -DskipTests
java -jar load-generator/target/load-generator-1.0.0-SNAPSHOT.jar --rate=$Rate --duration=$Duration --error-rate=0.05
Write-Host 'Consumer lag:'
Invoke-RestMethod http://localhost:8083/api/v1/system/kafka | Format-Table

