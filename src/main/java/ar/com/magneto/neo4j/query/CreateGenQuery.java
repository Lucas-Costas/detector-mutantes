package ar.com.magneto.neo4j.query;

import ar.com.magneto.domain.Gen;

import java.util.HashMap;
import java.util.Map;

public class CreateGenQuery implements CypherQuery {

    private static final String INDEX_HORIZONTAL = "indexH";
    private static final String INDEX_VERTICAL = "indexV";
    private static final String GENOME_ID = "genomeId";
    private static final String BASE = "base";

    private Map<String, Object> parametros = new HashMap<>();

    public CreateGenQuery(Gen gen) {
        parametros.put(INDEX_HORIZONTAL,gen.getCoordinate().getHorizontalIndex());
        parametros.put(INDEX_VERTICAL,gen.getCoordinate().getVerticalIndex());
        parametros.put(GENOME_ID,gen.getGenomeId());
        parametros.put(BASE,gen.getBase());
    }

    @Override
    public String query() {
        return "CREATE (a:Gen) " +
                "SET a.base= $base, a.indexV= $indexV, a.indexH= $indexH, a.genomeId = $genomeId " +
                "RETURN id(a)";
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

}
