package ar.com.magneto.neo4j;

import ar.com.magneto.neo4j.query.CypherQuery;
import ar.com.magneto.neo4j.query.NoReturnCypherQuery;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;

public class Neo4jAdapter implements AutoCloseable {

    private final Driver driver;

    private String uri = "bolt://localhost:7687/";
    private String user = "neo4j";
    private String password = "s3cr3t";

    public Neo4jAdapter() {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() {
        driver.close();
    }

    public Integer executeWithIntegerResult(CypherQuery<Integer> cypherQuery ) {
        return execWriteTransaction(cypherQuery.asOperation());
    }

    public void execute(NoReturnCypherQuery cypherQuery){
        execWriteTransaction(cypherQuery.asOperation());
    }

    private <T> T execWriteTransaction(TransactionWork<T> cypherOperation){
        try ( Session session = driver.session() ){
            return session.writeTransaction( cypherOperation );
        }
    }

}
