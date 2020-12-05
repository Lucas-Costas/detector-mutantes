package ar.com.magneto.neo4j.query;

import java.util.HashMap;
import java.util.Map;

public class DeleteGenomeQuery implements NoReturnCypherQuery {

    private static final String GENOME_ID = "genomeId";

    private Map<String, Object> parametros = new HashMap<>();

    public DeleteGenomeQuery(String genomeId) {
        parametros.put(GENOME_ID,genomeId);
    }

    @Override
    public String query() {
        return "MATCH (b:Gen {genomeId: $genomeId }) DETACH DELETE b";
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

}
