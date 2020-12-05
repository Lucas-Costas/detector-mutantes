package ar.com.magneto.neo4j.query;

import org.neo4j.driver.TransactionWork;

import java.util.Map;

public interface CypherQuery<T> {
    String query();
    Map<String, Object> getParametros();
    TransactionWork<T> asOperation();
}
