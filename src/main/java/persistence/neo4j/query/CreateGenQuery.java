package persistence.neo4j.query;

import domain.Gen;
import persistence.neo4j.query.CypherQuery;

import java.util.HashMap;
import java.util.Map;

public class CreateGenQuery implements CypherQuery {

    private static final String INDEX_HORIZONTAL = "indexH";
    private static final String INDEX_VERTICAL = "indexV";
    private static final String BASE = "base";

    private Map<String, Object> parametros = new HashMap<>();

    public CreateGenQuery(Gen gen) {
        parametros.put(INDEX_HORIZONTAL,gen.getCoordinate().getHorizontalIndex());
        parametros.put(INDEX_VERTICAL,gen.getCoordinate().getVerticalIndex());
        parametros.put(BASE,gen.getBase());
    }

    @Override
    public String query() {
        return "CREATE (a) " +
                "SET a.base= $base, a.indexV= $indexV, a.indexH= $indexH " +
                "RETURN id(a)";
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

}
