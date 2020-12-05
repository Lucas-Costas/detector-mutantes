package ar.com.magneto.neo4j.query;

import java.util.HashMap;
import java.util.Map;

public class AddVerticalRelationshipsQuery implements CypherQuery {

    private static final String GENOME_ID = "genomeId";

    private Map<String, Object> parametros = new HashMap<>();

    public AddVerticalRelationshipsQuery(String genomeId) {
        parametros.put(GENOME_ID,genomeId);
    }

    @Override
    public String query() {
        return "MATCH (a),(b {indexH:a.indexH,indexV:a.indexV+1,genomeId: $genomeId }) " +
                "CREATE (a)-[r:VERTICAL]->(b) " +
                "RETURN b.base";
    }

    @Override
    public Map<String, Object> getParametros() {
        return parametros;
    }
}
