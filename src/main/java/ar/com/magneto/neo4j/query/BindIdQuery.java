package ar.com.magneto.neo4j.query;

import java.util.HashMap;
import java.util.Map;

public class BindIdQuery implements CypherQuery {

    private static final String DNA_ID = "dnaId";
    private static final String FIRST_NODE_ID = "firstNodeId";

    private Map<String, Object> parametros = new HashMap<>();

    public BindIdQuery(String id, Long firstNodeId) {
        parametros.put(DNA_ID,id);
        parametros.put(FIRST_NODE_ID,firstNodeId);
    }

    @Override
    public String query() {
        return "MATCH (a:Dna {dnaId: $dnaId}),(b:Gen) " +
                "WHERE id(b) = $firstNodeId " +
                "CREATE (a)-[:REPRESENTA]->(b) " +
                "RETURN b.genomeId";
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

}
