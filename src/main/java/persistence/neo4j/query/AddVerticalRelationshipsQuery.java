package persistence.neo4j.query;

import persistence.neo4j.query.CypherQuery;

import java.util.HashMap;
import java.util.Map;

public class AddVerticalRelationshipsQuery implements CypherQuery {

    @Override
    public String query() {
        return "MATCH (a),(b {indexH:a.indexH,indexV:a.indexV+1}) " +
                "CREATE (a)-[r:VERTICAL]->(b) " +
                "RETURN toString(COUNT(b))";
    }

    @Override
    public Map<String, Object> getParametros() {
        return new HashMap<>();
    }
}
