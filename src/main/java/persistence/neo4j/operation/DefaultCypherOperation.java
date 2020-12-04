package persistence.neo4j.operation;

import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import persistence.neo4j.query.CypherQuery;

public class DefaultCypherOperation implements TransactionWork<String> {

    private CypherQuery cypherQuery;

    public DefaultCypherOperation(CypherQuery cypherQuery) {
        this.cypherQuery = cypherQuery;
    }

    @Override
    public String execute(Transaction tx)
    {
        tx.run(cypherQuery.query(),cypherQuery.getParametros());
        return "OK";
    }
}
