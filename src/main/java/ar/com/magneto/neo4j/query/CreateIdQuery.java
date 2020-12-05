package ar.com.magneto.neo4j.query;

import java.util.HashMap;
import java.util.Map;

public class CreateIdQuery implements CypherQuery {

    private static final String ID = "dnaId";

    private Map<String, Object> parametros = new HashMap<>();

    public CreateIdQuery(String id) {
        parametros.put(ID,id);
    }

    @Override
    public String query() {
        return "CREATE (a:Dna) " +
                "SET a.dnaId = $dnaId " +
                "RETURN a.dnaId";
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

}
