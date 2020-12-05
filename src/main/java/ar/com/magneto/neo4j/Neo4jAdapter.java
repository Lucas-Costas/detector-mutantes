package ar.com.magneto.neo4j;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;
import ar.com.magneto.neo4j.operation.DefaultCypherOperation;
import ar.com.magneto.neo4j.operation.SingleIntegerChyperOperation;
import ar.com.magneto.neo4j.operation.SingleLongChyperOperation;
import ar.com.magneto.neo4j.query.CypherQuery;

public class Neo4jAdapter implements AutoCloseable {

    private final Driver driver;

    private String uri = "bolt://localhost:7687/";
    private String user = "neo4j";
    private String password = "s3cr3t";

    public Neo4jAdapter() {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public Integer executeWithIntegerResult(CypherQuery cypherQuery ) {
        return execWriteTransaction(new SingleIntegerChyperOperation(cypherQuery));
    }

    public Long executeWithLongResult(CypherQuery cypherQuery ) {
        return execWriteTransaction(new SingleLongChyperOperation(cypherQuery));
    }

    public void execute(CypherQuery cypherQuery){
        execWriteTransaction(new DefaultCypherOperation(cypherQuery));
    }

    private <T> T execWriteTransaction(TransactionWork<T> cypherOperation){
        try ( Session session = driver.session() ){
            return session.writeTransaction( cypherOperation );
        }
    }

}
