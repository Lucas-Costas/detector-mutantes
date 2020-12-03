import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

public class Testing implements AutoCloseable {

    private final Driver driver;

    public Testing( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public void executeQuery(CypherQuery cypherQuery )
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    Result result = tx.run(cypherQuery.query(),cypherQuery.getParametros());
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println( greeting );
        }
    }

    public static void main( String... args ) throws Exception
    {
        try ( Testing testing = new Testing( "bolt://localhost:7687/", "neo4j", "s3cr3t" ) )
        {
            String[] secuencias = {"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"};
            registrarSecuenciasHorizontales(testing, secuencias);
            testing.executeQuery(new AddVerticalRelationsQuery());
            testing.executeQuery(new AddDiagonalRelationsQuery());
        }
    }

    private static void registrarSecuenciasHorizontales(Testing testing, String[] secuencias) {
        for (int j = 1; j <= secuencias.length; j++) {
            String[] secuencia = secuencias[j-1].split("");
            testing.executeQuery(new CreateGen(new Gen(1,j,secuencia[0])));
            for (int i = 1; i < secuencia.length; i++) {
                testing.executeQuery(new AddHorizontalRelationQuery(new GenId(i,j),new Gen(i+1,j,secuencia[i])));
            }
        }
    }

}
