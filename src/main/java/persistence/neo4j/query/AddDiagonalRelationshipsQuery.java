package persistence.neo4j.query;

import persistence.neo4j.query.CypherQuery;

import java.util.HashMap;
import java.util.Map;

public class AddDiagonalRelationshipsQuery implements CypherQuery {

    @Override
    public String query() {
        return "MATCH (a),(b {indexH:a.indexH+1,indexV:a.indexV+1})\n" +
                "CREATE (a)-[r:DIAGONAL]->(b)\n" +
                "RETURN toString(COUNT (b))";
    }

    @Override
    public Map<String, Object> getParametros() {
        return new HashMap<>();
    }
}
