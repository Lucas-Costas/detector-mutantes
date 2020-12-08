package ar.com.magneto.neo4j;

import ar.com.magneto.exception.Neo4jAdapterException;
import ar.com.magneto.neo4j.query.CypherQuery;
import ar.com.magneto.neo4j.query.NoReturnCypherQuery;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class Neo4jAdapter implements AutoCloseable {

    private static final String CLOSE_CONNECTION_ERROR = "Ocurrio un error al cerrar a conexión con Neo4J";
    private static final String EXEC_TRANSACTION_ERROR = "Ocurrio un error al ejecutar la transacción en Neo4J";

    private Driver driver;

    @Value("${neo4j.url}")
    private String uri;

    @Value("${neo4j.user}")
    private String user;

    @Value("${neo4j.password}")
    private String password;

    @PostConstruct
    public void init() {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() {
        try {
            driver.close();
        } catch (Exception ex){
            throw new Neo4jAdapterException(CLOSE_CONNECTION_ERROR,ex);
        }
    }

    public Integer executeWithIntegerResult(CypherQuery<Integer> cypherQuery ) {
        return execWriteTransaction(cypherQuery.asOperation());
    }

    public void execute(NoReturnCypherQuery cypherQuery){
        execWriteTransaction(cypherQuery.asOperation());
    }

    private <T> T execWriteTransaction(TransactionWork<T> cypherOperation){
        try ( Session session = driver.session() ){
            return session.writeTransaction(cypherOperation);
        } catch (Exception ex) {
            throw new Neo4jAdapterException(EXEC_TRANSACTION_ERROR, ex);
        }
    }

}
