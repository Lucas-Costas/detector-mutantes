package ar.com.magneto.neo4j.query;

import ar.com.magneto.neo4j.operation.SingleIntegerChyperOperation;
import org.neo4j.driver.TransactionWork;

import java.util.HashMap;
import java.util.Map;

public class CountMutantSecuencesQuery implements CypherQuery<Integer> {

    @Override
    public String query() {
        return "MATCH path = (a)-[*3..]->(b {base:a.base}) " +
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
        return new HashMap<>();
    }

    @Override
    public TransactionWork<Integer> asOperation() {
        return new SingleIntegerChyperOperation(this);
    }

}
