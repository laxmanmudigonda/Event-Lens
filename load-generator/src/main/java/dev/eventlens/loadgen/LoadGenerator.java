package dev.eventlens.loadgen;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.LockSupport;

public final class LoadGenerator {
    private static final List<String> SERVICES=List.of("auth-service","payment-service","order-service","inventory-service","notification-service");
    private static final List<String> INFO=List.of("Request completed","Order created","Cache refreshed","Inventory reserved","Notification delivered");
    private static final List<String> ERRORS=List.of("Database connection timeout","Payment provider rejected request","Downstream service unavailable","Invalid token","Inventory reservation failed");
    private LoadGenerator(){}
    public static void main(String[] args) throws Exception {
        Config c=Config.parse(args); var http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();var json=new ObjectMapper().findAndRegisterModules();
        var pool=Executors.newFixedThreadPool(Math.min(32,Math.max(4,c.rate/10)));var sent=new AtomicLong();var accepted=new AtomicLong();var failed=new AtomicLong();var latencyNs=new LongAdder();
        long start=System.nanoTime(),deadline=start+Duration.ofSeconds(c.duration).toNanos(),interval=1_000_000_000L/c.rate,next=start;var random=new Random(c.seed);
        while(System.nanoTime()<deadline){long seq=sent.incrementAndGet();String service=SERVICES.get(random.nextInt(Math.min(c.services,SERVICES.size())));boolean isError=random.nextDouble()<c.errorRate;String trace="trace-"+(seq/5);var body=Map.of("timestamp",Instant.now().toString(),"service",service,"environment","load-test","level",isError?"ERROR":"INFO","message",isError?ERRORS.get(random.nextInt(ERRORS.size())):INFO.get(random.nextInt(INFO.size())),"traceId",trace,"requestId","load-"+seq,"host","generator-1","metadata",Map.of("sequence",seq,"runSeed",c.seed));String payload=json.writeValueAsString(body);
            pool.submit(()->{long t=System.nanoTime();try{var request=HttpRequest.newBuilder(URI.create(c.url+"/api/v1/logs")).header("Content-Type","application/json").header("X-API-Key",c.apiKey).POST(HttpRequest.BodyPublishers.ofString(payload,StandardCharsets.UTF_8)).timeout(Duration.ofSeconds(10)).build();var response=http.send(request,HttpResponse.BodyHandlers.discarding());if(response.statusCode()==202)accepted.incrementAndGet();else failed.incrementAndGet();}catch(Exception ex){failed.incrementAndGet();}finally{latencyNs.add(System.nanoTime()-t);}});
            next+=interval;long wait=next-System.nanoTime();if(wait>0)LockSupport.parkNanos(wait);
        }
        pool.shutdown();pool.awaitTermination(30,TimeUnit.SECONDS);double elapsed=(System.nanoTime()-start)/1e9;long complete=accepted.get()+failed.get();
        System.out.printf(Locale.ROOT,"sent=%d accepted=%d failed=%d elapsed=%.2fs throughput=%.2f req/s avgLatency=%.2fms%n",sent.get(),accepted.get(),failed.get(),elapsed,accepted.get()/elapsed,complete==0?0:latencyNs.sum()/1e6/complete);
    }
    record Config(String url,String apiKey,int rate,int duration,double errorRate,int services,long seed){
        static Config parse(String[] args){Map<String,String> p=new HashMap<>();for(String arg:args){if(arg.startsWith("--")&&arg.contains("=")){String[] pair=arg.substring(2).split("=",2);p.put(pair[0],pair[1]);}}return new Config(p.getOrDefault("url","http://localhost:8081"),p.getOrDefault("api-key","evl_dev_key"),positive(p,"rate",100),positive(p,"duration",60),Double.parseDouble(p.getOrDefault("error-rate","0.05")),Math.min(5,positive(p,"services",5)),Long.parseLong(p.getOrDefault("seed","42")));}
        private static int positive(Map<String,String> p,String key,int fallback){int n=Integer.parseInt(p.getOrDefault(key,String.valueOf(fallback)));if(n<1)throw new IllegalArgumentException(key+" must be positive");return n;}
    }
}
