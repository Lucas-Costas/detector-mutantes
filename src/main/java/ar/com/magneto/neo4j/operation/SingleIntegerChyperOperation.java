package ar.com.magneto.neo4j.operation;

import ar.com.magneto.exception.CypherOperationException;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import ar.com.magneto.neo4j.query.CypherQuery;
import org.neo4j.driver.exceptions.NoSuchRecordException;

public class SingleIntegerChyperOperation implements TransactionWork<Integer> {

    private static final String RESPONSE_SIZE_INVALID = "La query no retornó exáctamente un registro como se esperaba";

    private CypherQuery<Integer> cypherQuery;

    public SingleIntegerChyperOperation(CypherQuery<Integer> cypherQuery) {
        this.cypherQuery = cypherQuery;
    }

    @Override
    public Integer execute(Transaction tx)
    {
        try {
            return tx.run(cypherQuery.query(),cypherQuery.getParametros())
                    .single()
                    .get(0)
                    .asInt();
        } catch (NoSuchRecordException ex) {
            throw new CypherOperationException(RESPONSE_SIZE_INVALID);
        }
    }

}
