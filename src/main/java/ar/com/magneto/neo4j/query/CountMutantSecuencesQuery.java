package ar.com.magneto.neo4j.query;

import ar.com.magneto.neo4j.operation.SingleIntegerChyperOperation;
import org.neo4j.driver.TransactionWork;

import java.util.HashMap;
import java.util.Map;

public class CountMutantSecuencesQuery implements CypherQuery<Integer> {

    private static final String GENOME_ID = "genomeId";

    private Map<String, Object> parametros = new HashMap<>();

    public CountMutantSecuencesQuery(String genomeId) {
        parametros.put(GENOME_ID,genomeId);
    }

    @Override
    public String query() {
        return "MATCH path = (a {genomeId: $genomeId })-[*3..]->(b {base:a.base, genomeId:a.genomeId}) " +
                "WHERE id(a) <> id(b) " +
                "AND none(n IN nodes(path) WHERE n.base<>a.base) " +
                "AND ( " +
                " all(r IN relationships(path) WHERE type(r)=\"VERTICAL\") OR " +
                " all(r IN relationships(path) WHERE type(r)=\"HORIZONTAL\") OR " +
                " all(r IN relationships(path) WHERE type(r)=\"DIAGONAL\") " +
                ") " +
                "RETURN COUNT(path);";
    }

    @Override
    public Map<String, Object> getParametros() {
        return parametros;
    }

    @Override
    public TransactionWork<Integer> asOperation() {
        return new SingleIntegerChyperOperation(this);
    }

}
