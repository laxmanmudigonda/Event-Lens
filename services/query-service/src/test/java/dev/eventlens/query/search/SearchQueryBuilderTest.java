package dev.eventlens.query.search;
import static org.junit.jupiter.api.Assertions.*; import dev.eventlens.model.LogLevel; import java.time.Instant; import org.junit.jupiter.api.Test;
class SearchQueryBuilderTest {
 private final SearchQueryBuilder builder=new SearchQueryBuilder();
 @Test void combinesSafeFiltersAndText(){var query=builder.build(new SearchCriteria("timeout","payments","prod",LogLevel.ERROR,Instant.parse("2026-01-01T00:00:00Z"),null,"trace-1",0,50,"desc"));assertTrue(query.toString().contains("payments"));assertTrue(query.toString().contains("match"));}
 @Test void rejectsUnboundedAndDeepPages(){assertThrows(IllegalArgumentException.class,()->builder.build(new SearchCriteria(null,null,null,null,null,null,null,0,201,"desc")));assertThrows(IllegalArgumentException.class,()->builder.build(new SearchCriteria(null,null,null,null,null,null,null,100,200,"desc")));}
}
