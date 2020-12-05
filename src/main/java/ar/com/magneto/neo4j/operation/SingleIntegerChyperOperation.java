package ar.com.magneto.neo4j.operation;

import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import ar.com.magneto.neo4j.query.CypherQuery;

public class SingleIntegerChyperOperation implements TransactionWork<Integer> {

    private CypherQuery cypherQuery;

    public SingleIntegerChyperOperation(CypherQuery cypherQuery) {
        this.cypherQuery = cypherQuery;
    }

    @Override
    public Integer execute(Transaction tx)
    {
        return tx.run(cypherQuery.query(),cypherQuery.getParametros())
                .single()
                .get(0)
                .asInt();
    }

}