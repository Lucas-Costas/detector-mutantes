package persistence.neo4j.operation;

import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import persistence.neo4j.query.CypherQuery;

public class SingleLongChyperOperation implements TransactionWork<Long> {

    private CypherQuery cypherQuery;

    public SingleLongChyperOperation(CypherQuery cypherQuery) {
        this.cypherQuery = cypherQuery;
    }

    @Override
    public Long execute(Transaction tx)
    {
        return tx.run(cypherQuery.query(),cypherQuery.getParametros())
                .single()
                .get(0)
                .asLong();
    }

}
