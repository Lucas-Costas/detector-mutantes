package persistence.neo4j.query;

import java.util.Map;

public interface CypherQuery {

    public String query();
    public Map<String, Object> getParametros();

}
