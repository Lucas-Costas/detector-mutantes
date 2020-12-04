package persistence.neo4j.query;

import domain.Gen;
import domain.GenCoordinate;

import java.util.HashMap;
import java.util.Map;

public class AddHorizontalRelationshipsQuery implements CypherQuery {

    private static final String INDEX_H_INICIO = "indexHA";
    private static final String INDEX_V_INICIO = "indexVA";
    private static final String INDEX_H_FIN = "indexHB";
    private static final String INDEX_V_FIN = "indexVB";
    private static final String BASE_FIN = "baseB";

    private Map<String, Object> parametros = new HashMap<>();

    public AddHorizontalRelationshipsQuery(GenCoordinate genInicialId, Gen newGen) {
        parametros.put(INDEX_H_INICIO,genInicialId.getHorizontalIndex());
        parametros.put(INDEX_V_INICIO,genInicialId.getVerticalIndex());
        parametros.put(INDEX_H_FIN, newGen.getCoordinate().getHorizontalIndex());
        parametros.put(INDEX_V_FIN,newGen.getCoordinate().getVerticalIndex());
        parametros.put(BASE_FIN,newGen.getBase());
    }

    @Override
    public String query() {
        return "MATCH (a {indexH: $indexHA, indexV: $indexVA}) " +
                "CREATE (a)-[r:HORIZONTAL]->(b) " +
                "SET b.base= $baseB , b.indexH= $indexHB , b.indexV= $indexVB " +
                "RETURN b.base";
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

}
