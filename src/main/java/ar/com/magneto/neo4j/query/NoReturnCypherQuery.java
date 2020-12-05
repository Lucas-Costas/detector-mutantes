package ar.com.magneto.neo4j.query;

import ar.com.magneto.neo4j.operation.DefaultCypherOperation;
import org.neo4j.driver.TransactionWork;

public interface NoReturnCypherQuery extends CypherQuery<String> {

    @Override
    default TransactionWork<String> asOperation() {
        return new DefaultCypherOperation(this);
    }

}
